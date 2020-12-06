/*
 * Copyright (c) 2020. Byoskill Leroy (Sylvain Leroy).
 * All rights reserved.
 */

package com.byoskill.tools.springcrudgenerator.restgenerator;

import com.byoskill.tools.springcrudgenerator.rapid.catalog.Catalog;
import com.byoskill.tools.springcrudgenerator.rapid.reflectionscanner.ClassScanner;
import com.byoskill.tools.springcrudgenerator.rapid.reflectionscanner.model.ClassInformation;
import com.byoskill.tools.springcrudgenerator.rapid.reflectionscanner.model.FieldInformation;
import com.byoskill.tools.springcrudgenerator.rapid.reflectionscanner.model.ParameterizedTypeInfo;
import com.byoskill.tools.springcrudgenerator.rapid.reflectionscanner.model.TypingInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class JpaEntityScanner implements GeneratorConstants {

    public static final String                     DTO_GENERATOR_HANDLEBARS_TEMPLATE = "dtoGenerator.handlebars";
    private final       Set<String>                generatedTypes                    = new HashSet<>();
    private final       List<JpaEntityInformation> jpaEntityInformations             = new ArrayList<>();
    private final       Catalog                    catalog;

    public JpaEntityScanner(final Catalog catalog) {

        this.catalog = catalog;
    }

    private static void generateLightDTO(final JpaEntityInformation jpaEntityInformation) throws Exception {

        final List<ColumnInformation> lightDtoFields = addPropertiesForLightDTO(jpaEntityInformation.getEntityType());
        jpaEntityInformation.setSimpleColumns(lightDtoFields);

    }

    private static List<ColumnInformation> addPropertiesForLightDTO(final ClassInformation classInformation) {
        final List<FieldInformation> entityInformationFields = classInformation.getFields();
        return entityInformationFields.stream()
                                      .filter(field -> !field.isTransientOrVolatileField())
                                      .filter(FieldInformation::isAnnotatedWithJPAOrHibernate)
                                      .filter(field -> field.isAnnotatedPropertyWith(Column.class) || field.isAnnotatedPropertyWith(Id.class))
                                      .filter(field -> !field.isAnnotatedPropertyWith(EmbeddedId.class))
                                      .filter(field -> !field.isAnnotatedPropertyWith(ManyToMany.class))
                                      .filter(field -> !field.isAnnotatedPropertyWith(OneToOne.class))
                                      .filter(field -> !field.isAnnotatedPropertyWith(ManyToOne.class))
                                      .filter(field -> !field.isAnnotatedPropertyWith(OneToMany.class))
                                      .peek(field -> field.putAttr(GeneratorConstants.P_COMPLEX_JPA_ATTR, false))
                                      .peek(field -> field.putAttr(GeneratorConstants.P_ISJPAKEY, field.isAnnotatedPropertyWith(SequenceGenerator.class) || field.isAnnotatedPropertyWith(Id.class)))
                                      .map(ColumnInformation::fromField)
                                      .collect(Collectors.toList());
    }

    public List<JpaEntityInformation> getAnalyzedEntities() {
        return jpaEntityInformations;
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

        final ClassScanner     classScanner        = ClassScanner.createClassScanner(entityClassName);
        final ClassInformation entityJavaClassInfo = classScanner.scan();
        catalog.addClassDefinition(entityJavaClassInfo);

        if (log.isTraceEnabled()) {
            log.trace(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(entityJavaClassInfo));
        }

        final JpaEntityInformation jpaEntityInformation = new JpaEntityInformation(entityJavaClassInfo);
        jpaEntityInformations.add(jpaEntityInformation);

        generateLightDTO(jpaEntityInformation);
        generateDtoWithRelationships(jpaEntityInformation);

        catalog.addJpaEntity(jpaEntityInformation);

    }

    private void generateDtoWithRelationships(final JpaEntityInformation jpaEntityInformation) throws Exception {

        final List<ColumnInformation> complexColumns = addPropertiesForDTO(jpaEntityInformation.getEntityType());
        jpaEntityInformation.setComplexColumns(complexColumns);

        // Autowired dependencies
        final Set<TypingInfo> dependencies = new HashSet<>();
        complexColumns.forEach(dtoField -> {
            if (dtoField.getType() instanceof ParameterizedTypeInfo) {
                final ParameterizedTypeInfo type           = (ParameterizedTypeInfo) dtoField.getType();
                final List<TypingInfo>      typeParameters = type.getTypeParameters();
                dependencies.addAll(typeParameters);
                typeParameters.forEach(tp -> {
                    try {
                        generate(tp.asClass());
                    } catch (final Exception e) {
                        log.error("Cannot find dependency {}", tp);
                    }
                });
            } else {
                dependencies.add(dtoField.getType());
            }
        });
        jpaEntityInformation.setDependencies(dependencies);
    }

    private List<ColumnInformation> addPropertiesForDTO(final ClassInformation classInformation) {
        final List<FieldInformation> fields = classInformation.getFields();
        final List<ColumnInformation> fieldList = fields.stream()
                                                        .filter(field -> !field.isTransientOrVolatileField())
                                                        .filter(FieldInformation::isAnnotatedWithJPAOrHibernate)
                                                        .filter(field -> field.isAnnotatedPropertyWith(ManyToMany.class)
                                                                || field.isAnnotatedPropertyWith(OneToOne.class)
                                                                || field.isAnnotatedPropertyWith(ManyToOne.class)
                                                                || field.isAnnotatedPropertyWith(OneToMany.class)
                                                                || field.isAnnotatedPropertyWith(EmbeddedId.class))
                                                        .peek(field -> field.putAttr(GeneratorConstants.P_COMPLEX_JPA_ATTR, true))
                                                        .map(ColumnInformation::fromField)
                                                        .collect(Collectors.toList());
        return fieldList;
    }

}
