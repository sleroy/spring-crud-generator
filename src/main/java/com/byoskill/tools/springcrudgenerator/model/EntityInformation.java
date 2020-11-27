package com.byoskill.tools.springcrudgenerator.model;

import lombok.Data;

import java.util.List;

@Data
public class EntityInformation {
    private String                      simpleName;
    private String                      canonicalName;
    private int                         modifiers;
    private String                      name;
    private List<FieldInformation>      fields;
    private AnnotationInformations annotations = new AnnotationInformations();

    public List<FieldInformation> getFields() {
        return fields;
    }

    public void setFields(List<FieldInformation> fields) {
        this.fields = fields;
    }


}
