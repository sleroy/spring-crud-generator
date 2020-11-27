package com.byoskill.tools.springcrudgenerator;


import com.byoskill.tools.example.Payment;
import org.junit.jupiter.api.Test;

public class EntityDtoGeneratorTest {

    @Test
    public void generate() throws Exception {
        {
            EntityDtoGeneratorOptions entityDtoGeneratorOptions = new EntityDtoGeneratorOptions();

            entityDtoGeneratorOptions.setOutputFolder("src/test/java");
            entityDtoGeneratorOptions.setEntityClassName(Payment.class);
            entityDtoGeneratorOptions.setPrimaryKeyType(Long.class.getCanonicalName());
            entityDtoGeneratorOptions.setModelPackageName("ch.demo.generation.model");

            EntityDtoGenerator entityDtoGenerator = new EntityDtoGenerator(entityDtoGeneratorOptions);
            entityDtoGenerator.generate();

        }

    }

}