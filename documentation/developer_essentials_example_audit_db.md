Configuring the "audit logging to database" example project
===========================================================

In i2 Analyze, you can use audit logging to record information about user activity in the Information Store. To do so, you must write and configure an implementation of the `IAuditLogger` interface. In Developer Essentials, IBM provides the `awc-audit-database-example` example, which writes audit logging information to a relational database.

Before you begin
----------------

The audit logging to database example project requires the development version of i2 Analyze, prepared according to the Developer Essentials deployment instructions. However, the example has no additional requirements.

About this task
---------------

When you build the audit logging to database example, you create a JAR file that contains an implementation of the `IAuditLogger` interface. To enable audit functionality, you must add the JAR file to an existing i2 Analyze deployment, and then configure the platform to use the class that the JAR file contains.

Because this example involves writing information to a database, you must also add information about that database to the platform configuration.

Procedure
---------

The examples in Developer Essentials require links to the i2 Analyze libraries, and to the API documentation. These links are configured in a separate project directory that you must import into Eclipse before you can build any of the example projects.

1.  If you did not already add the `master` directory and the related artifacts to your Eclipse workspace while you worked with one of the other examples, add it now.
    1.  In Eclipse, click **Window** &gt; **Preferences** to open the Preferences window.
    2.  In the Preferences window, select **General** &gt; **Workspace** &gt; **Linked Resources**.
    3.  Add an entry to the **Defined path variables** list:

        -   Name: `TOOLKIT_ROOT`
        -   Location: `C:\IBM\i2analyze\SDK\sdk-projects\master\build\toolkit`

        The shared libraries and several of the example projects rely on the presence of `TOOLKIT_ROOT` in your development environment.

    4.  In Eclipse, click **File** &gt; **Import** to open the Import window.
    5.  In the Import window, click **General** &gt; **Existing Projects into Workspace**, and then click **Next**.
    6.  Click **Browse** at the top of the window, and then select the `C:\IBM\i2analyze\SDK\sdk-projects\master` directory.
    7.  Click **Finish** to complete the import process.

Developing and testing implementations of `IAuditLogger` requires a development version of the `awc` server. Developer Essentials includes a project that you can import into to Eclipse and configure to be that server.

1.  If you did not already add the `awc` directory to your Eclipse workspace while you worked with one of the other audit logging examples, add it now.
    1.  In your Eclipse workspace, stop any running servers.
    2.  In Windows Explorer, navigate to the `C:\IBM\i2analyze\deploy-dev\wlp\usr\servers\awc\apps` directory. Delete the `awc.war` subdirectory, which contains the `awc` application.
    3.  Back in Eclipse, refresh your view of the Enterprise Explorer tab.
    4.  Repeat the instructions that you followed when you added the `master` directory to your Eclipse workspace. This time, add the `C:\IBM\i2analyze\SDK\sdk-projects\awc` directory to Eclipse.
    5.  In the **Servers** tab at the bottom of the Eclipse application window, right-click the **awc** server and select **Add and Remove**.
    6.  In the Add and Remove window, move **awc** from the **Available** list to the **Configured** list, and then click **Finish** to close the window.
    7.  Verify that you can still start both servers without seeing any new errors in the Console tab in Eclipse.
        Note: If you restart the **awc** server while a client is connected to it, you might see an error in the **Console** tab that reports an unavailable server. If the server then initializes normally, you can safely disregard this report.

The audit logging examples in Developer Essentials all require the contents of the `awc-audit-example-common` project.

1.  If you have not already done so, repeat the instructions that you followed when you added the `master` directory to your Eclipse workspace. This time, add the `C:\IBM\i2analyze\SDK\sdk-projects\awc-audit-example-common` directory to Eclipse.

Additionally, the audit logging to database example requires the `awc-audit-database-example` project, which contains the source code that is specific to this example.

1.  Repeat the instructions again to import `C:\IBM\i2analyze\SDK\sdk-projects\awc-audit-database-example` into Eclipse.
2.  In the `awc` project directory, navigate to `fragment\WEB-INF\classes\DiscoServerSettingsMandatory.properties`. Uncomment the class for the `IAuditLogger` implementation in this example project, and comment out the other settings.

    ``` pre
    #AuditLogger=com.example.audit.CSVAuditLogger
    AuditLogger=com.example.audit.DatabaseAuditLogger
    #AuditLogger=com.example.audit.FileAuditLogger
    #AuditLogger=com.i2group.disco.audit.NoOpAuditLogger
    ```

