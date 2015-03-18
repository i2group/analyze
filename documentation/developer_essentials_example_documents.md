Configuring the "binary documents access" example project
=========================================================

The "data access on-demand" approach to data acquisition in the Intelligence Analysis Platform involves querying external data through a new service on the read side of the platform. In Developer Essentials, the `da-subset-documents-example` example extends the `da-subset-filesystem-example` example to include images or other binary documents in the returned data.

Before you begin
----------------

The binary documents access example project requires the development version of the Intelligence Analysis Platform, prepared according to the instructions in the deployment guide. However, the example has no additional requirements. You do not need to configure the `da-subset-filesystem-example` example project before you configure this project.

Note: All the data acquisition example projects in Developer Essentials use the same XML data, and the same code for transforming and converting that data into Intelligence Analysis Platform items. These artifacts are in the `IAP-Deployment-Toolkit\SDK\sdk-projects\da-example-common` directory, to which all of the examples have access.

About this task
---------------

The binary documents access example defines and configures a pair of read-side services that provide users with access to an external data source through the Intelligence Portal. Configuring the example requires you to add the data source to the existing Intelligence Analysis Platform deployment, and to add the services to the read side of the platform.

The example demonstrates two different mechanisms for including binary documents in the returned data: through a direct reference to the URL of a document, or through a call to an implementation of the `IExternalDataDocumentSource` interface. The sample data contains items that are associated with static image files through the first mechanism, and items with images that are returned by a servlet through the second.

In the example, the HTTP Server configuration file `httpd.conf` is used to manipulate the `<DocumentId>` element of a transformed item into the URL of a static file or a servlet. The file contains rewrite conditions that identify references to documents, and rewrite rules that convert those references into URLs. Depending on the contents of the references, the URLs target a directory that contains image files, or a servlet method.

Note: The binary documents access example includes the source code for a suitable servlet in the file at `src\main\com\example\ExampleExternalDataDocumentSource.java`.

Procedure
---------

The examples in Developer Essentials require links to the Intelligence Analysis Platform libraries, and to the API documentation. These links are configured in a separate project directory that you must import into Eclipse before you can build any of the example projects.

1.  If you did not already add the `shared` directory to your Eclipse workspace while you worked with one of the other examples, add it now.
    1.  In Eclipse, click **Window** \> **Preferences** to open the Preferences window.
    2.  In the Preferences window, select **General** \> **Workspace** \> **Linked Resources**.
    3.  Add an entry to the **Defined path variables** list:

        -   Name: `TOOLKIT_ROOT`
        -   Location: `C:\IBM\iap-3.0.9\IAP-Deployment-Toolkit`

        The shared libraries and several of the example projects rely on the presence of `TOOLKIT_ROOT` in your development environment.

    4.  In Eclipse, click **File** \> **Import** to open the Import window.
    5.  In the Import window, click **General** \> **Existing Projects into Workspace**, and then click **Next**.
    6.  Click **Browse** at the top of the window, and then select the `IAP-Deployment-Toolkit\SDK\sdk-projects\shared` directory.
    7.  Click **Finish** to complete the import process.

By its nature, the binary documents access example requires you to modify your deployment of the platform. The scripts in the Deployment Toolkit can change `topology.xml` automatically. You must then redeploy the platform by hand.

1.  Optional: If the development version of the Intelligence Analysis Platform is running, stop the servers from Eclipse.
2.  Open a command prompt as Administrator, and navigate to the `IAP-Deployment-Toolkit\scripts` directory.
3.  Run the following command:

    ``` {.pre .codeblock}
    python da-setup.py -t add-daod-data-source -a read -d da-subset-documents-example
    ```

    Important: For successful integration with Eclipse, the value that you provide to the `-d` option must match the name of the example project.

    When the command runs successfully, it modifies `topology.xml` to add information about a new data source. By default, the data source gets the same name as the value that you provide to the `-d` option.

    The command also creates a Java library that maps from platform-compatible XML to Intelligence Analysis Platform items. Unlike in the "data load direct" project, the `add-daod-data-source` target of the `da-setup.py` command automatically determines what schema the deployed platform is using, and performs the necessary configuration.

4.  Open the topology file that represents your deployment of the Intelligence Analysis Platform in an XML editor. By default, this file is at `IAP-Deployment-Toolkit\configuration\environment\topology.xml`.
5.  Edit the `<iap-data-source>` element that defines the data access on-demand data source so that its attributes reflect the functionality of the example:

    ``` {.pre .codeblock}
    <iap-data-source id="daod1" ar="false">
       <DataSource EdrsPresent="false" ScsPresent="true" SesPresent="true"
                   ScsBrowseSupported="true" ScsSearchSupported="true"
                   ScsNetworkSearchSupported="false"
                   ScsDumbbellSearchSupported="false"
                   ScsFilteredSearchSupported="false"
                   EdrsGetContextSupported="false"
                   EdrsGetLatestItemsSupported="false" Id="" Version="0">
          <Shape>DAOD</Shape>
          <Name>DAOD Document Example</Name>
       </DataSource>
    </iap-data-source>
    ```

    Make a record of the ID. The default ID is `daod1` if no other data sources are added.

