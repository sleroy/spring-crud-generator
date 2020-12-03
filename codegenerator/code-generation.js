const Handlebars = require('handlebars');
const log = require('./logger').logger;
const fs = require('fs');
const path = require('path');
const Template = require('./template-renderer').Template;

const GLOBAL_JS = 'globals.js';
class CodeGeneration {
	constructor(argv) {
		this.argv = argv;
		this.projectInformation = {
			project: argv.generation,
			templates: path.join(argv.generation, 'templates'),
			partials: path.join(argv.generation, 'partials'),
			helpers: path.join(argv.generation, 'helpers'),
			script: path.join(argv.generation, 'generation.js')
		};
	}

	readCatalog(catalogPath) {
		let rawdata = fs.readFileSync(catalogPath);
		return JSON.parse(rawdata);
	}

	readScript(scriptPath) {
		return fs.readFileSync(scriptPath, 'utf8');
	}

	loadPartials() {
		const files = fs.readdirSync(this.projectInformation.partials);
		//listing all files using forEach
		files.forEach((fileName) => {
			// Do whatever you want to do with the file
			const absPath = path.join(this.projectInformation.partials, fileName);
			const partialName = path.parse(fileName).name;
			log.info(`Registering partial ${partialName} from ${absPath}`);
			const partialContent = this.readScript(absPath) + '';
			Handlebars.registerPartial(partialName, partialContent);
		});
	}

	loadHelpers(globalsObject) {
		const files = fs.readdirSync(this.projectInformation.helpers);
		//listing all files using forEach
		files.forEach((fileName) => {
			// Do whatever you want to do with the file
			const absPath = path.join(this.projectInformation.helpers, fileName);
			const partialName = path.parse(fileName).name;
			log.info(`Registering helper ${partialName} from ${absPath}`);
            const partialContent = this.readScript(absPath) + '';
            const globals = globalsObject;
            console.log(globalsObject);
			const helper = eval(partialContent);
			Handlebars.registerHelper(partialName, helper);
		});
	}

	loadGlobals() {
		log.info('Read globals.');
		const partialContent = this.readScript(path.join(this.projectInformation.project, GLOBAL_JS)) + '';
		const globals = eval(partialContent);
		return globals;
	}

	generate() {
		log.debug('Loading catalog from ', this.argv.catalog);
		log.debug('Loading Generation project from ', this.argv.generation);
		log.info('Project output path is  ', this.argv.project);

		log.info('Compilation of the template');
		try {
            const globals = this.loadGlobals()();
			// Context of the execution
			const catalog = this.readCatalog(this.argv.catalog);
			const generationInfo = this.projectInformation;
			const script = this.readScript(generationInfo.script);
			const project = this.argv.project;
			const template = new Template(generationInfo);
			log.info(`Provided globals are`, globals);
			this.loadPartials();
			this.loadHelpers(globals);

			log.info('Execution of the code generation script');
			eval(script);
		} catch (e) {
			log.error('Cannot generate the code', e);
		}
	}
}

module.exports.CodeGeneration = CodeGeneration;
