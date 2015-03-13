Configuring the "user interface extension" example project
==========================================================

The Intelligence Analysis Platform supports extensions that enable users to interact with external data sources through enhancements to the Intelligence Portal. In Developer Essentials, IBM provides the `ui-subset-example` example, which includes a demonstration of how to integrate custom elements into the standard portal user interface.

Before you begin
----------------

The user interface extension example project requires the development version of the Intelligence Analysis Platform, prepared according to the deployment instructions in Developer Essentials. The example also has some additional prerequisites:

-   Microsoft Visual Studio Express 2013 for Web
-   Microsoft Silverlight 5 SDK
-   Microsoft Silverlight 5 Developer Runtime

All of these additional prerequisites are available as free downloads from the Microsoft website.

About this task
---------------

The Intelligence Portal is an extensible Microsoft Silverlight web application that supports adding functionality by creating web pages. The portal hosts web pages in new tabs, and you can enable users to display the new tabs by adding custom buttons to the header bar.

The portal also supports customization by adding and modifying the commands that are available through buttons and menu items throughout the user interface. You can arrange for custom commands to appear, and for any commands to disappear or become disabled, according to the state of the user interface and the currently selected items.

The user interface extension example that IBM provides in Developer Essentials demonstrates both approaches to customizing the Intelligence Portal, and the procedure that you must follow to deploy them.

Note: The user interface extension example is one part of a two-part project that demonstrates custom server functionality in a customized Intelligence Portal. The project is not complete until you configure and deploy both parts, but you can demonstrate how the changes to the user interface work without deploying the second part.

Procedure
---------

When you installed Developer Essentials, you added a `ui` directory to the Deployment Toolkit that contains a configuration file for user interface extensions. You must edit this file to provide the locations of `MSBuild.exe` (which can compile XAP files) and the directory that contains your extension code.

1.  Open the configuration file for user interface extensions in a text editor. By default, this file is at `IAP-Deployment-Toolkit\configuration\environment\ui\environment.properties`.
2.  Set the properties appropriately for your environment. If you followed the deployment instructions for Developer Essentials, the correct values look like this example:

    ``` {.pre .codeblock}
    ui.project.dir=${toolkit.base.dir}/SDK/sdk-projects/ui-subset-example
    msbuild.exe.path=C:/Windows/Microsoft.NET/Framework/v4.0.30319/MSBuild.exe
    ```

    Note: In general, the directory that you specify in the `ui.project.dir` setting can contain multiple projects. If you want to develop several user interface extensions, you can put them all in the same directory structure.

The procedure for extending the Intelligence Portal starts with extracting the files from the standard web application, which is the `Apollo.xap` file. Then, you add your custom functionality to the standard content before you recompile and redeploy a new version of the XAP file.

1.  Open a command prompt as Administrator, and navigate to the `IAP-Deployment-Toolkit\scripts` directory.
2.  Run the following command:

    ``` {.pre .codeblock}
    python dev-ui.py -t dev-unpack-xap
    ```

    This command extracts the assemblies from the existing `Apollo.xap` file to the `extractedxap` directory of the example project.

3.  In Visual Studio, open the solution file at `IAP-Deployment-Toolkit\SDK\sdk-projects\ui-subset-example\src\PortalExtensibilityExample.sln`. The Visual Studio solution contains two projects:
    -   `SubsettingExample` demonstrates how to add a custom HTML page and display it inside a custom tab.
    -   `CommandExtensibilityExample` demonstrates how to add and modify commands in the pop-up menus for items, and in the actions toolbar.

4.  Build the solution in debug mode. Visual Studio puts the DLL files that it generates in the `IAP-Deployment-Toolkit\SDK\sdk-projects\ui-subset-example\build` directory.
5.  Return to the command prompt, and run the following command to copy the built DLL files to the `IAP-Deployment-Toolkit\configuration\fragments\xap-supplement` directory.

    ``` {.pre .codeblock}
    python dev-ui.py -t dev-update-fragments
    ```

    To add custom components to the Intelligence Portal user interface, you must create and populate a directory that is named `configuration\fragments\xap-supplement`. If your custom components also require HTML content, you add that directly to `configuration\fragments\write`. The `dev-update-fragments` task does this work, but only additively. If you later decide to remove custom components, you must delete them manually from `xap-supplement`.

    Note: If you remove custom components after you deployed them, you must also delete the recompiled XAP file from the `fragments\write` directory, and then redo the next three steps.

6.  In Eclipse, ensure that the read and write servers are stopped. In the **Servers** tab, stop the read server, followed by the write server.
7.  At the command prompt, run the following command to redeploy the write server:

    ``` {.pre .codeblock}
    python deploy.py -s write -t update-liberty
    ```

    As part of its work, the `update-liberty` task compiles your custom components from the `xap-supplement` directory into a new version of the XAP file.

8.  Back in Eclipse, in the **Servers** tab, start the write server followed by the read server.
9.  To test the updated deployment, open a web browser and navigate to http://localhost/apollo. Log in to the Intelligence Portal with any of the user names and passwords that are specified in `liberty-example-users.xml`, which you used during deployment of Developer Essentials.

Results
-------

The user interface extension example adds a **Subsetting** menu to the top of the Intelligence Portal user interface. When you click the menu, the portal displays a submenu that contains a **Launch Subsetting Tab** command. When you run the command, a **Subsetting Example** tab opens. However, the tab itself displays an error until you also deploy the server-side part of this example.

Elsewhere in the user interface, the example adds commands that are named **Example command 1**, **Example command 2**, and **Example inserted command**. Depending on the current state of the application, the Intelligence Portal displays or hides these commands in different locations.

* * * * *

© Copyright IBM Corporation 2015.