6.  Redeploy the Intelligence Analysis Platform by running the same commands that you used to deploy the platform originally:

    ``` {.pre .codeblock}
    python deploy.py -s write -t deploy-liberty
    python deploy.py -s read -t deploy-liberty
    ```

    Note: Sometimes, the `deploy-liberty` command can fail. It displays the following error message:

    AMQ6004: An error occurred during WebSphere MQ initialization or ending.

    The failure occurs when the command attempts to delete and re-create the WebSphere MQ queue manager. To resolve the problem, use WebSphere MQ Explorer to delete the queue manager, and then rerun the `deploy-liberty` command.

7.  Use the Services application in Windows (`services.msc`) to restart IBM HTTP Server. Do not attempt to start the application server or the web applications yet.

The commands so far added an incomplete application to the read-side server. (The application includes all the infrastructure for supporting data access on-demand, but no actual implementation.) You must remove this application from the workspace and replace it with the example.

1.  Refresh the Eclipse user interface to ensure that it reflects the current state of the project.
2.  In your Eclipse workspace, open the WebSphere Application Server V8.5 Liberty Profile/servers/read/apps folder, and delete the `da-subset-documents-example.war` application.
3.  Repeat the instructions that you followed when you added the `shared` directory to your Eclipse workspace. This time, add the `IAP-Deployment-Toolkit\SDK\sdk-projects\da-subset-documents-example` directory to Eclipse.
4.  In the **Servers** tab at the bottom of the Eclipse application window, right-click the **read** server and select Add and Remove.
5.  In the Add and Remove window, move **da-subset-documents-example** from the **Available** list to the **Configured** list, and then click **Finish** to close the window.

At this stage, the data access on-demand aspect of the example is complete. The following steps describe how to enable the binary documents or images to be included in the returned data.

1.  Move the example image files to the HTTP Server.
    1.  In Windows Explorer, navigate to the `IBM\HTTPServer\htdocs\` directory and create a directory called `ImageStore`.
    2.  Navigate to the `IAP-Deployment-Toolkit\SDK\sdk-projects\da-subset-documents-example\documents` directory and copy the four `.jpg` files to the `IBM\HTTPServer\htdocs\ImageStore` directory.

2.  In Windows Explorer, navigate to the `IAP-Deployment-Toolkit\configuration\environment\dsid` directory.

    The `dsid` directory is populated and maintained by the deployment scripts. This directory contains settings files for each of the data sources that are defined in `topology.xml`.

3.  Open the `dsid.daod1.properties` file for your data source in a text editor, where `daod1` is the ID value in the `<iap-data-source>` element of the topology file. Record the value of the `DataSourceId` property, which is the GUID generated when the Intelligence Analysis Platform was redeployed.
4.  Open the `httpd.conf` file for the HTTP Server. By default, the file is at `C:\IBM\HTTPServer\conf\httpd.conf`.
5.  Add the following code before the `Begin#IBM#IAP#Generated#Configuration#RewriteRules` line.

    ``` {.pre .codeblock}
    RewriteCond %{QUERY_STRING} ^documentId=DA-documents/([^&]+)
    RewriteRule ^/apollo/services/download/ImageStore/%1? [P]
    RewriteCond %{QUERY_STRING} ^documentId=DA-servlet/([^&]+)
    RewriteRule ^/apollo/services/download/DataSourceId/services/dadownload?documentId=%1 [P]
    ```

    Here, `ImageStore` is the location of the image files that you copied previously, and `DataSourceId` is the value from the `dsid.daod1.properties` file.

    In these lines, the `RewriteCond` statements match items with document identifiers that begin with `DA-`. (That prefix is added to the item data during the transformation process, so that the Intelligence Analysis Platform can identify binary documents that are not in its own document store.)

    For items that refer to a static image, the first `RewriteRule` generates the URL of an image file. For items that refer to an image that the servlet creates, the second `RewriteRule` generates the URL of a call to a servlet method.

At this stage, deployment of the binary documents access example is complete. You can start the Intelligence Analysis Platform, and connect to it through the Intelligence Portal.

1.  In Eclipse, start the write server, followed by the read server.
2.  In a web browser, load or refresh your view of the Intelligence Portal. Browse the "DAOD Document Example" data source to see that the items to which it provides access are available to the user.

* * * * *

© Copyright IBM Corporation 2015.


