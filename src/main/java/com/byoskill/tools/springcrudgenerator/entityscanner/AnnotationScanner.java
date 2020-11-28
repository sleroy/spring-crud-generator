package com.byoskill.tools.springcrudgenerator.entityscanner;

import com.byoskill.tools.springcrudgenerator.model.AnnotationInformations;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.lang.annotation.Annotation;

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
        final var annotationInformations = new AnnotationInformations();

        return annotationInformations;
    }
}
