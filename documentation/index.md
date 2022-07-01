# i2 Analyze Developer Essentials

i2 Analyze Developer Essentials contains tools, libraries, and examples that enable development and deployment of custom extensions to i2 Analyze. Developer Essentials also includes API documentation and guides to deploying the software and the example projects.

The example projects extend deployments of i2 Analyze. The names of the example projects indicate which deployment type they apply to.

The Developer Essentials examples are supplied in the form of Eclipse projects. You can use other editors to interact with the examples, but the instructions here assume that you are using a version of Eclipse IDE for Java™ EE Developers that supports Java 11.

Most of the examples culminate in building a JAR file and copying it to the `toolkit` directory of an i2 Analyze deployment. After you modify the configuration, you redeploy i2 Analyze to add the example to the system. Other examples run separately from i2 Analyze as clients.

Important: When you develop a custom extension, use it in a test deployment of i2 Analyze first. After you are satisfied that the extension is complete, you can move it from the test deployment to your production environment.

- **[Preparing to use i2 Analyze Developer Essentials](Preparing-to-use-i2-Analyze-Developer-Essentials.md)**

  i2 Analyze Developer Essentials is a set of files and example projects that build on a standard i2 Analyze deployment. Preparing to use Developer Essentials involves installing and configuring it to work in a dedicated test environment.

- **[Configuring the 'group-based default security dimension values' example project](Configuring-the-group-based-default-security-dimension-values-example-project.md)**

  In i2 Analyze, you can specify the security dimension values that records in the Information Store receive when users create them in Analyst's Notebook Premium. By default, every record is given the security dimension values. In Developer Essentials, i2 provides the `opal-default-security-example` example, which gives records different security dimension values depending on the user groups that their creator is a member of.

- **[Configuring the 'audit logging to file' example project](Configuring-the-audit-logging-to-file-example-project.md)**

  In i2 Analyze, you can use audit logging to record information about user activity in the Information Store. To do so, you must write and configure an implementation of the `IAuditLogger` interface. In Developer Essentials, i2 provides the `opal-audit-file-example` example, which writes audit logging information to a log file.

- **[Configuring the 'audit logging to CSV' example project](Configuring-the-audit-logging-to-CSV-example-project.md)**

  In i2 Analyze, you can use audit logging to record information about user activity in the Information Store. To do so, you must write and configure an implementation of the `IAuditLogger` interface. In Developer Essentials, i2 provides the `opal-audit-csv-example` example, which writes audit logging information to a CSV file.

- **[Configuring the 'audit logging to database' example project](Configuring-the-audit-logging-to-database-example-project.md)**

  In i2 Analyze, you can use audit logging to record information about user activity in the Information Store. To do so, you must write and configure an implementation of the `IAuditLogger` interface. In Developer Essentials, i2 provides the `opal-audit-database-example` example, which writes audit logging information to a relational database.

- **[Configuring the 'scheduled task' example project](Configuring-the-scheduled-task-example-project.md)**

  In i2 Analyze, you can develop custom 'tasks' that the server executes on a user-defined schedule. A task is simply a Java class that implements a specific interface and performs a custom action when invoked.

- **[Debugging Developer Essentials](Debugging-Developer-Essentials.md)**

  After you deploy your code to your development deployment of i2 Analyze, you can debug your code. To debug your code, you must configure your IDE to connect to your development deployment.

- **[Troubleshooting the example projects](Troubleshooting-the-example-projects.md)**

  The example projects in i2 Analyze Developer Essentials are sensitive to changes or inconsistencies in the configuration of the development environment. The errors that you see can be cryptic, but there are standard approaches to resolving the problems that they identify.
  