/*
 * Copyright (c) 2020. Byoskill Leroy (Sylvain Leroy).
 * All rights reserved.
 */

package com.byoskill.tools.springcrudgenerator.rapid.reflectionscanner.model;

import com.byoskill.tools.springcrudgenerator.rapid.reflectionscanner.TypeConverter;
import lombok.Data;

import java.io.Serializable;
import java.lang.reflect.Field;

@Data
public class FieldInformation implements Serializable {
    private int                    modifiers;
    private String                 name;
    private AnnotationInformations annotations = new AnnotationInformations();
    private TypingInfo             type;

    public static FieldInformation from(final Field field) {
        final FieldInformation fieldInformation = new FieldInformation();
        fieldInformation.setName(field.getName());
        fieldInformation.setModifiers(field.getModifiers());
        fieldInformation.setType(TypeConverter.convert(field.getGenericType()));
        return fieldInformation;
    }
}
