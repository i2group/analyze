Configuring the "data load direct" example project
==================================================

The "data load direct" approach to data acquisition in an i2 Analyze deployment with the Onyx services involves importing external data into the Analysis Repository. In Developer Essentials, IBM provides the `onyx-da-arload-filesystem-example` example, in which the external data source is an XML file.

Before you begin
----------------

You must have a development deployment of i2 Analyze and a configured development environment. For more information, see <a href="developer_essentials_deploying.md" class="xref" title="IBM i2 Analyze Developer Essentials is a set of files and example projects that build on a standard i2 Analyze deployment. Preparing to use Developer Essentials involves installing and configuring it to work in a dedicated test environment.">Preparing to use IBM i2 Analyze Developer Essentials</a>.

About this task
---------------

The data load direct example is a Java application that behaves like a client of i2 Analyze. As a result, this example does not require you to modify your existing deployment of the platform. Importing and testing the example mostly involves standard operations in an IDE.

Procedure
---------

All data acquisition projects that you create with Developer Essentials require a Java library that maps from platform-compatible XML to Analysis Repository items. The library is specific to a particular i2 Analyze schema. You can generate the library by running a command against the schema in question.

1.  Navigate to the `toolkit\scripts` directory and run the following command:
    ``` pre
    setup -s onyx-server -t generateMappingJar 
    -x C:\IBM\i2analyze\toolkit\configuration\examples\schemas\en_US\law-enforcement-schema.xml
    -o C:\IBM\i2analyze\SDK\sdk-projects\onyx-da-arload-filesystem-example\schema-mapping-jar\schema.jar
    ```

    Note: This command assumes that your target deployment of i2 Analyze uses the law enforcement schema, which is the default setting for the development version. If the target uses a different schema, then you must change the command and the project configuration to match.

    When the command runs successfully, it creates the library in the `schema-mapping-jar` directory, which is on the build path for the `onyx-da-arload-filesystem-example` project.

    The Java library is the only change that you must make to the example project. You can now import the example project to Eclipse, and run it to add items from the supplied XML file to the Analysis Repository.

The data acquisition example projects in Developer Essentials use the same XML data, and the same code for transforming and converting that data into items in the Analysis Repository. These artifacts are in the `onyx-da-example-common` project, which you must add to your development environment. You can import this project into Eclipse by completing the following instructions.

1.  Import the `onyx-da-example-common` project.
    1.  In Eclipse, click **File** &gt; **Import** to open the Import window.
    2.  In the Import window, click **General** &gt; **Existing Projects into Workspace**, and then click **Next**.
    3.  Click **Browse** at the top of the window, and then select the `C:\IBM\i2analyze\SDK\sdk-projects\onyx-da-example-common` directory.
    4.  Click **Finish** to complete the import process.

2.  The data load direct example is contained within the `onyx-da-arload-filesystem-example` project. You must add this project to your development environment. If you are using Eclipse, you can repeat step 1 to import the `C:\IBM\i2analyze\SDK\sdk-projects\onyx-da-arload-filesystem-example` project.

After you add the project to your development environment, you can inspect the behavior of the example.

1.  Run the following command to start the i2 Analyze server:
    ``` pre
    setup -t start -s onyx-server
    ```

2.  Attempt to run the `ExampleDataLoaderMain.java` file. In Eclipse, run the example as follows:
    1.  In the Eclipse workspace, select `ExampleDataLoaderMain.java`. In the Enterprise Explorer pane, `ExampleDataLoaderMain.java` is in **onyx-da-arload-filesystem-example** &gt; **src** &gt; **main** &gt; **java** &gt; **com** &gt; **example**.
    2.  Click **Run** &gt; **Run As** &gt; **Java Application**.

    If you have not run the example before, this step fails because the application requires a command-line argument. However, attempting to run the example initializes the configuration setting that you must make to run the example successfully. You must run the example with the `-load` argument. In Eclipse, you can run the example as follows:

3.  Click **Run** &gt; **Run Configurations** to open the Run Configurations window.
4.  In the left pane of the window, select **Java Application** &gt; **ExampleDataLoaderMain**.
5.  In the right pane of the window, select the **Arguments** tab, and then type `-load` in the "**Program arguments**" box.
6.  Click **Run** to close the window and run the example application.
    The example adds a handful of items from the supplied XML file to the Analysis Repository.
    Note: Running the example with the `-load` argument for a second time fails. The example uses a simple mechanism to create identifiers for generated items, and the command service rejects requests to create an item with the same identifier as an existing item. The `-sync` and `-purge` arguments remain available.

7.  Use the Services application in Windows ( `services.msc` ) to restart IBM HTTP Server.
8.  In a web browser, load or refresh your view of the Intelligence Portal, and browse the Analysis Repository to see that the example project added new items.

**Parent topic:** <a href="developer_essentials_welcome.md" class="link" title="IBM i2 Analyze Developer Essentials contains tools, libraries, and examples that enable development and deployment of custom extensions to i2 Analyze. Developer Essentials also includes API documentation and guides to deploying the software and the example projects.">IBM i2 Analyze Developer Essentials</a>

------------------------------------------------------------------------

© Copyright IBM Corporation 2014, 2018.


