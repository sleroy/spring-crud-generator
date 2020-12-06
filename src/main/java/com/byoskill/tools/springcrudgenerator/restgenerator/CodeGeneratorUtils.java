/*
 * Copyright (c) 2020. Byoskill Leroy (Sylvain Leroy).
 * All rights reserved.
 */

package com.byoskill.tools.springcrudgenerator.restgenerator;

import com.byoskill.tools.springcrudgenerator.rapid.reflectionscanner.model.TypingInfo;

import javax.persistence.ForeignKey;
import javax.persistence.Id;
import java.lang.reflect.Field;

public class CodeGeneratorUtils {
    public static boolean skipKeyField(final Field field) {
        return field.isAnnotationPresent(Id.class) || field.isAnnotationPresent(ForeignKey.class);
    }

    public static boolean isCollection(final Class<?> type) {
        return type.isAssignableFrom(Iterable.class);
    }

    public static boolean isCollection(final TypingInfo type) throws ClassNotFoundException {
        if (type.isParameterizedType()) {
            final Class<?> aClass = type.asClass();
            return Iterable.class.isAssignableFrom(aClass);
        }
        return false;
    }
}
