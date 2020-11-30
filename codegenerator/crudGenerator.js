+/**
 * Several variables are provided for the execution of the generation.
 * 
 * log: a logger to print your messages
 * template: a component to load and execute a template with a payload
 *  example: template.nunjucks("templateName", payload") : string
 *  example: template.handlebars("templateName", payload") : string
 * catalog: returns the catalog of objects to be manipulated for the generation.
 * fs: file operations
 *  fs.write("path", content)
 */

log.warn(`script:Rendering project into ${project}`);

function computePath(basepath, packageName, className) {
	const targetDir = basepath + '/' + packageName.replace(/\./g, '/');
	fs.mkdirSync(targetDir, { recursive: true });
	return targetDir + '/' + className + '.java';
}

var helpers = {
	javatype: (type) => {
		if (type.variant == 'class') {
			return type.simpleName;
		} else if (type.variant == 'parameterized') {
			var tpm = '';
			for (var t = 0, nt = type.typeParameters.length; t < nt; t++) {
				tpm = type.typeParameters[t].simpleName + ',';
			}

			return type.simpleName + ' < ' + tpm + ' >';
		}
	}
};

log.info('script:Generation of the REST project');

var dtos = catalog.dtos;

const projectPaths = {
	modelFolder: project + 'src/main/gen/java',
	serviceFolder: project + 'src/main/gen/java',
	processorFolder: project + 'src/main/gen/java',
	testFolder: project + 'src/test/java',
	templateFolder: project + 'src/main/resources/templates'
};

log.info(`Number of DTO to generate num=${dtos.length}`);

for (var i = 0, ni = dtos.length; i < ni; ++i) {
	var dtoInfo = dtos[i];
	var dto = dtoInfo.dto;

	log.info(`Generate DTO with name ${dto.canonicalName}`);

	var content = template.handlebars(`${projectPaths.templateFolder}/dtoGenerator.handlebars`, {
		dto: dto,
		originalEntity: dtoInfo.originalEntity
	});
	log.debug(`Generated content ${content}`);

	const outputFilePath = computePath(projectPaths.modelFolder, dto.packageName, dto.simpleName);
	log.info(`Output file path${outputFilePath}`);
	fs.writeFileSync(outputFilePath, content);
}
