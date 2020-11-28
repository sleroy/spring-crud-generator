package com.byoskill.tools.springcrudgenerator.template;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

/**
 * This class generates from a template passed as a String and a payload a new content.
 */
@Slf4j
public class FileTemplateGenerator {

    public static final String HANDLEBARS = "Handlebars";
    @SuppressWarnings("HardcodedFileSeparator")
    public static final String LIBS       = "/libs/handlebars-v4.7.6.js";
    private final       String templateContent;
    private final       Object payload;
    private final       String scriptContent;

    public FileTemplateGenerator(final String templateContent, final Object payload) throws IOException {

        this.templateContent = templateContent;
        this.payload = payload;
        scriptContent = readExecutionTemplate();
    }

    private String readExecutionTemplate() throws IOException {
        try (final InputStream templateStream = loadResource("/templates/templateScript.js")) {
            return IOUtils.toString(templateStream, StandardCharsets.UTF_8);
        }
    }

    private InputStream loadResource(final String libs) {
        return getClass().getResourceAsStream(libs);
    }

    /**
     * Compiles the template and executes it with the payload.
     *
     * @return the content generated with the template and the payload
     */
    public String generate() {
        log.info("Generation of a template");
        final StopWatch started = StopWatch.createStarted();

        // Produce RhinoJS context
        final Context cx = Context.enter();
        try {
            return executeRhino(cx);
        } catch (final IOException e) {
            log.error("Cannot load the library Handlebars", e);
        } finally {
            Context.exit();
            started.stop();
            log.info("Template generated in {} ms", started.getTime(TimeUnit.MILLISECONDS));
        }
        return "<error>";
    }

    private String executeRhino(final Context cx) throws IOException {
        log.debug("Using template\n{}", templateContent);

        final Scriptable globalScope = cx.initStandardObjects();
        initScopeWithParameters(cx, globalScope);

        final String generatedContent = evaluateRhinoScriptAndObtainTemplate(cx, globalScope);
        System.out.println(generatedContent);
        return generatedContent;
    }

    private void initScopeWithParameters(final Context cx, final Scriptable globalScope) throws IOException {
        loadingHandlebarsLibrary(cx, globalScope);
        injectProperty(globalScope, System.out, "out");
        injectProperty(globalScope, log, "log");
        injectProperty(globalScope, templateContent, "template");
        injectPayload(globalScope);
    }

    private String evaluateRhinoScriptAndObtainTemplate(final Context cx, final Scriptable globalScope) {
        final Object result = cx.evaluateString(globalScope, scriptContent, "<cmd>", 1, null);
        // We expect the rhino script to return a String.
        return Context.toString(result);
    }

    private void loadingHandlebarsLibrary(final Context cx, final Scriptable globalScope) throws IOException {
        final InputStream libraryStream = loadResource(LIBS);
        try (final Reader handlebarsLibReader = new InputStreamReader(libraryStream, StandardCharsets.UTF_8)) {
            cx.evaluateReader(globalScope, handlebarsLibReader, HANDLEBARS, 1, null);
        }
    }

    private static void injectProperty(final Scriptable scope, final Object object, final String key) {

        final Object value = Context.javaToJS(object, scope);
        ScriptableObject.putProperty(scope, key, value);
    }

    private void injectPayload(final Scriptable scope) {
        injectProperty(scope, payload, "payload");
    }

}
