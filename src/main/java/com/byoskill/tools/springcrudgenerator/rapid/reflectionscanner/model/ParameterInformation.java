package com.byoskill.tools.springcrudgenerator.rapid.reflectionscanner.model;

import com.byoskill.tools.springcrudgenerator.rapid.reflectionscanner.TypeConverter;

import java.lang.reflect.Parameter;
import java.lang.reflect.Type;

public class ParameterInformation extends FieldInformation {
    public static ParameterInformation from(final Parameter parameter, final Type ptype) {
        final ParameterInformation parameterInformation = new ParameterInformation();
        parameterInformation.setName(parameter.getName());
        parameterInformation.setModifiers(parameter.getModifiers());
        parameterInformation.setType(TypeConverter.convert(ptype));
        return parameterInformation;
    }
}
