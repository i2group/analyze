Configuring the "user interface extension" example project
==========================================================

i2 Analyze supports extensions that enable users to interact with external data sources through enhancements to the Intelligence Portal. In Developer Essentials, IBM provides the `ui-subset-example` example, which includes a demonstration of how to integrate custom elements into the standard Intelligence Portal user interface.

Before you begin
----------------

The user interface extension example project requires the development version of i2 Analyze, prepared according to the Developer Essentials deployment instructions. The example also has some additional prerequisites:
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

The two-part project is structured so that the `da-subset-rest-example` project, which implements the custom functionality, refers to the `ui-subset-example` project. To demonstrate the custom user interface in isolation, you need to configure both projects.

1.  Open the configuration file for user interface extensions for the `da-subset-rest-example` project in a text editor: `C:\IBM\i2analyze\SDK\sdk-projects\da-subset-rest-example\ui-project.properties`.
2.  Ensure that the properties are set for your environment. If you followed the deployment instructions for Developer Essentials, the correct values look like this example:

    ``` pre
    ui.project.dir=C:/IBM/i2analyze/SDK/sdk-projects/ui-subset-example
    msbuild.exe.path=C:/Windows/Microsoft.NET/Framework/v4.0.30319/MSBuild.exe
    ```

The procedure for extending the Intelligence Portal starts with extracting the files from the standard web application, which is the `Apollo.xap` file. Then, you add your custom functionality to the standard content before you recompile and redeploy a new version of the XAP file.

1.  Open a command prompt as Administrator, and navigate to the `C:\IBM\i2analyze\SDK\sdk-projects\master` directory.
2.  Run the following command:

    ``` pre
    build -pr da-subset-rest-example -t unpackXap
    ```

    This command extracts the assemblies from the existing `Apollo.xap` file to the `extractedxap` directory of the `C:\IBM\i2analyze\SDK\sdk-projects\ui-subset-example` project.

3.  In Visual Studio, click **Open Project** and open the solution file at `C:\IBM\i2analyze\SDK\sdk-projects\ui-subset-example\src\PortalExtensibilityExample.sln`. The Visual Studio solution contains two projects:
    -   `SubsettingExample` demonstrates how to add a custom HTML page and display it inside a custom tab.
    -   `CommandExtensibilityExample` demonstrates how to add and modify commands in the pop-up menus for items, and in the actions toolbar.

4.  Build the solution. Visual Studio puts the DLL files that it generates in the `C:\IBM\i2analyze\SDK\sdk-projects\ui-subset-example\build` directory.
5.  Return to the command prompt, and run the following command to compile the new XAP file.

    ``` pre
    build -pr da-subset-rest-example -t packXap
    ```

6.  In Eclipse, ensure that the `i2analyze` server is stopped.
7.  At the command prompt, run the following command to redeploy the server:

    ``` pre
    build -t deploy
    ```

8.  Back in Eclipse, in the **Servers** tab, start the `i2analyze` server.
9.  To test the platform, open a web browser and navigate to http://localhost/apollo. You can log in to the Intelligence Portal with any of the default users.

Results
-------

The user interface extension example adds a **Subsetting** menu to the top of the Intelligence Portal user interface. When you click the menu, the portal displays a submenu that contains a **Launch Subsetting Tab** command. When you run the command, a **Subsetting Example** tab opens. However, the tab itself displays an error until you also deploy the server-side part of this example. To deploy the server-side part of the example, see <a href="https://github.com/IBM-i2/Analyze/blob/master/documentation/developer_essentials_example_rest.md" class="xref" title="(Opens in a new tab or window)">Configuring the &quot;two-phase data retrieval&quot; example project</a>.

Elsewhere in the user interface, the example adds commands that are named **Example command 1**, **Example command 2**, and **Example inserted command**. Depending on the current state of the application, the Intelligence Portal displays or hides these commands in different locations.

**Parent topic:** <a href="developer_essentials_welcome.md" class="link" title="IBM i2 Analyze Developer Essentials contains tools, libraries, and examples that enable development and deployment of custom extensions to i2 Analyze.">IBM i2 Analyze Developer Essentials</a>

------------------------------------------------------------------------

© Copyright IBM Corporation 2014, 2016.


