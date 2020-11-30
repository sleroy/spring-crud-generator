/*
 * Copyright (c) 2020. Byoskill Leroy (Sylvain Leroy).
 * All rights reserved.
 */

package com.byoskill.tools.springcrudgenerator.rapid.reflectionscanner;

import com.byoskill.tools.springcrudgenerator.rapid.reflectionscanner.model.ClassInformation;
import com.byoskill.tools.springcrudgenerator.rapid.reflectionscanner.model.FieldInformation;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * The type Entity scanner is a component to convert the Class reflected representation into a serializable model
 * called {@link ClassInformation}.
 */
@Slf4j
public class ClassScanner extends ClassInformation {
    private final Class<?> entityClass;

    private ClassScanner(final Class<?> entityClassName) {
        entityClass = entityClassName;
    }

    public static ClassScanner createClassScanner(final Class<?> entityClassName) {
        return new ClassScanner(entityClassName);
    }

    public ClassInformation scan() {
        final ClassInformation classInformation = ClassInformation.from(entityClass);


        final FieldScanner           fieldScanner      = FieldScanner.createFieldScanner(entityClass);
        final List<FieldInformation> fieldInformations = fieldScanner.scan();
        classInformation.setFields(fieldInformations);

        return classInformation;
    }
}
