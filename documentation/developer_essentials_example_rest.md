Configuring the "two-phase data retrieval" example project
==========================================================

i2 Analyze supports variations on the "data access on-demand" approach to data acquisition. One of the examples that IBM provides in Developer Essentials is the `da-subset-rest-example`, which demonstrates a two-phase technique for data retrieval from an external source. The example also contains code that allows it to be called and displayed by custom code in the Intelligence Portal.

Before you begin
----------------

The two-phase data retrieval example project requires the development version of i2 Analyze, prepared according to the deployment instructions in Developer Essentials.

To test the functionality of the example, you must also configure the user interface extension example project by following the instructions in <a href="https://github.com/IBM-i2/Analyze/blob/master/documentation/developer_essentials_example_ui.md" class="xref" title="(Opens in a new tab or window)">Configuring the &quot;user interface extension&quot; example project</a>.

Note: All the data acquisition example projects in Developer Essentials use the same XML data, and the same code for transforming and converting that data into items in the Analysis Repository. These artifacts are in the `SDK\sdk-projects\da-example-common` directory, to which all of the data acquisition examples have access.

About this task
---------------

The `da-subset-rest-example` project takes its name from one of the services that it contains, which has a REST API. This API enables the service to be called easily from code in HTML pages that the Intelligence Portal can host as part of a user interface extension.

Compared with the data access on-demand example project, this example demonstrates a different approach to data acquisition. The first request from a client causes i2 Analyze to query the external data source. The example code places any results in a subset, and returns a token that identifies that subset to the client. If the client specifies the same token in subsequent requests, the platform responds by using data from the subset.

Note: The two-phase data retrieval example is one part of a two-part project that demonstrates custom server functionality in a customized Intelligence Portal. The project is not complete until you configure and deploy both parts, and you cannot interact with this example until you also configure the user interface extension example project.

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

2.  Repeat steps 1d to 1g to import `C:\IBM\i2analyze\SDK\sdk-projects\da-example-common` into Eclipse.
3.  If the development version of the `i2analyze` server is running, stop the server from Eclipse.
4.  Open a command prompt as Administrator, and navigate to the `C:\IBM\i2analyze\SDK\sdk-projects\master` directory.
5.  Run the following command:

    ``` pre
    build -pr da-subset-rest-example -t addDaodDataSource
    ```

    Important: For successful integration with Eclipse, the value that you provide to the `-pr` option must match the name of the example project.

    When the command runs successfully, it modifies the topology file in your development version of i2 Analyze to add information about a new data source. By default, the data source gets the same name as the value that you provide to the `-pr` option.

6.  Open the topology file that represents your deployment of i2 Analyze in an XML editor. By default, this file is at `C:\IBM\i2analyze\SDK\sdk-projects\master\build\toolkit\configuration\environment\topology.xml`.
7.  Edit the `<i2-data-source>` element that defines the new data access on-demand data source so that its attributes reflect the functionality of the example:

    ``` pre
    <i2-data-source id="daod3" ar="false">
       <DataSource ScsSearchSupported="false" EdrsGetContextSupported="false"
                   ScsNetworkSearchSupported="false" EdrsGetLatestItemsSupported="false"
                   Version="0" ScsPresent="false" ScsFilteredSearchSupported="false"
                   ScsBrowseSupported="false" Id="" ScsDumbbellSearchSupported="false"
                   SesPresent="true" EdrsPresent="false"
                   >
          <Shape>DAOD</Shape>
          <Name>da-subset-rest-example</Name>
       </DataSource>
    </i2-data-source>
    ```

    For this data source, all of the attributes must be set to `false`, apart from `SesPresent`. This data source supports only a subset exploration service.

8.  To redeploy i2 Analyze, run the following command:

    ``` pre
    build -t deploy
    ```

9.  In Windows Explorer, navigate to the `C:\IBM\i2analyze\SDK\sdk-projects\master\build\toolkit\configuration\environment\dsid` directory.

    The `dsid` directory is populated and maintained by the deployment scripts. This directory contains settings files for each of the data sources that `topology.xml` defines.

10. Open the `dsid.<id>.properties` file for your data source in a text editor, where `<id>` is the value of the `id` attribute in the `<i2-data-source>` element for this data source in the topology file. Record the value of the `DataSourceId` property, which was generated when you redeployed i2 Analyze.
11. Open `C:\IBM\i2analyze\SDK\sdk-projects\master\build\toolkit\configuration\fragments\core\ApolloClientSettings.xml` in a text editor. Add these lines to the end of the file, just before the `</settings>` closing tag:

    ``` pre
    <SubsettingExampleGenerationUILocation>
    http://localhost:9081/<DataSourceId>/SubsettingHtml.html</SubsettingExampleGenerationUILocation>
    <SubsettingExampleDisplayRecordUrl>
    http://localhost:9081/<DataSourceId>/ItemDetails.jsp?id={0}&amp;source={1}</SubsettingExampleDisplayRecordUrl>
    ```

    These settings provide code in the user interface extension project with the locations of the resources that it must use to respond to user requests. The port numbers must match the value of the `WC_defaulthost` property in the `C:\IBM\i2analyze\SDK\sdk-projects\master\build\toolkit\configuration\environment\i2analyze\port-def.props` file.

