package com.byoskill.tools.springcrudgenerator.template;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * This class generates from a template passed as a String and a payload a new content.
 */
@Slf4j
public class FileTemplateGenerator {

    public static final String HANDLEBARS = "Handlebars";
    public static final String LIBS       = "/libs/handlebars-v4.7.6.js";
    private final       String templateContent;
    private final       Object payload;
    private final       String scriptContent;

    public FileTemplateGenerator(String templateContent, Object payload) throws IOException {

        this.templateContent = templateContent;
        this.payload         = payload;
        scriptContent        = IOUtils.toString(getClass().getResourceAsStream("/templates/templateScript.js"), StandardCharsets.UTF_8);
    }

    public String generate() {
        log.info("Generation of a template");
        StopWatch started = StopWatch.createStarted();
        // Produce RhinoJS context
        Context cx = Context.enter();
        try {
            log.debug("Using template\n{}", templateContent);
            Scriptable globalScope      = cx.initStandardObjects();
            Reader     esprimaLibReader = new InputStreamReader(getClass().getResourceAsStream(LIBS));
            cx.evaluateReader(globalScope, esprimaLibReader, HANDLEBARS, 1, null);

            injectProperty(globalScope, System.out, "out");
            injectProperty(globalScope, log, "log");
            injectProperty(globalScope, templateContent, "template");
            injectPayload(globalScope);

            Object result           = cx.evaluateString(globalScope, scriptContent, "<cmd>", 1, null);
            String generatedContent = Context.toString(result);
            System.out.println(generatedContent);
            return generatedContent;
        } catch (IOException e) {
            log.error("Cannot load the library Handlebars");
        } finally {
            Context.exit();
            started.stop();
            log.info("Template generated in {} ms", started.getTime(TimeUnit.MILLISECONDS));
        }
        return "<error>";
    }

    private void injectProperty(Scriptable scope, Object object, String key) {

        Object value = Context.javaToJS(object, scope);
        ScriptableObject.putProperty(scope, key, value);
    }

    private void injectPayload(Scriptable scope) {
        injectProperty(scope, this.payload, "payload");
    }

}
