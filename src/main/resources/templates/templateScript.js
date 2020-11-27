/** The script assumes that handle bars is already loaded */

function generationWithHandleBars() {
    log.info("Compilation of the template");
    try {
        const compiledTemplate = Handlebars.compile(template);
        log.info("Processing of the template")
        return compiledTemplate(payload); // Value is returned
    } catch(e) {
        log.error("Error happened during the generation of the template {}", e);
        return "<error>";
    }
}

generationWithHandleBars();