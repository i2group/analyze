Configuring the "two-phase data retrieval" example project
==========================================================

The Intelligence Analysis Platform supports variations on the "data access on-demand" approach to data acquisition. One of the examples that IBM provides in Developer Essentials is the `da-subset-rest-example`, which demonstrates a two-phase technique for data retrieval from an external source. The example also contains code that allows it to be called and displayed by custom code in the Intelligence Portal.

Before you begin
----------------

The two-phase data retrieval example project requires the development version of the Intelligence Analysis Platform, prepared according to the deployment instructions in Developer Essentials. To test the functionality of the example, you must also configure the user interface extension example project.

Note: All the data acquisition example projects in Developer Essentials use the same XML data, and the same code for transforming and converting that data into Intelligence Analysis Platform items. These artifacts are in the `IAP-Deployment-Toolkit\SDK\sdk-projects\da-example-common` directory, to which all of the examples have access.

About this task
---------------

The `da-subset-rest-example` project takes its name from one of the services that it contains, which has a REST API. This API enables the service to be called easily from code in HTML pages that the Intelligence Portal can host as part of a user interface extension. Compared with the data access on-demand example project, this example has two significant innovations:

-   The data source, the data acquisition services, and the REST service are designed for deployment on a separate server from the standard Intelligence Analysis Platform read side.
-   The project demonstrates a different approach to data acquisition on-demand. The first request from a client causes the Intelligence Analysis Platform to query the external data source. The example code places any results in a subset, and returns a token that identifies that subset to the client. If the client specifies the same token in subsequent requests, the platform responds by using data from the subset.

Note: The two-phase data retrieval example is one part of a two-part project that demonstrates custom server functionality in a customized Intelligence Portal. The project is not complete until you configure and deploy both parts, and you cannot interact with this example until you also configure the user interface extension example project.

Procedure
---------

The examples in Developer Essentials require links to the Intelligence Analysis Platform libraries, and to the API documentation. These links are configured in a separate project directory that you must import into Eclipse before you can build any of the example projects.

1.  If you did not already add the `shared` directory to your Eclipse workspace while you worked with one of the other examples, add it now.
    1.  In Eclipse, click **Window** \> **Preferences** to open the Preferences window.
    2.  In the Preferences window, select **General** \> **Workspace** \> **Linked Resources**.
    3.  Add an entry to the **Defined path variables** list:

        -   Name: `TOOLKIT_ROOT`
        -   Location: `C:\IBM\iap-3.0.5.5\IAP-Deployment-Toolkit`

        The shared libraries and several of the example projects rely on the presence of `TOOLKIT_ROOT` in your development environment.

    4.  In Eclipse, click **File** \> **Import** to open the Import window.
    5.  In the Import window, click **General** \> **Existing Projects into Workspace**, and then click **Next**.
    6.  Click **Browse** at the top of the window, and then select the `IAP-Deployment-Toolkit\SDK\sdk-projects\shared` directory.
    7.  Click **Finish** to complete the import process.

Because it involves a new read server, the two-phase data retrieval example requires significant changes to your deployment of the platform. The scripts in the Deployment Toolkit can modify some settings automatically, but you must also make some manual alterations, and then redeploy the platform by hand.

1.  Optional: If the development version of the Intelligence Analysis Platform is running, stop the servers from Eclipse. Stop the read-side server, followed by the write-side server.
2.  The configuration file for the new read server is in deployment toolkit, at `configuration\environment\newread\environment.properties`. Complete the file with the same values that you specified when you configured the standard read server.
3.  Open the topology file that represents your deployment of the Intelligence Analysis Platform in an XML editor. By default, this file is at `IAP-Deployment-Toolkit\configuration\environment\topology.xml`.
4.  Edit the topology file to add the following `<application>` element immediately before the closing tag of the `<applications>` element:

    ``` {.pre .codeblock}
       ...
       <application name="newread" host-name="localhost">
          <wars>
          </wars>
       </application>
    </applications>
    ```

    The new `<application>` element declares that your deployment contains an extra application. The element is also a target for the deployment scripts, which can now automate much of the rest of the configuration process.

5.  You need to add an `<iap-data-source>` element to the topology file that represents the new data source. Open a command prompt as Administrator, and navigate to the `IAP-Deployment-Toolkit\scripts` directory.
6.  Run the following command:

    ``` {.pre .codeblock}
    python da-setup.py -t add-data-source -a newread -d da-subset-rest-example
    ```

    Important: For successful integration with Eclipse, the value that you provide to the `-d` option must match the name of the example project.

    When the command runs successfully, it modifies `topology.xml` to add information about a new data source, which it associates with the `newread` application. By default, the data source gets the same name as the value that you provide to the `-d` option.

7.  Reopen the topology file, and edit the `<iap-data-source>` element that defines the new data access on-demand data source so that its attributes reflect the functionality of the example:

    ``` {.pre .codeblock}
    <iap-data-source id="da-subset-rest-example-id" ar="false">
       <DataSource EdrsPresent="false" ScsPresent="false" SesPresent="true"
                   ScsBrowseSupported="false" ScsSearchSupported="false"
                   ScsNetworkSearchSupported="false"
                   ScsDumbbellSearchSupported="false"
                   ScsFilteredSearchSupported="false"
                   EdrsGetContextSupported="false"
                   EdrsGetLatestItemsSupported="false" Id="" Version="0">
          <Shape>DAOD</Shape>
          <Name>DAOD REST Example</Name>
       </DataSource>
    </iap-data-source>
    ```

    For this data source, all of the attributes must be set to `false`, apart from `SesPresent`. This data source supports only a subset exploration service.

