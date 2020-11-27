package com.byoskill.tools.springcrudgenerator;

import javax.persistence.ForeignKey;
import javax.persistence.Id;
import java.lang.reflect.Field;

public class CodeGeneratorUtils {
    public static boolean skipKeyField(Field field) {
        return field.isAnnotationPresent(Id.class) || field.isAnnotationPresent(ForeignKey.class);
    }

    public static boolean isCollection(Class<?> type) {
        return type.isAssignableFrom(Iterable.class);
    }
}
