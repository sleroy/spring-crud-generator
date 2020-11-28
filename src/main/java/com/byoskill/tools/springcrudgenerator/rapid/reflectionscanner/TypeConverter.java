/*
 * Copyright (c) 2020. Byoskill Leroy (Sylvain Leroy).
 * All rights reserved.
 */

package com.byoskill.tools.springcrudgenerator.rapid.reflectionscanner;

import com.byoskill.tools.springcrudgenerator.rapid.reflectionscanner.model.ClassTypingInfo;
import com.byoskill.tools.springcrudgenerator.rapid.reflectionscanner.model.ParameterizedTypeInfo;
import com.byoskill.tools.springcrudgenerator.rapid.reflectionscanner.model.TypingInfo;
import com.byoskill.tools.springcrudgenerator.rapid.reflectionscanner.model.UnparseableTypingInfo;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class TypeConverter {
    private final Type typing;

    /**
     * Instantiates a new Type converter.
     *
     * @param potentialGenericType the potential generic type
     */
    private TypeConverter(final Type potentialGenericType) {
        typing = potentialGenericType;
    }

    /**
     * Convert typing info.
     *
     * @param genericType the generic type
     * @return the typing info
     */
    public static @NotNull TypingInfo convert(final Type genericType) {
        return new TypeConverter(genericType).convert();
    }

    private @NotNull TypingInfo convert() {
        if (typing instanceof Class) {
            return ClassTypingInfo.from((Class<?>) typing);
        } else if (typing instanceof ParameterizedType) {
            return ParameterizedTypeInfo.from((ParameterizedType) typing);
        }
        return UnparseableTypingInfo.INSTANCE;
    }
}
