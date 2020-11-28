/*
 * Copyright (c) 2020. Byoskill Leroy (Sylvain Leroy).
 * All rights reserved.
 */

package com.byoskill.tools.springcrudgenerator.rapid.reflectionscanner;

import com.byoskill.tools.springcrudgenerator.rapid.reflectionscanner.model.EntityInformation;
import com.byoskill.tools.springcrudgenerator.rapid.reflectionscanner.model.FieldInformation;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * The type Entity scanner is a component to convert the Class reflected representation into a serializable model
 * called {@link EntityInformation}.
 */
@Slf4j
public class EntityScanner extends EntityInformation {
    private final Class<?> entityClass;

    private EntityScanner(final Class<?> entityClassName) {
        entityClass = entityClassName;
    }

    public static EntityScanner createEntityScanner(final Class<?> entityClassName) {
        return new EntityScanner(entityClassName);
    }

    public EntityInformation scan() {
        final EntityInformation entityInformation = new EntityInformation();
        entityInformation.setSimpleName(entityClass.getSimpleName());
        entityInformation.setCanonicalName(entityClass.getCanonicalName());
        entityInformation.setModifiers(entityClass.getModifiers());
        entityInformation.setName(entityClass.getName());

        final FieldScanner           fieldScanner      = FieldScanner.createFieldScanner(entityClass);
        final List<FieldInformation> fieldInformations = fieldScanner.scan();
        entityInformation.setFields(fieldInformations);

        return entityInformation;
    }
}
