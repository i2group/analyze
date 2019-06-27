Configuring the "binary documents access" example project
=========================================================

The "data access on-demand" approach to data acquisition in an i2 Analyze deployment with the Onyx services involves querying external data through a new service. In Developer Essentials, the `onyx-da-subset-documents-example` example extends the `onyx-da-subset-filesystem-example` example to include images or other binary documents in the returned data.

Before you begin
----------------

You must have a development deployment of i2 Analyze and a configured development environment. For more information, see <a href="developer_essentials_deploying.md" class="xref" title="IBM i2 Analyze Developer Essentials is a set of files and example projects that build on a standard i2 Analyze deployment. Preparing to use Developer Essentials involves installing and configuring it to work in a dedicated test environment.">Preparing to use IBM i2 Analyze Developer Essentials</a>.

About this task
---------------

The binary documents access example defines and configures a pair of services that provide users with access to an external data source through the Intelligence Portal. Configuring the example requires you to add the data source to the existing i2 Analyze deployment, and to add the services to the platform.

The example demonstrates two different mechanisms for including binary documents in the returned data: through a direct reference to the URL of a document, or through a call to an implementation of the `IExternalDataDocumentSource` interface. The sample data contains items that are associated with static image files through the first mechanism, and items with images that a servlet returns through the second mechanism.

In the example, the rewrite rules file `http-custom-rewrite-rules.txt` is used to manipulate the `<DocumentId>` element of a transformed item into the URL of a static file or a servlet. The file contains rewrite conditions that identify references to documents, and rewrite rules that convert those references into URLs. Depending on the contents of the references, the URLs target a directory that contains image files, or a servlet method.

Note: The binary documents access example includes the source code for a suitable servlet in the file at `C:\IBM\i2analyze\SDK\sdk-projects\onyx-da-subset-documents-example\src\main\java\com\example\ExampleExternalDataDocumentSource.java`.

Procedure
---------

The binary documents access example requires you to modify your deployment of the platform. The scripts in the deployment toolkit can change `topology.xml` automatically. You must then redeploy the platform by hand.

1.  Navigate to the `toolkit\scripts` directory and run the following command to add a data access on-demand data source to the deployment:
    ``` pre
    setup -t addDaodDataSource -dn onyx-da-subset-documents-example -s onyx-server
    ```

    When the command runs successfully, it modifies the topology file in your development version of i2 Analyze to add information about a new data source. By default, the data source gets the same name as the value that you provide to the `-dn` option.

    The command also creates a Java library that maps from platform-compatible XML to Analysis Repository items. Unlike in the "data load direct" project, the `addDaodDataSource` target of the `setup` command automatically determines what schema the deployed platform is using, and performs the necessary configuration.

The data acquisition example projects in Developer Essentials use the same XML data, and the same code for transforming and converting that data into items in the Analysis Repository. These artifacts are in the `onyx-da-example-common` project, which you must add to your development environment. You can import this project into Eclipse by completing the following instructions.

1.  Import the `onyx-da-example-common` project.
    1.  In Eclipse, click **File** &gt; **Import** to open the Import window.
    2.  In the Import window, click **General** &gt; **Existing Projects into Workspace**, and then click **Next**.
    3.  Click **Browse** at the top of the window, and then select the `C:\IBM\i2analyze\SDK\sdk-projects\onyx-da-example-common` directory.
    4.  Click **Finish** to complete the import process.

2.  The binary documents access example requires the `onyx-da-subset-documents-example` project, which contains the source code that is specific to this example. You must add this project to your development environment. If you are using Eclipse, you can repeat step 1 to import the `SDK\sdk-projects\onyx-da-subset-documents-example` project.

After you add the project to your development environment, you can inspect the behavior of the example.

1.  To be able to test the implementation, you must build a JAR file of the project and export it into your i2 Analyze toolkit. If you are using Eclipse, complete the following instructions.
    1.  In Eclipse, double-click the `project.jardesc` file in the `onyx-da-subset-documents-example` project to open the Export window with default settings for the project applied.
    2.  In the Export window, change the **Export destination** to `toolkit\configuration\fragments\onyx-da-subset-documents-example\WEB-INF\lib\onyx-da-subset-documents-example.jar`.
    3.  Click **Finish** to export the JAR file to the specified destination.
        If the directory that you specified does not exist, a message is displayed. If you click **Yes**, the directory structure is created for you

    If you are not using Eclipse, you must build the JAR file according to your development environment.

    The built JAR file must comprise the contents of the `src/main/java` directories of the `onyx-da-subset-documents-example` and `onyx-da-example-common` projects. Your JAR file must be named `onyx-da-subset-documents-example.jar` and be in the `toolkit\configuration\fragments\onyx-da-subset-documents-example\WEB-INF\lib` directory.

Your deployment of i2 Analyze requires the binary documents access example `fragment` directory that includes the configuration files. For the binary documents access example, the deployment also requires the `fragment` directory from the `onyx-da-example-common` project.

