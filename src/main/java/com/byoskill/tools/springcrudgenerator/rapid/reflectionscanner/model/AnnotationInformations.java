/*
 * Copyright (c) 2020. Byoskill Leroy (Sylvain Leroy).
 * All rights reserved.
 */

package com.byoskill.tools.springcrudgenerator.rapid.reflectionscanner.model;

import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The type Annotation informations is a container for the list of annotations associated with a declaration.
 */
@Getter
@ToString
public class AnnotationInformations implements Serializable {

    private final Map<String, AnnotationInformation> annotations = new LinkedHashMap<>();

    public boolean isAnnotatedWith(final Class<?> typeName) {
        return isAnnotatedWith(typeName.getName());
    }

    /**
     * Matches exact qualified name or qualified name starting by the term.
     * @param qualifiedName the qualified name of an annotation or the starting sentence.
     * @return true if the annotation matching the condition was found.
     */
    public boolean isAnnotatedWith(final String qualifiedName) {
        return annotations.containsKey(qualifiedName) || annotations.keySet().stream().anyMatch(k -> k.startsWith(qualifiedName));
    }

    /**
     * Returns the number of contained annotations.
     *
     * @return the number of annotations.
     */
    public int size() {
        return annotations.size();
    }

    public void addAnnotation(final AnnotationInformation declaredAnnotation) {
        annotations.put(declaredAnnotation.getQualifiedName(), declaredAnnotation);
    }
}
