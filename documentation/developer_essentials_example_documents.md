Configuring the "binary documents access" example project
=========================================================

The "data access on-demand" approach to data acquisition in i2 Analyze involves querying external data through a new service. In Developer Essentials, the `onyx-da-subset-documents-example` example extends the `onyx-da-subset-filesystem-example` example to include images or other binary documents in the returned data.

Before you begin
----------------

The binary documents access example project requires the development version of i2 Analyze, prepared according to the Developer Essentials deployment instructions. However, the example has no additional requirements. You do not need to configure the `onyx-da-subset-filesystem-example` example project before you configure this project.

Note: All the data acquisition example projects in Developer Essentials use the same XML data, and the same code for transforming and converting that data into items in the Analysis Repository. These artifacts are in the `SDK\sdk-projects\onyx-da-example-common` directory, to which all of the data acquisition examples have access.

About this task
---------------

The binary documents access example defines and configures a pair of services that provide users with access to an external data source through the Intelligence Portal. Configuring the example requires you to add the data source to the existing i2 Analyze deployment, and to add the services to the platform.

The example demonstrates two different mechanisms for including binary documents in the returned data: through a direct reference to the URL of a document, or through a call to an implementation of the `IExternalDataDocumentSource` interface. The sample data contains items that are associated with static image files through the first mechanism, and items with images that a servlet returns through the second mechanism.

In the example, the rewrite rules file `http-custom-rewrite-rules.txt` is used to manipulate the `<DocumentId>` element of a transformed item into the URL of a static file or a servlet. The file contains rewrite conditions that identify references to documents, and rewrite rules that convert those references into URLs. Depending on the contents of the references, the URLs target a directory that contains image files, or a servlet method.

Note: The binary documents access example includes the source code for a suitable servlet in the file at `C:\IBM\i2analyze\SDK\sdk-projects\onyx-da-subset-documents-example\src\main\java\com\example\ExampleExternalDataDocumentSource.java`.

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

2.  Repeat steps 1d to 1g to import `C:\IBM\i2analyze\SDK\sdk-projects\onyx-da-example-common` into Eclipse.

By its nature, the binary documents access example requires you to modify your deployment of the platform. The scripts in the deployment toolkit can change `topology.xml` automatically. You must then redeploy the platform by hand.

1.  If the development version of the `onyx-server` is running, stop the server from Eclipse.
2.  Open a command prompt as Administrator, and navigate to the `C:\IBM\i2analyze\SDK\sdk-projects\master` directory.
3.  Run the following command:

    ``` pre
    build -pr onyx-da-subset-documents-example -t addDaodDataSource
    ```

    Important: For successful integration with Eclipse, the value that you provide to the `-pr` option must match the name of the example project.

    When the command runs successfully, it modifies the topology file in your development version of i2 Analyze to add information about a new data source. By default, the data source gets the same name as the value that you provide to the `-pr` option.

    The command also creates a Java library that maps from platform-compatible XML to Analysis Repository items. Unlike in the "data load direct" project, the `addDaodDataSource` target of the `build` command automatically determines what schema the deployed platform is using, and performs the necessary configuration.

4.  Open the topology file that represents your deployment of i2 Analyze in an XML editor. By default, this file is at `C:\IBM\i2analyze\SDK\sdk-projects\master\build\toolkit\configuration\environment\topology.xml`.
5.  Edit the `<i2-data-source>` element that defines the data access on-demand data source so that its attributes reflect the functionality of the example:

    ``` pre
    <i2-data-source id="daod1" ar="false">
       <DataSource ScsSearchSupported="true" EdrsGetContextSupported="false" 
                   ScsNetworkSearchSupported="false" EdrsGetLatestItemsSupported="false"
                   Version="0" SesPresent="true" ScsFilteredSearchSupported="false"
                   ScsBrowseSupported="true" Id="" ScsDumbbellSearchSupported="false"
                   ScsPresent="true" EdrsPresent="false"
                   >
          <Shape>DAOD</Shape>
          <Name>onyx-da-subset-documents-example</Name>
       </DataSource>
    </i2-data-source>
    ```

    Note: The first data access on-demand data source that you add to the topology file gets the identifier `daod1`. Later additions receive different identifiers.

6.  To redeploy i2 Analyze, run the following command:

    ``` pre
    build -t deploy -s onyx-server
    ```

The commands so far deployed the example project application from the deployment toolkit. To enable easy debugging of your code, the next steps are all about replacing the application that you deployed from the toolkit with code from Eclipse.

1.  Refresh the Eclipse user interface to ensure that it reflects the current state of the project.
2.  In your Eclipse workspace, open the `WebSphere Application Server Liberty/servers/onyx-server/apps` folder, and delete the `onyx-da-subset-documents-example.war` application.
3.  Repeat the instructions that you followed when you added the `master` directory to your Eclipse workspace. This time, add the `C:\IBM\i2analyze\SDK\sdk-projects\onyx-da-subset-documents-example` directory to Eclipse.
4.  Ensure that the JAR file from `onyx-da-example-common` is loaded correctly into the deployment assembly for the `onyx-da-subset-documents-example` project:
    1.  In Package Explorer, right-click **onyx-da-subset-documents-example**, and select **Properties** to display the Properties window.
    2.  Click **Deployment Assembly**, and look for any error messages about `onyx-da-example-common`.
    3.  If there are error messages, remove `onyx-da-example-common` from the packaging structure, and then use **Add** &gt; **Project** to add it again.

