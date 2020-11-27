package com.byoskill.tools.springcrudgenerator.template;

import com.byoskill.tools.springcrudgenerator.model.DtoInformation;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Map;

class FileTemplateGeneratorTest {

    @Test
    public void generate() throws IOException {

        String                        templateContent = "package {{dto.packageName}};\n" +
                "\n" +
                "public class {{dto.className}} {\n" +
                "\n" +
                "\n" +
                "}\n" +
                "\n";
        DtoInformation        dtoInformation                    = new DtoInformation();
        dtoInformation.setClassName("PaymentDto");
        dtoInformation.setPackageName("com.byoskill.generation");

        Map<String, Object>   payload               = Map.of("dto", dtoInformation);
        FileTemplateGenerator fileTemplateGenerator = new FileTemplateGenerator(templateContent, payload);
        fileTemplateGenerator.generate();
    }

}