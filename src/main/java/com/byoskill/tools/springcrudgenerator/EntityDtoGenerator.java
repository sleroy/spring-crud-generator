package com.byoskill.tools.springcrudgenerator;

import com.byoskill.tools.springcrudgenerator.entityscanner.EntityScanner;
import com.byoskill.tools.springcrudgenerator.model.DtoInformation;
import com.byoskill.tools.springcrudgenerator.model.EntityInformation;
import com.byoskill.tools.springcrudgenerator.model.FieldInformation;
import com.byoskill.tools.springcrudgenerator.template.JavaFileGenerator;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.lang.reflect.ParameterizedType;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class EntityDtoGenerator implements GeneratorConstants {

    public static final String                    DTO_GENERATOR_HANDLEBARS_TEMPLATE = "dtoGenerator.handlebars";
    private final       EntityDtoGeneratorOptions options;
    private final       Set<String>               generatedTypes                    = new HashSet<>();
    private final       List<DtoMapping>          generatedDtoTypes                 = new ArrayList<>();

    public EntityDtoGenerator(EntityDtoGeneratorOptions options) {

        this.options = options;
    }

    public List<DtoMapping> getGeneratedDtoTypes() {
        return generatedDtoTypes;
    }

    public void generate() throws Exception {
        log.info("Generation of the DTO for the entity {}", options.getEntityClassName());
        // Generate the light "dto"
        // Generate the DTO with relationships
        Class<?> entityClassName = options.getEntityClassName();
        generateDtos(entityClassName);

    }

    private void generateDtos(Class<?> entityClassName) throws Exception {
        if (this.generatedTypes.contains(entityClassName.getCanonicalName())) return;

        generatedTypes.add(entityClassName.getCanonicalName());
        EntityInformation entityInformation = new EntityScanner(options.getEntityClassName()).scan();
        DtoMapping dtoMapping = new DtoMapping(entityInformation,
                convertClassIntoDTOName(entityClassName),
                convertClassIntoLightDTOName(entityClassName),
                entityInformation.getFields());
        generatedDtoTypes.add(dtoMapping);

        generateLightDTO(dtoMapping);
        generateDtoWithRelationships(dtoMapping);


    }

    @NotNull
    private String convertClassIntoLightDTOName(Class<?> entityClassName) {
        return options.getModelPackageName() + "." + entityClassName.getSimpleName() + LIGHT_DTO;
    }

    @NotNull
    private String convertClassIntoDTOName(Class<?> entityClassName) {
        return options.getModelPackageName() + "." + entityClassName.getSimpleName() + DTO;
    }

    private void generateLightDTO(DtoMapping dtoMapping) throws Exception {

        DtoInformation dtoInformation = new DtoInformation();
        dtoInformation.setEntityInformation(dtoMapping.getEntityType());
        dtoInformation.setClassName(dtoMapping.getEntityLightDtoClassname());
        dtoInformation.setPackageName(options.getModelPackageName());

        List<FieldInformation> lightDtoFields = addPropertiesForLightDTO(dtoInformation.getEntityInformation());
        dtoMapping.setLightDtoFields(lightDtoFields);


        JavaFileGenerator javaFile = new JavaFileGenerator(options.getModelPackageName(), dtoInformation.getClassName(), dtoInformation);
        javaFile.setTemplate(DTO_GENERATOR_HANDLEBARS_TEMPLATE);
        javaFile.writeTo(System.out);
        javaFile.writeTo(Paths.get(options.getOutputFolder()));

    }

    private void generateDtoWithRelationships(DtoMapping dtoMapping) throws Exception {

        DtoInformation dtoInformation = new DtoInformation();
        dtoInformation.setEntityInformation(dtoMapping.getEntityType());
        dtoInformation.setClassName(dtoMapping.getEntityDtoClassname());
        dtoInformation.setPackageName(options.getModelPackageName());

        List<FieldInformation> lightDtoFields = addPropertiesForDTO(dtoInformation.getEntityInformation());
        dtoMapping.setLightDtoFields(lightDtoFields);


        JavaFileGenerator javaFile = new JavaFileGenerator(options.getModelPackageName(), dtoInformation.getClassName(), dtoInformation);
        javaFile.setTemplate(DTO_GENERATOR_HANDLEBARS_TEMPLATE);
        javaFile.writeTo(System.out);
        javaFile.writeTo(Paths.get(options.getOutputFolder()));
        /**

         String entityNameDto = dtoMapping.getDtoType();
         TypeSpec.Builder entityDto = TypeSpec.classBuilder(dtoMapping.getEntityDtoClassname())
         .addModifiers(Modifier.PUBLIC)
         .addMethod(MethodSpec.constructorBuilder().addModifiers(Modifier.PUBLIC).build())
         .addJavadoc("DTO  with relationships for @{$T}", ClassName.get(entityClassName));
         List<Field> dtoFields = addPropertiesForDTO(entityDto, entityClassName);
         dtoMapping.setDtoFields(dtoFields);

         entityDto.superclass(ClassName.bestGuess(dtoMapping.getDtoLightType()));

         JavaFile javaFile = JavaFile.builder(options.getModelPackageName(), entityDto.build())
         .build();
         javaFile.writeTo(System.out);
         javaFile.writeTo(Paths.get(options.getOutputFolder()));
         */

    }

    private List<FieldInformation> addPropertiesForDTO(EntityInformation entityInformation) throws Exception {
        List<FieldInformation> fieldList = entityInformation.getFields().stream()
                .filter(field -> !isTransientOrVolatileField(field))
                .filter(field -> isAnnotatedWithJPAOrHibernate(field))
                .filter(field -> isAnnotatedPropertyWith(field, ManyToMany.class)
                        || isAnnotatedPropertyWith(field, OneToOne.class)
                        || isAnnotatedPropertyWith(field, ManyToOne.class)
                        || isAnnotatedPropertyWith(field, OneToMany.class)
                        || isAnnotatedPropertyWith(field, EmbeddedId.class))
                .collect(Collectors.toList());
        return fieldList;
    }

    private boolean isTransientOrVolatileField(FieldInformation field) {
        int modifiers = field.getModifiers();
        return java.lang.reflect.Modifier.isTransient(modifiers) || java.lang.reflect.Modifier.isVolatile(modifiers);
    }

    private boolean isAnnotatedWithJPAOrHibernate(FieldInformation field) {
        return (hasJPAOrHibernateAnnotation(field));
    }

    private boolean isAnnotatedPropertyWith(FieldInformation field, Class<?> entityClassName) {
        return field.getAnnotations().isAnnotatedWith(entityClassName);
    }

    /**
     * private void writeProperty(TypeSpec.Builder entityLight, FieldInformation field) throws Exception {
     * Type     fieldType     = field.getGenericType();
     * TypeName fieldTypePoet = null;
     * if (fieldType instanceof ParameterizedType) {
     * fieldTypePoet = convertParameterizedType((ParameterizedType) fieldType);
     * } else if (isAnnotatedPropertyWith(field, beanMap, EmbeddedId.class)) {
     * fieldTypePoet = ClassName.bestGuess(convertClassIntoLightDTOName(field.getType()));
     * } else {
     * fieldTypePoet = TypeName.get(field.getType());
     * }
     * <p>
     * <p>
     * FieldSpec fieldSpec = FieldSpec.builder(fieldTypePoet, field.getName(), Modifier.PRIVATE).build();
     * entityLight.addField(fieldSpec);
     * <p>
     * entityLight.addMethod(MethodSpec.methodBuilder("get" + StringUtils.capitalize(field.getName()))
     * .addModifiers(Modifier.PUBLIC)
     * .returns(fieldTypePoet)
     * .addCode(CodeBlock.builder()
     * .addStatement("return " + field.getName())
     * .build())
     * .build());
     * <p>
     * entityLight.addMethod(MethodSpec.methodBuilder("set" + StringUtils.capitalize(field.getName()))
     * .addModifiers(Modifier.PUBLIC)
     * .returns(void.class)
     * .addParameter(fieldTypePoet, field.getName(), Modifier.FINAL)
     * .addCode(CodeBlock.builder()
     * .addStatement("this.$L = $L", field.getName(), field.getName())
     * .build())
     * .build());
     * }
     */

    private TypeName convertParameterizedType(ParameterizedType fieldType) {
        TypeName          fieldTypePoet;
        ParameterizedType type    = fieldType;
        ClassName         rawType = ClassName.get((Class<?>) type.getRawType());
        //TODO:: Handling outer classes
        TypeName[] typeArguments = convertTypeArguments(type);
        fieldTypePoet = ParameterizedTypeName.get(rawType, typeArguments);
        return fieldTypePoet;
    }

    @NotNull
    private TypeName[] convertTypeArguments(ParameterizedType type) {
        return List.of(type.getActualTypeArguments()).
                stream()
                .peek(targ -> {
                    try {
                        generateDtos((Class<?>) targ);
                    } catch (Exception e) {
                        log.error("Cannot convert the type argument", targ, e);
                    }
                })
                .map(targ -> convertClassIntoLightDTOName((Class) targ))
                .map(ClassName::bestGuess)
                .toArray(TypeName[]::new);
    }


    private boolean hasJPAOrHibernateAnnotation(FieldInformation fieldInformation) {
        return fieldInformation.getAnnotations().isAnnotatedWith("org.hibernate.annotations")
                || fieldInformation.getAnnotations().isAnnotatedWith("javax.persistence");

    }

    private List<FieldInformation> addPropertiesForLightDTO(EntityInformation entityInformation) throws Exception {
        return entityInformation.getFields().stream()
                .filter(field -> !isTransientOrVolatileField(field))
                .filter(field -> isAnnotatedWithJPAOrHibernate(field))
                .filter(field -> isAnnotatedPropertyWith(field, Column.class))
                .filter(field -> !isAnnotatedPropertyWith(field, EmbeddedId.class))
                .filter(field -> !isAnnotatedPropertyWith(field, ManyToMany.class))
                .filter(field -> !isAnnotatedPropertyWith(field, OneToOne.class))
                .filter(field -> !isAnnotatedPropertyWith(field, ManyToOne.class))
                .filter(field -> !isAnnotatedPropertyWith(field, OneToMany.class))
                .collect(Collectors.toList());
    }
}
