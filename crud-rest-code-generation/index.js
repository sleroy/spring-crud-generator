#!/usr/bin/env node

const yargs = require('yargs/yargs')
const path = require('path');
const {
  hideBin
} = require('yargs/helpers')
const CodeGeneration = require('byoskill-code-generator/bin/code-generation').default;

const cli = yargs(hideBin(process.argv))

cli
  .help('h')
  .alias('h', 'help')
  .demandOption(['output', 'entitymodel'])
  .describe('entitymodel', 'Path to an entity model exported from the Java program')
  .describe('output', 'the output path where the code will be generated')
  .command('crud-generator [entitymodel] [output]',
    'Generates DTO, converters from a JPA entity model')



new CodeGeneration({
  project: __dirname,
  catalog: cli.argv.entitymodel,
  output: cli.argv.output
}).generate()