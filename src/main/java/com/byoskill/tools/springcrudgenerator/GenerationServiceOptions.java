package com.byoskill.tools.springcrudgenerator;

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

    public void setEntityDtoTypeName(String entityDtoTypeName) {
        this.entityDtoTypeName = entityDtoTypeName;
    }

    public String getRestPath() {
        return restPath;
    }

    public void setRestPath(String restPath) {

        this.restPath = restPath;
    }

    public String getServiceClassName() {
        return serviceClassName;
    }

    public void setServiceClassName(String serviceClassName) {
        this.serviceClassName = serviceClassName;
    }

    public String getServicePackageName() {
        return servicePackageName;
    }

    public void setServicePackageName(String servicePackageName) {

        this.servicePackageName = servicePackageName;
    }

    public String getRepositoryClassName() {
        return repositoryClassName;
    }

    public void setRepositoryClassName(String repositoryClassName) {
        this.repositoryClassName = repositoryClassName;
    }

    public String getOutputFolder() {
        return outputFolder;
    }

    public void setOutputFolder(String outputFolder) {
        this.outputFolder = outputFolder;
    }

    public String getEntityConverterClassName() {


        return entityConverterClassName;
    }

    public void setEntityConverterClassName(String entityConverterClassName) {
        this.entityConverterClassName = entityConverterClassName;
    }

    public String getEntityClassName() {
        return entityClassName;
    }

    public void setEntityClassName(String entityClassName) {
        this.entityClassName = entityClassName;
    }

    public String getPrimaryKeyType() {
        return primaryKeyType;
    }

    public void setPrimaryKeyType(String primaryKeyType) {
        this.primaryKeyType = primaryKeyType;
    }
}
