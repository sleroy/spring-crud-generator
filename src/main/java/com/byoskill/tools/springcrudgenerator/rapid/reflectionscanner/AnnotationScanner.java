/*
 * Copyright (c) 2020. Byoskill Leroy (Sylvain Leroy).
 * All rights reserved.
 */

package com.byoskill.tools.springcrudgenerator.rapid.reflectionscanner;

import com.byoskill.tools.springcrudgenerator.rapid.reflectionscanner.model.AnnotationInformation;
import com.byoskill.tools.springcrudgenerator.rapid.reflectionscanner.model.AnnotationInformations;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.stream.Stream;

@Slf4j
@ToString
public class AnnotationScanner {
    private final Annotation[] declaredAnnotations;

    private AnnotationScanner(final Annotation[] annotations) {
        declaredAnnotations = annotations.clone();
    }

    public static AnnotationScanner of(final Annotation[] annotations) {
        return new AnnotationScanner(annotations);
    }

    public AnnotationInformations scan() {
        log.debug("Converting the annotations (num={})", declaredAnnotations.length);
        final var                annInfo = new AnnotationInformations();
        final Stream<Annotation> stream  = Arrays.stream(declaredAnnotations);
        stream.forEachOrdered(new AnnotationInformationConverter(annInfo));

        log.debug("Annotations converted (num={})", annInfo.size());
        return annInfo;
    }

    @ToString
    private static class AnnotationInformationConverter implements Consumer<Annotation> {
        private final AnnotationInformations annotationInformations;
        private final ObjectMapper           objectMapper;

        public AnnotationInformationConverter(final AnnotationInformations annotationInformations) {
            this.annotationInformations = annotationInformations;
            objectMapper = new ObjectMapper();
            objectMapper.findAndRegisterModules();
        }

        @Override
        public void accept(final Annotation ann) {
            final Class<? extends Annotation> annotationType = ann.annotationType();
            final String                      qualifiedName  = annotationType.getCanonicalName();
            final String                      simpleName     = annotationType.getSimpleName();
            final String                      packageName    = annotationType.getPackageName();

            final AnnotationInformation declaredAnnotation = new AnnotationInformation(qualifiedName, simpleName);
            declaredAnnotation.setPackageName(packageName);

            final Class<? extends Annotation> type = ann.annotationType();

            for (final Method method : type.getDeclaredMethods()) {
                try {
                    readMemberValueConvertJSON(ann, declaredAnnotation, method);
                } catch (final IllegalAccessException | JsonProcessingException | InvocationTargetException e) {
                    log.error("Cannot read the member value of {} the annotation {}", method, qualifiedName, e);
                }
            }
            annotationInformations.addAnnotation(declaredAnnotation);
        }

        private void readMemberValueConvertJSON(final Annotation ann, final AnnotationInformation declaredAnnotation, final Method method) throws IllegalAccessException, JsonProcessingException, InvocationTargetException {
            final Object value             = method.invoke(ann, (Object[]) null);
            final String memberValueAsJSON = objectMapper.writeValueAsString(value);
            declaredAnnotation.addMemberValue(method.getName(), memberValueAsJSON);
        }
    }
}
