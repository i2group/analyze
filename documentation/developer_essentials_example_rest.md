Configuring the "two-phase data retrieval" example project
==========================================================

The Intelligence Analysis Platform supports variations on the "data access on-demand" approach to data acquisition. One of the examples that IBM provides in Developer Essentials is the `da-subset-rest-example`, which demonstrates a two-phase technique for data retrieval from an external source. The example also contains code that allows it to be called and displayed by custom code in the Intelligence Portal.

Before you begin
----------------

The two-phase data retrieval example project requires the development version of the Intelligence Analysis Platform, prepared according to the deployment instructions in Developer Essentials. To test the functionality of the example, you must also configure the user interface extension example project.
<span class="notetitle">Note:</span> All the data acquisition example projects in Developer Essentials use the same XML data, and the same code for transforming and converting that data into Intelligence Analysis Platform items. These artifacts are in the `SDK\sdk-projects\da-example-common` directory, to which all of the data acquisition examples have access.

About this task
---------------

The `da-subset-rest-example` project takes its name from one of the services that it contains, which has a REST API. This API enables the service to be called easily from code in HTML pages that the Intelligence Portal can host as part of a user interface extension. Compared with the data access on-demand example project, this example demonstrates a different approach to data acquisition. The first request from a client causes the Intelligence Analysis Platform to query the external data source. The example code places any results in a subset, and returns a token that identifies that subset to the client. If the client specifies the same token in subsequent requests, the platform responds by using data from the subset.
<span class="notetitle">Note:</span> The two-phase data retrieval example is one part of a two-part project that demonstrates custom server functionality in a customized Intelligence Portal. The project is not complete until you configure and deploy both parts, and you cannot interact with this example until you also configure the user interface extension example project.

Procedure
---------

The examples in Developer Essentials require links to the Intelligence Analysis Platform libraries, and to the API documentation. These links are configured in a separate project directory that you must import into Eclipse before you can build any of the example projects.

1.  <span class="ph cmd">If you did not already add the `master` directory and the related artifacts to your Eclipse workspace while you worked with one of the other examples, add it now.</span>
    1.  <span class="ph cmd">In Eclipse, click <span class="ph menucascade">**Window** \> **Preferences**</span> to open the <span class="keyword wintitle">Preferences</span> window.</span>
    2.  <span class="ph cmd">In the <span class="keyword wintitle">Preferences</span> window, select <span class="ph menucascade">**General** \> **Workspace** \> **Linked Resources**</span>.</span>
    3.  <span class="ph cmd">Add an entry to the **Defined path variables** list:</span>

        -   Name: `TOOLKIT_ROOT`
        -   Location: `C:\IBM\iap\SDK\sdk-projects\master\build\IAP-Deployment-Toolkit`

        The shared libraries and several of the example projects rely on the presence of `TOOLKIT_ROOT` in your development environment.

    4.  <span class="ph cmd">In Eclipse, click <span class="ph menucascade">**File** \> **Import**</span> to open the <span class="keyword wintitle">Import</span> window.</span>
    5.  <span class="ph cmd">In the <span class="keyword wintitle">Import</span> window, click <span class="ph menucascade">**General** \> **Existing Projects into Workspace**</span>, and then click **Next**.</span>
    6.  <span class="ph cmd">Click **Browse** at the top of the window, and then select the `C:\IBM\iap\SDK\sdk-projects\master` directory.</span>
    7.  <span class="ph cmd">Click **Finish** to complete the import process.</span>
    8.  <span class="ph cmd">Repeat the import process to import `C:\IBM\iap\SDK\sdk-projects\da-example-common`</span>
        <span class="notetitle">Note:</span> Ensure that both JAR files have been loaded correctly in the project's <span class="ph uicontrol">Deployment Assemblies</span> before continuing. If either JAR cannot be found, remove the JAR and re-add it.

2.  <span class="ph cmd">Run the following command:</span>

    ``` pre
    build -pr da-subset-rest-example -s iap -t addDaodDataSource
    ```

    <span class="importanttitle">Important:</span> For successful integration with Eclipse, the value that you provide to the `-pr` option must match the name of the example project.

    When the command runs successfully, it modifies `topology.xml` to add information about a new data source. By default, the data source gets the same name as the value that you provide to the `-pr` option.

3.  <span class="ph cmd">Reopen the SDK topology file, and edit the `<iap-data-source>` element that defines the new data access on-demand data source so that its attributes reflect the functionality of the example:</span>

    ``` pre
    <iap-data-source id="daod1" ar="false">
       <DataSource EdrsPresent="false" ScsPresent="false" SesPresent="true"
                   ScsBrowseSupported="false" ScsSearchSupported="false"
                   ScsNetworkSearchSupported="false"
                   ScsDumbbellSearchSupported="false"
                   ScsFilteredSearchSupported="false"
                   EdrsGetContextSupported="false"
                   EdrsGetLatestItemsSupported="false" Id="" Version="0">
          <Shape>DAOD</Shape>
          <Name>da-subset-rest-example</Name>
       </DataSource>
    </iap-data-source>
    ```

    For this data source, all of the attributes must be set to `false`, apart from `SesPresent`. This data source supports only a subset exploration service.

4.  <span class="ph cmd">In Windows Explorer, navigate to the `master\build\IAP-Deployment-Toolkit\configuration\environment\dsid` directory.</span>

    The `dsid` directory is populated and maintained by the deployment scripts. This directory contains settings files for each of the data sources that are defined in `topology.xml`.

