Configuring the "two-phase data retrieval" example project
==========================================================

i2 Analyze supports variations on the "data access on-demand" approach to data acquisition. One of the examples that IBM provides in Developer Essentials is the `onyx-da-subset-rest-example`, which demonstrates a two-phase technique for data retrieval from an external source. The example also contains code that allows it to be called and displayed by custom code in the Intelligence Portal.

Before you begin
----------------

You must have a development deployment of i2 Analyze and a configured development environment. For more information, see <a href="developer_essentials_deploying.md" class="xref" title="IBM i2 Analyze Developer Essentials is a set of files and example projects that build on a standard i2 Analyze deployment. Preparing to use Developer Essentials involves installing and configuring it to work in a dedicated test environment.">Preparing to use IBM i2 Analyze Developer Essentials</a>.

To test the functionality of the example, you must also configure the user interface extension example project by following the instructions in <a href="https://github.com/IBM-i2/Analyze/blob/master/documentation/developer_essentials_example_ui.md" class="xref" title="(Opens in a new tab or window)">Configuring the &quot;user interface extension&quot; example project</a>.

About this task
---------------

The `onyx-da-subset-rest-example` project takes its name from one of the services that it contains, which has a REST API. This API enables the service to be called easily from code in HTML pages that the Intelligence Portal can host as part of a user interface extension.

Compared with the data access on-demand example project, this example demonstrates a different approach to data acquisition. The first request from a client causes i2 Analyze to query the external data source. The example code places any results in a subset, and returns a token that identifies that subset to the client. If the client specifies the same token in subsequent requests, the platform responds by using data from the subset.

Note: The two-phase data retrieval example is one part of a two-part project that demonstrates custom server functionality in a customized Intelligence Portal. The project is not complete until you configure and deploy both parts, and you cannot interact with this example until you also configure the user interface extension example project.

Procedure
---------

The two-phase data retrieval example requires you to modify your deployment of the platform. The scripts in the deployment toolkit can modify the deployment automatically.

1.  Navigate to the `toolkit\scripts` directory and run the following command to add a data access on-demand data source to the deployment:
    ``` pre
    setup -t addDaodDataSource -dn onyx-da-subset-rest-example -s onyx-server
    ```

    When the command runs successfully, it modifies the topology file in your development version of i2 Analyze to add information about a new data source. By default, the data source gets the same name as the value that you provide to the `-dn` option, but you can modify the name later if you want to.

The two-phase data retrieval example project relies on the presence of `WLP_HOME_DIR` in your development environment. This directory is the home directory of Liberty. You can configure this in Eclipse by completing the following instructions.

1.  Define the class path for Liberty in Eclipse so that you can work with the example:
    1.  In Eclipse, click **Window** &gt; **Preferences** to open the Preferences window.
    2.  In the Preferences window, select **General** &gt; **Workspace** &gt; **Linked Resources**.
    3.  Add an entry to the **Defined path variables** list:
        -   Name: `WLP_HOME_DIR`
        -   Location: `C:\IBM\i2analyze\deploy\wlp`

The data acquisition example projects in Developer Essentials use the same XML data, and the same code for transforming and converting that data into items in the Analysis Repository. These artifacts are in the `onyx-da-example-common` project, which you must add to your development environment. You can import this project into Eclipse by completing the following instructions.

1.  Import the `onyx-da-example-common` project.
    1.  In Eclipse, click **File** &gt; **Import** to open the Import window.
    2.  In the Import window, click **General** &gt; **Existing Projects into Workspace**, and then click **Next**.
    3.  Click **Browse** at the top of the window, and then select the `C:\IBM\i2analyze\SDK\sdk-projects\onyx-da-example-common` directory.
    4.  Click **Finish** to complete the import process.

2.  The two-phase data retrieval example requires the `onyx-da-subset-rest-example` project which contains the source code that is specific to this example. You must add this project to your development environment. If you are using Eclipse, you can repeat step 1 to import the `SDK\sdk-projects\onyx-da-subset-rest-example` project.

After you add the project to your development environment, you can inspect the behavior of the example.

1.  To be able to test the implementation, you must build a JAR file of the project and export it into your i2 Analyze toolkit. If you are using Eclipse, complete the following instructions.
    1.  In Eclipse, double-click `project.jardesc` file in the `onyx-da-subset-rest-example` project to open the Export window with default settings for the project applied.
    2.  In the Export window, change the **Export destination** to `toolkit\configuration\fragments\onyx-da-subset-rest-example\WEB-INF\lib\onyx-da-subset-rest-example.jar`.
    3.  Click **Finish** to export the JAR file to the specified destination.
        If the directory that you specified does not exist, a message is displayed. If you click **Yes**, the directory structure is created for you

    If you are not using Eclipse, you must build the JAR file according to your development environment.

    The built JAR file must comprise the contents of the `src/main/java` directories of the `onyx-da-subset-rest-example` and `onyx-da-example-common` projects. Your JAR file must be named `onyx-da-subset-rest-example.jar` and be in the `toolkit\configuration\fragments\onyx-da-subset-rest-example\WEB-INF\lib` directory.

Your deployment of i2 Analyze requires the two-phase data retrieval example `fragment` directory that includes the configuration files. For the two-phase data retrieval example, the deployment also requires the `fragment` directory from the `onyx-da-example-common` project.

