/*
 * Copyright (c) 2020. Byoskill Leroy (Sylvain Leroy).
 * All rights reserved.
 */

package com.byoskill.tools.springcrudgenerator;


import com.byoskill.tools.example.Payment;
import com.byoskill.tools.springcrudgenerator.restgenerator.EntityDtoGenerator;
import com.byoskill.tools.springcrudgenerator.restgenerator.EntityDtoGeneratorOptions;
import org.junit.jupiter.api.Test;

public class EntityDtoGeneratorTest {

    @Test
    public void generate() throws Exception {
        {
            final EntityDtoGeneratorOptions entityDtoGeneratorOptions = new EntityDtoGeneratorOptions();

            entityDtoGeneratorOptions.setOutputFolder("src/test/java");
            entityDtoGeneratorOptions.setEntityClassName(Payment.class);
            entityDtoGeneratorOptions.setPrimaryKeyType(Long.class.getCanonicalName());
            entityDtoGeneratorOptions.setModelPackageName("ch.demo.generation.model");

            final EntityDtoGenerator entityDtoGenerator = new EntityDtoGenerator(entityDtoGeneratorOptions);
            entityDtoGenerator.generate();

        }

    }

}