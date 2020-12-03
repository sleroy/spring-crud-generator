/*
 * Copyright (c) 2020. Byoskill Leroy (Sylvain Leroy).
 * All rights reserved.
 */

package com.byoskill.tools.springcrudgenerator.rapid.reflectionscanner.model;

import com.byoskill.tools.springcrudgenerator.rapid.reflectionscanner.TypeConverter;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import lombok.Data;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

@Data
public class FieldInformation implements Serializable {
    private int                    modifiers;
    private String                 name;
    private AnnotationInformations annotations = new AnnotationInformations();
    private TypingInfo             type;
    private Map<String, Object> extraProperties = new HashMap<>();

    public static FieldInformation from(final Field field) {
        final FieldInformation fieldInformation = new FieldInformation();
        fieldInformation.setName(field.getName());
        fieldInformation.setModifiers(field.getModifiers());
        fieldInformation.setType(TypeConverter.convert(field.getGenericType()));
        return fieldInformation;
    }

    @JsonAnyGetter
    public Map<String, Object> getExtraProperties() {
        return extraProperties;
    }

    @JsonAnySetter
    public void setExtraProperties(final Map<String, Object> extraProperties) {
        this.extraProperties = extraProperties;
    }

    public void putAttr(final String attrName, final Object attrValue) {
        extraProperties.put(attrName, attrValue);
    }
}
