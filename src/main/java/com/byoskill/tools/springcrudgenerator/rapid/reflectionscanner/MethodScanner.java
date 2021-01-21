/*
 * Copyright (c) 2020. Byoskill Leroy (Sylvain Leroy).
 * All rights reserved.
 */

package com.byoskill.tools.springcrudgenerator.rapid.reflectionscanner;

import com.byoskill.tools.springcrudgenerator.rapid.reflectionscanner.model.AnnotationInformations;
import com.byoskill.tools.springcrudgenerator.rapid.reflectionscanner.model.MethodInformation;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
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
public class MethodScanner {
    private final Class<?> entityClass;

    /**
     * Instantiates a new Field scanner.
     *
     * @param entityClass the entity class
     */
    private MethodScanner(final Class<?> entityClass) {

        this.entityClass = entityClass;
    }

    /**
     * Create and instantiates a new field scanner.
     *
     * @param entityClass the entity class
     * @return the field scanner
     */
    public static @NotNull MethodScanner createMethodScanner(final Class<?> entityClass) {
        return new MethodScanner(entityClass);
    }

    /**
     * Scan the Class fields to produce a model available for the template generation.
     *
     * @return the list of field information.
     */
    public List<MethodInformation> scan() {

        log.debug("Scanning the methods (entity={})", entityClass);
        final Stream<Method> methodStream = Arrays.stream(entityClass.getMethods());
        final List<MethodInformation> methodInformations = methodStream.map(new MethodInformationScanner())
                                                            .collect(Collectors.toList());

        log.debug("Public methods scanned... (methods={})", methodInformations.size());
        return Collections.unmodifiableList(methodInformations);

    }

    /**
     * Converts the reflected field object into a field information model.
     */
    private static class MethodInformationScanner implements Function<Method, MethodInformation> {

        @Override
        public MethodInformation apply(final Method method) {
            log.debug("Scanning and converting the method {}", method.getName());
            final MethodInformation methodInformation = MethodInformation.from(method);

            final AnnotationScanner      annotationScanner = AnnotationScanner.of(method.getDeclaredAnnotations());
            final AnnotationInformations annInfo           = annotationScanner.scan();
            methodInformation.setAnnotations(annInfo);

            return methodInformation;
        }
    }
}
