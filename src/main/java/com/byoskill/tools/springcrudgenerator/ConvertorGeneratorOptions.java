package com.byoskill.tools.springcrudgenerator;

import lombok.Data;

@Data
public class ConvertorGeneratorOptions {
    private DtoMapping dtoMapping;
    private String           convertPackageName;
    private String           outputFolder;
    private String           converterName;

    public String getConvertPackageName() {
        return convertPackageName;
    }

    public void setConvertPackageName(String convertPackageName) {
        this.convertPackageName = convertPackageName;
    }

    public String getOutputFolder() {
        return outputFolder;
    }

    public void setOutputFolder(String outputFolder) {
        this.outputFolder = outputFolder;
    }
}
