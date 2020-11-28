/*
 * Copyright (c) 2020. Byoskill Leroy (Sylvain Leroy).
 * All rights reserved.
 */

package com.byoskill.tools.springcrudgenerator.rapid.templategenerator;

import com.byoskill.tools.springcrudgenerator.restgenerator.templates.DtoInformation;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Map;

class FileTemplateGeneratorTest {

    @Test
    public void generate() throws IOException {

        final String templateContent = "package {{dto.packageName}};\n" +
                "\n" +
                "public class {{dto.className}} {\n" +
                "\n" +
                "\n" +
                "}\n" +
                "\n";
        final DtoInformation dtoInformation = new DtoInformation();
        dtoInformation.setClassName("PaymentDto");
        dtoInformation.setPackageName("com.byoskill.generation");

        final Map<String, Object>   payload               = Map.of("dto", dtoInformation);
        final FileTemplateGenerator fileTemplateGenerator = new FileTemplateGenerator(templateContent, payload);
        fileTemplateGenerator.generate();
    }

}