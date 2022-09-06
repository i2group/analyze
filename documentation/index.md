# i2 Analyze Developer Essentials

i2 Analyze Developer Essentials contains tools, libraries, and examples that enable you to extend and modify the behavior of an i2 Analyze deployment, to issue commands to it, and to retrieve information about it.
Developer Essentials also includes Java API documentation and guides to deploying the software and the example projects.

An i2 Analyze server exposes a [Java API](https://i2group.github.io/analyze/docs/index.html?overview-summary.html) and a [REST API](https://docs.i2group.com/analyze/public-rest-api.html#overview) that provide separate, complementary functionality.
Depending on your goals, the code that you write will generally target one API or the other. 

- **To develop custom extensions for i2 Analyze**, use the [Java API](https://i2group.github.io/analyze/docs/index.html?overview-summary.html).
  In particular, you can write extensions that change the default behavior of i2 Analyze with regard to event auditing and record security settings.
  You can also develop tasks for the server to execute on a user-defined schedule.

  For [projects that target the Java API](java-api/index.md), Developer Essentials contains documentation that describes how to set up a development environment and explains the configuration and operation of a number of examples.

- **To interact with or fetch data from an i2 Analyze server**, or to retrieve the current schemas and server settings, use the [REST API](https://docs.i2group.com/analyze/public-rest-api.html#overview).
For example, you can search the Information Store and retrieve results, or you might send an alert to a particular group of i2 Analyze users.

  For [projects that target the REST API](rest-api/index.md), Developer Essentials explains how to generate client code from the API specification, and includes examples of calling the API to achieve a variety of common tasks.
