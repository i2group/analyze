Configuring the "binary documents access" example project
=========================================================

The "data access on-demand" approach to data acquisition in i2 Analyze involves querying external data through a new service. In Developer Essentials, the `da-subset-documents-example` example extends the `da-subset-filesystem-example` example to include images or other binary documents in the returned data.

Before you begin
----------------

The binary documents access example project requires the development version of i2 Analyze, prepared according to the instructions in the deployment guide. However, the example has no additional requirements. You do not need to configure the `da-subset-filesystem-example` example project before you configure this project.

<span class="notetitle">Note:</span> All the data acquisition example projects in Developer Essentials use the same XML data, and the same code for transforming and converting that data into i2 Analyze items. These artifacts are in the `C:\IBM\i2analyze\SDK\sdk-projects\da-example-common` directory, to which all of the data acquisition examples have access.

About this task
---------------

The binary documents access example defines and configures a pair of services that provide users with access to an external data source through the Intelligence Portal. Configuring the example requires you to add the data source to the existing i2 Analyze deployment, and to add the services to the platform.

The example demonstrates two different mechanisms for including binary documents in the returned data: through a direct reference to the URL of a document, or through a call to an implementation of the `IExternalDataDocumentSource` interface. The sample data contains items that are associated with static image files through the first mechanism, and items with images that are returned by a servlet through the second mechanism.

In the example, the rewrite rules file `http-custom-rewrite-rules.txt` is used to manipulate the `<DocumentId>` element of a transformed item into the URL of a static file or a servlet. The file contains rewrite conditions that identify references to documents, and rewrite rules that convert those references into URLs. Depending on the contents of the references, the URLs target a directory that contains image files, or a servlet method.

<span class="notetitle">Note:</span> The binary documents access example includes the source code for a suitable servlet in the file at `C:\IBM\i2analyze\SDK\sdk-projects\da-subset-documents-example\src\main\java\com\example\ExampleExternalDataDocumentSource.java`.

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

By its nature, the binary documents access example requires you to modify your deployment of the platform. The scripts in the Deployment Toolkit can change `topology.xml` automatically. You must then redeploy the platform by hand.

1.  <span class="ph cmd">If the development version of i2 Analyze is running, stop the server from Eclipse.</span>
2.  <span class="ph cmd">Open a command prompt as Administrator, and navigate to the `C:\IBM\i2analyze\SDK\sdk-projects\master` directory.</span>
3.  <span class="ph cmd">Run the following command:</span>

    ``` pre
    build -pr da-subset-documents-example -t addDaodDataSource
    ```

    <span class="importanttitle">Important:</span> For successful integration with Eclipse, the value that you provide to the `-pr` option must match the name of the example project.

    When the command runs successfully, it modifies `topology.xml` to add information about a new data source. By default, the data source gets the same name as the value that you provide to the `-pr` option.

    The command also creates a Java library that maps from platform-compatible XML to i2 Analyze items. Unlike in the "data load direct" project, the `addDaodDataSource` target of the `build` command automatically determines what schema the deployed platform is using, and performs the necessary configuration.

4.  <span class="ph cmd">Open the topology file that represents your deployment of i2 Analyze in an XML editor.</span> By default, this file is at `C:\IBM\i2analyze\SDK\sdk-projects\master\build\toolkit\configuration\environment\topology.xml`.
5.  <span class="ph cmd">Edit the `<i2-data-source>` element that defines the data access on-demand data source so that its attributes reflect the functionality of the example:</span>

    ``` pre
    <i2-data-source id="daod1" ar="false">
       <DataSource ScsSearchSupported="true" EdrsGetContextSupported="false" 
                   ScsNetworkSearchSupported="false" EdrsGetLatestItemsSupported="false"
                   Version="0" SesPresent="true" ScsFilteredSearchSupported="false"
                   ScsBrowseSupported="true" Id="" ScsDumbbellSearchSupported="false"
                   ScsPresent="true" EdrsPresent="false"
                   >
          <Shape>DAOD</Shape>
          <Name>da-subset-documents-example</Name>
       </DataSource>
    </i2-data-source>
    ```

    <span class="notetitle">Note:</span> If this is the first data source that you added, the value is `daod1`.

6.  <span class="ph cmd">To redeploy i2 Analyze, run the following command:</span>

    ``` pre
    build -t deploy
    ```

The commands so far deployed the example project application from the toolkit. To enable easy debugging of your code, the next steps are all about replacing the application that you deployed from the toolkit with code from Eclipse.

