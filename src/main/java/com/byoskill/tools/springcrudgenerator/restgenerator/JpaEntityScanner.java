/*
 * Copyright (c) 2020. Byoskill Leroy (Sylvain Leroy).
 * All rights reserved.
 */

package com.byoskill.tools.springcrudgenerator.restgenerator;

import com.byoskill.tools.springcrudgenerator.rapid.catalog.Catalog;
import com.byoskill.tools.springcrudgenerator.rapid.reflectionscanner.ClassScanner;
import com.byoskill.tools.springcrudgenerator.rapid.reflectionscanner.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import javax.persistence.*;
import java.util.*;
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
        final List<FieldInformation> columnsPerFieldAnnotations  = classInformation.getFields();
        final List<FieldInformation> columnsPerMethodAnnotations = scanForJpaAnnotatedMethods(classInformation);
        List<FieldInformation>       entityInformationFields     = new ArrayList<>();
        entityInformationFields.addAll(columnsPerFieldAnnotations);
        entityInformationFields.addAll(columnsPerMethodAnnotations);

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
                                      .peek(field -> field.putAttr(GeneratorConstants.P_ISJPAKEY,
                                                                   field.isAnnotatedPropertyWith(SequenceGenerator.class) || field
                                                                           .isAnnotatedPropertyWith(Id.class)))
                                      .map(ColumnInformation::fromField)
                                      .collect(Collectors.toList());
    }

    private static List<FieldInformation> scanForJpaAnnotatedMethods(final ClassInformation classInformation) {

        final List<MethodInformation> methods           = classInformation.getMethods();
        final List<FieldInformation>  fieldsFromGetters = fieldsFromGetters(methods);
        final Set<String> knownFields = fieldsFromGetters.stream()
                                                         .map(FieldInformation::getName)
                                                         .collect(Collectors.toSet());
        final List<FieldInformation> allFields            = new ArrayList<>(fieldsFromGetters);
        final List<FieldInformation> fieldInformationList = fieldsFromSetters(methods);
        fieldInformationList.stream()
                            .peek(field -> {
                                log.error("Annotations should never put on the setter of {} for {}", field.getName(),
                                          classInformation.getName());
                            })
                            .filter(field -> {
                                if (knownFields.contains(field.getName())) {
                                    log.error("DUAL INITIALIZATION OF JPA FIELD {} for {}", field.getName(), classInformation.getName());
                                    return false;
                                }
                                return true;
                            }).forEach(allFields::add);

        allFields.forEach(f -> {
            log.info("We recommend to switch the JPA Annotations from the methods to the fields : field {} for the class {}", f.getName()
                    , classInformation
                    .getName());
        });
        return allFields;
    }

    @NotNull
    private static List<FieldInformation> fieldsFromGetters(final List<MethodInformation> methods) {
        return methods.stream()
                      .filter(JpaEntityScanner::isGetter)
                      .map(JpaEntityScanner::fromGetter)
                      .filter(Objects::nonNull)
                      .collect(Collectors.toList());
    }


    @NotNull
    private static List<FieldInformation> fieldsFromSetters(final List<MethodInformation> methods) {
        return methods.stream()
                      .filter(JpaEntityScanner::isSetter)
                      .map(JpaEntityScanner::fromSetter)
                      .collect(Collectors.toList());
    }

    private static boolean isSetter(final MethodInformation m) {
        return m.hasJPAOrHibernateAnnotation()
                && m.getName().startsWith("set")
                && m.getParameters().size() == 1;
    }

    private static boolean isGetter(final MethodInformation m) {
        return m.hasJPAOrHibernateAnnotation()
                && m.getParameters().isEmpty();
    }


    private static FieldInformation fromSetter(final MethodInformation m) {
        final FieldInformation                    fieldInformation = new FieldInformation();
        final Map.Entry<String, FieldInformation> firstParameter   = m.getParameters().entrySet().iterator().next();

        fieldInformation.setName(m.getName().substring(3).toLowerCase(Locale.ROOT));
        fieldInformation.setAnnotations(m.getAnnotations());
        fieldInformation.setModifiers(m.getModifiers());
        fieldInformation.setType(firstParameter.getValue().getType());
        return fieldInformation;
    }

    private static FieldInformation fromGetter(final MethodInformation m) {
        final FieldInformation fieldInformation = new FieldInformation();
        if (m.getName().startsWith("get")) {
            fieldInformation.setName(m.getName().substring(3).toLowerCase(Locale.ROOT));
        } else if (m.getName().startsWith("is")) {
            fieldInformation.setName(m.getName().substring(2).toLowerCase(Locale.ROOT));
        } else {
            log.error("Unsupported method name " + m.getName() + " from " + m.getClass().getName());
            return null;
        }
        fieldInformation.setAnnotations(m.getAnnotations());
        fieldInformation.setModifiers(m.getModifiers());
        fieldInformation.setType(m.getType());
        return fieldInformation;
    }

    public List<JpaEntityInformation> getAnalyzedEntities() {
        return jpaEntityInformations;
    }

    public void scan(final Class<?> clazz) throws Exception {

        log.debug("JPA Entity analysis for the entity {}", clazz);
        // Generate the light "dto"
        // Generate the DTO with relationships
        scanInformation(clazz);

    }

    private void scanInformation(final Class<?> entityClassName) throws Exception {
        if (entityClassName.isAnonymousClass()) {
            log.warn("We do not scan anonymous classes as {}", entityClassName);
            return;
        }
        if (!entityClassName.isAnnotationPresent(Entity.class)) {
            log.debug("The  class is not an entity,  annotation @Entity is missing on this {}. This is not an error.", entityClassName);
            return;
        }
        log.debug("JPA Entity analysis for the entity {}", entityClassName);
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
                        scan(tp.asClass());
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
        final List<FieldInformation> columnsPerFieldAnnotations  = classInformation.getFields();
        final List<FieldInformation> columnsPerMethodAnnotations = scanForJpaAnnotatedMethods(classInformation);
        if (columnsPerFieldAnnotations.stream()
                                      .anyMatch(FieldInformation::isAnnotatedWithJPAOrHibernate)
                && !columnsPerMethodAnnotations.isEmpty()) {
            log.error("JPA Annotations should never be mixed between the fields and the methods : please review the class {}",
                      classInformation
                    .getName());
        }
        List<FieldInformation> entityInformationFields = new ArrayList<>();
        entityInformationFields.addAll(columnsPerFieldAnnotations);
        entityInformationFields.addAll(columnsPerMethodAnnotations);
        return entityInformationFields.stream()
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
    }

}
