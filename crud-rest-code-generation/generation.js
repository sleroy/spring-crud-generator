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


const LIGHT_DTO_NAME = 'LightDto';
const DTO_NAME = 'Dto';
const APP_PACKAGE_TO_REPLACE = 'com.osiatis.sequoia';

exports.default = class Generation {
	constructor(context) {
		// Using latest ES6 syntax, you may explode the context into several variables.
		this.context = context
		this.output = context.output
		this.log = context.log
		this.template = this.context.template
		this.path = this.context.path
		this.fs = this.context.fs
		this.catalog = this.context.catalog;
		this.globals = this.context.globals;
		// Options to generate the code
		this.genOpts = {
			modelFolder: this.output,
			converterFolder: this.output,
			serviceFolder: this.output,
			processorFolder: this.output,
			testFolder: this.output,
			dtoPackageName: 'com.byoskill.generated.example.dto',
			converterPackageName: 'com.byoskill.generated.example.converters',
			processorPackageName: 'com.byoskill.generated.example.processors',
			servicePackageName: 'com.byoskill.generated.example.services'
		};

		this.log.info('script:Generation of the REST project');

		this.jpaEntities = this.catalog.expectJsonObjectFile().jpaEntities;

	}

	generate() {
		this.log.warn(`script:Rendering project into ${this.output}`)



		this.log.info("*******************************************************************************");
		this.log.info("* Generation of the DTOS");
		this.log.info(`Number of DTO to generate num=${this.jpaEntities.length}`);
		this.log.info("*******************************************************************************");

		for (var jpaEntity of this.jpaEntities) {
			if ( jpaEntity.simpleName == "") {
				this.log.error("Invalid JPA Entity {}", {jpaEntity});
				throw new Error("Found an invalid entity");
			}

			const dtoLightName = jpaEntity.entityType.simpleName + LIGHT_DTO_NAME;
			const dtoName = jpaEntity.entityType.simpleName + DTO_NAME

			// Converting types as DTO types for complex relationships
			this.convertTypesAsDtoTypes(jpaEntity);


			// Generate light dto
			this.generateDtoLight(dtoLightName, jpaEntity);
			this.generateDto(dtoName, dtoLightName, jpaEntity);
		}

		/*******************************************************************************
		 * Generation of the converters
		 /*****************************************************************************/		
		this.log.info("*******************************************************************************");
		this.log.info("* Generation of the converters");
		this.log.info(`Number of converts to generate num=${this.jpaEntities.length}`);
		this.log.info("*******************************************************************************");
		for (var jpaEntity of this.jpaEntities) {
			this.writeConverters(jpaEntity);
		}
	}


	writeConverters(jpaEntity) {
		this.globals.usedJavaTypes.clear();		

		const entityInfo = jpaEntity.entityType;
		const converterName = entityInfo.simpleName + 'Converter';
		if ( entityInfo.simpleName == "") {
			this.log.error("Invalid JPA Entity {}", {jpaEntity});
			throw new Error("Found an invalid entity");
		}

		this.log.info(`Generate Converter:`, {converterName});

		const payload = {
			entity: entityInfo,
			dependencies: jpaEntity.dependencies.map((dep) => {
				dep.simpleName += 'Converter';
				dep.canonicalName = this.genOpts.converterPackageName + '.' + dep.simpleName;
				return dep;
			}),

			jpa: jpaEntity,
			dtoName: this.genOpts.dtoPackageName + '.' + entityInfo.simpleName + 'Dto',
			dtoLightName: this.genOpts.dtoPackageName + '.' + entityInfo.simpleName + 'LightDto',
			converterName: converterName,
			converterQName: this.genOpts.converterPackageName + '.' + entityInfo.simpleName + 'Converter',
			postRender: true,
			packageName: this.genOpts.converterPackageName,
			globals: this.globals,
			genOpts: this.genOpts
		};
		const outputFilePath = this.computePath(this.genOpts.converterFolder, this.genOpts.converterPackageName, payload.converterName);
		this.template.writeHandlebars(`convertor.handlebars`, payload, outputFilePath);
	}

	/**
	 * Complete for each complex field by adding a dtoType mentioning
	 * @param {*} jpaEntity 
	 */
	convertTypesAsDtoTypes(jpaEntity) {
		jpaEntity.complexColumns.forEach(cc => {
			cc.dtoType = Object.assign({}, cc.type);
			if (cc.entityCollection) {
				cc.dtoType.typeParameters[0].simpleName = cc.dtoType.typeParameters[0].simpleName + LIGHT_DTO_NAME;
				cc.dtoType.typeParameters[0].canonicalName =  cc.dtoType.typeParameters[0].packageName.replace(APP_PACKAGE_TO_REPLACE, this.genOpts.dtoPackageName) + "." + cc.dtoType.typeParameters[0].simpleName;
			} else {
				cc.dtoType.simpleName = cc.dtoType.simpleName + LIGHT_DTO_NAME;
				cc.dtoType.canonicalName = cc.dtoType.packageName.replace(APP_PACKAGE_TO_REPLACE, this.genOpts.dtoPackageName)+ "." + cc.dtoType.simpleName;
			}
		});
	}

	generateDto(dtoName, dtoLightName, jpaEntity) {
		this.globals.usedJavaTypes.clear();

		this.log.info(`Generate DTO with name ${dtoName}`);

		const dtoPackageName = jpaEntity.entityType.packageName.replace(APP_PACKAGE_TO_REPLACE, this.genOpts.dtoPackageName);
		const outputFilePath = this.computePath(this.genOpts.modelFolder, dtoPackageName, dtoName);
		this.template.writeHandlebars(`dtoGenerator.handlebars`, {
			dtoName: dtoName,
			dtoLightName: dtoLightName,
			dtoPackageName: dtoPackageName,
			jpaEntity,
			originalEntity: jpaEntity.entityType,
			postRender: true,
			simpleColumns: jpaEntity.simpleColumns,
			fields: jpaEntity.complexColumns,
			globals: this.globals,
			genOpts: this.genOpts
		}, outputFilePath);
	}

	generateDtoLight(dtoLightName, jpaEntity) {
		this.globals.usedJavaTypes.clear();

		
		const dtoPackageName = jpaEntity.entityType.packageName.replace(APP_PACKAGE_TO_REPLACE, this.genOpts.dtoPackageName);

		this.log.info(`Generate DTO with name ${dtoLightName}`);

		const outputFilePath = this.computePath(this.genOpts.modelFolder, dtoPackageName, dtoLightName);
		this.log.info(`Output file path${outputFilePath}`);
		this.template.writeHandlebars(`dtoLightGenerator.handlebars`, {
			dtoName: dtoLightName,
			dtoPackageName: dtoPackageName,
			jpaEntity,
			originalEntity: jpaEntity.entityType,
			postRender: true,
			fields: jpaEntity.simpleColumns,
			globals: this.globals,
			genOpts: this.genOpts
		}, outputFilePath);
	}

	computePath(basepath, packageName, className) {
		const targetDir = basepath + '/' + packageName.replace(/\./g, '/');
		fs.mkdirSync(targetDir, {
			recursive: true
		});
		return targetDir + '/' + className + '.java';
	}
}