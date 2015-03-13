Configuring the "data load direct" example project
==================================================

The "data load direct" approach to data acquisition in the Intelligence Analysis Platform involves importing external data into the Analysis Repository. In Developer Essentials, IBM provides the `da-arload-filesystem-example` example, in which the external data source is an XML file.

Before you begin
----------------

The data load direct example project requires the development version of the Intelligence Analysis Platform, prepared according to the instructions in the deployment guide. However, the example has no additional requirements.

Note: All the data acquisition example projects in Developer Essentials use the same XML data, and the same code for transforming and converting that data into Intelligence Analysis Platform items. These artifacts are in the `IAP-Deployment-Toolkit\SDK\sdk-projects\da-example-common` directory, to which all of the examples have access.

About this task
---------------

The data load direct example is a Java application that behaves like a client of the write side of the Intelligence Analysis Platform. As a result, this example does not require you to modify your existing deployment of the platform. Importing and testing the example mostly involves standard operations in Eclipse.

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

Next, all data acquisition projects that you create with Developer Essentials require a Java library that maps from platform-compatible XML to Intelligence Analysis Platform items. The library is specific to a particular Intelligence Analysis Platform schema. You can generate the library by running a command against the schema in question.

1.  Open a command prompt, and navigate to the `IAP-Deployment-Toolkit\scripts` directory.
2.  Run the following command:

    ``` {.pre .codeblock}
    python da-setup.py -t generate-mapping-jar
                       -x ..\configuration\examples\schemas\
                                   en_US\law-enforcement-schema.xml
                       -o ..\SDK\sdk-projects\da-arload-filesystem-example\
                                   schema-mapping-jar\law-enforcement-schema.jar
    ```

    Note: This command assumes that your target deployment of the Intelligence Analysis Platform uses the law enforcement schema, which is the default setting for the development version. If the target uses a different schema, then you must change the command and the project configuration to match.

    When the command runs successfully, it creates the library in the `schema-mapping-jar` directory, which is on the build path for the `da-arload-filesystem-example` project.

The Java library is the only change that you must make to the example project. You can now add the example project to Eclipse, and run it to add items from the supplied XML file to the Analysis Repository.

1.  Repeat the instructions that you followed when you added the `shared` directory to your Eclipse workspace. This time, add the `IAP-Deployment-Toolkit\SDK\sdk-projects\da-arload-filesystem-example` directory to Eclipse.
2.  If the development version of the Intelligence Analysis Platform is not already running, then start it from within Eclipse. Start the write-side server, followed by the read-side server.
3.  Attempt to run the example.
    1.  In the Eclipse workspace, select `ExampleDataLoaderMain.java`.
    2.  Click **Run** \> **Run As** \> **Java Application**.

    If you have not run the example before, this step fails because the application requires a command-line argument. However, attempting to run the example initializes the configuration setting that you must make to run the example successfully.
4.  Click **Run** \> **Run Configurations** to open the Run Configurations window.
5.  In the left pane of the window, select **Java Application** \> **ExampleDataLoaderMain**.
6.  In the right pane of the window, select the **Arguments** tab, and then type `-load` in the "**Program arguments**" box.
7.  Click **Run** to close the window and run the example application. The example adds a handful of items from the supplied XML file to the Analysis Repository.

    Note: Running the example with the `-load` argument for a second time fails. The example uses a simple mechanism to create identifiers for generated items, and the command service rejects requests to create an item with the same identifier as an existing item. The `-sync` and `-purge` arguments remain available.

8.  In a web browser, load or refresh your view of the Intelligence Portal, and browse the Analysis Repository to see that the example project added new items.

* * * * *

© Copyright IBM Corporation 2015.