5.  <span class="ph cmd">Open the `dsid.daod1.properties` file for your data source in a text editor, where `daod1` is the ID value in the `<iap-data-source>` element of the topology file. Record the value of the `DataSourceId` property, which is the GUID generated when the Intelligence Analysis Platform was redeployed.</span>
6.  <span class="ph cmd">Open `C:\IBM\iap\SDK\sdk-projects\master\build\IAP-Deployment-Toolkit\configuration\fragments\core\ApolloClientSettings.xml` in a text editor. Add these lines to the end of the file, just before the `</settings>` closing tag:</span>

    ``` pre
    <SubsettingExampleGenerationUILocation>
    http://localhost:9081/DataSourceId/SubsettingHtml.html</SubsettingExampleGenerationUILocation>
    <SubsettingExampleDisplayRecordUrl>
    http://localhost:9081/DataSourceId/ItemDetails.jsp?id={0}&amp;source={1}</SubsettingExampleDisplayRecordUrl>
    ```

    These settings provide code in the user interface extension project with the locations of the resources that it must use to respond to user requests. The port numbers must match the value of the `WC_defaulthost` property in the `C:\IBM\iap\SDK\sdk-projects\master\build\IAP-Deployment-Toolkit\configuration\environment\iap\port-def.props` file.

7.  <span class="ph cmd">Replace `DataSourceId` in both values with the identifier that you copied from the `dsid.da-subset-rest-example-id.properties` file. Save and close `ApolloClientSettings.xml`.</span>
8.  <span class="ph cmd">Redeploy the Intelligence Analysis Platform by running the command that you used to deploy the platform originally:</span>

    ``` pre
    build -t deploy
    ```

9.  <span class="ph cmd">Use the Services application in Windows (`services.msc`) to restart IBM HTTP Server. Do not attempt to start the application server yet.</span>

The commands so far added an incomplete application to your Intelligence Analysis Platform deployment. (The application includes all the infrastructure for supporting data access on-demand, but no actual implementation.) In Eclipse, you must create a server to host the application, remove the incomplete application from the workspace, and then replace it with the example.

1.  <span class="ph cmd">In your Eclipse workspace, open the <span class="ph filepath">WebSphere Application Server Liberty Profile/servers/iap/apps</span> folder, and delete the `da-subset-rest-example.war` application.</span>
2.  <span class="ph cmd">Repeat the instructions that you followed when you added the `shared` directory to your Eclipse workspace. This time, add the `C:\IBM\iap\SDK\sdk-projects\da-subset-rest-example` directory to Eclipse.</span>
3.  <span class="ph cmd">In the **Servers** tab at the bottom of the Eclipse application window, right-click the **iap** server and select <span class="ph uicontrol">Add and Remove</span>.</span>
4.  <span class="ph cmd">In the <span class="keyword wintitle">Add and Remove</span> window, move **da-subset-rest-example** from the **Available** list to the **Configured** list, and then click **Finish** to close the window.</span>

    <span class="notetitle">Note:</span> If the **Configured** list already contains an entry for the **da-subset-rest-example**, Eclipse displays a warning about moving the entry from the **Available** list to it. You can safely ignore the warning for this example.

At this stage, deployment of the two-phase data retrieval example is complete. You can start the Intelligence Analysis Platform, and connect to it through the Intelligence Portal.

1.  <span class="ph cmd">In Eclipse, start the server.</span>
2.  <span class="ph cmd">In a web browser, load or refresh your view of the Intelligence Portal. In the menu bar, click <span class="ph menucascade">**Subsetting** \> **Launch Subsetting Tab**</span> to display the custom user interface.</span>
3.  <span class="ph cmd">Follow the on-screen instructions to request data from the new data source and present it in the user interface in different permutations.</span>

After you develop and test a data access on-demand solution for the Intelligence Analysis Platform, the next step is to publish it to a live deployment.

These instructions assume that you have access to two instances of the Intelligence Analysis Platform:

-   The development version of the platform, deployed according to the instructions in IBM i2 Intelligence Analysis Platform Developer Essentials. This instance contains the custom application that you want to publish.
-   The production version of the platform, deployed according to the instructions in the IBM i2 Intelligence Analysis Platform Deployment Guide. This instance has all of its default settings.

1.  <span class="ph cmd">Copy `C:\IBM\iap\SDK\sdk-projects\master\build\IAP-Deployment-Toolkit\configuration` into the production version of the `IAP-Deployment-Toolkit` .</span>
2.  <span class="ph cmd">Edit the following files to ensure that the default settings match your environment:</span>
    -   `configuration\environment\iap\environment.properties`
    -   `configuration\environment\http-server.properties`
    -   `configuration\environment\credentials.properties`
    -   `configuration\environment\topology.xml`

3.  <span class="ph cmd"> Open a command prompt as Administrator, and navigate to the `IAP-Deployment-Toolkit\scripts` directory.</span>
4.  <span class="ph cmd">Run the following command:</span>

    ``` pre
    setup -t deploy
    ```

**Parent topic:** [IBM i2 Intelligence Analysis Platform Developer Essentials](developer_essentials_welcome.html "IBM i2 Intelligence Analysis Platform Developer Essentials contains tools, libraries, and examples that enable development and deployment of custom extensions to the Intelligence Analysis Platform.")

------------------------------------------------------------------------

© Copyright IBM Corporation 2014, 2015.


