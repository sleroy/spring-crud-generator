package com.byoskill.tools.springcrudgenerator;

import com.byoskill.tools.springcrudgenerator.model.EntityInformation;
import com.byoskill.tools.springcrudgenerator.model.FieldInformation;
import lombok.Data;

import java.lang.reflect.Field;
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

    public DtoMapping(EntityInformation entityClassName, String dtoType, String dtoLightType, List<FieldInformation> fields) {

        this.entityType   = entityClassName;
        this.dtoType      = dtoType;
        this.dtoLightType = dtoLightType;
        this.fields       = fields;
    }

    public String getEntityLightDtoClassname() {
        String[] split = dtoLightType.split("\\.");
        return split[split.length - 1];
    }

    public String getEntityDtoClassname() {
        String[] split = dtoType.split("\\.");
        return split[split.length - 1];
    }

}
