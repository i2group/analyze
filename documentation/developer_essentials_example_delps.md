Configuring the "data load ELP stage" example project
=====================================================

The "data load ELP stage" approach to data acquisition in the Intelligence Analysis Platform involves importing external data into an additional, semi-permanent data store. In Developer Essentials, IBM provides the `da-elpsload-filesystem-example` example, in which the external data source is an XML file.

Before you begin
----------------

The data load ELP stage example project requires the development version of the Intelligence Analysis Platform, prepared according to the instructions in the deployment guide. However, the example has no additional requirements.

Note: All the data acquisition example projects in Developer Essentials use the same XML data, and the same code for transforming and converting that data into Intelligence Analysis Platform items. These artifacts are in the `IAP-Deployment-Toolkit\SDK\sdk-projects\da-example-common` directory, to which all of the examples have access.

About this task
---------------

The data load ELP stage example is a Java application that you run on the write side of the Intelligence Analysis Platform to create events that affect the read side. Specifically, the events cause changes to data in an ELP stage, which is a read-only analog of the Analysis Repository. As a result, this example requires you to add an ELP stage to the read side of your existing Intelligence Analysis Platform deployment.

Procedure
---------

The examples in Developer Essentials require links to the Intelligence Analysis Platform libraries, and to the API documentation. These links are configured in a separate project directory that you must import into Eclipse before you can build any of the example projects.

1.  If you did not already add the `shared` directory to your Eclipse workspace while you worked with one of the other examples, add it now.
    1.  In Eclipse, click **Window** \> **Preferences** to open the Preferences window.
    2.  In the Preferences window, select **General** \> **Workspace** \> **Linked Resources**.
    3.  Add an entry to the **Defined path variables** list:

        -   Name: `TOOLKIT_ROOT`
        -   Location: `C:\IBM\iap-3.0.7\IAP-Deployment-Toolkit`

        The shared libraries and several of the example projects rely on the presence of `TOOLKIT_ROOT` in your development environment.

    4.  In Eclipse, click **File** \> **Import** to open the Import window.
    5.  In the Import window, click **General** \> **Existing Projects into Workspace**, and then click **Next**.
    6.  Click **Browse** at the top of the window, and then select the `IAP-Deployment-Toolkit\SDK\sdk-projects\shared` directory.
    7.  Click **Finish** to complete the import process.

By its nature, the data load ELP stage example requires you to modify your deployment of the platform. Adding an ELP stage to the read side means adding a data store, a web application, and all their accompanying infrastructure. You must modify the `topology.xml` file to add the new information, and then redeploy the platform to update it to the new configuration.

1.  Optional: If the development version of the Intelligence Analysis Platform is running, stop the servers from Eclipse.
2.  Open a command prompt as Administrator, and navigate to `IAP-Deployment-Toolkit\scripts`.
3.  Run the following command:

    ``` {.pre .codeblock}
    python da-setup.py -t add-delps-data-source -a read -d da-elpsload-filesystem-example
    ```

    Important: For successful integration with Eclipse, the value that you provide to the `-d` option must match the name of the example project.

    When the command runs successfully, it modifies `topology.xml` to add information about a new data source. By default, the data source gets the same name as the value that you provide to the `-d` option.

    The command also creates a Java library that maps from platform-compatible XML to Intelligence Analysis Platform items. Unlike in the "data load direct" project, the `add-delps-data-source` target of the `da-setup.py` command automatically determines what schema the deployed platform is using, and performs the necessary configuration.

4.  Open the topology file that represents your deployment of the Intelligence Analysis Platform in an XML editor. By default, this file is at `IAP-Deployment-Toolkit\configuration\environment\topology.xml`.
5.  In the `<iap-data-sources>` element, find the `<iap-data-source>` element with `id="delps1"`. Set the contents of the `<Name>` element to "`DELPS Example`".
6.  In the `<databases>` element, find the `<database>` element with `database-name="delps1"`. Set the value of the `host-name` attribute to `"localhost"`. Save and close the topology file.
7.  In the same directory as the topology file, open the `credentials.properties` file in a text editor.
8.  Edit the file to add credential information for the new "`delps1`" database:

    ``` {.pre .codeblock}
    db.delps1.user-name=username
    db.delps1.password=password
    ```

    The credentials that you provide here must match the other "`db`" properties in the file.

9.  Redeploy the Intelligence Analysis Platform by running the same commands that you used to deploy the platform originally:

    ``` {.pre .codeblock}
    python deploy.py -s write -t deploy-liberty
    python deploy.py -s read -t deploy-liberty
    ```

    Note: Sometimes, the `deploy-liberty` command can fail. It displays the following error message:

    `AMQ6004: An error occurred during WebSphere MQ initialization or ending.`

    The failure occurs when the command attempts to delete and re-create the WebSphere MQ queue manager. To resolve the problem, use WebSphere MQ Explorer to delete the queue manager, and then rerun the `deploy-liberty` command.