12. Replace `<DataSourceId>` in both values with the identifier that you copied from the `dsid.<id>.properties` file. Save and close `ApolloClientSettings.xml`.
13. Redeploy i2 Analyze again by running the command that you used to deploy the platform originally:

    ``` pre
    build -t deploy
    ```

14. Edit the `server.xml` for your WebSphere Application Server Liberty server to exclude the Apache Wink JAR files, and to use the `jaxrs-1.1` feature:
    1.  Navigate to the `C:\IBM\i2analyze\deploy-dev\wlp\usr\servers\i2analyze` directory, and then open the `server.xml` file in an XML editor.
    2.  In `server.xml`, locate the `<webApplication>` element whose `id` attribute has the value `da-subset-rest-example`.
    3.  The web application contains a child `<classLoader>` element. Within `<classLoader>` is a `<fileset>` element. Add an `excludes` attribute to the `<fileset>` element that matches the following example:

        ``` pre
        <fileset id="SharedLibrary" dir="${shared.resource.dir}/i2-common/lib"
                 excludes="wink-*.jar"/>
        ```

    4.  In the same file, add the following child `<feature>` element to the `<featureManager>` element to add the `jaxrs-1.1` feature to the server:

        ``` pre
        <feature>jaxrs-1.1</feature>
        ```

        Note: If the `jaxrs-2.0` feature is already present, remove the `<feature>jaxrs-2.0</feature>` element. Any applications that use `jaxrs-2.0` will no longer work.

15. Use the Services application in Windows (`services.msc`) to restart IBM HTTP Server. Do not attempt to start the application server yet.

The commands so far deployed the example project application from the deployment toolkit. To enable easy debugging of your code, the next steps are all about replacing the application that you deployed from the toolkit with code from Eclipse.

1.  Refresh the Eclipse user interface to ensure that it reflects the current state of the project.
2.  In your Eclipse workspace, open the `WebSphere Application Server Liberty/servers/i2analyze/apps` folder, and delete the `da-subset-rest-example.war` application.
3.  Repeat the instructions that you followed when you added the `master` directory to your Eclipse workspace. This time, add the `C:\IBM\i2analyze\SDK\sdk-projects\da-subset-rest-example` directory to Eclipse.
4.  Ensure that the JAR file from `da-example-common` is loaded correctly into the deployment assembly for the `da-subset-rest-example` project:
    1.  In Package Explorer, right-click **da-subset-rest-example**, and select **Properties** to display the Properties window.
    2.  Click **Deployment Assembly**, and look for any error messages about `da-example-common`.
    3.  If there are error messages, remove `da-example-common` from the packaging structure, and then use **Add** &gt; **Project** to add it again.

5.  In the **Servers** tab at the bottom of the Eclipse application window, right-click the **i2analyze** server and select **Add and Remove**.
6.  In the Add and Remove window, move **da-subset-rest-example** from the **Available** list to the **Configured** list, and then click **Finish** to close the window.

At this stage, deployment of the two-phase data retrieval example is complete. You can start i2 Analyze and connect to it through the Intelligence Portal.

1.  In Eclipse, start the `i2Analyze` server.
2.  In a web browser, load or refresh your view of the Intelligence Portal. In the menu bar, click **Subsetting** &gt; **Launch Subsetting Tab** to display the custom user interface.
3.  Follow the on-screen instructions to request data from the new data source and present it in the user interface in different permutations.

After you develop and test a data access on-demand solution for i2 Analyze, the next step is to publish it to a live deployment.

These instructions assume that you have access to two instances of i2 Analyze:

-   The development version of the platform, deployed according to the instructions in IBM i2 Analyze Developer Essentials. This instance contains the custom application that you want to publish.
-   The production version of the platform, deployed according to the instructions in the IBM i2 Analyze deployment documentation. This instance has all of its default settings.

1.  Copy `C:\IBM\i2analyze\SDK\sdk-projects\master\build\toolkit\configuration` from the development version into the production version of the deployment toolkit.
2.  Edit the following files to ensure that the default settings match your environment:
    -   `configuration\environment\i2analyze\environment.properties`
    -   `configuration\environment\http-server.properties`
    -   `configuration\environment\credentials.properties`
    -   `configuration\environment\topology.xml`

3.  On the production version of the platform, open a command prompt as Administrator, and navigate to the `toolkit\scripts` directory.
4.  To redeploy the platform, run the following command:

    ``` pre
    setup -t deploy
    ```

5.  For this solution, you must make the same modifications to the `server.xml` file for the production version of the platform that you made to the file for the development version. Any applications that use the excluded JAR files or `jaxrs-2.0` will no longer work.
6.  Use the Services application in Windows (`services.msc`) to restart IBM HTTP Server.

**Parent topic:** <a href="developer_essentials_welcome.md" class="link" title="IBM i2 Analyze Developer Essentials contains tools, libraries, and examples that enable development and deployment of custom extensions to i2 Analyze.">IBM i2 Analyze Developer Essentials</a>

------------------------------------------------------------------------

© Copyright IBM Corporation 2014, 2016.


