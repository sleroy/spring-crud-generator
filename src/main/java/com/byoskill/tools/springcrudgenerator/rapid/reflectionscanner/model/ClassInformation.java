/*
 * Copyright (c) 2020. Byoskill Leroy (Sylvain Leroy).
 * All rights reserved.
 */

package com.byoskill.tools.springcrudgenerator.rapid.reflectionscanner.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ClassInformation implements Serializable {
    private String                 simpleName;
    private String                 canonicalName;
    private int                    modifiers;
    private String                 name;
    private List<FieldInformation> fields      = List.of();
    private AnnotationInformations annotations = new AnnotationInformations();
    private String                 packageName;

    public static ClassInformation from(final Class<?> entityClass) {
        final ClassInformation classInformation = new ClassInformation();
        classInformation.setSimpleName(entityClass.getSimpleName());
        classInformation.setCanonicalName(entityClass.getCanonicalName());
        classInformation.setModifiers(entityClass.getModifiers());
        classInformation.setName(entityClass.getName());
        classInformation.setPackageName(entityClass.getPackageName());
        return classInformation;
    }

    @JsonIgnore
    public void setClassName(final String packageName, final String simpleName) {
        this.simpleName = simpleName;
        name = simpleName;
        this.packageName = packageName;
        canonicalName = packageName + "." + simpleName;
    }
}