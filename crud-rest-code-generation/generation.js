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
 */

log.warn(`script:Rendering project into ${project}`)


function computePath(basepath, packageName, className) {
	const targetDir = basepath + "/" + packageName.replace(/\./g, "/");
	fs.mkdirSync(targetDir, {
		recursive: true
	});
	return targetDir + "/" + className + ".java";
}

Handlebars.registerHelper("javatype", function (type) {
	if (type.variant == "class") {
		return type.simpleName;
	} else if (type.variant == "parameterized") {
		var tpm = type.typeParameters.map(t => t.simpleName).join(',');		
		return type.rawtype.simpleName + "<" + tpm + ">";
	}
});

Handlebars.registerHelper("capitalize", function (word) {
	return word[0].toUpperCase() + word.slice(1);
});


log.info("script:Generation of the REST project");

var dtos = catalog.dtos;

const projectPaths = {
	modelFolder: project + "src/main/gen/java",
	serviceFolder: project + "src/main/gen/java",
	processorFolder: project + "src/main/gen/java",
	testFolder: project + "src/test/java",
};

log.info(`Number of DTO to generate num=${dtos.length}`);

for (var i = 0, ni = dtos.length; i < ni; ++i) {
	var dtoInfo = dtos[i];
	var dto = dtoInfo.dto;

	log.info(`Generate DTO with name ${dto.canonicalName}`);

	var content = template.handlebars(
		`dtoGenerator.handlebars`, {
			dto: dto,
			originalEntity: dtoInfo.originalEntity,
		}
	);
	log.debug(`Generated content ${content}`);

	const outputFilePath = computePath(
		projectPaths.modelFolder,
		dto.packageName,
		dto.simpleName
	);
	log.info(`Output file path${outputFilePath}`);
	fs.writeFileSync(outputFilePath, content);
}