1.  <span class="ph cmd">Refresh the Eclipse user interface to ensure that it reflects the current state of the project.</span>
2.  <span class="ph cmd">In your Eclipse workspace, open the <span class="ph filepath">WebSphere Application Server Liberty Profile/servers/i2analyze/apps</span> folder, and delete the `da-subset-documents-example.war` application.</span>
3.  <span class="ph cmd">Repeat the instructions that you followed when you added the `master` directory to your Eclipse workspace. This time, add the `C:\IBM\i2analyze\SDK\sdk-projects\da-subset-documents-example` directory to Eclipse.</span>
4.  <span class="ph cmd">Ensure that the JAR file from `da-example-common` is loaded correctly into the deployment assembly for the `da-subset-documents-example` project:</span>
    1.  <span class="ph cmd">In Package Explorer, right-click **da-subset-documents-example**, and select **Properties** to display the Properties window.</span>
    2.  <span class="ph cmd">Click **Deployment Assembly**, and look for any error messages about `da-example-common`.</span>
    3.  <span class="ph cmd">If there are error messages, remove `da-example-common` from the packaging structure, and then use <span class="ph menucascade">**Add** \> **Project**</span> to add it again.</span>

5.  <span class="ph cmd">In the **Servers** tab at the bottom of the Eclipse application window, right-click the **i2analyze** server and select <span class="ph uicontrol">Add and Remove</span>.</span>
6.  <span class="ph cmd">In the <span class="keyword wintitle">Add and Remove</span> window, move **da-subset-documents-example** from the **Available** list to the **Configured** list, and then click **Finish** to close the window.</span>

At this stage, the data access on-demand aspect of the example is complete. The following steps describe how to enable the binary documents or images to be included in the returned data.

1.  <span class="ph cmd">Copy the example image files to the HTTP server.</span>
    1.  <span class="ph cmd">In Windows Explorer, navigate to the `C:\IBM\HTTPServer\htdocs` directory and create a directory called `ImageStore`.</span>
    2.  <span class="ph cmd">Navigate to the `C:\IBM\i2analyze\SDK\sdk-projects\da-subset-documents-example\fragment\documents` directory and copy the four `.jpg` files to the `C:\IBM\HTTPServer\htdocs\ImageStore` directory. </span>

2.  <span class="ph cmd">Open the `httpd.conf` file, by default the file is at `C:\IBM\HTTPServer\conf\httpd.conf`, and comment out any lines that begin with `Win32DisableAcceptEx`.</span> For example:

    ``` pre
    # Win32DisableAcceptEx
    ```

3.  <span class="ph cmd">Open the `http-custom-rewrite-rules.txt` file.</span> By default, the file is in the `SDK\sdk-projects\master\build\toolkit\configuration\environment\proxy` directory.
    1.  <span class="ph cmd">Add the following code between the `!Start_After_Rules!` and `!End_After_Rules!` lines:</span>

        ``` pre
        RewriteCond %{QUERY_STRING} ^documentId=DA-documents/([^&]+)
        RewriteRule ^/apollo/services/download /ImageStore/%1? [PT]
        RewriteCond %{QUERY_STRING} ^documentId=DA-servlet/([^&]+)
        RewriteRule ^/apollo/services/download /${contextRoot:da-subset-documents-example}/services/dadownload?documentId=%1 [PT]
        ```

        Here, `da-subset-documents-example` is the war name for the example. To find the war name, open the `topology.xml` file and locate the `war` element where the value of the `i2-data-source` attribute matches the data source that was added previously. The war name is the value of the `name` attribute of this element.

        In these lines, the `RewriteCond` statements match items with document identifiers that begin with `DA-`. (That prefix is added to the item data during the transformation process, so that i2 Analyze can identify binary documents that are not in its own document store.)

        For items that refer to a static image, the first `RewriteRule` generates the URL of an image file. For items that refer to an image that the servlet creates, the second `RewriteRule` generates the URL of a call to a servlet method.

4.  <span class="ph cmd">To redeploy i2 Analyze, run the following command:</span>

    ``` pre
    build -t deploy
    ```

5.  <span class="ph cmd">Use the Services application in Windows (`services.msc`) to restart IBM HTTP Server.</span>

At this stage, deployment of the binary documents access example is complete. You can start i2 Analyze, and connect to it through the Intelligence Portal.

1.  <span class="ph cmd">In Eclipse, start the server.</span>
2.  <span class="ph cmd">In a web browser, load or refresh your view of the Intelligence Portal. Browse the **da-subset-documents-example** data source to see that the items to which it provides access are available to the user.</span>

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

5.  <span class="ph cmd">Use the Services application in Windows (`services.msc`) to restart IBM HTTP Server.</span>

**Parent topic:** [IBM i2 Analyze Developer Essentials](developer_essentials_welcome.html "IBM i2 Analyze Developer Essentials contains tools, libraries, and examples that enable development and deployment of custom extensions to i2 Analyze.")

------------------------------------------------------------------------

© Copyright IBM Corporation 2014, 2016.


