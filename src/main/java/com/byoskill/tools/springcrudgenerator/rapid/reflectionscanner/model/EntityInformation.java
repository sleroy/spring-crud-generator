/*
 * Copyright (c) 2020. Byoskill Leroy (Sylvain Leroy).
 * All rights reserved.
 */

package com.byoskill.tools.springcrudgenerator.rapid.reflectionscanner.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class EntityInformation implements Serializable {
    private String                 simpleName;
    private String                 canonicalName;
    private int                    modifiers;
    private String                 name;
    private List<FieldInformation> fields;
    private AnnotationInformations annotations = new AnnotationInformations();


}
