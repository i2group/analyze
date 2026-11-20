# Troubleshooting the example projects

The example projects in i2 Analyze Developer Essentials are sensitive to changes or inconsistencies in the configuration of the development environment. The errors that you see can be cryptic, but there are standard approaches to resolving the problems that they identify.

## There are compilation errors in your development environment after importing an example project

For an unmodified example project, compilation errors usually occur when the `TOOLKIT_ROOT` variable is not set, or when it is set incorrectly.

To check `TOOLKIT_ROOT`, run `.\gradlew projects` in the `SDK\java-api-projects` directory.

-   If Gradle stops with an error that `TOOLKIT_ROOT` is not set, set it as an environment variable or pass it with `-PTOOLKIT_ROOT=<path>`.

-   If Gradle runs but the source code cannot find i2 Analyze classes, `TOOLKIT_ROOT` is set to the wrong directory. Ensure that it is the path of the deployment toolkit directory, and that `application\shared\lib` inside it contains JAR files.

-   If the command line works but your IDE reports errors, the IDE might not see the `TOOLKIT_ROOT` environment variable. Restart the IDE from a terminal where the variable is set, or set the property in the Gradle settings of your IDE, and then refresh the Gradle project.

## Gradle reports that it cannot find a Java installation for languageVersion=21

The example projects require Java 21. If Gradle runs on an older JDK and cannot find a Java 21 installation, it stops with an error that starts `Cannot find a Java installation on your machine` and mentions `languageVersion=21`.

To resolve the error, point Gradle at the Java 21 OpenJDK that the deployment toolkit installs. Set the `JAVA_HOME` environment variable to `C:\i2\i2analyze\deploy\java`, or select that directory as the Gradle JVM in your IDE settings, and then run the command again.