1.  Copy the contents of the `SDK\sdk-projects\onyx-da-subset-rest-example\fragment` directory to the `toolkit\configuration\fragments\onyx-da-subset-rest-example` directory. Overwrite any old files with the new versions.
2.  The example data is contained within the `onyx-da-example-common` fragment. Copy the contents of the `SDK\sdk-projects\onyx-da-example-common\fragment` directory to the `toolkit\configuration\fragments\onyx-da-subset-rest-example` directory. Overwrite any old files with the new versions.
3.  To make the data access on-demand data source available, you must add the data source to the `topology.xml` file.
    1.  In an XML editor, open the `toolkit\configuration\environment\topology.xml` file.
    2.  Edit the `<i2-data-source>` element that defines the data access on-demand data source so that its attributes reflect the functionality of the example:
        ``` pre
        <i2-data-source id="daod1" ar="false">
           <DataSource ScsSearchSupported="false" EdrsGetContextSupported="false"
                       ScsNetworkSearchSupported="false" EdrsGetLatestItemsSupported="false"
                       Version="0" ScsPresent="false" ScsFilteredSearchSupported="false"
                       ScsBrowseSupported="false" Id="" ScsDumbbellSearchSupported="false"
                       SesPresent="true" EdrsPresent="false">
              <Shape>DAOD</Shape>
              <Name>onyx-da-subset-rest-example</Name>
           </DataSource>
        </i2-data-source>
        ```

        Note: The first data access on-demand data source that you add to the topology file gets the identifier `daod1`. Later additions receive different identifiers.

        For this data source, all of the attributes must be set to `false`, apart from `SesPresent`. This data source supports only a subset exploration service.

4.  Navigate to the `toolkit\scripts` directory and run the following command to redeploy i2 Analyze with all the changes that you made:
    ``` pre
    setup -t deploy -s onyx-server
    ```

5.  In Windows Explorer, navigate to the `C:\IBM\i2analyze\toolkit\configuration\environment\dsid` directory.
    The `dsid` directory is populated and maintained by the deployment scripts. This directory contains settings files for each of the data sources that `topology.xml` defines.

6.  Open the `dsid.<id>.properties` file for your data source in a text editor, where `<id>` is the value of the `id` attribute in the `<i2-data-source>` element for this data source in the topology file. Record the value of the `DataSourceId` property, which was generated when you redeployed i2 Analyze.
7.  Open `C:\IBM\i2analyze\toolkit\configuration\fragments\onyx-services-ar\ApolloClientSettings.xml` in a text editor. Add these lines to the end of the file, just before the `</settings>` closing tag:
    ``` pre
    <SubsettingExampleGenerationUILocation>
    http://localhost:9081/<DataSourceId>/SubsettingHtml.html</SubsettingExampleGenerationUILocation>
    <SubsettingExampleDisplayRecordUrl>
    http://localhost:9081/<DataSourceId>/ItemDetails.jsp?id={0}&amp;source={1}</SubsettingExampleDisplayRecordUrl>
    ```

    These settings provide code in the user interface extension project with the locations of the resources that it must use to respond to user requests. The port numbers must match the value of the `WC_defaulthost` property in the `C:\IBM\i2analyze\toolkit\configuration\environment\onyx-server\port-def.props` file.

8.  Replace `<DataSourceId>` in both values with the identifier that you copied from the `dsid.<id>.properties` file. Save and close `ApolloClientSettings.xml`.
9.  Redeploy i2 Analyze again by running the command that you used to deploy the platform originally:
    ``` pre
    setup -t deploy -s onyx-server
    ```

At this stage, deployment of the two-phase data retrieval example is complete. You can start i2 Analyze and connect to it through the Intelligence Portal.

1.  Run the following command to start the i2 Analyze server:
    ``` pre
    setup -t start -s onyx-server
    ```

2.  Use the Services application in Windows ( `services.msc` ) to restart IBM HTTP Server.
3.  In a web browser, load or refresh your view of the Intelligence Portal. In the menu bar, click **Subsetting** &gt; **Launch Subsetting Tab** to display the custom user interface.
4.  Follow the on-screen instructions to request data from the new data source and present it in the user interface in different permutations.
5.  Debug the `onyx-da-subset-rest-example` project by using Eclipse and your deployment of i2 Analyze. For more information about debugging the example, see <a href="https://github.com/IBM-i2/Analyze/blob/master/documentation/developer_essentials_debug.md" class="xref" title="(Opens in a new tab or window)">Debugging Developer Essentials in Eclipse</a>.

After you develop and test a data access on-demand solution for i2 Analyze, the next step is to publish it to a live deployment. Complete the following steps on your live deployment of i2 Analyze.

1.  Copy the `onyx-da-subset-rest-example` fragment directory from your development deployment to your live deployment.
2.  Update the `topology.xml` and `ApolloClientSettings.xml` files with the same changes that you made to your development environment.
3.  Redeploy the live deployment of i2 Analyze.

**Parent topic:** <a href="developer_essentials_welcome.md" class="link" title="IBM i2 Analyze Developer Essentials contains tools, libraries, and examples that enable development and deployment of custom extensions to i2 Analyze. Developer Essentials also includes API documentation and guides to deploying the software and the example projects.">IBM i2 Analyze Developer Essentials</a>

------------------------------------------------------------------------

© Copyright IBM Corporation 2014, 2018.


