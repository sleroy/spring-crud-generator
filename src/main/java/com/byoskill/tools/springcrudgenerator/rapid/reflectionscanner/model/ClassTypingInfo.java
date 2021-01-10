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
    public static final String  CLASS = "class";
    private             String  simpleName;
    private             String  packageName;
    private             String  canonicalName;
    private             boolean primitive;
    private             boolean annotation;
    private             boolean array;
    private             boolean anEnum;
    private             boolean anInterface;

    public ClassTypingInfo(final String signature) {
        super(signature, CLASS);
    }

    public static TypingInfo from(final Class<?> clazz) {
        final ClassTypingInfo classTypingInfo = new ClassTypingInfo(clazz.getCanonicalName());
        classTypingInfo.simpleName = clazz.getSimpleName();
        classTypingInfo.canonicalName = clazz.getCanonicalName();
        classTypingInfo.packageName = clazz.getPackage().getName();
        classTypingInfo.primitive = (clazz.isPrimitive());
        classTypingInfo.array = (clazz.isArray());
        classTypingInfo.anEnum = (clazz.isEnum());
        classTypingInfo.anInterface = (clazz.isInterface());
        classTypingInfo.annotation = clazz.isAnnotation();
        return classTypingInfo;
    }


    public Class<?> asClass() throws ClassNotFoundException {
        return Class.forName(canonicalName);
    }

}
