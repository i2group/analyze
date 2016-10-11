/*
 * Copyright (c) 2014, 2016 IBM Corp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    IBM Corp - initial API and implementation and initial documentation
 */

import org.apache.commons.cli.Option

GENERATE_MAPPING_JAR_TASK_NAME                      = 'generateMappingJar'
STAGE_TOOLKIT_AND_COMMON_OVERRIDES_TASK_NAME        = 'stageToolkitAndCommonOverrides'
STAGE_BUILT_PROJECTS_TASK_NAME                      = 'stageBuiltProjects'
DEPLOY_EXAMPLE_TASK_NAME                            = 'deployExample'
DEPLOY_TASK_NAME                                    = 'deploy'

devEssTasks = [
                STAGE_TOOLKIT_AND_COMMON_OVERRIDES_TASK_NAME,
                STAGE_BUILT_PROJECTS_TASK_NAME,
                GENERATE_MAPPING_JAR_TASK_NAME,
                'addDaodDataSource',
                'unpackXap',
                'packXap',
                DEPLOY_EXAMPLE_TASK_NAME,
                DEPLOY_TASK_NAME
              ]

prNotRequiredDevEssTasks = [
                STAGE_TOOLKIT_AND_COMMON_OVERRIDES_TASK_NAME,
                STAGE_BUILT_PROJECTS_TASK_NAME,
                GENERATE_MAPPING_JAR_TASK_NAME,
                DEPLOY_EXAMPLE_TASK_NAME,
                DEPLOY_TASK_NAME
              ]