5.  In the **Servers** tab at the bottom of the Eclipse application window, right-click the **onyx-server** server and select **Add and Remove**.
6.  In the Add and Remove window, move **onyx-da-subset-documents-example** from the **Available** list to the **Configured** list, and then click **Finish** to close the window.

At this stage, the data access on-demand aspect of the example is complete. The following steps describe how to enable the binary documents or images to be included in the returned data.

1.  Copy the example image files to the HTTP server.
    1.  In Windows Explorer, navigate to the `C:\IBM\HTTPServer\htdocs` directory and create a directory called `ImageStore`.
    2.  Navigate to the `C:\IBM\i2analyze\SDK\sdk-projects\onyx-da-subset-documents-example\fragment\documents` directory and copy the four `.jpg` files to the `C:\IBM\HTTPServer\htdocs\ImageStore` directory.

2.  Navigate to the `C:\IBM\HTTPServer\conf` directory and open the `httpd.conf` configuration file in a text editor.
3.  Comment out any lines in the file that begin with `Win32DisableAcceptEx`, and then save and close the file. For example:

    ``` pre
    # Win32DisableAcceptEx
    ```

4.  Navigate to the `SDK\sdk-projects\master\build\toolkit\configuration\environment\proxy` directory, and open the `http-custom-rewrite-rules.txt` file in a text editor.
5.  Add the following code between the `!Start_After_Rules!` and `!End_After_Rules!` lines:

    ``` pre
    RewriteCond %{QUERY_STRING} ^documentId=DA-documents/([^&]+)
    RewriteRule ^/apollo/services/download /ImageStore/%1? [PT]
    RewriteCond %{QUERY_STRING} ^documentId=DA-servlet/([^&]+)
    RewriteRule ^/apollo/services/download /${contextRoot:onyx-da-subset-documents-example}/services/dadownload?documentId=%1 [PT]
    ```

    In these lines, the `RewriteCond` statements match items with document identifiers that begin with `DA-`. (That prefix is added to the item data during the transformation process, so that i2 Analyze can identify binary documents that are not in its own document store.)

    For items that refer to a static image, the first `RewriteRule` generates the URL of an image file. For items that refer to an image that the servlet creates, the second `RewriteRule` generates the URL of a call to a servlet method.

    Note: In the second `RewriteRule`, `onyx-da-subset-documents-example` is the name of the WAR file for this example. In the topology file, this name corresponds to the value of the `name` attribute of the `<war>` element in which the value of the `i2-data-source` attribute matches the name of the data source.

6.  To redeploy i2 Analyze, run the following command:

    ``` pre
    build -t deploy -s onyx-server
    ```

7.  Use the Services application in Windows (`services.msc`) to restart IBM HTTP Server.

At this stage, deployment of the binary documents access example is complete. You can start i2 Analyze and connect to it through the Intelligence Portal.

1.  In Eclipse, start the `onyx-server`.
2.  In a web browser, load or refresh your view of the Intelligence Portal. Browse the **onyx-da-subset-documents-example** data source to see that the items to which it provides access are available to the user.

After you develop and test a data access on-demand solution for i2 Analyze, the next step is to publish it to a live deployment.

These instructions assume that you have access to two instances of i2 Analyze:

-   The development version of the platform, deployed according to the instructions in IBM i2 Analyze Developer Essentials. This instance contains the custom application that you want to publish.
-   The production version of the platform, deployed according to the instructions in the IBM i2 Analyze deployment documentation. This instance has all of its default settings.

1.  Copy `C:\IBM\i2analyze\SDK\sdk-projects\master\build\toolkit\configuration` from the development version into the production version of the deployment toolkit.
2.  Edit the following files to ensure that the default settings match your environment:
    -   `configuration\environment\onyx-server\environment.properties`
    -   `configuration\environment\http-server.properties`
    -   `configuration\environment\credentials.properties`
    -   `configuration\environment\topology.xml`

3.  On the production version of the platform, open a command prompt as Administrator, and navigate to the `toolkit\scripts` directory.
4.  To redeploy the platform, run the following command:

    ``` pre
    setup -t deploy -s onyx-server
    ```

5.  Use the Services application in Windows (`services.msc`) to restart IBM HTTP Server.

**Parent topic:** <a href="developer_essentials_welcome.md" class="link" title="IBM i2 Analyze Developer Essentials contains tools, libraries, and examples that enable development and deployment of custom extensions to i2 Analyze.">IBM i2 Analyze Developer Essentials</a>

------------------------------------------------------------------------

© Copyright IBM Corporation 2014, 2017.


