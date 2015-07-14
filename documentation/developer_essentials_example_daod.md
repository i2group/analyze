Configuring the "data access on-demand" example project
=======================================================

The "data access on-demand" approach to data acquisition in the Intelligence Analysis Platform involves querying external data through a new service. In Developer Essentials, IBM provides the `da-subset-filesystem-example` example, in which the external data source is an XML file.

Before you begin
----------------

The data access on-demand example project requires the development version of the Intelligence Analysis Platform, prepared according to the instructions in the deployment guide. However, the example has no additional requirements.
<span class="notetitle">Note:</span> All the data acquisition example projects in Developer Essentials use the same XML data, and the same code for transforming and converting that data into Intelligence Analysis Platform items. These artifacts are in the `SDK\sdk-projects\da-example-common` directory, to which all of the data acquisition examples have access.

About this task
---------------

The data access on-demand example defines and configures services that provide users with access to an external data source through the Intelligence Portal. Configuring the example requires you to add the data source to the existing Intelligence Analysis Platform deployment, and to add the services to the platform.

Procedure
---------

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

By its nature, the data access on-demand example requires you to modify your deployment of the platform. The scripts in the Deployment Toolkit can change `topology.xml` automatically. You must then redeploy the platform by hand.

1.  Optional: <span class="ph cmd">If the development version of the Intelligence Analysis Platform is running, stop the server from Eclipse.</span>
2.  <span class="ph cmd">Open a command prompt as Administrator, and navigate to the `C:\IBM\iap\SDK\sdk-projects\master` directory.</span>
3.  <span class="ph cmd">Run the following command:</span>

    ``` pre
    build -pr da-subset-filesystem-example -t addDaodDataSource
    ```

    <span class="importanttitle">Important:</span> For successful integration with Eclipse, the value that you provide to the `-pr` option must match the name of the example project.

    When the command runs successfully, it modifies the SDK topology file to add information about a new data source. By default, the data source gets the same name as the value that you provide to the `-pr` option, this can be subsequently modified if required.

    The command also creates a Java library that maps from platform-compatible XML to Intelligence Analysis Platform items. Unlike in the "data load direct" project, the `addDaodDataSource` target of the `build` command automatically determines what schema the deployed platform is using, and performs the necessary configuration.

4.  <span class="ph cmd">Open the topology file that represents your deployment of the Intelligence Analysis Platform in an XML editor.</span> By default, this file is at `C:\IBM\iap\SDK\sdk-projects\master\build\IAP-Deployment-Toolkit\configuration\environment\topology.xml`.
5.  <span class="ph cmd">Edit the `<iap-data-source>` element that defines the data access on-demand data source so that its attributes reflect the functionality of the example:</span>

    ``` pre
    <iap-data-source id="da-subset-filesystem-example-id" ar="false">
       <DataSource EdrsPresent="false" ScsPresent="true" SesPresent="true"
                   ScsBrowseSupported="true" ScsSearchSupported="true"
                   ScsNetworkSearchSupported="false"
                   ScsDumbbellSearchSupported="false"
                   ScsFilteredSearchSupported="false"
                   EdrsGetContextSupported="false"
                   EdrsGetLatestItemsSupported="false" Id="" Version="0">
          <Shape>DAOD</Shape>
          <Name>da-subset-filesystem-example</Name>
       </DataSource>
    </iap-data-source>
    ```

6.  <span class="ph cmd">To redeploy the Intelligence Analysis Platform, run the following command:</span>

    ``` pre
    build -t deploy
    ```

7.  <span class="ph cmd">Use the Services application in Windows (`services.msc`) to restart IBM HTTP Server. Do not attempt to start the application server yet.</span>

The commands so far added an incomplete application to the server. (The application includes all the infrastructure for supporting data access on-demand, but no actual implementation.) You must remove this application from the workspace and replace it with the example.

1.  <span class="ph cmd">Refresh the Eclipse project to ensure that it reflects the current state.</span>
2.  <span class="ph cmd">In your Eclipse workspace, open the <span class="ph filepath">WebSphere Application Server Liberty Profile/servers/iap/apps</span> folder, and delete the `da-subset-filesystem-example.war` application.</span>
3.  <span class="ph cmd">Repeat the instructions that you followed when you added the `master` directory to your Eclipse workspace. This time, add the `C:\IBM\iap\SDK\sdk-projects\da-subset-filesystem-example` directory to Eclipse.</span>
4.  <span class="ph cmd">In the **Servers** tab at the bottom of the Eclipse application window, right-click the **iap** server and select <span class="ph uicontrol">Add and Remove</span>.</span>
5.  <span class="ph cmd">In the <span class="keyword wintitle">Add and Remove</span> window, move **da-subset-filesystem-example** from the **Available** list to the **Configured** list, and then click **Finish** to close the window.</span>

At this stage, deployment of the data access on-demand example is complete. You can start the Intelligence Analysis Platform, and connect to it through the Intelligence Portal.

1.  <span class="ph cmd">In Eclipse, start the iap server.</span>
2.  <span class="ph cmd">In a web browser, load or refresh your view of the Intelligence Portal. Browse the "da-subset-filesystem-example" data source to see that the items to which it provides access are available to the user.</span>

After you develop and test a data access on-demand solution for the Intelligence Analysis Platform, the next step is to publish it to a live deployment.

These instructions assume that you have access to two instances of the Intelligence Analysis Platform:

-   The development version of the platform, deployed according to the instructions in IBM i2 Intelligence Analysis Platform Developer Essentials. This instance contains the custom application that you want to publish.
-   The production version of the platform, deployed according to the instructions in the IBM i2 Intelligence Analysis Platform Deployment Guide. This instance has all of its default settings.

1.  <span class="ph cmd">Copy `C:\IBM\iap\SDK\sdk-projects\master\build\IAP-Deployment-Toolkit\configuration` into the production version of the `IAP-Deployment-Toolkit`.</span>
2.  <span class="ph cmd">Edit the following files to ensure that the default settings match your environment:</span>
    -   `configuration\environment\iap\environment.properties`
    -   `configuration\environment\http-server.properties`
    -   `configuration\environment\credentials.properties`
    -   `configuration\environment\topology.xml`

3.  <span class="ph cmd"> Open a command prompt as Administrator, and navigate to the `IAP-Deployment-Toolkit\scripts` directory.</span>
4.  <span class="ph cmd">To redeploy the platform, run the following command:</span>

    ``` pre
    setup -t deploy
    ```

**Parent topic:** [IBM i2 Intelligence Analysis Platform Developer Essentials](developer_essentials_welcome.html "IBM i2 Intelligence Analysis Platform Developer Essentials contains tools, libraries, and examples that enable development and deployment of custom extensions to the Intelligence Analysis Platform.")

------------------------------------------------------------------------

© Copyright IBM Corporation 2014, 2015.