1.  Copy the contents of the `SDK\sdk-projects\onyx-da-subset-documents-example\fragment` directory to the `toolkit\configuration\fragments\onyx-da-subset-documents-example` directory. Overwrite any old files with the new versions.
2.  The example data is contained within the `onyx-da-example-common` fragment. Copy the contents of the `SDK\sdk-projects\onyx-da-example-common\fragment` directory to the `toolkit\configuration\fragments\onyx-da-subset-documents-example` directory. Overwrite any old files with the new versions.
3.  To make the data access on-demand data source available, you must add the data source to the `topology.xml` file.
    1.  In an XML editor, open the `toolkit\configuration\environment\topology.xml` file.
    2.  Edit the `<i2-data-source>` element that defines the data access on-demand data source so that its attributes reflect the functionality of the example:
        ``` pre
        <i2-data-source id="daod1" ar="false">
           <DataSource ScsSearchSupported="true" EdrsGetContextSupported="false" 
                       ScsNetworkSearchSupported="false" EdrsGetLatestItemsSupported="false"
                       Version="0" SesPresent="true" ScsFilteredSearchSupported="false"
                       ScsBrowseSupported="true" Id="" ScsDumbbellSearchSupported="false"
                       ScsPresent="true" EdrsPresent="false">
              <Shape>DAOD</Shape>
              <Name>onyx-da-subset-documents-example</Name>
           </DataSource>
        </i2-data-source>
        ```

        Note: The first data access on-demand data source that you add to the topology file gets the identifier `daod1`. Later additions receive different identifiers.

At this stage, the data access on-demand aspect of the example is complete. The following steps describe how to enable the binary documents or images to be included in the returned data.

1.  Copy the example image files to the HTTP server.
    1.  In Windows Explorer, navigate to the `C:\IBM\HTTPServer\htdocs` directory and create a directory called `ImageStore`.
    2.  Navigate to the `C:\IBM\i2analyze\SDK\sdk-projects\onyx-da-subset-documents-example\fragment\documents` directory and copy the four `.jpg` files to the `C:\IBM\HTTPServer\htdocs\ImageStore` directory.

2.  Navigate to the `C:\IBM\HTTPServer\conf` directory and open the `httpd.conf` configuration file in a text editor.
3.  Comment out any lines in the file that begin with `Win32DisableAcceptEx`, and then save and close the file.
    For example:
    ``` pre
    # Win32DisableAcceptEx
    ```

4.  Navigate to the `toolkit\configuration\environment\proxy` directory, and open the `http-custom-rewrite-rules.txt` file in a text editor.
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

6.  Navigate to the `toolkit\scripts` directory and run the following command to redeploy i2 Analyze with all the changes that you made:
    ``` pre
    setup -t deploy -s onyx-server
    ```

At this stage, deployment of the binary documents access example is complete. You can start i2 Analyze, and connect to it through the Intelligence Portal.

1.  Run the following command to start the i2 Analyze server:
    ``` pre
    setup -t start -s onyx-server
    ```

2.  Use the Services application in Windows ( `services.msc` ) to restart IBM HTTP Server.
3.  In a web browser, load or refresh your view of the Intelligence Portal. Browse the **onyx-da-subset-documents-example** data source to see that the items to which it provides access are available to the user.
4.  Debug the `onyx-da-subset-documents-example` project by using Eclipse and your deployment of i2 Analyze. For more information about debugging the example, see <a href="https://github.com/IBM-i2/Analyze/blob/master/documentation/developer_essentials_debug.md" class="xref" title="(Opens in a new tab or window)">Debugging Developer Essentials in Eclipse</a>.

After you develop and test a data access on-demand solution for i2 Analyze, the next step is to publish it to a live deployment. Complete the following steps on your live deployment of i2 Analyze.

1.  Navigate to the `toolkit\scripts` directory of your live deployment, and run the following command to add a data access on-demand data source to the deployment:
    ``` pre
    setup -t addDaodDataSource -dn onyx-da-subset-documents-example -s onyx-server
    ```

    By default, the data source gets the same name as the value that you provide to the `-dn` option, but you can modify the name later if you want to.

2.  Copy the `onyx-da-subset-documents-example` fragment directory from your development deployment to your live deployment.
3.  Update the `topology.xml` and `http-custom-rewrite-rules.txt` files with the same changes that you made to your development environment.
4.  Redeploy the live deployment of i2 Analyze.

**Parent topic:** <a href="developer_essentials_welcome.md" class="link" title="IBM i2 Analyze Developer Essentials contains tools, libraries, and examples that enable development and deployment of custom extensions to i2 Analyze. Developer Essentials also includes API documentation and guides to deploying the software and the example projects.">IBM i2 Analyze Developer Essentials</a>

------------------------------------------------------------------------

© Copyright IBM Corporation 2014, 2019.


