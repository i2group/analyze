Configuring the "data access on-demand" example project
=======================================================

The "data access on-demand" approach to data acquisition in the Intelligence Analysis Platform involves querying external data through a new service. In Developer Essentials, IBM provides the `da-subset-filesystem-example` example, in which the external data source is an XML file.

Before you begin
----------------

The data access on-demand example project requires the development version of the Intelligence Analysis Platform, prepared according to the instructions in the deployment guide. However, the example has no additional requirements.

Note: All the data acquisition example projects in Developer Essentials use the same XML data, and the same code for transforming and converting that data into Intelligence Analysis Platform items. These artifacts are in the `SDK\sdk-projects\da-example-common` directory, to which all of the data acquisition examples have access.

About this task
---------------

The data access on-demand example defines and configures services that provide users with access to an external data source through the Intelligence Portal. Configuring the example requires you to add the data source to the existing Intelligence Analysis Platform deployment, and to add the services to the platform.

Procedure
---------

1.  If you did not already add the `master` directory and the related artifacts to your Eclipse workspace while you worked with one of the other examples, add it now.
    1.  In Eclipse, click **Window** \> **Preferences** to open the Preferences window.
    2.  In the Preferences window, select **General** \> **Workspace** \> **Linked Resources**.
    3.  Add an entry to the **Defined path variables** list:

        -   Name: `TOOLKIT_ROOT`
        -   Location: `C:\IBM\iap\SDK\sdk-projects\master\build\IAP-Deployment-Toolkit`

        The shared libraries and several of the example projects rely on the presence of `TOOLKIT_ROOT` in your development environment.

    4.  In Eclipse, click **File** \> **Import** to open the Import window.
    5.  In the Import window, click **General** \> **Existing Projects into Workspace**, and then click **Next**.
    6.  Click **Browse** at the top of the window, and then select the `C:\IBM\iap\SDK\sdk-projects\master` directory.
    7.  Click **Finish** to complete the import process.
    8.  Repeat the import process to import `C:\IBM\iap\SDK\sdk-projects\da-example-common`

By its nature, the data access on-demand example requires you to modify your deployment of the platform. The scripts in the Deployment Toolkit can change `topology.xml` automatically. You must then redeploy the platform by hand.

1.  If the development version of the Intelligence Analysis Platform is running, stop the server from Eclipse.
2.  Open a command prompt as Administrator, and navigate to the `C:\IBM\iap\SDK\sdk-projects\master` directory.
3.  Run the following command:

    ``` {.pre .codeblock}
    build -pr da-subset-filesystem-example -t addDaodDataSource
    ```

    Important: For successful integration with Eclipse, the value that you provide to the `-pr` option must match the name of the example project.

    When the command runs successfully, it modifies the SDK topology file to add information about a new data source. By default, the data source gets the same name as the value that you provide to the `-pr` option, but you can modify the name later if you want to.

    The command also creates a Java library that maps from platform-compatible XML to Intelligence Analysis Platform items. Unlike in the "data load direct" project, the `addDaodDataSource` target of the `build` command automatically determines what schema the deployed platform is using, and performs the necessary configuration.

4.  Open the topology file that represents your deployment of the Intelligence Analysis Platform in an XML editor. By default, this file is at `C:\IBM\iap\SDK\sdk-projects\master\build\IAP-Deployment-Toolkit\configuration\environment\topology.xml`.
5.  Edit the `<iap-data-source>` element that defines the data access on-demand data source so that its attributes reflect the functionality of the example:

    ``` {.pre .codeblock}
    <iap-data-source id="daod1" ar="false">
       <DataSource EdrsGetContextSupported="false" ScsBrowseSupported="true"
                   ScsSearchSupported="true" ScsPresent="true"
                   ScsFilteredSearchSupported="false" EdrsPresent="false"
                   ScsNetworkSearchSupported="false" Id="" SesPresent="true"
                   Version="0" EdrsGetLatestItemsSupported="false"
                   ScsDumbbellSearchSupported="false">
          <Shape>DAOD</Shape>
          <Name>da-subset-filesystem-example</Name>
       </DataSource>
    </iap-data-source>
    ```

6.  To redeploy the Intelligence Analysis Platform, run the following command:

    ``` {.pre .codeblock}
    build -t deploy
    ```

7.  Use the Services application in Windows (`services.msc`) to restart IBM HTTP Server. Do not attempt to start the application server yet.

The commands so far deployed the example project application from the toolkit. To enable easy debugging of your code, the next steps are all about replacing the application that you deployed from the toolkit with code from Eclipse.

1.  Refresh the Eclipse project to ensure that it reflects the current state.
2.  In your Eclipse workspace, open the `WebSphere Application Server Liberty Profile/servers/iap/apps` folder, and delete the `da-subset-filesystem-example.war` application.
3.  Repeat the instructions that you followed when you added the `master` directory to your Eclipse workspace. This time, add the `C:\IBM\iap\SDK\sdk-projects\da-subset-filesystem-example` directory to Eclipse.
4.  Ensure that the JAR file from `da-example-common` is loaded correctly into the deployment assembly for the `da-subset-filesystem-example` project:
    1.  In Package Explorer, right-click **da-subset-filesystem-example**, and select **Properties** to display the Properties window.
    2.  Click **Deployment Assembly**, and look for any error messages about `da-example-common`.
    3.  If there are error messages, remove `da-example-common` from the packaging structure, and then use **Add** \> **Project** to add it again.

5.  In the **Servers** tab at the bottom of the Eclipse application window, right-click the **iap** server and select Add and Remove.
6.  In the Add and Remove window, move **da-subset-filesystem-example** from the **Available** list to the **Configured** list, and then click **Finish** to close the window.

At this stage, deployment of the data access on-demand example is complete. You can start the Intelligence Analysis Platform, and connect to it through the Intelligence Portal.

1.  In Eclipse, start the **iap** server.
2.  In a web browser, load or refresh your view of the Intelligence Portal. Browse the **da-subset-filesystem-example** data source to see that the items to which it provides access are available to the user.

After you develop and test a data access on-demand solution for the Intelligence Analysis Platform, the next step is to publish it to a live deployment.

These instructions assume that you have access to two instances of the Intelligence Analysis Platform:

-   The development version of the platform, deployed according to the instructions in IBM i2 Intelligence Analysis Platform Developer Essentials. This instance contains the custom application that you want to publish.
-   The production version of the platform, deployed according to the instructions in the IBM i2 Intelligence Analysis Platform Deployment Guide. This instance has all of its default settings.

1.  Copy `C:\IBM\iap\SDK\sdk-projects\master\build\IAP-Deployment-Toolkit\configuration` into the production version of the `IAP-Deployment-Toolkit`.
2.  Edit the following files to ensure that the default settings match your environment:
    -   `configuration\environment\iap\environment.properties`
    -   `configuration\environment\http-server.properties`
    -   `configuration\environment\credentials.properties`
    -   `configuration\environment\topology.xml`

3.  Open a command prompt as Administrator, and navigate to the `IAP-Deployment-Toolkit\scripts` directory.
4.  To redeploy the platform, run the following command:

    ``` {.pre .codeblock}
    setup -t deploy
    ```

* * * * *

© Copyright IBM Corporation 2014, 2015.


