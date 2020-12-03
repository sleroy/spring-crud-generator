/**
 * Several variables are provided for the execution of the generation.
 *
 * log: a logger to print your messages
 * template: a component to load and execute a template with a payload
 *  example: template.nunjucks("templateName", payload") : string
 *  example: template.handlebars("templateName", payload") : string
 * catalog: returns the catalog of objects to be manipulated for the generation.
 * fs: file operations
 *  fs.write("path", content)
 * path: path operations
 * globals : contains a list of global variables declared at the begin of the execution and available in both templates, helpers and partials
 * project: project folder
 * generationInfo: contains various folder locations
	templates: path.join(argv.generation, 'templates'),
	partials: path.join(argv.generation, 'partials'),
	helpers: path.join(argv.generation, 'helpers'),
	script: path.join(argv.generation, 'generation.js')
 */

log.warn(`script:Rendering project into ${project}`);

function computePath(basepath, packageName, className) {
	const targetDir = basepath + '/' + packageName.replace(/\./g, '/');
	fs.mkdirSync(targetDir, {
		recursive: true
	});
	return targetDir + '/' + className + '.java';
}

log.info('script:Generation of the REST project');

var dtos = catalog.dtos;

// Options to generate the code
const genOpts = {
	modelFolder: project + 'src/main/gen/java',
	converterFolder: project + 'src/main/gen/java',
	serviceFolder: project + 'src/main/gen/java',
	processorFolder: project + 'src/main/gen/java',
	testFolder: project + 'src/test/java',
	dtoPackageName: 'com.byoskill.generated.example.model',
	converterPackageName: 'com.byoskill.generated.example.converters',
	processorPackageName: 'com.byoskill.generated.example.processors',
	servicePackageNmame: 'com.byoskill.generated.example.services'
};

/*******************************************************************************
 * Generation of the DTOS
 /*****************************************************************************/
log.info(`Number of DTO to generate num=${dtos.length}`);

for (var dtoInfo of dtos) {
	var dto = dtoInfo.dto;
	globals.usedJavaTypes.clear();

	log.info(`Generate DTO with name ${dto.canonicalName}`);

	var content = template.handlebars(`dtoGenerator.handlebars`, {
		dto: dto,
		originalEntity: dtoInfo.originalEntity,
		postRender: true,
		globals: globals
	});
	log.debug(`Generated content ${content}`);

	const outputFilePath = computePath(genOpts.modelFolder, genOpts.dtoPackageName, dto.simpleName);

	console.log(globals);
	log.info(`Output file path${outputFilePath}`);
	fs.writeFileSync(outputFilePath, content);
}

/*******************************************************************************
 * Generation of the converters
 /*****************************************************************************/
log.info(`Number of converts to generate num=${dtos.length}`);

for (var entityInfo of catalog.entities) {
	globals.usedJavaTypes.clear();

	const payload = {
		entity: entityInfo,
		dtoName: genOpts.dtoPackageName + '.' + entityInfo.simpleName + 'Dto',
		dtoLightName: genOpts.dtoPackageName + '.' + entityInfo.simpleName + 'LightDto',
		converterName: entityInfo.simpleName + 'Converter',
		converterQName: genOpts.converterPackageName + '.' + entityInfo.simpleName + 'Converter',
		postRender: true,
		packageName: genOpts.converterPackageName,
		globals: globals,
		genOpts: genOpts
	};
	var content = template.handlebars(`convertor.handlebars`, payload);
	log.debug(`Generated content ${content}`);

	const outputFilePath = computePath(
		genOpts.converterFolder,
		genOpts.converterPackageName,
		payload.converterName
	);

	console.log(globals);
	log.info(`Output file path${outputFilePath}`);
	fs.writeFileSync(outputFilePath, content);
}
