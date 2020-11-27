import ch.salt.common.core.exception.BaseException;
import ch.salt.common.core.exception.NotFoundException;
import ch.salt.common.service.AbstractService;
import com.squareup.javapoet.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.lang.model.element.Modifier;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;


public class SaltServiceGenerator {
    private final GenerationServiceOptions generationServiceOptions;
    private       ClassName                converterType;

    public SaltServiceGenerator(GenerationServiceOptions generationServiceOptions) {

        this.generationServiceOptions = generationServiceOptions;
        converterType = ClassName.bestGuess(generationServiceOptions.getEntityConverterClassName());
    }


    public void generate() throws IOException {

        TypeSpec serviceRest = TypeSpec.classBuilder(generationServiceOptions.getServiceClassName())
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addField(newRepositoryField())
                .addField(newConverterField())
                .addField(newLoggerField())
                .addMethod(constructor())
                .addMethod(newGetOneMethod())
                .addMethod(newFindAllMethod())
                .addMethod(newUpdateOneMethod())
                .addMethod(newDeleteOneMethod())
                .addMethod(newCreateMethod())
                .addMethod(countMethod())
                .addMethod(existById())
                .addMethod(saveAll())
                .addJavadoc("REST Service for $L", generationServiceOptions.getRepositoryClassName())
                .addAnnotation(Component.class)
                .addAnnotation(Transactional.class)
                .superclass(AbstractService.class)
                .addAnnotation(AnnotationSpec.builder(Path.class).addMember("value", "$S", generationServiceOptions.getRestPath()).build())
                .build();

        JavaFile javaFile = JavaFile.builder(generationServiceOptions.getServicePackageName(), serviceRest)
                .build();
        javaFile.writeTo(System.out);
        javaFile.writeTo(Paths.get(generationServiceOptions.getOutputFolder()));
    }

    private FieldSpec newRepositoryField() {
        return FieldSpec.builder(buildRepositoryType(), "dao", Modifier.PRIVATE).build();
    }

    private FieldSpec newConverterField() {
        return FieldSpec.builder(buildConverterType(), "converter", Modifier.PRIVATE).build();
    }

    private FieldSpec newLoggerField() {
        return FieldSpec.builder(Logger.class, "LOGGER", Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
                .initializer(" $T.getLogger(" + this.generationServiceOptions.getServiceClassName() + ".class)", LogManager.class)
                .build();
    }

    private MethodSpec constructor() {
        return MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Autowired.class)
                .addParameter(buildRepositoryType(), "dao", Modifier.FINAL)
                .addParameter(buildConverterType(), "converter", Modifier.FINAL)
                .addCode(CodeBlock.builder()
                        .addStatement("super()")
                        .addStatement("this.dao = dao")
                        .addStatement("this.converter = converter")
                        .build())
                .build();
    }

    private MethodSpec newGetOneMethod() {
        MethodSpec.Builder mB = MethodSpec.methodBuilder("getById");
        return declareEndpoint(mB, "/{id}", GET.class)
                .addParameter(createIdParameter())
                .returns(buildEntityDtoType())
                .addCode(CodeBlock.of("return dao.findById(id)\n" +
                        "\t.map(c -> converter.toDTO(c))" +
                        "\t.orElseThrow(() -> new $T(LOGGER, \"ENTITY_NOT_FOUND\", \"Entity not found\"));\n", NotFoundException.class))
                .build();
    }

    private MethodSpec newFindAllMethod() {


        MethodSpec.Builder mB = MethodSpec.methodBuilder("findAll");
        return declareEndpoint(mB, "/", GET.class)
                .addParameter(
                        ParameterSpec.builder(int.class, "size", Modifier.FINAL)
                                .addAnnotation(AnnotationSpec.builder(QueryParam.class).addMember("value", "$S", "size").build())
                                .addAnnotation(AnnotationSpec.builder(DefaultValue.class).addMember("value", "$S", "30").build())
                                .addAnnotation(Positive.class)
                                .build())
                .addParameter(
                        ParameterSpec.builder(int.class, "page", Modifier.FINAL)
                                .addAnnotation(AnnotationSpec.builder(QueryParam.class).addMember("value", "$S", "page").build())
                                .addAnnotation(AnnotationSpec.builder(DefaultValue.class).addMember("value", "$S", "1").build())
                                .addAnnotation(Positive.class)
                                .build())
                .addParameter(
                        ParameterSpec.builder(Sort.Direction.class, "direction", Modifier.FINAL)
                                .addAnnotation(AnnotationSpec.builder(QueryParam.class).addMember("value", "$S", "direction").build())
                                .build())
                .addParameter(
                        ParameterSpec.builder(String.class, "field", Modifier.FINAL)
                                .addAnnotation(AnnotationSpec.builder(QueryParam.class).addMember("value", "$S", "field").build())
                                .build())
                .returns(PageAnswer.class)
                .addCode(CodeBlock.builder()
                        .addStatement("$T pageRequest = PageRequest.of(page, size, direction, field)", PageRequest.class)
                        .addStatement("return $T.of(dao.findAll(pageRequest)\n" +
                                "\t.map(e -> converter.toDTO(e)))", PageAnswer.class)
                        .build())
                .build();
    }

