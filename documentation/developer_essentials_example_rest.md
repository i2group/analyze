Configuring the "two-phase data retrieval" example project
==========================================================

i2 Analyze supports variations on the "data access on-demand" approach to data acquisition. One of the examples that IBM provides in Developer Essentials is the `da-subset-rest-example`, which demonstrates a two-phase technique for data retrieval from an external source. The example also contains code that allows it to be called and displayed by custom code in the Intelligence Portal.

Before you begin
----------------

The two-phase data retrieval example project requires the development version of i2 Analyze, prepared according to the deployment instructions in Developer Essentials.

To test the functionality of the example, you must also configure the user interface extension example project by following the instructions in [Configuring the "user interface extension" example project](https://github.com/IBM-i2/Analyze/blob/master/documentation/developer_essentials_example_ui.md "(Opens in a new tab or window)").

<span class="notetitle">Note:</span> All the data acquisition example projects in Developer Essentials use the same XML data, and the same code for transforming and converting that data into i2 Analyze items. These artifacts are in the `C:\IBM\i2analyze\SDK\sdk-projects\da-example-common` directory, to which all of the data acquisition examples have access.

About this task
---------------

The `da-subset-rest-example` project takes its name from one of the services that it contains, which has a REST API. This API enables the service to be called easily from code in HTML pages that the Intelligence Portal can host as part of a user interface extension.

Compared with the data access on-demand example project, this example demonstrates a different approach to data acquisition. The first request from a client causes i2 Analyze to query the external data source. The example code places any results in a subset, and returns a token that identifies that subset to the client. If the client specifies the same token in subsequent requests, the platform responds by using data from the subset.

<span class="notetitle">Note:</span> The two-phase data retrieval example is one part of a two-part project that demonstrates custom server functionality in a customized Intelligence Portal. The project is not complete until you configure and deploy both parts, and you cannot interact with this example until you also configure the user interface extension example project.

Procedure
---------

The examples in Developer Essentials require links to the i2 Analyze libraries, and to the API documentation. These links are configured in a separate project directory that you must import into Eclipse before you can build any of the example projects.

1.  <span class="ph cmd">If you did not already add the `master` directory and the related artifacts to your Eclipse workspace while you worked with one of the other examples, add it now.</span>
    1.  <span class="ph cmd">In Eclipse, click <span class="ph menucascade">**Window** \> **Preferences**</span> to open the <span class="keyword wintitle">Preferences</span> window.</span>
    2.  <span class="ph cmd">In the <span class="keyword wintitle">Preferences</span> window, select <span class="ph menucascade">**General** \> **Workspace** \> **Linked Resources**</span>.</span>
    3.  <span class="ph cmd">Add an entry to the **Defined path variables** list:</span>

        -   Name: `TOOLKIT_ROOT`
        -   Location: `C:\IBM\i2analyze\SDK\sdk-projects\master\build\toolkit`

        The shared libraries and several of the example projects rely on the presence of `TOOLKIT_ROOT` in your development environment.

    4.  <span class="ph cmd">In Eclipse, click <span class="ph menucascade">**File** \> **Import**</span> to open the <span class="keyword wintitle">Import</span> window.</span>
    5.  <span class="ph cmd">In the <span class="keyword wintitle">Import</span> window, click <span class="ph menucascade">**General** \> **Existing Projects into Workspace**</span>, and then click **Next**.</span>
    6.  <span class="ph cmd">Click **Browse** at the top of the window, and then select the `C:\IBM\i2analyze\SDK\sdk-projects\master` directory.</span>
    7.  <span class="ph cmd">Click **Finish** to complete the import process.</span>
    8.  <span class="ph cmd">Repeat the import process to import `C:\IBM\i2analyze\SDK\sdk-projects\da-example-common`</span>

2.  <span class="ph cmd">If the development version of i2 Analyze is running, stop the server from Eclipse.</span>
3.  <span class="ph cmd">Open a command prompt as Administrator, and navigate to the `C:\IBM\i2analyze\SDK\sdk-projects\master` directory.</span>
4.  <span class="ph cmd">Run the following command:</span>

    ``` pre
    build -pr da-subset-rest-example -t addDaodDataSource
    ```

    <span class="importanttitle">Important:</span> For successful integration with Eclipse, the value that you provide to the `-pr` option must match the name of the example project.

    When the command runs successfully, it modifies the SDK `topology.xml` file in the `C:\IBM\i2analyze\SDK\sdk-projects\master\build\toolkit\configuration\environment` directory to add information about a new data source. By default, the data source gets the same name as the value that you provide to the `-pr` option.

5.  <span class="ph cmd">Reopen the SDK `topology.xml` file, and edit the `<i2-data-source>` element that defines the new data access on-demand data source so that its attributes reflect the functionality of the example:</span>

    ``` pre
    <i2-data-source id="daod3" ar="false">
       <DataSource ScsSearchSupported="false" EdrsGetContextSupported="false"  
                   ScsNetworkSearchSupported="false" EdrsGetLatestItemsSupported="false" Version="0" 
                   ScsPresent="false" ScsFilteredSearchSupported="false"  
                   ScsBrowseSupported="false" Id="" ScsDumbbellSearchSupported="false"  
                   SesPresent="true" EdrsPresent="false" 
                   >
          <Shape>DAOD</Shape>
          <Name>da-subset-rest-example</Name>
       </DataSource>
    </i2-data-source>
    ```

    For this data source, all of the attributes must be set to `false`, apart from `SesPresent`. This data source supports only a subset exploration service.

6.  <span class="ph cmd">In Windows Explorer, navigate to the `C:\IBM\i2analyze\SDK\sdk-projects\master\build\toolkit\configuration\environment\dsid` directory.</span>

    The `dsid` directory is populated and maintained by the deployment scripts. This directory contains settings files for each of the data sources that are defined in `topology.xml`.

7.  <span class="ph cmd">Open the `dsid.<id>.properties` file for your data source in a text editor, where `<id>` is the value of the `id` attribute in the `<i2-data-source>` element for this data source in the topology file. Record the value of the `DataSourceId` property, which was generated when you redeployed i2 Analyze.</span>
8.  <span class="ph cmd">Open `C:\IBM\i2analyze\SDK\sdk-projects\master\build\toolkit\configuration\fragments\core\ApolloClientSettings.xml` in a text editor. Add these lines to the end of the file, just before the `</settings>` closing tag:</span>

    ``` pre
    <SubsettingExampleGenerationUILocation>
    http://localhost:9081/<DataSourceId>/SubsettingHtml.html</SubsettingExampleGenerationUILocation>
    <SubsettingExampleDisplayRecordUrl>
    http://localhost:9081/<DataSourceId>/ItemDetails.jsp?id={0}&amp;source={1}</SubsettingExampleDisplayRecordUrl>
    ```

    These settings provide code in the user interface extension project with the locations of the resources that it must use to respond to user requests. The port numbers must match the value of the `WC_defaulthost` property in the `C:\IBM\i2analyze\SDK\sdk-projects\master\build\toolkit\configuration\environment\i2analyze\port-def.props` file.

9.  <span class="ph cmd">Replace `<DataSourceId>` in both values with the identifier that you copied from the `dsid.<id>.properties` file. Save and close `ApolloClientSettings.xml`.</span>
10. <span class="ph cmd">Redeploy i2 Analyze by running the command that you used to deploy the platform originally:</span>

    ``` pre
    build -t deploy
    ```

11. <span class="ph cmd">Edit the i2 Analyze Liberty profile server `server.xml` to exclude the Apache Wink `.jar` files and to use the `jaxrs-1.1` feature:</span>
    1.  <span class="ph cmd">Using an XML editor, open the `server.xml` file in the `C:\IBM\i2analyze\deploy-dev\wlp\usr\servers\i2analyze` directory.</span>
    2.  <span class="ph cmd">Locate the `<webApplication>` element with the `id` attribute value of `da-subset-rest-example`.</span>
    3.  <span class="ph cmd">The web application contains a child `<classLoader>` element. Within the `<classLoader>` element is a `<fileset>` element. Add the `excludes` attribute to the `<fileset>` element so that it matches the following example:</span>

        ``` pre
        <fileset id="SharedLibrary" dir="${shared.resource.dir}/i2-common/lib" excludes="wink-*.jar"/>
        ```

    4.  <span class="ph cmd">Add the `jaxrs-1.1` feature to the server. In the `server.xml`, add the following child `<feature>` element to the `<featureManager>` element:</span>

        ``` pre
        <feature>jaxrs-1.1</feature>
        ```

        <span class="notetitle">Note:</span> If the `jaxrs-2.0` feature is already present, remove the `<feature>jaxrs-2.0</feature>` element. Any applications that use `jaxrs-2.0` will no longer work.

12. <span class="ph cmd">Use the Services application in Windows (`services.msc`) to restart IBM HTTP Server. Do not attempt to start the application server yet.</span>

The commands so far deployed the example project application from the toolkit. To enable easy debugging of your code, the next steps are all about replacing the application that you deployed from the toolkit with code from Eclipse.

1.  <span class="ph cmd">Refresh the Eclipse user interface to ensure that it reflects the current state of the project.</span>
2.  <span class="ph cmd">In your Eclipse workspace, open the <span class="ph filepath">WebSphere Application Server Liberty Profile/servers/i2analyze/apps</span> folder, and delete the `da-subset-rest-example.war` application.</span>
3.  <span class="ph cmd">Repeat the instructions that you followed when you added the `master` directory to your Eclipse workspace. This time, add the `C:\IBM\i2analyze\SDK\sdk-projects\da-subset-rest-example` directory to Eclipse.</span>
4.  <span class="ph cmd">Ensure that the JAR file from `da-example-common` is loaded correctly into the deployment assembly for the `da-subset-rest-example` project:</span>
    1.  <span class="ph cmd">In Package Explorer, right-click **da-subset-rest-example**, and select **Properties** to display the Properties window.</span>
    2.  <span class="ph cmd">Click **Deployment Assembly**, and look for any error messages about `da-example-common`.</span>
    3.  <span class="ph cmd">If there are error messages, remove `da-example-common` from the packaging structure, and then use <span class="ph menucascade">**Add** \> **Project**</span> to add it again.</span>

5.  <span class="ph cmd">In the **Servers** tab at the bottom of the Eclipse application window, right-click the **i2analyze** server and select <span class="ph uicontrol">Add and Remove</span>.</span>
6.  <span class="ph cmd">In the <span class="keyword wintitle">Add and Remove</span> window, move **da-subset-rest-example** from the **Available** list to the **Configured** list, and then click **Finish** to close the window.</span>

At this stage, deployment of the two-phase data retrieval example is complete. You can start i2 Analyze, and connect to it through the Intelligence Portal.

1.  <span class="ph cmd">In Eclipse, start the server.</span>
2.  <span class="ph cmd">In a web browser, load or refresh your view of the Intelligence Portal. In the menu bar, click <span class="ph menucascade">**Subsetting** \> **Launch Subsetting Tab**</span> to display the custom user interface.</span>
3.  <span class="ph cmd">Follow the on-screen instructions to request data from the new data source and present it in the user interface in different permutations.</span>

After you develop and test a data access on-demand solution for i2 Analyze, the next step is to publish it to a live deployment.

These instructions assume that you have access to two instances of i2 Analyze:

-   The development version of the platform, deployed according to the instructions in IBM i2 Analyze Developer Essentials. This instance contains the custom application that you want to publish.
-   The production version of the platform, deployed according to the instructions in the IBM i2 Analyze Deployment Guide. This instance has all of its default settings.

1.  <span class="ph cmd">Copy `C:\IBM\i2analyze\SDK\sdk-projects\master\build\toolkit\configuration` into the production version of the `toolkit`.</span>
2.  <span class="ph cmd">Edit the following files to ensure that the default settings match your environment:</span>
    -   `configuration\environment\i2analyze\environment.properties`
    -   `configuration\environment\http-server.properties`
    -   `configuration\environment\credentials.properties`
    -   `configuration\environment\topology.xml`

3.  <span class="ph cmd">Open a command prompt as Administrator, and navigate to the `toolkit\scripts` directory.</span>
4.  <span class="ph cmd">To redeploy the platform, run the following command:</span>

    ``` pre
    setup -t deploy
    ```

5.  <span class="ph cmd">For this solution you must complete the same modifications to `server.xml` file that were completed in the development version of the platform, in the `server.xml` for the i2 Analyze Liberty profile server that is used in the live deployment. Any applications that use these `.jar` files or `jaxrs-2.0` will no longer work.</span>
6.  <span class="ph cmd">Use the Services application in Windows (`services.msc`) to restart IBM HTTP Server.</span>

**Parent topic:** [IBM i2 Analyze Developer Essentials](developer_essentials_welcome.html "IBM i2 Analyze Developer Essentials contains tools, libraries, and examples that enable development and deployment of custom extensions to i2 Analyze.")

------------------------------------------------------------------------

© Copyright IBM Corporation 2014, 2016.


