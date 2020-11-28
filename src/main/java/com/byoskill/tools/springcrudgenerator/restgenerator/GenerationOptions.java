/*
 * Copyright (c) 2020. Byoskill Leroy (Sylvain Leroy).
 * All rights reserved.
 */

package com.byoskill.tools.springcrudgenerator.restgenerator;

public class GenerationOptions {

    private String outputFolder;
    private String packageName;
    private String restPath;

    public String getOutputFolder() {
        return outputFolder;
    }

    public void setOutputFolder(final String outputFolder) {
        this.outputFolder = outputFolder;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(final String packageName) {
        this.packageName = packageName;
    }

    public String getRestPath() {
        return restPath;
    }

    public void setRestPath(final String restPath) {
        this.restPath = restPath;
    }
}