8.  Edit the Lucene index settings for the new application so that they are distinct from the settings for all the other applications in the topology file:

    ``` {.pre .codeblock}
    <application name="newread" host-name="localhost">
       <lucene-indexes>
          <lucene-index id="daod-subset-rest-example"
                        main-index-location="C:/iap-data/db/daod-subset-rest-example-main"
                        alternatives-location="C:/iap-data/db/daod-subset-rest-example-alternative"/>
       </lucene-indexes>
       <wars>
          <war target="daod" name="da-subset-rest-example"
               iap-datasource-id="da-subset-rest-example-id">
             <lucene-index-ids>
                <lucene-index-id value="daod-subset-rest-example"/>
             </lucene-index-ids>
             ...
    ```

9.  Run the following command to register the new data source with other parts of the Intelligence Analysis Platform deployment:

    ``` {.pre .codeblock}
    python deploy.py -s write -t register-datasources
    ```

    Registering the data source also generates a GUID that is used to refer to it from elsewhere in the deployment, including in its URL.

10. In Windows Explorer, navigate to the `IAP-Deployment-Toolkit\configuration\environment\dsid` directory.

    The `dsid` directory is populated and maintained by the deployment scripts. This directory contains settings files for each of the data sources that are defined in `topology.xml`.

11. Open the `dsid.da-subset-rest-example-id.properties` file in a text editor. Copy the value of the `DataSourceId` property, which is the GUID that registration generated.
12. Open `IAP-Deployment-Toolkit\configuration\fragments\write\ApolloClientSettings.xml` in a text editor. Add these lines to the end of the file, just before the `</settings>` closing tag:

    ``` {.pre .codeblock}
    <SubsettingExampleGenerationUILocation>
    http://localhost:20032/DataSourceId/SubsettingHtml.html</SubsettingExampleGenerationUILocation>
    <SubsettingExampleDisplayRecordUrl>
    http://localhost:20032/DataSourceId/ItemDetails.jsp?id={0}&amp;source={1}</SubsettingExampleDisplayRecordUrl>
    ```

    These settings provide code in the user interface extension project with the locations of the resources that it must use to respond to user requests. The port numbers must match the value of the `WC_defaulthost` property in the `IAP-Deployment-Toolkit\configuration\environment\newread\port-def.props` file.

13. Replace `DataSourceId` in both values with the identifier that you copied from the `dsid.da-subset-rest-example-id.properties` file. Save and close `ApolloClientSettings.xml`.
14. Redeploy the Intelligence Analysis Platform by running similar commands to the ones that you used to deploy the platform originally:

    ``` {.pre .codeblock}
    python deploy.py -s write -t deploy-liberty
    python deploy.py -s newread -t deploy-liberty
    ```

    The command that targets the write server redeploys it with information about the new server on the read side. The command that targets the "newread" server deploys that server for the first time.

    Note: Sometimes, the first `deploy-liberty` command can fail. It displays the following error message:

    `AMQ6004: An error occurred during WebSphere MQ initialization or ending.`

    The failure occurs when the command attempts to delete and re-create the WebSphere MQ queue manager. To resolve the problem, use WebSphere MQ Explorer to delete the queue manager, and then rerun the `deploy-liberty` command.

15. Use the Services application in Windows (`services.msc`) to restart IBM HTTP Server. Do not attempt to start the application server or the web applications yet.

The commands so far added an incomplete application to your Intelligence Analysis Platform deployment. (The application includes all the infrastructure for supporting data access on-demand, but no actual implementation.) In Eclipse, you must create a server to host the application, remove the incomplete application from the workspace, and then replace it with the example.

1.  In the **Servers** tab at the bottom of the Eclipse application window, add another server:
    1.  In the Eclipse application window, click the **Servers** tab.
    2.  Right-click inside the tab, and then click **New** \> **Server** to open the New Server window.
    3.  In the New Server window, click **IBM** \> **WebSphere Application Server V8.5 Liberty Profile** to select the server type.
    4.  Change the **Server name** to `newread`, and then click **Next**.
    5.  From the **Liberty profile server** list, select **newread**, and then click **Finish**.
    6.  Back in the **Servers** tab, double-click the **newread** server. A **newread** tab opens in the top part of the Eclipse application window.
    7.  Modify the settings in the new tab:
        -   Clear the **Run applications directly from the workspace** check box
        -   Click **Publishing** \> **Never publish automatically**

    8.  Press Ctrl+s to save the settings

2.  Refresh the Eclipse user interface to ensure that it reflects the current state of the project.
3.  In your Eclipse workspace, open the `WebSphere Application Server V8.5 Liberty Profile/servers/newread/apps` folder, and delete the `da-subset-rest-example.war` application.
4.  Repeat the instructions that you followed when you added the `shared` directory to your Eclipse workspace. This time, add the `IAP-Deployment-Toolkit\SDK\sdk-projects\da-subset-rest-example` directory to Eclipse.
5.  In the **Servers** tab at the bottom of the Eclipse application window, right-click the **newread** server and select **Add and Remove**.
6.  In the Add and Remove window, move **da-subset-rest-example** from the **Available** list to the **Configured** list, and then click **Finish** to close the window.

    Note: If the **Configured** list already contains an entry for the **da-subset-rest-example**, Eclipse displays a warning about moving the entry from the **Available** list to it. You can safely ignore the warning for this example.

At this stage, deployment of the two-phase data retrieval example is complete. You can start the Intelligence Analysis Platform, and connect to it through the Intelligence Portal.

1.  In Eclipse, start the write-side server, followed by the read-side server, and finally the "newread" server.
2.  In a web browser, load or refresh your view of the Intelligence Portal. In the menu bar, click **Subsetting** \> **Launch Subsetting Tab** to display the custom user interface.
3.  Follow the on-screen instructions to request data from the new data source and present it in the user interface in different permutations.

* * * * *

© Copyright IBM Corporation 2014.


