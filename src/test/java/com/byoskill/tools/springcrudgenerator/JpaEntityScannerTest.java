/*
 * Copyright (c) 2020. Byoskill Leroy (Sylvain Leroy).
 * All rights reserved.
 */

package com.byoskill.tools.springcrudgenerator;


import com.byoskill.tools.springcrudgenerator.rapid.catalog.Catalog;
import com.byoskill.tools.springcrudgenerator.rapid.catalog.CatalogImpl;
import com.byoskill.tools.springcrudgenerator.restgenerator.JpaEntityScanner;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Set;

public class JpaEntityScannerTest {

    @Test
    public void generate() throws Exception {
        final Catalog catalog = new CatalogImpl();

        try (URLClassLoader urlClassLoader = new URLClassLoader(new URL[]{new File("/home/sleroy/git/sequoia-backend/java/sequoia-domain" +
                                                                                           "/target" +
                                                                                           "/sequoia-domain-5.0.12.jar").toURL()})) {

            Reflections reflections = new Reflections("com.osiatis.sequoia",
                                                      new SubTypesScanner(false));

            Set<Class<? extends Object>> allClasses =
                    reflections.getSubTypesOf(Object.class);

            final JpaEntityScanner jpaEntityScanner = new JpaEntityScanner(catalog);
            for ( Class cl : allClasses) {
                jpaEntityScanner.scan(cl);
            }

            final ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.findAndRegisterModules();
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File("catalog.json"), catalog);
        }


    }
}