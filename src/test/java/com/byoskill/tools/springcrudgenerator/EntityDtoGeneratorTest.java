/*
 * Copyright (c) 2020. Byoskill Leroy (Sylvain Leroy).
 * All rights reserved.
 */

package com.byoskill.tools.springcrudgenerator;


import com.byoskill.tools.example.Payment;
import com.byoskill.tools.springcrudgenerator.rapid.catalog.Catalog;
import com.byoskill.tools.springcrudgenerator.rapid.catalog.CatalogImpl;
import com.byoskill.tools.springcrudgenerator.restgenerator.EntityDtoGenerator;
import com.byoskill.tools.springcrudgenerator.restgenerator.EntityDtoGeneratorOptions;
import com.byoskill.tools.springcrudgenerator.restgenerator.RestProjectInformation;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.io.File;

public class EntityDtoGeneratorTest {

    @Test
    public void generate() throws Exception {
        final Catalog catalog = new CatalogImpl();

        final EntityDtoGeneratorOptions entityDtoGeneratorOptions = new EntityDtoGeneratorOptions();
        entityDtoGeneratorOptions.setOutputFolder("src/test/java");
        entityDtoGeneratorOptions.setModelPackageName("ch.demo.generation.model");

        final EntityDtoGenerator entityDtoGenerator = new EntityDtoGenerator(entityDtoGeneratorOptions, catalog);
        entityDtoGenerator.generate(Payment.class);

        final RestProjectInformation projectInformation = new RestProjectInformation();
        projectInformation.setModelFolder("src/gen/java");
        projectInformation.setProcessorFolder("src/gen/java");
        projectInformation.setProjectFolder("src/gen/java");
        projectInformation.setRestFolder("src/gen/java");


        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File("catalog.json"), catalog);
    }
}