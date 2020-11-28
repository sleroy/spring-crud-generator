/*
 * Copyright (c) 2020. Byoskill Leroy (Sylvain Leroy).
 * All rights reserved.
 */

package com.byoskill.tools.springcrudgenerator.restgenerator;

import com.byoskill.tools.springcrudgenerator.rapid.reflectionscanner.model.EntityInformation;
import com.byoskill.tools.springcrudgenerator.rapid.reflectionscanner.model.FieldInformation;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class DtoMapping {
    private EntityInformation      entityType;
    private String                 dtoType;
    private String                 dtoLightType;
    private List<FieldInformation> fields = new ArrayList<>();
    private List<FieldInformation> lightDtoFields;
    private List<FieldInformation> dtoFields;

    public DtoMapping(final EntityInformation entityClassName, final String dtoType, final String dtoLightType, final List<FieldInformation> fields) {

        entityType = entityClassName;
        this.dtoType = dtoType;
        this.dtoLightType = dtoLightType;
        this.fields = fields;
    }

    public String getEntityLightDtoClassname() {
        final String[] split = dtoLightType.split("\\.");
        return split[split.length - 1];
    }

    public String getEntityDtoClassname() {
        final String[] split = dtoType.split("\\.");
        return split[split.length - 1];
    }

}
