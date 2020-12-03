
/*
 * Copyright (c) 2020. Byoskill Leroy (Sylvain Leroy).
 * All rights reserved.
 */

import com.byoskill.tools.springcrudgenerator.rapid.catalog.Catalog;
import com.squareup.javapoet.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.lang.model.element.Modifier;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class ConvertorGenerator {

    public static final  String             METHOD_TO_ENTITY                 = "toEntity";
    public static final  String             METHOD_MAP_FIELDS_FROM_LIGHT_DTO = "mapFieldsFromLightDto";
    public static final  String             METHOD_MAP_FIELDS_FROM_ENTITY    = "mapFieldsFromEntity";
    public static final  String             METHOD_TO_LIGHT_DTO              = "toLightDTO";
    public static final  String             PARAM_ENTITY                     = "entity";
    public static final  String             PARAM_DTO                        = "dto";
    public static final  String             METHOD_TO_DTO                    = "toDTO";
    private static final String             PARAM_ENTITIES                   = "entities";
    private static final String             PARAM_DTOS                       = "dtos";
    private final        Set<String>        dependencies                     = new HashSet<>();
    private              MethodSpec.Builder constructor;
    private       TypeSpec.Builder converterType;
    private final Catalog          catalog;

    public ConvertorGenerator(final Catalog catalog) {

        this.catalog = catalog;
    }

    public void generate() throws IOException {

        final DtoMapping dtoMapping = options.getDtoMapping();

        constructor = MethodSpec.constructorBuilder().addModifiers(Modifier.PUBLIC);
        converterType = TypeSpec.classBuilder(options.getConverterName())
                                .addAnnotation(Component.class)
                                .addModifiers(Modifier.PUBLIC)
                                .addJavadoc("Converter class.");

        final ClassName dtoLightType = ClassName.bestGuess(dtoMapping.getDtoLightType());
        final Class<?>  entityType   = dtoMapping.getEntityType();
        converterType.addMethod(
                MethodSpec.methodBuilder(METHOD_TO_ENTITY)
                          .addModifiers(Modifier.PUBLIC)
                          .addParameter(ParameterSpec.builder(dtoLightType, PARAM_DTO).build())
                          .addCode(generateFromLightDtoToEntityCode(dtoMapping))
                          .returns(ClassName.get(entityType))
                          .build()
        );

        converterType.addMethod(
                MethodSpec.methodBuilder(METHOD_MAP_FIELDS_FROM_LIGHT_DTO)
                          .addModifiers(Modifier.PUBLIC)
                          .addParameter(ParameterSpec.builder(entityType, PARAM_ENTITY).build())
                          .addParameter(ParameterSpec.builder(dtoLightType, PARAM_DTO).build())
                          .addCode(generateMapDtoToEntityCode(dtoMapping))
                          .returns(void.class)
                          .build()
        );

        converterType.addMethod(
                MethodSpec.methodBuilder(METHOD_MAP_FIELDS_FROM_ENTITY)
                          .addModifiers(Modifier.PUBLIC)
                          .addParameter(ParameterSpec.builder(entityType, PARAM_ENTITY).build())
                          .addParameter(ParameterSpec.builder(dtoLightType, PARAM_DTO).build())
                          .addCode(generateMapEntityToDtoCode(dtoMapping))
                          .returns(void.class)
                          .build()
        );


        converterType.addMethod(
                MethodSpec.methodBuilder(METHOD_TO_LIGHT_DTO)
                          .addModifiers(Modifier.PUBLIC)
                          .addParameter(ParameterSpec.builder(entityType, PARAM_ENTITY).build())
                          .addCode(generateFromEntityToLightDtoCode(dtoMapping))
                          .returns(dtoLightType)
                          .build()
        );

        // Streams conversion for entities
        converterType.addMethod(
                MethodSpec.methodBuilder(METHOD_TO_LIGHT_DTO)
                          .addModifiers(Modifier.PUBLIC)
                          .addParameter(ParameterSpec.builder(ParameterizedTypeName.get(Set.class, entityType), PARAM_ENTITIES).build())
                          .addCode(CodeBlock.builder()
                                            .addStatement("return entities.stream().map(this::$L).collect($T.toSet())", METHOD_TO_LIGHT_DTO, Collectors.class)
                                            .build())
                          .returns(ParameterizedTypeName.get(ClassName.get(Set.class), dtoLightType))
                          .build()
        );


        converterType.addMethod(
                MethodSpec.methodBuilder(METHOD_TO_LIGHT_DTO)
                          .addModifiers(Modifier.PUBLIC)
                          .addParameter(ParameterSpec.builder(ParameterizedTypeName.get(List.class, entityType), PARAM_ENTITIES).build())
                          .addCode(CodeBlock.builder()
                                            .addStatement("return entities.stream().map(this::$L).collect($T.toList())", METHOD_TO_LIGHT_DTO, Collectors.class)
                                            .build())
                          .returns(ParameterizedTypeName.get(ClassName.get(List.class), dtoLightType))
                          .build()
        );


        converterType.addMethod(
                MethodSpec.methodBuilder(METHOD_TO_LIGHT_DTO)
                          .addModifiers(Modifier.PUBLIC)
                          .addParameter(ParameterSpec.builder(ParameterizedTypeName.get(Collection.class, entityType), PARAM_ENTITIES).build())
                          .addCode(CodeBlock.builder()
                                            .addStatement("return entities.stream().map(this::$L).collect($T.toList())", METHOD_TO_LIGHT_DTO, Collectors.class)
                                            .build())
                          .returns(ParameterizedTypeName.get(ClassName.get(List.class), dtoLightType))
                          .build()
        );

// Streams conversion for dtos
        converterType.addMethod(
                MethodSpec.methodBuilder(METHOD_TO_ENTITY)
                          .addModifiers(Modifier.PUBLIC)
                          .addParameter(ParameterSpec.builder(ParameterizedTypeName.get(ClassName.get(Set.class), dtoLightType), PARAM_DTOS).build())
                          .addCode(CodeBlock.builder()
                                            .addStatement("return $L.stream().map(this::$L).collect($T.toSet())", PARAM_DTOS, METHOD_TO_ENTITY, Collectors.class)
                                            .build())
                          .returns(ParameterizedTypeName.get(ClassName.get(Set.class), TypeName.get(entityType)))
                          .build()
        );
        converterType.addMethod(
                MethodSpec.methodBuilder(METHOD_TO_ENTITY)
                          .addModifiers(Modifier.PUBLIC)
                          .addParameter(ParameterSpec.builder(ParameterizedTypeName.get(ClassName.get(List.class), dtoLightType), PARAM_DTOS).build())
                          .addCode(CodeBlock.builder()
                                            .addStatement("return $L.stream().map(this::$L).collect($T.toList())", PARAM_DTOS, METHOD_TO_ENTITY, Collectors.class)
                                            .build())
                          .returns(ParameterizedTypeName.get(ClassName.get(List.class), TypeName.get(entityType)))
                          .build()
        );
        converterType.addMethod(
                MethodSpec.methodBuilder(METHOD_TO_ENTITY)
                          .addModifiers(Modifier.PUBLIC)
                          .addParameter(ParameterSpec.builder(ParameterizedTypeName.get(ClassName.get(Collection.class), dtoLightType), PARAM_DTOS).build())
                          .addCode(CodeBlock.builder()
                                            .addStatement("return $L.stream().map(this::$L).collect($T.toList())", PARAM_DTOS, METHOD_TO_ENTITY, Collectors.class)
                                            .build())
                          .returns(ParameterizedTypeName.get(ClassName.get(List.class), TypeName.get(entityType)))
                          .build()
        );


        converterType.addMethod(
                MethodSpec.methodBuilder(METHOD_TO_ENTITY)
                          .addModifiers(Modifier.PUBLIC)
                          .addParameter(ParameterSpec.builder(ClassName.bestGuess(dtoMapping.getDtoType()), PARAM_DTO).build())
                          .addCode(generateFromDtoToEntityCode(dtoMapping))
                          .returns(ClassName.get(entityType))
                          .build()
        );

        converterType.addMethod(
                MethodSpec.methodBuilder(METHOD_TO_DTO)
                          .addModifiers(Modifier.PUBLIC)
                          .addParameter(ParameterSpec.builder(entityType, PARAM_ENTITY).build())
                          .addCode(generateFromEntityToDtoCode(dtoMapping))
                          .returns(ClassName.bestGuess(dtoMapping.getDtoType()))
                          .build()
        );

        converterType.addMethod(constructor.build());


        final JavaFile javaFile = JavaFile.builder(options.getConvertPackageName(), converterType.build())
                                          .build();
        javaFile.writeTo(System.out);
        javaFile.writeTo(Paths.get(options.getOutputFolder()));

    }

    private CodeBlock generateFromLightDtoToEntityCode(final DtoMapping dtoMapping) {
        final Class entityType = dtoMapping.getEntityType();
        final CodeBlock.Builder builder = CodeBlock.builder()
                                                   .addStatement("$T entity = new $T()", entityType, entityType)
                                                   .addStatement("$L(entity, dto)", METHOD_MAP_FIELDS_FROM_LIGHT_DTO);

        return builder
                .addStatement("return entity")
                .build();
    }

    private CodeBlock generateMapDtoToEntityCode(final DtoMapping dtoMapping) {
        final CodeBlock.Builder builder = CodeBlock.builder();

        dtoMapping.getLightDtoFields().stream()
                  .filter(field -> !CodeGeneratorUtils.skipKeyField(field))
                  .map(field -> StringUtils.capitalize(field.getName()))
                  .forEach(capitalizeField -> builder.addStatement("entity.set$L(dto.get$L())", capitalizeField, capitalizeField));
        return builder.build();
    }

    private CodeBlock generateMapEntityToDtoCode(final DtoMapping dtoMapping) {
        final CodeBlock.Builder builder = CodeBlock.builder();

        dtoMapping.getLightDtoFields().stream()
                  .filter(field -> !CodeGeneratorUtils.skipKeyField(field))
                  .map(field -> StringUtils.capitalize(field.getName()))
                  .forEach(capitalizeField -> builder.addStatement("dto.set$L(entity.get$L())", capitalizeField, capitalizeField));
        return builder.build();
    }

    private CodeBlock generateFromEntityToLightDtoCode(final DtoMapping dtoMapping) {
        final ClassName jLightDtoType = ClassName.bestGuess(dtoMapping.getEntityLightDtoClassname());
        final CodeBlock.Builder builder = CodeBlock.builder()
                                                   .addStatement("$T dto = new $T()", jLightDtoType, jLightDtoType)
                                                   .addStatement("$L(entity, dto)", METHOD_MAP_FIELDS_FROM_ENTITY);

        return builder
                .addStatement("return dto")
                .build();
    }

    private CodeBlock generateFromDtoToEntityCode(final DtoMapping dtoMapping) {
        final Class<?> jEntityType = dtoMapping.getEntityType();
        final CodeBlock.Builder builder = CodeBlock.builder()
                                                   .addStatement("$T entity = new $T()", jEntityType, jEntityType)
                                                   .addStatement("$L(entity, dto)", METHOD_MAP_FIELDS_FROM_LIGHT_DTO);

        dtoMapping.getDtoFields().stream()
                  .filter(field -> !CodeGeneratorUtils.skipKeyField(field))
                  .forEach(field -> convertComplexDtoProperty(field, builder));
        return builder
                .addStatement("return entity")
                .build();

    }

    private CodeBlock generateFromEntityToDtoCode(final DtoMapping dtoMapping) {
        final ClassName jDtoType = ClassName.bestGuess(dtoMapping.getDtoType());
        final CodeBlock.Builder builder = CodeBlock.builder()
                                                   .addStatement("$T dto = new $T()", jDtoType, jDtoType);

        builder.addStatement("$L(entity, dto)", METHOD_MAP_FIELDS_FROM_ENTITY);

        dtoMapping.getDtoFields().stream()
                  .filter(field -> !CodeGeneratorUtils.skipKeyField(field))
                  .forEach(field -> convertComplexEntityProperty(field, builder));
        return builder
                .addStatement("return dto")
                .build();

    }

    @NotNull
    private CodeBlock.Builder convertComplexDtoProperty(final Field field, final CodeBlock.Builder builder) {
        final String capitalizeField       = StringUtils.capitalize(field.getName());
        final String convertedVariableName = field.getName() + "Property";

        final Type      entityType         = field.getGenericType();
        final ClassName implementationType = getImplementation(field.getGenericType());
        final ClassName converterType      = ClassName.bestGuess(options.getConvertPackageName() + "." + implementationType.simpleName() + "Converter");
        final String    converterTypeField = StringUtils.uncapitalize(converterType.simpleName());
        requiresConverter(converterType);

        if (CodeGeneratorUtils.isCollection(field.getType())) {
            builder.addStatement("$T $L= $L.$L(dto.get$L());", TypeName.get(field.getType()), convertedVariableName, converterTypeField, METHOD_TO_ENTITY, capitalizeField);
            return builder.addStatement("entity.set$L($L)", capitalizeField, convertedVariableName);
        } else {
            builder.addStatement("$T $L= $L.$L(dto.get$L());", TypeName.get(field.getType()), convertedVariableName, converterTypeField, METHOD_TO_ENTITY, capitalizeField);
            return builder.addStatement("entity.set$L($L)", capitalizeField, convertedVariableName);
        }


    }

    @NotNull
    private CodeBlock.Builder convertComplexEntityProperty(final Field field, final CodeBlock.Builder builder) {
        final String capitalizeField = StringUtils.capitalize(field.getName());

        final ClassName implementationType = getImplementation(field.getGenericType());
        final ClassName converterType      = ClassName.bestGuess(options.getConvertPackageName() + "." + implementationType.simpleName() + "Converter");
        final String    converterTypeField = StringUtils.uncapitalize(converterType.simpleName());
        requiresConverter(converterType);

        if (CodeGeneratorUtils.isCollection(field.getType())) {
            return builder.addStatement("dto.set$L($L.$L(entity.get$L()))", capitalizeField, converterTypeField, METHOD_TO_LIGHT_DTO, capitalizeField);
        } else {
            return builder.addStatement("dto.set$L($L.$L(entity.get$L()))", capitalizeField, converterTypeField, METHOD_TO_LIGHT_DTO, capitalizeField);
        }

    }

    private ClassName getImplementation(final Type type) {
        if (type instanceof ParameterizedType) {
            final Type actualTypeArgument = ((ParameterizedType) type).getActualTypeArguments()[0];
            return ClassName.get((Class) actualTypeArgument);
        } else {
            return ClassName.get((Class) type);
        }
    }

    private void requiresConverter(final ClassName converterTypeDependency) {
        if (!dependencies.contains(converterTypeDependency.canonicalName())) {
            dependencies.add(converterTypeDependency.canonicalName());
            final String uncapitalize = StringUtils.uncapitalize(converterTypeDependency.simpleName());
            converterType.addField(FieldSpec.builder(converterTypeDependency, uncapitalize).build());
            constructor.addParameter(ParameterSpec.builder(converterTypeDependency, uncapitalize).build());
            constructor.addCode(CodeBlock.builder().add("this.$L = $L;", uncapitalize, uncapitalize).build());
        }
    }

}
