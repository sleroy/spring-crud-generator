/*
 * Copyright (c) 2020. Byoskill Leroy (Sylvain Leroy).
 * All rights reserved.
 */

package com.byoskill.tools.springcrudgenerator.restgenerator;

import lombok.Data;

@Data
public class ConvertorGeneratorOptions {
    private DtoMapping dtoMapping;
    private String     convertPackageName;
    private String     outputFolder;
    private String     converterName;

    public String getConvertPackageName() {
        return convertPackageName;
    }

    public void setConvertPackageName(final String convertPackageName) {
        this.convertPackageName = convertPackageName;
    }

    public String getOutputFolder() {
        return outputFolder;
    }

    public void setOutputFolder(final String outputFolder) {
        this.outputFolder = outputFolder;
    }
}
