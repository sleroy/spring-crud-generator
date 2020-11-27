package com.byoskill.tools.springcrudgenerator.entityscanner;

import com.byoskill.tools.springcrudgenerator.model.EntityInformation;
import com.byoskill.tools.springcrudgenerator.model.FieldInformation;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;

@Slf4j
public class EntityScanner extends EntityInformation {
    private final Class<?> entityClass;

    public EntityScanner(Class<?> entityClassName) {
        super();
        this.entityClass = entityClassName;
    }

    public EntityInformation scan() {
        EntityInformation entityInformation = new EntityInformation();
        entityInformation.setSimpleName(entityClass.getSimpleName());
        entityInformation.setCanonicalName(entityClass.getCanonicalName());
        entityInformation.setModifiers(entityClass.getModifiers());
        entityInformation.setName(entityClass.getName());

        ArrayList<FieldInformation> fields       = new ArrayList<>();
        FieldScanner                fieldScanner = new FieldScanner(fields, entityClass);
        fieldScanner.scan();
        entityInformation.setFields(fields);

        return entityInformation;
    }
}