10. Use the Services application in Windows (`services.msc`) to restart IBM HTTP Server.
11. Repeat the instructions that you followed when you added the `shared` directory to your Eclipse workspace. This time, add the `IAP-Deployment-Toolkit\SDK\sdk-projects\da-elpsload-filesystem-example` directory to Eclipse.
12. In Eclipse, refresh the user interface and then start the write server, followed by the read server.

The final part of configuration is to provide the example with the identifier of the ELP stage data source. The items that the client creates must contain the identifier of the data source in which they are stored.

1.  In Windows Explorer, navigate to the `IAP-Deployment-Toolkit\configuration\environment\dsid` directory.

    The `dsid` directory is populated and maintained by the deployment scripts. This directory contains settings files for each of the data sources that are defined in `topology.xml`.

2.  Open the `dsid.delps1.properties` file in a text editor, and copy the value of the `DataSourceId` property.
3.  In Eclipse, open the `da-elpsload-filesytem-example/src/resources/loader.properties` file.
4.  Set the `DataLoaderDataSourceId` property to the value that you copied.

You can now run the example to add items from the supplied XML file to the new ELP stage.

1.  Attempt to run the example.

    1.  In the Eclipse workspace, select `ExampleDelpsLoaderMain.java`.
    2.  Click **Run** \> **Run As** \> **Java Application**.

    If you have not run the example before, this step fails because the application requires a command-line argument. However, attempting to run the example initializes the configuration setting that you must make to run the example successfully.

2.  Click **Run** \> **Run Configurations** to open the Run Configurations window.
3.  In the left pane of the window, select **Java Application** \> **ExampleDelpsLoaderMain**.
4.  In the right pane of the window, select the **Arguments** tab, and then type `-load` in the "**Program arguments**" box.
5.  Click **Run** to close the window and run the example application. The example adds a handful of items from the supplied XML file to the ELP stage.

    Note: Running the example with the `-load` argument for a second time does not fail, but also does not add duplicate items to the ELP stage. The ELP stage ignores the events that attempt to create the same items again.
    
    Note: you may have the following error message :
    Invoking 'load'...
Exception in thread "main" java.lang.RuntimeException: com.i2group.apollo.service.common.ValidationException: Items with IDs '[p1, p2, p3, p4, ass1, ass2, ass3]' already exist.
	at com.i2group.apollo.externaldata.bulkdataloader.internal.BulkDataLoaderImplementation.executeCommandServiceCommands(BulkDataLoaderImplementation.java:304)
	at com.i2group.apollo.externaldata.bulkdataloader.internal.BulkDataLoaderImplementation.createOrUpdateItems(BulkDataLoaderImplementation.java:245)
	at com.i2group.apollo.externaldata.bulkdataloader.internal.BulkDataLoaderImplementation.createItems(BulkDataLoaderImplementation.java:117)
	at com.i2group.apollo.externaldata.loader.AnalysisRepositoryLoader.createItems(AnalysisRepositoryLoader.java:84)
	at com.example.ExampleDataLoader.load(ExampleDataLoader.java:68)
	at com.example.ExampleDataLoaderMain.main(ExampleDataLoaderMain.java:62)
Caused by: com.i2group.apollo.service.common.ValidationException: Items with IDs '[p1, p2, p3, p4, ass1, ass2, ass3]' already exist.
	at sun.reflect.NativeConstructorAccessorImpl.newInstance0(Native Method)
	at sun.reflect.NativeConstructorAccessorImpl.newInstance(NativeConstructorAccessorImpl.java:56)
	at sun.reflect.DelegatingConstructorAccessorImpl.newInstance(DelegatingConstructorAccessorImpl.java:39)
	at java.lang.reflect.Constructor.newInstance(Constructor.java:527)
	at com.sun.xml.internal.ws.fault.SOAPFaultBuilder.createException(SOAPFaultBuilder.java:142)
	at com.sun.xml.internal.ws.client.sei.SyncMethodHandler.invoke(SyncMethodHandler.java:120)
	at com.sun.xml.internal.ws.client.sei.SyncMethodHandler.invoke(SyncMethodHandler.java:90)
	at com.sun.xml.internal.ws.client.sei.SEIStub.invoke(SEIStub.java:119)
	at com.sun.proxy.$Proxy51.executeCommands(Unknown Source)
	at com.i2group.apollo.externaldata.bulkdataloader.serviceproxy.CommandServiceProxy.executeCommands(CommandServiceProxy.java:83)
	at com.i2group.apollo.externaldata.bulkdataloader.internal.BulkDataLoaderImplementation.executeCommandServiceCommands(BulkDataLoaderImplementation.java:252)
	... 5 more
	
	This is because the data is already in the Database.
	QUESTION : How do I purge the data ?

6.  In a web browser, load or refresh your view of the Intelligence Portal, and browse the "DELPS Example" data source to see that the example project added new items.

* * * * *

© Copyright IBM Corporation 2014.


