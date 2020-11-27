package com.byoskill.tools.springcrudgenerator;

import lombok.Data;

@Data
public class EntityDtoGeneratorOptions {
    private String   modelPackageName;
    private String   outputFolder;
    private Class<?> entityClassName;
    private String   primaryKeyType;
}
