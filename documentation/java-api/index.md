# Extending i2 Analyze

This part of i2 Analyze Developer Essentials demonstrates how to change some of the default behavior of i2 Analyze.
To achieve these changes, you write Java classes that implement well-defined server extension points.

Most of the examples culminate in building a JAR file and copying it to the `toolkit` directory of an i2 Analyze deployment.
After you modify the configuration, you redeploy i2 Analyze to add the example to the system.

**Note:** The Developer Essentials examples are supplied in the form of Eclipse projects.
You can use other editors to interact with the examples, but the instructions here assume that you are using a version of Eclipse IDE for Java™ EE Developers that supports Java 11.

- **[Preparing to use Developer Essentials to extend i2 Analyze](Preparing-to-use-i2-Analyze-Developer-Essentials.md)**

  i2 Analyze Developer Essentials provides a set of files and example projects that build on a standard i2 Analyze deployment.
  Preparing to use Developer Essentials to extend i2 Analyze involves installing and configuring it to work in a dedicated test environment.

- **[Configuring the 'group-based default security dimension values' example project](Configuring-the-group-based-default-security-dimension-values-example-project.md)**

  In i2 Analyze, you can specify the security dimension values that records in the Information Store receive when users create them in Analyst's Notebook Premium.
  By default, every record is given the same security dimension values.
  In Developer Essentials, i2 provides the `opal-default-security-example` example, which gives records different security dimension values depending on the user groups that their creator is a member of.

- **[Configuring the 'audit logging to file' example project](Configuring-the-audit-logging-to-file-example-project.md)**

  In i2 Analyze, you can use audit logging to record information about user activity in the Information Store.
  To do so, you must write and configure an implementation of the `IAuditLogger` interface.
  In Developer Essentials, i2 provides the `opal-audit-file-example` example, which writes audit logging information to a log file.

- **[Configuring the 'audit logging to CSV' example project](Configuring-the-audit-logging-to-CSV-example-project.md)**

  In i2 Analyze, you can use audit logging to record information about user activity in the Information Store.
  To do so, you must write and configure an implementation of the `IAuditLogger` interface.
  In Developer Essentials, i2 provides the `opal-audit-csv-example` example, which writes audit logging information to a CSV file.

- **[Configuring the 'audit logging to database' example project](Configuring-the-audit-logging-to-database-example-project.md)**

  In i2 Analyze, you can use audit logging to record information about user activity in the Information Store.
  To do so, you must write and configure an implementation of the `IAuditLogger` interface.
  In Developer Essentials, i2 provides the `opal-audit-database-example` example, which writes audit logging information to a relational database.

- **[Configuring the 'scheduled task' example project](Configuring-the-scheduled-task-example-project.md)**

  In i2 Analyze, you can develop [custom 'tasks'](https://docs.i2group.com/analyze/custom-tasks.html) that the server executes on a user-defined schedule.
  A task is simply a Java class that implements a specific interface and performs a custom action when invoked.
  In Developer Essentials, i2 provides the `opal-scheduled-task-example` example, 
  which runs a task every five minutes to delete charts that have not recently been accessed.

- **[Configuring the 'alerting' example project](Configuring-the-alerting-example-project.md)**

  In i2 Analyze, you can send alerts to groups of users that you define.
  For example, you might send the same message to all users, or tell a particular set of users about changes to a set of records.
  In Developer Essentials, i2 provides the `opal-alerting-example` example, which builds on the scheduled task example to alert users when charts that they created are automatically deleted.

- **[Debugging Developer Essentials](Debugging-Developer-Essentials.md)**

  After you deploy your code to your development deployment of i2 Analyze, you can debug your code.
  To debug your code, you must configure your IDE to connect to your development deployment.

- **[Troubleshooting the example projects](Troubleshooting-the-example-projects.md)**

  The example projects in i2 Analyze Developer Essentials are sensitive to changes or inconsistencies in the configuration of the development environment.
  The errors that you see can be cryptic, but there are standard approaches to resolving the problems that they identify.

**Important:** When you develop a custom extension, use it in a test deployment of i2 Analyze first.
After you are satisfied that the extension is complete, you can move it from the test deployment to your production environment.
