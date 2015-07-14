Configuring the "binary documents access" example project
=========================================================

The "data access on-demand" approach to data acquisition in the Intelligence Analysis Platform involves querying external data through a new service. In Developer Essentials, the `da-subset-documents-example` example extends the `da-subset-filesystem-example` example to include images or other binary documents in the returned data.

Before you begin
----------------

The binary documents access example project requires the development version of the Intelligence Analysis Platform, prepared according to the instructions in the deployment guide. However, the example has no additional requirements. You do not need to configure the `da-subset-filesystem-example` example project before you configure this project.

<span class="notetitle">Note:</span> All the data acquisition example projects in Developer Essentials use the same XML data, and the same code for transforming and converting that data into Intelligence Analysis Platform items. These artifacts are in the `SDK\sdk-projects\da-example-common` directory, to which all of the data acquisition examples have access.

About this task
---------------

The binary documents access example defines and configures a pair of services that provide users with access to an external data source through the Intelligence Portal. Configuring the example requires you to add the data source to the existing Intelligence Analysis Platform deployment, and to add the services to the platform.

The example demonstrates two different mechanisms for including binary documents in the returned data: through a direct reference to the URL of a document, or through a call to an implementation of the `IExternalDataDocumentSource` interface. The sample data contains items that are associated with static image files through the first mechanism, and items with images that are returned by a servlet through the second.

In the example, the HTTP Server configuration file `httpd.conf` is used to manipulate the `<DocumentId>` element of a transformed item into the URL of a static file or a servlet. The file contains rewrite conditions that identify references to documents, and rewrite rules that convert those references into URLs. Depending on the contents of the references, the URLs target a directory that contains image files, or a servlet method.

<span class="notetitle">Note:</span> The binary documents access example includes the source code for a suitable servlet in the file at `da-subset-documents-example\src\main\java\com\example\ExampleExternalDataDocumentSource.java`.

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

By its nature, the binary documents access example requires you to modify your deployment of the platform. The Developer essentials scripts can change the SDK `topology.xml` automatically. You must then redeploy the platform by hand.

1.  Optional: <span class="ph cmd">If the development version of the Intelligence Analysis Platform is running, stop the server from Eclipse.</span>
2.  <span class="ph cmd">Open a command prompt as Administrator, and navigate to the `C:\IBM\iap\SDK\sdk-projects\master` directory.</span>
3.  <span class="ph cmd">Run the following command:</span>

    ``` pre
    build -pr da-subset-documents-example -t addDaodDataSource
    ```

    <span class="importanttitle">Important:</span> For successful integration with Eclipse, the value that you provide to the `-pr` option must match the name of the example project.

    When the command runs successfully, it modifies `topology.xml` to add information about a new data source. By default, the data source gets the same name as the value that you provide to the `-pr` option.

    The command also creates a Java library that maps from platform-compatible XML to Intelligence Analysis Platform items. Unlike in the "data load direct" project, the `addDaodDataSource` target of the `build` command automatically determines what schema the deployed platform is using, and performs the necessary configuration.

4.  <span class="ph cmd">Open the topology file that represents your deployment of the Intelligence Analysis Platform in an XML editor.</span> By default, this file is at `C:\IBM\iap\SDK\sdk-projects\master\build\IAP-Deployment-Toolkit\configuration\environment\topology.xml`.
5.  <span class="ph cmd">Edit the `<iap-data-source>` element that defines the data access on-demand data source so that its attributes reflect the functionality of the example:</span>

    ``` pre
    <iap-data-source id="daod1" ar="false">
       <DataSource EdrsPresent="false" ScsPresent="true" SesPresent="true"
                   ScsBrowseSupported="true" ScsSearchSupported="true"
                   ScsNetworkSearchSupported="false"
                   ScsDumbbellSearchSupported="false"
                   ScsFilteredSearchSupported="false"
                   EdrsGetContextSupported="false"
                   EdrsGetLatestItemsSupported="false" Id="" Version="0">
          <Shape>DAOD</Shape>
          <Name>da-subset-documents-example</Name>
       </DataSource>
    </iap-data-source>
    ```

    Make a record of the ID. The default ID is `daod1` if no other data sources are added.

