#!/usr/bin/env node
const yargs = require('yargs/yargs');
const { hideBin } = require('yargs/helpers');
const Handlebars = require('handlebars');
const fs = require('fs');
const log =require('./logger').logger;

class Template {
    constructor(project) {
        this.project= project;
    }

    handlebars(templateName, payload) {
        log.info(`Generation using the template ${templateName}`);
    }
}

function readCatalog(catalogPath) {
	let rawdata = fs.readFileSync(catalogPath);
	return JSON.parse(rawdata);
}

function readScript(scriptPath) {
	return  fs.readFileSync(scriptPath, 'utf8')
}

function codeGeneration(argv) {
	log.debug('Loading catalog from ', argv.catalog);
	log.debug('Loading script from ', argv.script);
	log.info('Project output path is  ', argv.project);

	log.info('Compilation of the template');
	try {
        const catalog = readCatalog(argv.catalog);
        const script = readScript(argv.script);
        const project = argv.project;
        const template = new Template(project);
        
        log.info('Execution of the code generation script');
        eval(script);

		
	} catch (e) {
		log.error('Cannot generate the code', e);
	}
}



const cli = yargs(hideBin(process.argv));

cli
	.help('h')
	.alias('h', 'help')
	.demandOption([ 'script', 'catalog', 'project' ])
	.describe('catalog', 'Data generated by spring-crud-generator')
	.describe('script', 'Script to render your code using this CLI')
	.describe('project', 'the project path to generate the code')
	.command('codegenerator [catalog] [script]',
		'Generates code using a catalog and a script')
	;


codeGeneration(cli.argv);