3.  Add the JAR files that the common and specific projects generate to the deployment assembly for the `awc` project.
    1.  In Enterprise Explorer, right-click **awc**, and select **Properties** to display the Properties window.
    2.  Click **Deployment Assembly**, and then **Add**. In the New Assembly Directive window, select **Project**, and then click **Next**.
    3.  Select both **awc-audit-example-common** and **awc-audit-database-example**, and then click **Finish**.

Before you can run the audit logging to database example, you must configure and then create the database that the example uses to record the logged information.

1.  Open the topology file that represents your development version of i2 Analyze in an XML editor. By default, this file is at `C:\IBM\i2analyze\SDK\sdk-projects\master\build\toolkit\configuration\environment\topology.xml`.
2.  Add information about the new audit database to the `<databases>` element in the topology file:

    ``` pre
    <databases>
       ...
       <database database-type="AuditExample" dialect="db2"
                 instance-name="DB2" database-name="AUDIT" xa="false"
                 edition="" id="audit" host-name="localhost"
                 port-number="50000" version=""/>
    </databases>
    ```

    In this code, the `database-type` attribute associates the database with the scripts in the `toolkit\scripts\database\db2\AuditExample` directory. If the audit logging database does not exist at run time, the scripts create it.

3.  In the `<war>` element for the `awc` application, add the new database as a data source.

    ``` pre
    <application http-server-host="false" name="awc" host-name="localhost">
       <wars>
          <war context-root="awc" i2-data-source-id="infostore" name="awc" target="awc">
             <data-sources>
                <data-source database-id="infostore" create-database="true"/>
                <data-source database-id="audit" create-database="true"/>
                ...
    ```

4.  Save your changes to `topology.xml`, and then open the `web.xml` file for editing. By default, this file is at `C:\IBM\i2analyze\SDK\sdk-projects\master\build\toolkit\configuration\fragments\awc\WEB-INF\web.xml`.
5.  Add information about the new database in a new `<resource-ref>` element, and then save the file:

    ``` pre
    <resource-ref>
       <res-ref-name>ds/InfoStore</res-ref-name>
       <res-type>javax.sql.DataSource</res-type>
    </resource-ref>
    <resource-ref>
       <res-ref-name>ds/AuditExample</res-ref-name>
       <res-type>javax.sql.DataSource</res-type>
    </resource-ref>
    ```

6.  Finally, add credentials for the new database to `C:\IBM\i2analyze\SDK\sdk-projects\master\build\toolkit\configuration\environment\credentials.properties`:

    ``` pre
    db.audit.user-name=UserName
    db.audit.password=Password
    ```

    The example project creates the database on the same server as the Information Store, so you can set UserName and Password to the same values that you use for the Information Store.

Next, to create the database, you must use the deployment toolkit to redeploy the development version of i2 Analyze.

1.  In your Eclipse workspace, stop any running servers.
2.  In Windows Explorer, navigate to the `C:\IBM\i2analyze\deploy-dev\wlp\usr\servers\awc\apps` directory. Delete the `awc.war` subdirectory, which contains the `awc` application.
3.  Open a command prompt as Administrator, and navigate to the `C:\IBM\i2analyze\SDK\sdk-projects\master` directory.
4.  Run the following command to redeploy the `awc` application:

    ``` pre
    build -t deploy -s awc
    ```

5.  Back in Eclipse, refresh your view of the Enterprise Explorer tab.
6.  In the **Servers** tab, right-click the **awc** server, and click **Clean** to bring the project in Eclipse back into sync with your deployment. At this point, you are ready to start the server again.
7.  Right-click the **awc** server, and then click **Start** (or **Restart**).

    This action starts the server with audit logging to database enabled. The logging behavior is governed by the settings file at `awc-audit-database-example\fragment\WEB-INF\classes\DatabaseAudit.properties`, which specifies the names of the database schema and the tables that store the information. Typical data looks like this:

    |                             |                                                                                                                      |
    |-----------------------------|----------------------------------------------------------------------------------------------------------------------|
    | `ID`                        | `1`                                                                                                                  |
    | `USER`                      | `Analyst`                                                                                                            |
    | `EVENT_TIME`                | `2016-10-04 17:14:53.309`                                                                                            |
    | `USER_SECURITY_GROUPS`      | `Analyst, Security Controller, Controlled`                                                                           |
    | `USER_SECURITY_PERMISSIONS` | `[SD-SL: [CON: UPDATE, UC: UPDATE], SD-SC: [HI: UPDATE, OSI: READ_ONLY]]`                                            |
    | `CLIENT_USER_AGENT`         | `Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.116 Safari/537.36` |
    | `CLIENT_IP_ADDRESS`         | `0:0:0:0:0:0:0:1`                                                                                                    |
    | `EXPRESSION`                | `black`                                                                                                              |
    | `FILTERS`                   | `[]`                                                                                                                 |
    | `DATASTORES`                | `InfoStore`                                                                                                          |

    The supplied version of `DatabaseAudit.properties` specifies the `I2AUDIT` schema as the target for audit logging data. The tables are named `QUICK_SEARCH` and `EXPAND`.

