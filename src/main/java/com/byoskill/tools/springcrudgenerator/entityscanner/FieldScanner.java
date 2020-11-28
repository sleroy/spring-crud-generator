package com.byoskill.tools.springcrudgenerator.entityscanner;

import com.byoskill.tools.springcrudgenerator.model.FieldInformation;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;

@Slf4j
public class FieldScanner {
    private final List<FieldInformation> fields;
    private final Class<?>               entityClass;

    public FieldScanner(final List<FieldInformation> fields, final Class<?> entityClass) {

        this.fields = fields;
        this.entityClass = entityClass;
    }

    public void scan() {

        Arrays.stream(entityClass.getDeclaredFields()).forEach(
                field -> {
                    final FieldInformation fieldInformation = new FieldInformation();
                    fieldInformation.setName(field.getName());
                    fieldInformation.setModifiers(field.getModifiers());
                    fieldInformation.setAnnotations(AnnotationScanner.of(field.getDeclaredAnnotations()).scan());
                    fields.add(fieldInformation);
                }
        );

    }
}
