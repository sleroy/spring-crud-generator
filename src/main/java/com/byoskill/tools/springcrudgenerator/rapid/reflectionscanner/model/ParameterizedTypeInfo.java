/*
 * Copyright (c) 2020. Byoskill Leroy (Sylvain Leroy).
 * All rights reserved.
 */

package com.byoskill.tools.springcrudgenerator.rapid.reflectionscanner.model;

import com.byoskill.tools.springcrudgenerator.rapid.reflectionscanner.TypeConverter;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
public class ParameterizedTypeInfo extends TypingInfo {
    public static final String           PARAMETERIZED  = "parameterized";
    private final       List<TypingInfo> typeParameters = new ArrayList<>();
    private             TypingInfo       rawtype;
    private             TypingInfo       ownerType;

    public ParameterizedTypeInfo(final String signature) {
        super(signature, PARAMETERIZED);
    }

    @NotNull
    public static TypingInfo from(@NotNull final ParameterizedType typing) {
        final ParameterizedTypeInfo parameterizedTypeInfo = new ParameterizedTypeInfo(typing.getTypeName());
        parameterizedTypeInfo.setOwnerType(TypeConverter.convert(typing.getOwnerType()));
        parameterizedTypeInfo.setRawtype(TypeConverter.convert(typing.getRawType()));
        for (final Type type : typing.getActualTypeArguments()) {
            parameterizedTypeInfo.addTypeParameter(TypeConverter.convert(type));
        }
        return parameterizedTypeInfo;
    }

    private void addTypeParameter(@NotNull final TypingInfo typeInfo) {
        typeParameters.add(typeInfo);

    }

    public boolean isParameterizedType() {
        return true;
    }

    public Class<?> asClass() throws ClassNotFoundException {
        return getRawtype().asClass();
    }

}