6.  <span class="ph cmd">To redeploy the Intelligence Analysis Platform, run the following command:</span>

    ``` pre
    build -t deploy
    ```

The commands so far added an incomplete application to the server. (The application includes all the infrastructure for supporting data access on-demand, but no actual implementation.) You must remove this application from the workspace and replace it with the example.

1.  <span class="ph cmd">Refresh the Eclipse user interface to ensure that it reflects the current state of the project.</span>
2.  <span class="ph cmd">In your Eclipse workspace, open the <span class="ph filepath">WebSphere Application Server Liberty Profile/servers/iap/apps</span> folder, and delete the `da-subset-documents-example.war` application.</span>
3.  <span class="ph cmd">Repeat the instructions that you followed when you added the `master` directory to your Eclipse workspace. This time, add the `C:\IBM\iap\SDK\sdk-projects\da-subset-documents-example` directory to Eclipse.</span>
4.  <span class="ph cmd">In the **Servers** tab at the bottom of the Eclipse application window, right-click the **iap** server and select <span class="ph uicontrol">Add and Remove</span>.</span>
5.  <span class="ph cmd">In the <span class="keyword wintitle">Add and Remove</span> window, move **da-subset-documents-example** from the **Available** list to the **Configured** list, and then click **Finish** to close the window.</span>

At this stage, the data access on-demand aspect of the example is complete. The following steps describe how to enable the binary documents or images to be included in the returned data.

1.  <span class="ph cmd">Move the example image files to the HTTP server.</span>
    1.  <span class="ph cmd">In Windows Explorer, navigate to the `IBM\HTTPServer\htdocs\` directory and create a directory called `ImageStore`.</span>
    2.  <span class="ph cmd">Navigate to the `SDK\sdk-projects\da-subset-documents-example\fragment\documents` directory and copy the four `.jpg` files to the `IBM\HTTPServer\htdocs\ImageStore` directory. </span>

2.  <span class="ph cmd">In Windows Explorer, navigate to the `\master\build\IAP-Deployment-Toolkit\configuration\environment\dsid` directory.</span>

    The `dsid` directory is populated and maintained by the deployment scripts. This directory contains settings files for each of the data sources that are defined in `topology.xml`.

3.  <span class="ph cmd">Open the `dsid.daod1.properties` file for your data source in a text editor, where `daod1` is the ID value in the `<iap-data-source>` element of the topology file. Record the value of the `DataSourceId` property, which is the GUID generated when the Intelligence Analysis Platform was redeployed.</span>
4.  <span class="ph cmd">Open the `httpd.conf` file for the HTTP Server.</span> By default, the file is at `C:\IBM\HTTPServer\conf\httpd.conf`.
5.  <span class="ph cmd">Add the following code before the `Begin#IBM#IAP#Generated#Configuration#RewriteRules` line.</span>

    ``` pre
    RewriteCond %{QUERY_STRING} ^documentId=DA-documents/([^&]+)
    RewriteRule ^/apollo/services/download /ImageStore/%1? [P]
    RewriteCond %{QUERY_STRING} ^documentId=DA-servlet/([^&]+)
    RewriteRule ^/apollo/services/download /DataSourceId/services/dadownload?documentId=%1 [P]
    ```

    Here, `ImageStore` is the location of the image files that you copied previously, and `DataSourceId` is the value from the `dsid.daod1.properties` file.

    In these lines, the `RewriteCond` statements match items with document identifiers that begin with `DA-`. (That prefix is added to the item data during the transformation process, so that the Intelligence Analysis Platform can identify binary documents that are not in its own document store.)

    For items that refer to a static image, the first `RewriteRule` generates the URL of an image file. For items that refer to an image that the servlet creates, the second `RewriteRule` generates the URL of a call to a servlet method.

6.  <span class="ph cmd">Use the Services application in Windows (`services.msc`) to restart IBM HTTP Server.</span>

At this stage, deployment of the binary documents access example is complete. You can start the Intelligence Analysis Platform, and connect to it through the Intelligence Portal.

1.  <span class="ph cmd">In Eclipse, start the server.</span>
2.  <span class="ph cmd">In a web browser, load or refresh your view of the Intelligence Portal. Browse the "da-subset-documents-example" data source to see that the items to which it provides access are available to the user.</span>

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


