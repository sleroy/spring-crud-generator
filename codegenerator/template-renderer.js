const Handlebars = require("handlebars");
const log = require('./logger').logger;
const fs = require('fs');
const path = require('path');

class Template {
    constructor(project) {
        this.project = project;
    }

    handlebars(templateName, payload) {
        log.info(`Generation using the template ${templateName}`);
        
        const templateFile = path.join(this.project.templates, templateName);
        log.info(`Location of the ${templateFile}`);

        const template = fs.readFileSync(templateFile) + '';
        const hb = Handlebars.compile(template);
        const renderedContent = hb(payload);
        log.info(`Content : \n${renderedContent}`);
        return renderedContent;
    }
}

module.exports.Template = Template;