After you develop and test an audit logging solution for i2 Analyze, the next step is to publish it to a live deployment. These instructions assume that you have access to two instances of i2 Analyze:

-   The development version of the platform, deployed according to the instructions in IBM i2 Analyze Developer Essentials. This instance contains the custom application that you want to publish.
-   The production version of the platform, deployed according to the instructions in the IBM i2 Analyze deployment documentation. This instance has all of its default settings.

To prepare your development version of i2 Analyze for production, you must edit the `topology.xml` file again, and then use the deployment toolkit to redeploy the platform.

1.  If the development version of the `awc` server is running, stop the server from Eclipse.
2.  In Windows Explorer, navigate to the `C:\IBM\i2analyze\deploy-dev\wlp\usr\servers\awc\apps` directory. Delete the `awc.war` subdirectory, which contains the `awc` application.
3.  In the topology file, edit the `<war>` element that defines the contents of the `awc` web application to include the common and specific fragments for the audit logging to database example.

    ``` pre
    <war context-root="awc" i2-data-source-id="infostore" name="awc" target="awc">
       <data-sources>
          <data-source database-id="infostore" create-database="true"/>
          <data-source database-id="audit" create-database="true"/>
       </data-sources>
       <file-store-ids>
          <file-store-id value="awc-job-store"/>
          <file-store-id value="awc-recordgroup-store"/>
       </file-store-ids>
       <fragments>
          <fragment name="awc"/>
          <fragment name="common"/>
          <fragment name="default-user-profile-provider"/>
          <fragment name="awc-audit-example-common"/>
          <fragment name="awc-audit-database-example"/>
       </fragments>
       <solr-collection-ids>
          <solr-collection-id collection-id="main_index"
                              data-dir="C:/IBM/i2analyze/data-dev/solr"
                              cluster-id="is_cluster"/>
       </solr-collection-ids>
    </war>
    ```

4.  At your command prompt, run the command to redeploy the `awc` application again:

    ``` pre
    build -t deploy -s awc
    ```

Before you publish the audit logging functionality to a live deployment of i2 Analyze, test that your changes to the topology file are behaving correctly.

1.  Back in the Servers tab in your Eclipse environment, ensure that the `awc` application is marked as **Synchronized**. Right click and **Publish** if it is not.
2.  Start the **awc** server, connect to the application through a client, and verify that the audit logging behavior is unchanged.
3.  Stop the **awc** server again.

At this stage, you are ready to copy the changes that you made to your development deployment to a production deployment of i2 Analyze.

1.  Copy `C:\IBM\i2analyze\SDK\sdk-projects\master\build\toolkit\configuration` from the development version into the production version of the deployment toolkit.
2.  Edit the following files to ensure that the settings match the production environment:
    -   `configuration\environment\awc\environment.properties`
    -   `configuration\environment\i2analyze\environment.properties`
    -   `configuration\environment\http-server.properties`
    -   `configuration\environment\credentials.properties`
    -   `configuration\environment\topology.xml`

3.  On the production version of the platform, open a command prompt as Administrator, and navigate to the `toolkit\scripts` directory.
4.  To redeploy the platform, run the following command:

    ``` pre
    setup -t deploy -s awc
    ```

5.  Use the Services application in Windows (`services.msc`) to restart IBM HTTP Server.

Results
-------

The result of following all of these steps is that your development and production deployments of i2 Analyze contain the same implementation of audit logging to database. When users interact with the Information Store, the platform writes data about those interactions to the database tables that you specified.

What to do next
---------------

It is likely that after you use the deployment toolkit to deploy the example, you want to return to developing and deploying it in Eclipse. That process involves the same steps that you followed after adding the new database to the deployment. Delete the `awc.war` file from the file system, and then clean the **awc** server in Eclipse.

This operation restores Eclipse to its state when you first added the `awc` project to the **awc** server. You can change the `awc-audit-database-example` project and see the effects on the development version of the platform exactly as before.

**Parent topic:** <a href="developer_essentials_welcome.md" class="link" title="IBM i2 Analyze Developer Essentials contains tools, libraries, and examples that enable development and deployment of custom extensions to i2 Analyze.">IBM i2 Analyze Developer Essentials</a>

------------------------------------------------------------------------

© Copyright IBM Corporation 2014, 2016.


