/*
 * Copyright (c) 2020. Byoskill Leroy (Sylvain Leroy).
 * All rights reserved.
 */

package com.byoskill.tools.springcrudgenerator.rapid.reflectionscanner.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * The type Class information.
 */
@Data
public class ClassInformation implements Serializable {
    private String                  simpleName;
    private String                  canonicalName;
    private int                     modifiers;
    private String                  name;
    private List<FieldInformation>  fields      = new ArrayList<>();
    private AnnotationInformations  annotations = new AnnotationInformations();
    private String                  packageName;
    private boolean                 primitive;
    private boolean                 array;
    private boolean                 anEnum;
    private boolean                 anInterface;
    private boolean                 annotation;
    private List<MethodInformation> methods     = new ArrayList<>();

    public ClassInformation() {
        super();
    }

    public static ClassInformation from(final Class<?> entityClass) {
        final ClassInformation classInformation = new ClassInformation();
        classInformation.setSimpleName(entityClass.getSimpleName());
        classInformation.setCanonicalName(entityClass.getCanonicalName());
        classInformation.setPackageName(entityClass.getPackageName());
        classInformation.setModifiers(entityClass.getModifiers());
        classInformation.setName(entityClass.getName());
        classInformation.setPrimitive(entityClass.isPrimitive());
        classInformation.setArray(entityClass.isArray());
        classInformation.setAnEnum(entityClass.isEnum());
        classInformation.setAnInterface(entityClass.isInterface());
        classInformation.setAnnotation(entityClass.isAnnotation());

        return classInformation;
    }


    @JsonIgnore
    public void setClassName(final String packageName, final String simpleName) {
        this.simpleName = simpleName;
        this.name = simpleName;
        this.packageName = packageName;
        this.canonicalName = packageName + "." + simpleName;
    }

    public boolean getAnnotation() {
        return annotation;
    }

    public void setAnnotation(final boolean annotation) {
        this.annotation = annotation;
    }

    public List<MethodInformation> getMethods() {
        return methods;
    }
}