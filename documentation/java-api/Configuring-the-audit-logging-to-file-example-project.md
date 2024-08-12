# Configuring the 'audit logging to file' example project

In i2 Analyze, you can use audit logging to record information about user activity in the Information Store. To do so, you must write and configure an implementation of the `IAuditLogger` interface. In Developer Essentials, i2 provides the `opal-audit-file-example` example, which writes audit logging information to a log file.

## Before you begin

You must have a development deployment of i2 Analyze and a configured development environment. For more information, see [Preparing to use i2 Analyze Developer Essentials](Preparing-to-use-i2-Analyze-Developer-Essentials.md).

## About this task

When you build the audit logging to file example, you create a JAR file that contains an implementation of the `IAuditLogger` interface. To enable audit functionality, you must redeploy i2 Analyze to include the new JAR file, and configure the platform to use the class that the JAR file contains.

## Procedure

The audit logging examples in Developer Essentials require the contents of the `opal-audit-example-common` project, which you must add to your development environment. You can import this project into Eclipse by completing the following instructions.

1.  In Eclipse, click **File** &gt; **Import** to open the Import window.

2.  In the Import window, click **General** &gt; **Existing Projects into Workspace**, and then click **Next**.

3.  Click **Browse** at the top of the window, and then select the `C:\i2\i2analyze\SDK\java-api-projects\opal-audit-example-common` directory.

4.  Click **Finish** to complete the import process.

5.  The audit logging to file example requires the `opal-audit-file-example` project, which contains the source code that is specific to this example. You must add this project to your development environment. If you are using Eclipse, you can repeat the previous steps to import the `SDK\java-api-projects\opal-audit-file-example` project.

After you add the project to your development environment, you can inspect the behavior of the example.

1.  To be able to test the implementation, you must build a JAR file of the project and export it into your i2 Analyze toolkit. If you are using Eclipse, complete the following instructions.

    1.  In Eclipse, double-click the `project.jardesc` file in the `opal-audit-file-example` project to open the Export window with default settings for the project applied.

    2.  In the Export window, change the **Export destination** to `C:\i2\i2analyze\toolkit\configuration\fragments\opal-audit-file-example\WEB-INF\lib\opal-audit-file-example.jar`.
    3.  Click **Finish** to export the JAR file to the specified destination.

        If the directory that you specified does not exist, a message is displayed. If you click **Yes**, the directory structure is created for you.

    If you are not using Eclipse, you must build the JAR file according to your development environment.

    The built JAR file must comprise the contents of the `src/main/java` directories of the `opal-audit-file-example` and `opal-audit-example-common` projects. Your JAR file must be named `opal-audit-file-example.jar` and be in the `toolkit\configuration\fragments\opal-audit-file-example\WEB-INF\lib` directory.

2.  For your deployment to use the exported `opal-audit-file-example.jar` file, you must add the `opal-audit-file-example` fragment to the `topology.xml` file.

    1.  In an XML editor, open the `toolkit\configuration\environment\topology.xml` file.

    2.  Edit the `<war>` element that defines the contents of the `opal-services-is` web application to include the `opal-audit-file-example` fragment.

            <war context-root="opal" i2-data-source-id="infostore"
                 name="opal-services-is" target="opal-services-is">
               <data-sources>
                  <data-source database-id="infostore" create-database="true"/>
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
                  <fragment name="opal-audit-file-example"/>
               </fragments>
               <solr-collection-ids>
                  <solr-collection-id collection-id="main_index"
                                      data-dir="C:/i2/i2analyze/data/solr"
                                      cluster-id="is_cluster"/>
               </solr-collection-ids>
            </war>

3.  Copy the contents of the `SDK\java-api-projects\opal-audit-file-example\fragment` directory to the `toolkit\configuration\fragments\opal-audit-file-example` directory. Overwrite any old files with the new versions.

4.  In a text editor, open the `configuration\fragments\common\WEB-INF\classes\ApolloServerSettingsMandatory.properties` file. Change the value of the `AuditLogger` property to use the new class as the `IAuditLogger` implementation in this example project.

        AuditLogger=com.example.audit.FileAuditLogger

5.  Navigate to the `toolkit\scripts` directory and run the following command to redeploy i2 Analyze with all the changes that you made:

        setup -t deploy

6.  Run the following command to start the i2 Analyze server:

        setup -t start

    This action starts the server with audit logging to file enabled. The logging behavior is governed by the settings file at `opal-audit-file-example\fragment\WEB-INF\classes\FileAudit.properties`, which controls where the log files are saved and how large they can be. Typical messages look like this one:

        2016-10-03T11:21:27.232Z. User: Analyst, IP: 0:0:0:0:0:0:0:1,
        UserAgent: Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko)
        Chrome/53.0.2785.116 Safari/537.36, Groups: [Analyst, Security Controller, Controlled],
        Operation: QuickSearch, DataStores: [InfoStore], Expression: green, Filters: []

    The supplied version of `FileAudit.properties` specifies the `wlp\usr\servers\opal-server\logs\opal-services` directory as the target for audit log files. If you are using analyze-deployment-tooling for your deployment of i2 Analyze, update the path to `/logs`. The files are named `auditn.log`, where n is an integer that increments with each new file.

7.  Debug the `opal-audit-file-example` project by using Eclipse and your deployment of i2 Analyze. For more information about debugging the example, see [Debugging Developer Essentials in Eclipse](Debugging-Developer-Essentials.md).

After you develop and test an audit logging solution for i2 Analyze, the next step is to publish it to a live deployment. Complete the following steps on your live deployment of i2 Analyze.

1.  Copy the `toolkit\configuration\fragments\opal-audit-file-example` fragment directory from your development deployment to your live deployment.

2.  Update the `topology.xml` and `ApolloServerSettingsMandatory.properties` files with the same changes that you made to your development environment.

3.  Ensure that the settings in the `FileAudit.properties` file are suitable for your live deployment.

4.  Redeploy the live i2 Analyze system.

## Results

The result of following all of these steps is that your development and production deployments of i2 Analyze contain the same implementation of audit logging to file. When users interact with the Information Store, the platform writes data about those interactions to the log files that you specified.