# Generating client code from the API specification

i2 provides the i2 Analyze REST API as an [OpenAPI version 3](https://github.com/OAI/OpenAPI-Specification/blob/main/versions/3.0.0.md) specification document, in JSON format. You can view the documentation for the REST API and obtain the specification document from the [i2 Analyze product documentation](https://docs.i2group.com/analyze/public-rest-api.html).

You do not have to use direct HTTP requests to take advantage of the REST API. Many third-party tools exist that can generate client code from OpenAPI specifications, in a variety of languages. You can build applications that use the generated code to interact with an i2 Analyze server.

This topic describes how to use one such tool - [OpenAPI Generator](https://openapi-generator.tech/) - to generate a skeleton Java project that includes client code for interacting with the REST API.

**Note:** The third-party tools that generate client code have varying levels of support for the OpenAPI 3.0 specification and for different languages. When you create your own projects, you might need to make small changes to the generated code so that it works correctly with the i2 Analyze REST API.

## Prerequisites

Before you start, ensure that you have followed the instructions in [Setting up a development environment](setting-up-dev-environment.md) to create an environment in which you can write and test your code.

## Generating the Java client code

To generate the Java client code and the skeleton project that uses it, follow these steps:

1. If you have not already done so, download this repository to the same workstation that hosts the development environment.

1. The `developer-essentials/SDK/rest-api-projects` directory of the repository contains three folders:
   ```
   - rest-api-projects
       - code-generator
       - generated-projects
       - sample-code
   ```

   Open a command line and navigate to the `code-generator` folder.

1. Run the following command:

   ```
   gradlew createJavaClientProject
   ```

   The supplied `createJavaClientProject` task inherits from `org.openapitools.generator.gradle.plugin.tasks.GenerateTask`.
   (For more details, see the [OpenAPI Generator documentation](https://github.com/OpenAPITools/openapi-generator/tree/master/modules/openapi-generator-gradle-plugin).)

   The task first downloads the specification document, and then uses the `apache-httpclient` template creator to generate the Java client code.
   If the command was successful, `Successfully generated code` is displayed on the command line, and the Java client project is generated in the `generated-projects` folder.

1. Navigate to the `generated-projects/java-client` folder and check the contents are similar to this list:

   ```
   - rest-api-projects
       - generated-projects
           - java-client
               - api
               - docs
               - gradle
               - src
               - build.gradle
               - build.sbt
               - gradle.properties
               - gradlew
               - gradlew.bat
               - pom.xml
               - README.md
               - settings.gradle
   ```

   The Java projects that OpenAPI Generator creates contain dependencies that can be resolved using either the [Gradle](https://gradle.org/) or [Maven](https://maven.apache.org/) build tools, which you can also use to build the project.

   `build.gradle`, `gradle.properties` and `settings.gradle` define the Gradle project, while `pom.xml` defines the Maven project.

   The other contents of the `java-client` folder are as follows:

   - The `src` directory contains the Java source files.
   - The `api` directory contains a YAML-formatted copy of the i2 Analyze REST API specification.
   - The `docs` directory contains Markdown-formatted documentation for the generated Java classes, which the code generator derives from the API specification.
   - The `gradle` directory and `gradlew` files are the "gradle wrapper", which can fetch a copy of Gradle to build the project with.
   
   **Note:** The artifacts generated here are specific to the Java client generator, but the generator produces similar project structures for other languages, according to their paradigms.
   For example, a TypeScript project will have a `package.json` file to manage dependencies.
     
We can take a closer look at the generated code when we open the project in an Integrated Development Environment (IDE).

## Opening the skeleton project

To work with the generated project, use an IDE that can resolve the project's dependencies.
This walkthrough assumes that you're using Microsoft Visual Studio Code ("VS Code"), which is a prerequisite of the i2 Analyze Containers deployment that you already configured.

**Note:** If you need to download VS Code again, you can do so from [https://code.visualstudio.com](https://code.visualstudio.com).

1. Start VS Code, and from the **File** menu, select **Open Folder**.

1. Select the `java-client` folder that you generated above.
   The following prompt appears:

   ```
   Build tool conflicts are detected in workspace. Which one would you like to use?
   ```
   
1. Select the option to use Gradle to resolve the project dependencies, and install any extensions that VS Code recommends.

## Exploring the generated code

In VS Code you can take a closer look at the generated project.
The code itself is generated in the `src` directory under `main/java/com/example/generated`.
There are three packages:

  - `api` contains classes that can make requests to the server, corresponding to the `operations` in the OpenAPI specification.
  - `invoker` contains supporting classes for executing those requests.
  - `model` contains transport classes that correspond to the request and response bodies defined in the `components` section of the OpenAPI specification.

For example, `com/example/generated/api/SearchApi.java` contains client code for making requests to the geospatial search and text search endpoints, `com/example/generated/invoker/ApiClient.java` contains common code that all the API classes use to execute a request, and `com/example/generated/model/TextSearchRequest.java` models the body of a request to the text search endpoint.

We will explore many of these classes in more detail as you progress through the walkthrough, which you're now ready to follow.
The next step is to extend the skeleton project and [write your own code that calls the generated Java client code](making-a-request.md).