    private MethodSpec newUpdateOneMethod() {
        MethodSpec.Builder mB = MethodSpec.methodBuilder("updateOne");
        return declareEndpoint(mB, "/", PUT.class)
                .addParameter(ParameterSpec.builder(buildEntityDtoType(), "entityDto", Modifier.FINAL).build())
                .returns(buildEntityDtoType())
                .addCode(CodeBlock.builder()
                        .addStatement("$T newEntity = converter.toEntity(entityDto)", this.buildEntityType())
                        .addStatement("return converter.toDTO(dao.save(newEntity))", PageAnswer.class)
                        .build())
                .build();
    }

    private MethodSpec newDeleteOneMethod() {
        MethodSpec.Builder mB = MethodSpec.methodBuilder("deleteOne");
        return declareEndpoint(mB, "/{id}", DELETE.class)
                .addParameter(createIdParameter())
                .returns(void.class)
                .addCode(CodeBlock.builder()
                        .addStatement("dao.deleteById(id)", PageAnswer.class)
                        .build())
                .build();
    }

    private MethodSpec newCreateMethod() {
        MethodSpec.Builder mB = MethodSpec.methodBuilder("createEntity");
        return declareEndpoint(mB, "/", POST.class)
                .addParameter(ParameterSpec.builder(buildEntityDtoType(), "entityDto", Modifier.FINAL).build())
                .returns(buildEntityDtoType())
                .addCode(CodeBlock.builder()
                        .addStatement("$T newEntity = converter.toEntity(entityDto)", buildEntityType())
                        .addStatement("return converter.toDTO(dao.save(newEntity))", PageAnswer.class)
                        .build())
                .build();
    }

    private MethodSpec countMethod() {
        MethodSpec.Builder mB = MethodSpec.methodBuilder("countAll");
        return declareEndpoint(mB, "/count", GET.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(Long.class)
                .addCode(CodeBlock.builder()
                        .addStatement("return dao.count()")
                        .build())
                .build();
    }

    private MethodSpec existById() {
        MethodSpec.Builder mB = MethodSpec.methodBuilder("existById");
        return declareEndpoint(mB, "/{id}", HEAD.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(createIdParameter())
                .returns(Response.class)
                .addCode(CodeBlock.builder()
                        .add("    if (dao.existsById(id)) {\n" +
                                "      return $T.ok().build();\n" +
                                "    } else {\n" +
                                "      return $T.status(Response.Status.NOT_FOUND).build();\n" +
                                "    }", Response.class, Response.class)
                        .build())
                .build();
    }

    private MethodSpec saveAll() {


        MethodSpec.Builder mB = MethodSpec.methodBuilder("saveAll");
        return declareEndpoint(mB, "/batch", POST.class)
                .addParameter(ParameterSpec.builder(ParameterizedTypeName.get(ClassName.get(List.class), buildEntityDtoType()), "entities", Modifier.FINAL).build())
                .returns(PageAnswer.class)
                .addCode(CodeBlock.builder()
                        .addStatement("List<$T> pageRequest =entities.stream().map(converter::toEntity).collect(Collectors.toList());", buildEntityType())
                        .addStatement("return $T.of(" +
                                "$T.stream(dao.saveAll(pageRequest).spliterator(), false)\n" +
                                "\t.map(converter::toDTO).collect($T.toList())" +
                                ")", PageAnswer.class, StreamSupport.class, Collectors.class)
                        .build())
                .build();
    }

    private ClassName buildRepositoryType() {
        return ClassName.bestGuess(generationServiceOptions.getRepositoryClassName());
    }

    private ClassName buildConverterType() {
        return ClassName.bestGuess(generationServiceOptions.getEntityConverterClassName());
    }

    private MethodSpec.Builder declareEndpoint(MethodSpec.Builder mB, String path, Class<?> restAnnotation) {
        CodeBlock mediaType = CodeBlock.builder()
                .add("{ $T.APPLICATION_JSON }", javax.ws.rs.core.MediaType.class)
                .build();
        mB.addAnnotation(AnnotationSpec.builder(Path.class).addMember("value", "$S", path).build())
                .addAnnotation(AnnotationSpec.builder(Produces.class).addMember("value", mediaType).build())
                .addAnnotation(AnnotationSpec.builder(Consumes.class).addMember("value", mediaType).build())
                .addAnnotation(restAnnotation)
                .addException(BaseException.class)
                .addModifiers(Modifier.PUBLIC);
        return mB;
    }

    @org.jetbrains.annotations.NotNull
    private ParameterSpec createIdParameter() {
        return ParameterSpec.builder(ClassName.bestGuess(generationServiceOptions.getPrimaryKeyType()), "id", Modifier.FINAL)
                .addAnnotation(AnnotationSpec.builder(PathParam.class).addMember("value", "$S", "id").build())
                .addAnnotation(NotNull.class)
                .build();
    }

    private ClassName buildEntityDtoType() {
        return ClassName.bestGuess(generationServiceOptions.getEntityDtoTypeName());
    }

    private ClassName buildEntityType() {
        return ClassName.bestGuess(generationServiceOptions.getEntityClassName());
    }

    private TypeName getListOfDtoType() {
        TypeName  entityDtoType  = buildEntityDtoType();
        ClassName list           = ClassName.get("java.util", "List");
        TypeName  listOfEntities = ParameterizedTypeName.get(list, entityDtoType);
        return listOfEntities;
    }
}