void executeCommand(String[] args) {
    CliBuilder cli = new CliBuilder(usage: 'build [-h] [-th] [-dh] [-a] [-pr PROJECT] [-s SERVER] -t TASK [-i] [-d] [--stacktrace]')

    cli.writer = new NoOpUsageWriter()
    // The 'task' command-line argument represents the Gradle task that will be run.
    cli.with {
        h longOpt: 'help', 'Shows this help message and exits'
        th longOpt: 'toolkithelp', 'Shows the standard toolkit help message and exits'
        a longOpt: 'additional', 'Shows additional arguments and tasks, and exits'
        dh longOpt: 'dahelp', 'Shows Data Access specific tasks and exits'
        pr longOpt: 'project', args: 1, argName: 'project', 'Specifies the project to run the command against'
        s longOpt: 'server', args: 1, argName: 'server', 'Specifies the server profile to manage'
        t longOpt: 'task', args: 1, argName: 'task', 'Specifies the task to perform'
        i longOpt: 'info', 'Displays all info messages'
        d longOpt: 'debug', 'Displays all debug messages'
        _ longOpt: 'stacktrace', 'Displays all stacktraces'
        // DA specific options
        x longOpt: 'schema', args: 1, argName: 'schema', 'Specifies the full path including the file name of the schema file used to generate the mapping'
        o longOpt: 'mappingJAR', args: 1, argName: 'mappingJAR', 'Specifies the full path, including the file name, to the mapping JAR file to be created'
        z longOpt: 'xsdPath', args: 1, argName: 'xsdPath', 'Specifies the full path to a directory where the generated XSD files will be stored'
        dn longOpt: 'datasourceName', args: 1, argName: 'datasourceName', 'Specifies the name of the data source to be created or modified'
        sc longOpt: 'singleCardFormat', argName: 'singleCardFormat', 'Generates files that validate using a single card format instead of the standard multiple card format'
        p longOpt: 'gradleProperties', args: Option.UNLIMITED_VALUES, valueSeparator: ',' as char, argName: 'gradleProperties', 'pass gradleProperties comma separated'
    }

    def usage =
"""
Standard deployment:
usage: ${cli.usage}
${printOption(cli, 'h')}
${printOption(cli, 'th')}
${printOption(cli, 'pr')}
${printOption(cli, 's')}
${printOption(cli, 't')}
${printOption(cli, 'x')}
${printOption(cli, 'o')}
${printOption(cli, 'i')}
${printOption(cli, 'd')}
${printOption(cli, 'stacktrace')}

The following projects are available:
${printTask('da-arload-filesystem-example', 'Demonstrates loading data into the analysis repository')}
${printTask('da-subset-filesystem-example', 'Demonstrates a data access on-demand subset')}
${printTask('da-subset-documents-example', 'Demonstrates a data access on-demand subset - including linked documents')}
${printTask('da-subset-rest-example', 'Demonstrates a two-phase data access on-demand subset')}
${printTask('awc-audit-csv-example', 'Demonstrates infostore auditing in machine-readable form to a CSV file')}
${printTask('awc-audit-file-example', 'Demonstrates infostore auditing in human-readable form to a file')}

The following tasks are available:
${printTask('generateMappingJar', 'Creates mapping classes and XSD files for the specified schema')}
${printTask('addDaodDataSource', 'Generates a fragment for data access on-demand, and updates topology.xml')}
${printTask('unpackXap', 'Extracts the assemblies from the existing Apollo.xap file to the extractedxap directory of the ui-subset-example directory')}
${printTask('packXap', 'Compiles the assemblies into the Apollo.xap file')}
${printTask('deploy', "Builds the sdk-projects and their dependencies into appropriate fragments and calls 'deployExample' on the toolkit")}

You may also run any toolkit commands specified in the setup script against this script. Those not mentioned in the list above will be called on the toolkit directly, with no intervention.

Examples of use:
build -pr da-arload-filesystem-example -t generateMappingJar -x C:\\IBM\\i2analyze\\SDK\\sdk-projects\\master\\build\\toolkit\\configuration\\examples\\schemas\\en_US\\law-enforcement-schema.xml -o C:\\IBM\\i2analyze\\SDK\\sdk-projects\\da-arload-filesystem-example\\schema-mapping-jar\\schema.jar
build -pr da-subset-documents-example -t addDaodDataSource
build -pr da-subset-rest-example -t unpackXap
build -pr da-subset-rest-example -t packXap
build  -t deploy
build -t startLiberty

"""
    def help =
"""This script configures the i2 Analyze Developer Essentials.
$usage"""

    OptionAccessor options = cli.parse(args)

    if (!options) {
        println help
        System.exit(0)
    }
    if (!options.help && !options.toolkithelp && !options.additional && !options.dahelp && options.task) {
        // Make sure the task name doesn't have whitespace, otherwise it won't work.
        String taskName = options.task.trim()

        if (devEssTasks.contains(taskName)) {
            if (!prNotRequiredDevEssTasks.contains(taskName) && !options.project) {
                println 'Error: Missing required option: -pr'
                System.exit(2)
            }
            
            def extraArgs = []
                    
                    if (options.schema) {
                        extraArgs << "-Pschema=$options.schema"
                    }
                    else {
                        if (taskName == GENERATE_MAPPING_JAR_TASK_NAME) {
                            println 'Error: Missing required option: -x'
                            System.exit(2)
                        }
                    }
            
            if (options.mappingJAR) {
                extraArgs << "-PmappingJAR=$options.mappingJAR"
            }
            else {
                if (taskName == GENERATE_MAPPING_JAR_TASK_NAME) {
                    println 'Error: Missing required option: -o'
                    System.exit(2)
                }
            }
            
            executeGradle(options, taskName, extraArgs)
        }
        else {
            // Remove arguments that the toolkit won't recognise
            ArrayList<String> argsList = args as ArrayList<String>
            if (argsList.contains('-pr')) {
                int prArgIndex = argsList.indexOf('-pr')
                argsList.remove(prArgIndex + 1)
                argsList.remove(prArgIndex)
            }
            executeToolkit(options, argsList)
        }
    }
    else {
        if (!options.help && !options.toolkithelp && !options.additional && !options.dahelp && !options.task) {
            println 'Error: Missing required option: -t\n'
            println usage
            System.exit(3)
        }
        else {
            if (options.help) {
                println help
            }

            def helpArgs = []
            if (options.toolkithelp) { helpArgs << '-h' }
            if (options.additional) { helpArgs << '-a' }
            if (options.dahelp) { helpArgs << '-dh' }
            if (helpArgs) {
                executeToolkit(options, helpArgs)
            }
        }
    }
}

String printOption(CliBuilder cli, String option) {
    def description = cli.options.getOption(option).getDescription()
    def optionLine = "${getPaddedShortOption(cli, option)} ${getPaddedLongOption(cli, option)}"
    description = wordWrap(description, optionLine, 38, 40)
    description
}

