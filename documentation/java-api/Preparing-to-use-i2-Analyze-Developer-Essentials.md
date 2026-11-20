# Preparing to use Developer Essentials to extend i2 Analyze

i2 Analyze Developer Essentials provides a set of files and example projects that build on a standard i2 Analyze deployment. Preparing to use Developer Essentials to extend i2 Analyze involves installing and configuring it to work in a dedicated test environment.

## Before you begin

You must have a deployment of i2 Analyze that you can use to develop and test the example projects. If you do not already have a deployment of i2 Analyze, you can create a deployment to use.

- You can use the containerized version of i2 Analyze by using the documentation and scripts in [analyze-deployment-tooling](https://i2group.github.io/analyze-deployment-tooling/). After you have created the deployment, follow the instructions in [Developing and deploying extensions](https://i2group.github.io/analyze-deployment-tooling/content/develop_extensions.html). You do not need to complete the rest of the instructions on this page.
- You can use a non-containerised version of i2 Analyze using the deployment toolkit. For more information about creating a deployment, see <a href="https://docs.i2group.com/analyze/analyze_deployment.html" class="xref" title="(Opens in a new tab or window)">Deploying i2 Analyze</a>.

## About this task

The following procedure explains how to link the examples in Developer Essentials to the i2 Analyze libraries, how to modify the i2 Analyze user registry, how to set the `TOOLKIT_ROOT` environment variable or Gradle property, and how to set up Java.

## Procedure

1.  Extract i2 Analyze Developer Essentials, and copy the `SDK` directory into the `C:\i2\i2analyze\` directory of your development deployment.

2.  Copy the contents of the `SDK\wlp-overrides` directory to the `i2analyze\deploy\wlp` directory and accept any overwrites.
    This action copies a modified user registry into the `wlp` directory for i2 Analyze to use. The registry contains more users and groups that help to demonstrate the functions of some of the examples.

3.  The example projects are Gradle projects, so you can use any IDE that supports Gradle, such as IntelliJ IDEA, Visual Studio Code, or Eclipse. To access the i2 Analyze libraries, the examples read a `TOOLKIT_ROOT` value that points to the `toolkit` directory of your deployment. Set `TOOLKIT_ROOT` in either of the following ways:
    -   Set an environment variable named `TOOLKIT_ROOT` with the value `C:\i2\i2analyze\toolkit`.
    -   Pass it to Gradle as a property, for example: `.\gradlew build -PTOOLKIT_ROOT=C:\i2\i2analyze\toolkit`.

    If neither is set, Gradle stops with an error that tells you `TOOLKIT_ROOT` is missing.

4.  Make sure that Gradle and your IDE use Java 21. From i2 Analyze version 4.4.8, Java 21 is required, both to run the examples and to compile them against the i2 Analyze libraries. The i2 Analyze application uses OpenJDK, and the deployment toolkit installs a Java 21 OpenJDK at `C:\i2\i2analyze\deploy\java`. If the JDK that is installed by the toolkit is not the default JDK in your environment, configure your IDE or Gradle to use it. For example, set the `JAVA_HOME` environment variable to `C:\i2\i2analyze\deploy\java`, or select that directory as the Gradle JVM in your IDE settings.

5.  Import the examples into your IDE as a Gradle project. In your IDE, open or import the `C:\i2\i2analyze\SDK\java-api-projects` directory. The IDE reads the `build.gradle` files in each example and configures the source folders and i2 Analyze libraries for you.

    If you start your IDE from the command line or a launcher, make sure that it can see the `TOOLKIT_ROOT` environment variable, or configure the property in the Gradle settings of your IDE.

    The `java-api-projects` directory contains a Gradle wrapper (`gradlew` and `gradlew.bat`), so you do not need to install Gradle separately. The commands in this documentation use `.\gradlew`, which works in PowerShell. In Command Prompt, you can run `gradlew` instead.

6.  Optionally, attach the i2 Analyze API documentation to the libraries in your IDE. The Javadoc is provided as a `.zip` file in the `C:\i2\i2analyze\SDK\java-api-projects\javadoc` directory.
