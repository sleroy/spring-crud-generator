package com.byoskill.tools.springcrudgenerator;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;

public class RepositoryMetadataScanner {
    private final Class<?>          clazz;
    private       ParameterizedType repositoryType;
    private       Class<?>          entityType;
    private       Class<?>          primaryKeyType;
    private       Method[]          methods;

    public RepositoryMetadataScanner(Class<?> clazz) {

        this.clazz = clazz;
    }

    public void scan() throws ClassNotFoundException {

        repositoryType = (ParameterizedType) clazz.getGenericInterfaces()[0];
        entityType     = Class.forName(repositoryType.getActualTypeArguments()[0].getTypeName());
        primaryKeyType = Class.forName(repositoryType.getActualTypeArguments()[1].getTypeName());

        methods = clazz.getMethods();
        for (var method : methods) {
            System.out.println(method);
        }

    }
}