String printTask(String taskName, String description) {
    String paddedTaskName = "    $taskName "
    wordWrap(description, paddedTaskName, 38, 40)
}

String wordWrap(text, firstLine, colWidth, indent = 0) {
    def sb = new StringBuilder()
    def line = firstLine.padRight(indent)

    text.split(/\s/).each { word ->
        if (line.size() + word.size() > (colWidth + indent)) {
            sb.append(line).append('\n')
            line = ' ' * indent
        }
        line += " $word"
    }
    sb.append(line)
}

String getPaddedShortOption(CliBuilder cli, String option) {
    String shortOption = ''
    if (cli.options.getOption(option).getOpt()) {
        shortOption = '-' + cli.options.getOption(option).getOpt() + ','
    }
    padLeft(shortOption, 4)
}

String getPaddedLongOption(CliBuilder cli, String option) {
    def longOption = '--' + cli.options.getOption(option).getLongOpt()
    def optionalArgName = ''
    if (cli.options.getOption(option).hasArg()) {
        optionalArgName = ' <' + cli.options.getOption(option).getArgName() + '> '
    }
    padRight(longOption + optionalArgName, 35)
}

String padLeft(String s, int padding) {
    String.format('%1$' + padding + 's', s)
}

String padRight(String s, int padding) {
    String.format('%1$-' + padding + 's', s)
}

void executeToolkit(OptionAccessor options, ArrayList<String> args) {
    String[] command = []

    if (System.properties['os.name'].toLowerCase().contains('windows')) {
        command += ['cmd', '/c']
    }

    command += ['setup', '-f']
    command += args

    if (!new File(devToolkitDir).exists()) {
        executeGradle(options, STAGE_TOOLKIT_AND_COMMON_OVERRIDES_TASK_NAME)
    }

    ProcessBuilder processBuilder = new ProcessBuilder(command)
    processBuilder.directory(new File("$devToolkitDir/scripts"))
    execute(processBuilder)
}

void executeGradle(OptionAccessor options, String task, def extraArgs = []) {
    String[] command = []
    if (System.properties['os.name'].toLowerCase().contains('windows')) {
        command += ['cmd', '/c']
    }

    command += ["$gradlePath/gradle", '--no-daemon']
    if (options.server) { command += "-Pserver=$options.server" }
    String projectArg = options.project ? ":$options.project:" : ''
    command += ["-Ptask=$task", "$projectArg$task"]
    command += extraArgs

    if (options.p) {
        command += options.ps.collect { prop ->
            "-P$prop"
        }
    }

    if (options.info) { command += ['-i'] }
    if (options.debug) { command += ['-d'] }
    if (options.stacktrace) { command += ['-s'] }
    ProcessBuilder processBuilder = new ProcessBuilder(command)
    execute(processBuilder)
}

void execute(ProcessBuilder processBuilder) {
    processBuilder.redirectErrorStream(true)
    Process process = processBuilder.start()
    StreamGobbler outputGobbler = new StreamGobbler(process.getInputStream())

    outputGobbler.start()
    process.waitFor()
    // We need to capture the return code from gradle or it will be lost
    def exitValue = process.exitValue()
    if (exitValue != 0) {
        System.exit(exitValue)
    }
}

class NoOpUsageWriter extends PrintWriter {
    NoOpUsageWriter() {
        super(System.out)
    }

    @Override
    void print(String x) { }

    @Override
    void println() { }
}

class StreamGobbler extends Thread {
    InputStream is

    StreamGobbler(InputStream is) {
        this.is = is
        setDaemon(true)
    }

    void run() {
        try {
            InputStreamReader isr = new InputStreamReader(is)
            BufferedReader br = new BufferedReader(isr)
            String line = null

            while ((line = br.readLine()) != null) {
                println line
            }
        }
        catch (IOException ioe) {
            // Nothing we can do
        }
    }
}

String normalisedPath(String path) {
    new File(path).toPath().normalize().toFile().getCanonicalPath()
}

currentLocationDir = new File(getClass().protectionDomain.codeSource.location.path).parent
devToolkitDir = "$currentLocationDir/build/toolkit"
gradlePath = normalisedPath("$currentLocationDir/../../../toolkit/tools/gradle/bin")
executeCommand(args)
