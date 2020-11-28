/*
 * Copyright (c) 2020. Byoskill Leroy (Sylvain Leroy).
 * All rights reserved.
 */

package com.byoskill.tools.springcrudgenerator.rapid.reflectionscanner.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * The type Class typing info represents a simple java Class type.
 */
@ToString
@Getter
@Setter
public class ClassTypingInfo extends TypingInfo {
    private String simpleName;
    private String packageName;
    private String canonicalName;

    public ClassTypingInfo(final String signature) {
        super(signature);
    }

    public static TypingInfo from(final Class<?> clazz) {
        final ClassTypingInfo classTypingInfo = new ClassTypingInfo(clazz.getCanonicalName());
        classTypingInfo.simpleName = clazz.getSimpleName();
        classTypingInfo.canonicalName = clazz.getCanonicalName();
        classTypingInfo.packageName = clazz.getPackageName();
        return classTypingInfo;
    }
}
