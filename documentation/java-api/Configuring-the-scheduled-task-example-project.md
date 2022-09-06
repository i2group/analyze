# Configuring the 'scheduled task' example project

In i2 Analyze, you can develop custom 'tasks' that the server executes on a user-defined schedule. A task is simply a Java class that implements a specific interface and performs a custom action when invoked.

## Before you begin

You must have a development deployment of i2 Analyze and a configured development environment. For more information, see [Preparing to use i2 Analyze Developer Essentials](Preparing-to-use-i2-Analyze-Developer-Essentials.md).

## About this task

When you build the scheduled task example, you create a JAR file that contains an implementation of the [`IScheduledTask` interface](https://i2group.github.io/analyze/docs/com/i2group/disco/task/spi/IScheduledTask.html). To enable the custom task, you must redeploy i2 Analyze to include the new JAR file, and configure the platform to use the class that the JAR file contains.

## Procedure

The `opal-scheduled-task-example` project contains the source code for this example. You must add this project to your development environment. If you are using Eclipse, complete the following instructions to import the project.

1. In Eclipse, click **File** &gt; **Import** to open the Import window.

1. In the Import window, click **General** &gt; **Existing Projects into Workspace**, and then click **Next**.

1. Click **Browse** at the top of the window, and then select the `SDK\java-api-projects\opal-scheduled-task-example` directory.

1. Click **Finish** to complete the import process.

After you add the project to your development environment, you can inspect its behavior.

The example task deletes charts that have not been accessed in the last five minutes. (In this context, "accessed" means being uploaded or downloaded, or having its version history requested by a user.)

1. To be able to test the implementation, you must build a JAR file of the project and export it into your i2 Analyze toolkit. If you are using Eclipse, complete the following instructions.

   1. In Eclipse, double-click the `project.jardesc` file in the `opal-scheduled-task-example` project to open the Export window with default settings for the project already applied.

   1. In the Export window, change the **Export destination** to `toolkit\configuration\fragments\opal-scheduled-task-example\WEB-INF\lib\opal-scheduled-task-example.jar`.

      If the directory does not exist, a message is displayed. If you click **Yes**, the directory structure is created for you.

   1. Click **Finish** to export the JAR file to the specified destination.

   If you are not using Eclipse, you must build the JAR file according to your development environment.

   The built JAR file must comprise the contents of the `src/main/java` directories of the `opal-scheduled-task-example` project. It must be named `opal-scheduled-task-example.jar`, and it must be in the `toolkit\configuration\fragments\opal-scheduled-task-example\WEB-INF\lib` directory.

1. For your deployment to use the exported `opal-scheduled-task-example.jar` file, you must add a fragment to the `topology.xml` file.

   1. In an XML editor, open the `toolkit\configuration\environment\topology.xml` file.

   1. Edit the `<war>` element that defines the contents of the `opal-services-is` web application to include the `opal-scheduled-task-example` fragment.

      ```xml
      <war context-root="opal" i2-data-source-id="infostore"
           name="opal-services-is" target="opal-services-is">
         <data-sources>
            <data-source database-id="infostore"
                         create-database="true"/>
         </data-sources>
         <file-store-ids>
            <file-store-id value="chart-store"/>
            <file-store-id value="job-store"/>
            <file-store-id value="recordgroup-store"/>
         </file-store-ids>
         <fragments>
            <fragment name="opal-services-is"/>
            <fragment name="opal-services"/>
            <fragment name="common"/>
            <fragment name="default-user-profile-provider"/>
            <fragment name="opal-scheduled-task-example"/>
         </fragments>
         <solr-collection-ids>
            <solr-collection-id collection-id="main_index"
                                data-dir="C:/i2/i2analyze/data/solr"
                                cluster-id="is_cluster"/>
         </solr-collection-ids>
      </war>
      ```

In order for the i2 Analyze server to discover the custom task, entries that identify it must be present in one of the settings files. A typical location for these entries is the `toolkit/configuration/fragments/opal-services/WEB-INF/classes/DiscoServerSettingsCommon.properties` file.

The new entries have two parts, in the following format:

```text
CustomTaskScheduler.<TaskName>.Class=<ImplementationClass>
CustomTaskScheduler.<TaskName>.Expression=<CronExpression>
```

Here, `<TaskName>` is a name that identifies a particular task instance; `<ImplementationClass>` is the fully qualified class name for the implementation of that task; and `<CronExpression>` is a cron schedule expression for when the task should run.

The format of the cron expression is:

`<minute> <hour> <day-of-month> <month> <day-of-week>`

For example, a cron expression of `*/5 * * * *` results in a task running every five minutes. See the [UNIX cron format documentation](https://www.ibm.com/docs/en/db2/11.1?topic=task-unix-cron-format) for more information.

1. In a text editor, open the `configuration\fragments\opal-services\WEB-INF\classes\DiscoServerSettingsCommon.properties` file. Add the following properties to use the new class as the `IScheduledTask` implementation in this example project, and to set the interval of the scheduler.

   ```text
   CustomTaskScheduler.DeleteUnusedCharts.Class=com.example.task.ExampleTask
   CustomTaskScheduler.DeleteUnusedCharts.Expression=*/5 * * * *
   ```

1. Navigate to the `toolkit\scripts` directory and run the following command to redeploy i2 Analyze with all the changes that you made:

   ```sh
   setup -t deploy
   ```

1. Run the following command to start the i2 Analyze server:

   ```sh
   setup -t start
   ```

   This action starts the server and the custom task scheduler. The scheduler log stores information about the state of all configured tasks, including when a task was last run, when it will next run, whether it has failed, and so on. You can find the scheduler log in the `i2_Scheduler.log` file, inside the i2 Analyze server.

1. Test the example by logging into Analyst's Notebook Premium, uploading a chart to the Chart Store, and then closing the chart. Leave the chart untouched for five minutes, and then attempt to reload it.

   The attempt will fail, because the scheduled task deleted the "unused" chart.

1. Debug the `opal-scheduled-task-example` project by using Eclipse and your deployment of i2 Analyze. For more information about debugging the example, see [Debugging Developer Essentials in Eclipse](Debugging-Developer-Essentials.md).

After you develop and test a scheduled task solution for i2 Analyze, the next step is to publish it to a live deployment. Complete the following steps on your live deployment of i2 Analyze.

1. Copy the `toolkit\configuration\fragments\opal-scheduled-task-example` fragment directory from your development deployment to your live deployment.

1. Update the `topology.xml` and `DiscoServerSettingsCommon.properties` files with the same changes that you made to your development environment.

1. Ensure that the `CustomTaskScheduler` cron expression in the `DiscoServerSettingsCommon.properties` file is suitable for your live deployment.

1. Redeploy the live i2 Analyze system.

## Results

The result of following all of these steps is that your development and production deployments of i2 Analyze contain the same implementation of the scheduled task. Every five minutes, the scheduler will execute the custom task to delete any chart that has not been accessed in the last five minutes.
