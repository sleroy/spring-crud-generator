/*
 * Copyright (c) 2020. Byoskill Leroy (Sylvain Leroy).
 * All rights reserved.
 */

package com.byoskill.tools.springcrudgenerator.rapid.templategenerator;

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

    public JavaFileGenerator(final String modelPackageName, final String className, final Object payload) {

        this.modelPackageName = modelPackageName;
        this.className = className;
        this.payload = payload;
    }

    public void writeTo(final Path path) throws IOException {
        final String[] split      = modelPackageName.split("\\.");
        final Path     folderPath = path.resolve(String.join(File.separator, split));
        Files.createDirectories(folderPath);
        final File file = new File(folderPath.toFile(), className + ".java");
        try (final PrintStream ps = new PrintStream(file)) {
            log.info("Writing file into {}", file);
            writeTo(ps);
        }

    }

    public void writeTo(final PrintStream out) throws IOException {

        final String                templateContent       = loadTemplate(templateName);
        final FileTemplateGenerator fileTemplateGenerator = new FileTemplateGenerator(templateContent, payload);
        out.println(fileTemplateGenerator.generate());

    }

    private String loadTemplate(final String templateName) throws IOException {
        return IOUtils.toString(getClass().getResourceAsStream("/templates/" + templateName), StandardCharsets.UTF_8);
    }

    public void setTemplate(final String templateName) {

        this.templateName = templateName;
    }
}
