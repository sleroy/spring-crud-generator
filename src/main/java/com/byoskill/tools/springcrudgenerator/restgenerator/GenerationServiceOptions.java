/*
 * Copyright (c) 2020. Byoskill Leroy (Sylvain Leroy).
 * All rights reserved.
 */

package com.byoskill.tools.springcrudgenerator.restgenerator;

import java.util.List;

public class GenerationServiceOptions {
    public  String                 serviceClassName;
    private List<MethodDeclarator> methodDeclarators;
    private String                 servicePackageName;
    private String                 repositoryClassName;
    private String                 outputFolder;
    private String                 restPath;
    private String                 entityDtoTypeName;
    private String                 entityConverterClassName;
    private String                 entityClassName;
    private String                 primaryKeyType;

    public String getEntityDtoTypeName() {
        return entityDtoTypeName;
    }

    public void setEntityDtoTypeName(final String entityDtoTypeName) {
        this.entityDtoTypeName = entityDtoTypeName;
    }

    public String getRestPath() {
        return restPath;
    }

    public void setRestPath(final String restPath) {

        this.restPath = restPath;
    }

    public String getServiceClassName() {
        return serviceClassName;
    }

    public void setServiceClassName(final String serviceClassName) {
        this.serviceClassName = serviceClassName;
    }

    public String getServicePackageName() {
        return servicePackageName;
    }

    public void setServicePackageName(final String servicePackageName) {

        this.servicePackageName = servicePackageName;
    }

    public String getRepositoryClassName() {
        return repositoryClassName;
    }

    public void setRepositoryClassName(final String repositoryClassName) {
        this.repositoryClassName = repositoryClassName;
    }

    public String getOutputFolder() {
        return outputFolder;
    }

    public void setOutputFolder(final String outputFolder) {
        this.outputFolder = outputFolder;
    }

    public String getEntityConverterClassName() {


        return entityConverterClassName;
    }

    public void setEntityConverterClassName(final String entityConverterClassName) {
        this.entityConverterClassName = entityConverterClassName;
    }

    public String getEntityClassName() {
        return entityClassName;
    }

    public void setEntityClassName(final String entityClassName) {
        this.entityClassName = entityClassName;
    }

    public String getPrimaryKeyType() {
        return primaryKeyType;
    }

    public void setPrimaryKeyType(final String primaryKeyType) {
        this.primaryKeyType = primaryKeyType;
    }
}
