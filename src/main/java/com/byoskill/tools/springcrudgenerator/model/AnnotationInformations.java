package com.byoskill.tools.springcrudgenerator.model;

import lombok.Data;

import java.util.List;

@Data
public class AnnotationInformations {

    private final List<AnnotationInformation> annotations = List.of();

    public boolean isAnnotatedWith(Class<?> typeName) {
        return isAnnotatedWith(typeName.getName());
    }

    public boolean isAnnotatedWith(String qualifiedName) {
        return annotations.stream().anyMatch(annotation ->
                annotation.getName().startsWith(qualifiedName));
    }

}
