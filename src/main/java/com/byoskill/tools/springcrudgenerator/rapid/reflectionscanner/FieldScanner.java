/*
 * Copyright (c) 2020. Byoskill Leroy (Sylvain Leroy).
 * All rights reserved.
 */

package com.byoskill.tools.springcrudgenerator.rapid.reflectionscanner;

import com.byoskill.tools.springcrudgenerator.rapid.reflectionscanner.model.AnnotationInformations;
import com.byoskill.tools.springcrudgenerator.rapid.reflectionscanner.model.FieldInformation;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The type Field scanner performs the conversion of the Class declared fields into a field information model.
 */
@Slf4j
@ToString
public class FieldScanner {
    private final Class<?> entityClass;

    /**
     * Instantiates a new Field scanner.
     *
     * @param entityClass the entity class
     */
    private FieldScanner(final Class<?> entityClass) {

        this.entityClass = entityClass;
    }

    /**
     * Create and instantiates a new field scanner.
     *
     * @param entityClass the entity class
     * @return the field scanner
     */
    public static @NotNull FieldScanner createFieldScanner(final Class<?> entityClass) {
        return new FieldScanner(entityClass);
    }

    /**
     * Scan the Class fields to produce a model available for the template generation.
     *
     * @return the list of field information.
     */
    public List<FieldInformation> scan() {

        log.debug("Scanning the fields (entity={})", entityClass);
        final Stream<Field> fieldStream = Arrays.stream(entityClass.getDeclaredFields());
        final List<FieldInformation> fieldInfo = fieldStream.map(new FieldInformationConverter())
                                                            .collect(Collectors.toList());

        log.debug("Fields scanned... (fields={})", fieldInfo.size());
        return Collections.unmodifiableList(fieldInfo);

    }

    /**
     * Converts the reflected field object into a field information model.
     */
    private static class FieldInformationConverter implements Function<Field, FieldInformation> {

        @Override
        public FieldInformation apply(final Field field) {
            log.debug("Scanning and converting the field {}", field.getName());
            final FieldInformation fieldInformation = FieldInformation.from(field);

            final AnnotationScanner      annotationScanner = AnnotationScanner.of(field.getDeclaredAnnotations());
            final AnnotationInformations annInfo           = annotationScanner.scan();
            fieldInformation.setAnnotations(annInfo);

            return fieldInformation;
        }
    }
}
