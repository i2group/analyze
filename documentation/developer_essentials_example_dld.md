Configuring the "data load direct" example project
==================================================

The "data load direct" approach to data acquisition in the Intelligence Analysis Platform involves importing external data into the Analysis Repository. In Developer Essentials, IBM provides the `da-arload-filesystem-example` example, in which the external data source is an XML file.

Before you begin
----------------

The data load direct example project requires the development version of the Intelligence Analysis Platform, prepared according to the instructions in the deployment guide. However, the example has no additional requirements.
<span class="notetitle">Note:</span> All the data acquisition example projects in Developer Essentials use the same XML data, and the same code for transforming and converting that data into Intelligence Analysis Platform items. These artifacts are in the `SDK\sdk-projects\da-example-common` directory, to which all of the data acquisition examples have access.

About this task
---------------

The data load direct example is a Java application that behaves like a client of the Intelligence Analysis Platform. As a result, this example does not require you to modify your existing deployment of the platform. Importing and testing the example mostly involves standard operations in Eclipse.

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

Next, all data acquisition projects that you create with Developer Essentials require a Java library that maps from platform-compatible XML to Intelligence Analysis Platform items. The library is specific to a particular Intelligence Analysis Platform schema. You can generate the library by running a command against the schema in question.

1.  <span class="ph cmd">Open a command prompt, and navigate to the `C:\IBM\iap\SDK\sdk-projects\master` directory.</span>
2.  <span class="ph cmd">Run the following command:</span>

    ``` pre
    build -t generateMappingJar 
    -x C:\IBM\iap\SDK\sdk-projects\master\build\IAP-Deployment-Toolkit\configuration\examples\schemas\en_US\law-enforcement-schema.xml
    -o C:\IBM\iap\SDK\sdk-projects\da-arload-filesystem-example\schema-mapping-jar\schema.jar
    ```

    <span class="notetitle">Note:</span> This command assumes that your target deployment of the Intelligence Analysis Platform uses the law enforcement schema, which is the default setting for the development version. If the target uses a different schema, then you must change the command and the project configuration to match.

    When the command runs successfully, it creates the library in the `schema-mapping-jar` directory, which is on the build path for the `da-arload-filesystem-example` project.

The Java library is the only change that you must make to the example project. You can now import the example project to Eclipse, and run it to add items from the supplied XML file to the Analysis Repository.

1.  <span class="ph cmd">Repeat the instructions that you followed when you added the `master` directory to your Eclipse workspace. This time, add the `IAP-Deployment-Toolkit\SDK\sdk-projects\da-arload-filesystem-example` directory to Eclipse.</span>
2.  <span class="ph cmd">If the development version of the Intelligence Analysis Platform is not already running, then start it from within Eclipse. </span>
3.  <span class="ph cmd">Attempt to run the example.</span>
    1.  <span class="ph cmd">In the Eclipse workspace, select `ExampleDataLoaderMain.java`.</span>
    2.  <span class="ph cmd">Click <span class="ph menucascade">**Run** \> **Run As** \> **Java Application**</span>.</span>

    If you have not run the example before, this step fails because the application requires a command-line argument. However, attempting to run the example initializes the configuration setting that you must make to run the example successfully.
4.  <span class="ph cmd">Click <span class="ph menucascade">**Run** \> **Run Configurations**</span> to open the <span class="keyword wintitle">Run Configurations</span> window.</span>
5.  <span class="ph cmd">In the left pane of the window, select <span class="ph menucascade">**Java Application** \> **ExampleDataLoaderMain**</span>.</span>
6.  <span class="ph cmd">In the right pane of the window, select the **Arguments** tab, and then type `-load` in the "**Program arguments**" box.</span>
7.  <span class="ph cmd">Click **Run** to close the window and run the example application.</span> The example adds a handful of items from the supplied XML file to the Analysis Repository.

    <span class="notetitle">Note:</span> Running the example with the `-load` argument for a second time fails. The example uses a simple mechanism to create identifiers for generated items, and the command service rejects requests to create an item with the same identifier as an existing item. The `-sync` and `-purge` arguments remain available.

8.  <span class="ph cmd">In a web browser, load or refresh your view of the Intelligence Portal, and browse the Analysis Repository to see that the example project added new items.</span>

**Parent topic:** [IBM i2 Intelligence Analysis Platform Developer Essentials](developer_essentials_welcome.html "IBM i2 Intelligence Analysis Platform Developer Essentials contains tools, libraries, and examples that enable development and deployment of custom extensions to the Intelligence Analysis Platform.")

------------------------------------------------------------------------

© Copyright IBM Corporation 2014, 2015.


