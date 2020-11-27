package com.byoskill.tools.springcrudgenerator.model;

import lombok.Data;

@Data
public class DtoInformation {
    private String packageName;
    private String className;
    private EntityInformation entityInformation;
}
