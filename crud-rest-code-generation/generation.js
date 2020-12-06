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

const LIGHT_DTO_NAME = 'LightDto';
const DTO_NAME = 'Dto';

function computePath(basepath, packageName, className) {
	const targetDir = basepath + '/' + packageName.replace(/\./g, '/');
	fs.mkdirSync(targetDir, {
		recursive: true
	});
	return targetDir + '/' + className + '.java';
}

log.info('script:Generation of the REST project');

var jpaEntities = catalog.jpaEntities;

// Options to generate the code
const genOpts = {
	modelFolder: project + 'src/test/java',
	converterFolder: project + 'src/test/java',
	serviceFolder: project + 'src/test/java',
	processorFolder: project + 'src/test/java',
	testFolder: project + 'src/test/java',
	dtoPackageName: 'com.byoskill.generated.example.model',
	converterPackageName: 'com.byoskill.generated.example.converters',
	processorPackageName: 'com.byoskill.generated.example.processors',
	servicePackageNmame: 'com.byoskill.generated.example.services'
};

/*******************************************************************************
 * Generation of the DTOS
 /*****************************************************************************/
log.info(`Number of DTO to generate num=${jpaEntities.length}`);

for (var jpaEntity of catalog.jpaEntities) {
	const dtoLightName = jpaEntity.entityType.simpleName + LIGHT_DTO_NAME;
	const dtoName = jpaEntity.entityType.simpleName + DTO_NAME;


	// Converting types as DTO types for complex relationships
	jpaEntity.complexColumns.forEach(cc => {		
		cc.dtoType = Object.assign({}, cc.type);
		if (cc.entityCollection) {
			cc.dtoType.typeParameters[0].simpleName = cc.dtoType.typeParameters[0].simpleName+ LIGHT_DTO_NAME
			cc.dtoType.typeParameters[0].canonicalName = genOpts.dtoPackageName + "." + cc.dtoType.typeParameters[0].simpleName;
		} else {
			cc.dtoType.simpleName = cc.dtoType.simpleName+ LIGHT_DTO_NAME
			cc.dtoType.canonicalName = cc.dtoType.canonicalName+ LIGHT_DTO_NAME
		}
	})


	// Generate light dto
	{
		globals.usedJavaTypes.clear();

		log.info(`Generate DTO with name ${dtoLightName}`);

		var content = template.handlebars(`dtoLightGenerator.handlebars`, {
			dtoName: dtoLightName,
			dtoPackageName: genOpts.dtoPackageName,
			jpaEntity,
			originalEntity: jpaEntity.entityType,
			postRender: true,
			fields: jpaEntity.simpleColumns,
			globals: globals,
			genOpts
		});
		log.debug(`Generated content ${content}`);

		const outputFilePath = computePath(genOpts.modelFolder, genOpts.dtoPackageName, dtoLightName);
		log.info(`Output file path${outputFilePath}`);
		fs.writeFileSync(outputFilePath, content);
	}
	{
		// Generate dto
		globals.usedJavaTypes.clear();

		log.info(`Generate DTO with name ${dtoName}`);

		var content = template.handlebars(`dtoGenerator.handlebars`, {
			dtoName: dtoName,
			dtoLightName: dtoLightName,
			dtoPackageName: genOpts.dtoPackageName,
			jpaEntity,
			originalEntity: jpaEntity.entityType,
			postRender: true,
			simpleColumns: jpaEntity.simpleColumns,
			fields: jpaEntity.complexColumns,
			globals: globals,
			genOpts
		});
		log.debug(`Generated content ${content}`);

		const outputFilePath = computePath(genOpts.modelFolder, genOpts.dtoPackageName, dtoName);
		log.info(`Output file path${outputFilePath}`);
		fs.writeFileSync(outputFilePath, content);
	}
}

/*******************************************************************************
 * Generation of the converters
 /*****************************************************************************/
log.info(`Number of converts to generate num=${jpaEntities.length}`);

for (var jpaEntity of catalog.jpaEntities) {
	globals.usedJavaTypes.clear();

	const entityInfo = jpaEntity.entityType;

	const payload = {
		entity: entityInfo,
		dependencies: jpaEntity.dependencies.map((dep) => {
			dep.simpleName += 'Converter';
			dep.canonicalName = genOpts.converterPackageName + '.' + dep.simpleName;
			return dep;
		}),
		
		jpa: jpaEntity,
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

	const outputFilePath = computePath(genOpts.converterFolder, genOpts.converterPackageName, payload.converterName);

	console.log(globals);
	log.info(`Output file path${outputFilePath}`);
	fs.writeFileSync(outputFilePath, content);
}
