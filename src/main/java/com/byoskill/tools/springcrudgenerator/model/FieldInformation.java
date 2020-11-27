package com.byoskill.tools.springcrudgenerator.model;

import lombok.Data;

import java.lang.annotation.Annotation;
import java.util.List;

@Data
public class FieldInformation {
    private int                    modifiers;
    private String                 name;
    private AnnotationInformations annotations = new AnnotationInformations();

}
