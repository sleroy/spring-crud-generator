package com.byoskill.tools.springcrudgenerator.rapid.reflectionscanner.model;

import com.byoskill.tools.springcrudgenerator.rapid.reflectionscanner.TypeConverter;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import lombok.Data;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

@Data
public class MethodInformation {
    private int                           modifiers;
    private String                        name;
    private AnnotationInformations        annotations     = new AnnotationInformations();
    private TypingInfo                    type;
    private Map<String, Object>           extraProperties = new HashMap<>();
    private Map<String, FieldInformation> parameters      = new HashMap<>();

    public static MethodInformation from(final Method method) {
        final MethodInformation methodInformation = new MethodInformation();
        methodInformation.setName(method.getName());
        methodInformation.setModifiers(method.getModifiers());
        methodInformation.setType(TypeConverter.convert(method.getGenericReturnType()));
        int i = 0;
        for (Type ptype : method.getGenericParameterTypes()) {
            final ParameterInformation parameterInformation = ParameterInformation.from(method.getParameters()[i++], ptype);
            methodInformation.getParameters().put(parameterInformation.getName(), parameterInformation);
        }
        return methodInformation;
    }

    public static FieldInformation from(final Field field) {
        final FieldInformation fieldInformation = new FieldInformation();
        fieldInformation.setName(field.getName());
        fieldInformation.setModifiers(field.getModifiers());
        fieldInformation.setType(TypeConverter.convert(field.getGenericType()));
        return fieldInformation;
    }

    @JsonAnyGetter
    public Map<String, Object> getExtraProperties() {
        return extraProperties;
    }

    @JsonAnySetter
    public void setExtraProperties(final Map<String, Object> extraProperties) {
        this.extraProperties = extraProperties;
    }

    public void putAttr(final String attrName, final Object attrValue) {
        extraProperties.put(attrName, attrValue);
    }


    public boolean isAnnotatedPropertyWith(final Class<?> aClass) {
        final AnnotationInformations annotations = getAnnotations();
        return annotations.isAnnotatedWith(aClass);
    }


    public boolean isTransientOrVolatileField() {
        final int modifiers = getModifiers();
        return java.lang.reflect.Modifier.isTransient(modifiers) || java.lang.reflect.Modifier.isVolatile(modifiers);
    }

    public boolean isAnnotatedWithJPAOrHibernate() {
        return (hasJPAOrHibernateAnnotation());
    }

    public boolean hasJPAOrHibernateAnnotation() {
        final AnnotationInformations annotations = getAnnotations();
        return annotations.isAnnotatedWith("org.hibernate.annotations")
                || annotations.isAnnotatedWith("javax.persistence");

    }
}
