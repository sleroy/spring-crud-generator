/*
 * Copyright (c) 2020. Byoskill Leroy (Sylvain Leroy).
 * All rights reserved.
 */

package com.byoskill.tools.springcrudgenerator.restgenerator;

import com.byoskill.tools.springcrudgenerator.rapid.catalog.Catalog;
import com.byoskill.tools.springcrudgenerator.rapid.reflectionscanner.ClassScanner;
import com.byoskill.tools.springcrudgenerator.rapid.reflectionscanner.model.AnnotationInformations;
import com.byoskill.tools.springcrudgenerator.rapid.reflectionscanner.model.ClassInformation;
import com.byoskill.tools.springcrudgenerator.rapid.reflectionscanner.model.FieldInformation;
import com.byoskill.tools.springcrudgenerator.restgenerator.templates.DtoInformation;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
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
    private final       Catalog                   catalog;

    public EntityDtoGenerator(final EntityDtoGeneratorOptions options, final Catalog catalog) {

        this.options = options;
        this.catalog = catalog;
    }

    public List<DtoMapping> getGeneratedDtoTypes() {
        return generatedDtoTypes;
    }

    public void generate(final Class<?> paymentClass) throws Exception {
        log.info("Generation of the DTO for the entity {}", paymentClass);
        // Generate the light "dto"
        // Generate the DTO with relationships
        generateDtos(paymentClass);

    }

    private void generateDtos(final Class<?> entityClassName) throws Exception {
        if (generatedTypes.contains(entityClassName.getCanonicalName())) return;
        generatedTypes.add(entityClassName.getCanonicalName());

        final ClassScanner     classScanner     = ClassScanner.createClassScanner(entityClassName);
        final ClassInformation classInformation = classScanner.scan();
        catalog.addEntitiy(classInformation);

        if (log.isTraceEnabled()) {
            log.trace(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(classInformation));
        }

        final DtoMapping dtoMapping = new DtoMapping(classInformation);
        generatedDtoTypes.add(dtoMapping);

        generateLightDTO(dtoMapping);
        generateDtoWithRelationships(dtoMapping);

        catalog.addDtoMapping(dtoMapping);

    }

    @NotNull
    private String convertClassIntoLightDTOName(final String entityClassSimpleName) {
        return entityClassSimpleName + LIGHT_DTO;
    }

    @NotNull
    private String convertClassIntoDTOName(final String entityClassSimpleName) {
        return entityClassSimpleName + DTO;
    }

    private void generateLightDTO(final DtoMapping dtoMapping) throws Exception {

        final DtoInformation dtoInformation = new DtoInformation();
        dtoInformation.setOriginalEntity(dtoMapping.getEntityType());

        final String dtoSimpleName = convertClassIntoLightDTOName(dtoMapping.getEntityType().getSimpleName());
        dtoInformation.getDto().setClassName(options.getModelPackageName(), dtoSimpleName);

        final List<FieldInformation> lightDtoFields = addPropertiesForLightDTO(dtoInformation.getOriginalEntity());
        dtoInformation.setFields(lightDtoFields);

        dtoInformation.getDto().setModifiers(Modifier.PUBLIC);

        dtoMapping.setDtoLightType(dtoInformation);

        catalog.addDto(dtoInformation);

    }

    private void generateDtoWithRelationships(final DtoMapping dtoMapping) throws Exception {

        final DtoInformation   dtoInformation = new DtoInformation();
        final ClassInformation dto            = dtoInformation.getDto();

        dtoInformation.setOriginalEntity(dtoMapping.getEntityType());

        final String dtoSimpleName = convertClassIntoDTOName(dtoMapping.getEntityType().getSimpleName());
        dto.setClassName(options.getModelPackageName(), dtoSimpleName);

        final List<FieldInformation> lightDtoFields = addPropertiesForDTO(dtoInformation.getOriginalEntity());
        dtoInformation.setFields(lightDtoFields);

        dto.setModifiers(Modifier.PUBLIC);
        catalog.addDto(dtoInformation);

    }

    private List<FieldInformation> addPropertiesForDTO(final ClassInformation classInformation) throws Exception {
        final List<FieldInformation> fieldList = classInformation.getFields().stream()
                                                                 .filter(field -> !isTransientOrVolatileField(field))
                                                                 .filter(this::isAnnotatedWithJPAOrHibernate)
                                                                 .filter(field -> isAnnotatedPropertyWith(field, ManyToMany.class)
                                                                         || isAnnotatedPropertyWith(field, OneToOne.class)
                                                                         || isAnnotatedPropertyWith(field, ManyToOne.class)
                                                                         || isAnnotatedPropertyWith(field, OneToMany.class)
                                                                         || isAnnotatedPropertyWith(field, EmbeddedId.class))
                                                                 .peek(field -> field.putAttr(GeneratorConstants.P_COMPLEX_JPA_ATTR, true))
                                                                 .collect(Collectors.toList());
        return fieldList;
    }

    private boolean isTransientOrVolatileField(final FieldInformation field) {
        final int modifiers = field.getModifiers();
        return java.lang.reflect.Modifier.isTransient(modifiers) || java.lang.reflect.Modifier.isVolatile(modifiers);
    }

    private boolean isAnnotatedWithJPAOrHibernate(final FieldInformation field) {
        return (hasJPAOrHibernateAnnotation(field));
    }

    private boolean isAnnotatedPropertyWith(final FieldInformation field, final Class<?> entityClassName) {
        final AnnotationInformations annotations = field.getAnnotations();
        return annotations.isAnnotatedWith(entityClassName);
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

    private TypeName convertParameterizedType(final ParameterizedType fieldType) {
        final TypeName          fieldTypePoet;
        final ParameterizedType type    = fieldType;
        final ClassName         rawType = ClassName.get((Class<?>) type.getRawType());
        //TODO:: Handling outer classes
        final TypeName[] typeArguments = convertTypeArguments(type);
        fieldTypePoet = ParameterizedTypeName.get(rawType, typeArguments);
        return fieldTypePoet;
    }

    @NotNull
    private TypeName[] convertTypeArguments(final ParameterizedType type) {
        return List.of(type.getActualTypeArguments()).
                stream()
                   .peek(targ -> {
                       try {
                           generateDtos((Class<?>) targ);
                       } catch (final Exception e) {
                           log.error("Cannot convert the type argument", targ, e);
                       }
                   })
                   .map(targ -> convertClassIntoLightDTOName(((Class) targ).getSimpleName()))
                   .map(ClassName::bestGuess)
                   .toArray(TypeName[]::new);
    }


    private boolean hasJPAOrHibernateAnnotation(final FieldInformation fieldInformation) {
        final AnnotationInformations annotations = fieldInformation.getAnnotations();
        return annotations.isAnnotatedWith("org.hibernate.annotations")
                || annotations.isAnnotatedWith("javax.persistence");

    }

    private List<FieldInformation> addPropertiesForLightDTO(final ClassInformation classInformation) throws Exception {
        final List<FieldInformation> entityInformationFields = classInformation.getFields();
        return entityInformationFields.stream()
                                      .filter(field -> !isTransientOrVolatileField(field))
                                      .filter(field -> isAnnotatedWithJPAOrHibernate(field))
                                      .filter(field -> isAnnotatedPropertyWith(field, Column.class))
                                      .filter(field -> !isAnnotatedPropertyWith(field, EmbeddedId.class))
                                      .filter(field -> !isAnnotatedPropertyWith(field, ManyToMany.class))
                                      .filter(field -> !isAnnotatedPropertyWith(field, OneToOne.class))
                                      .filter(field -> !isAnnotatedPropertyWith(field, ManyToOne.class))
                                      .filter(field -> !isAnnotatedPropertyWith(field, OneToMany.class))
                                      .peek(field -> field.putAttr(GeneratorConstants.P_ISJPAKEY,isAnnotatedPropertyWith(field, SequenceGenerator.class) || isAnnotatedPropertyWith(field, Id.class)))
                                      .collect(Collectors.toList());
    }


}
