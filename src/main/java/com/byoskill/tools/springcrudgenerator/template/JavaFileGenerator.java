package com.byoskill.tools.springcrudgenerator.template;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
public class JavaFileGenerator {
    private final String modelPackageName;
    private final String className;
    private final Object payload;
    private       String templateName;

    public JavaFileGenerator(String modelPackageName, String className, Object payload) {

        this.modelPackageName = modelPackageName;
        this.className        = className;
        this.payload          = payload;
    }

    public void writeTo(Path path) throws IOException {
        String[] split      = modelPackageName.split("\\.");
        Path     folderPath = path.resolve(String.join(File.separator, split));
        Files.createDirectories(folderPath);
        File file = new File(folderPath.toFile(), className + ".java");
        try (PrintStream ps = new PrintStream(file)) {
            log.info("Writing file into {}", file);
            writeTo(ps);
        }

    }

    public void writeTo(PrintStream out) throws IOException {

        String                templateContent       = loadTemplate(templateName);
        FileTemplateGenerator fileTemplateGenerator = new FileTemplateGenerator(templateContent, payload);
        out.println(fileTemplateGenerator.generate());

    }

    private String loadTemplate(String templateName) throws IOException {
        return IOUtils.toString(getClass().getResourceAsStream("/templates/" + templateName), StandardCharsets.UTF_8);
    }

    public void setTemplate(String templateName) {

        this.templateName = templateName;
    }
}
