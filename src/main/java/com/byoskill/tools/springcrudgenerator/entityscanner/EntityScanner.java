package com.byoskill.tools.springcrudgenerator.entityscanner;

import com.byoskill.tools.springcrudgenerator.model.EntityInformation;
import com.byoskill.tools.springcrudgenerator.model.FieldInformation;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;

@Slf4j
public class EntityScanner extends EntityInformation {
    private final Class<?> entityClassName;

    public EntityScanner(Class<?> entityClassName) {
        super();
        this.entityClassName = entityClassName;
    }

    public EntityInformation scan() {
        EntityInformation entityInformation = new EntityInformation();
        entityInformation.setSimpleName(entityClassName.getSimpleName());
        entityInformation.setCanonicalName(entityClassName.getCanonicalName());
        entityInformation.setModifiers(entityClassName.getModifiers());
        entityInformation.setName(entityClassName.getName());
        
        ArrayList<FieldInformation> fields       = new ArrayList<>();
        FieldScanner                fieldScanner = new FieldScanner(fields);
        fieldScanner.scan();
        entityInformation.setFields(fields);



        return entityInformation;
    }
}
