Configuring the "user interface extension" example project
==========================================================

i2 Analyze supports extensions that enable users to interact with external data sources through enhancements to the Intelligence Portal. In Developer Essentials, IBM provides the `onyx-ui-subset-example` example, which includes a demonstration of how to integrate custom elements into the standard Intelligence Portal user interface.

Before you begin
----------------

You must have a development deployment of i2 Analyze and a configured development environment. For more information, see <a href="developer_essentials_deploying.md" class="xref" title="IBM i2 Analyze Developer Essentials is a set of files and example projects that build on a standard i2 Analyze deployment. Preparing to use Developer Essentials involves installing and configuring it to work in a dedicated test environment.">Preparing to use IBM i2 Analyze Developer Essentials</a>.
The example also has some additional prerequisites:
-   Microsoft Visual Studio Express 2015 for Web
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

The two-part project is structured so that the `onyx-da-subset-rest-example` project, which implements the custom functionality, refers to the `onyx-ui-subset-example` project. To demonstrate the custom user interface, you need to configure both projects.

1.  Open the configuration file for user interface extensions for the `onyx-da-subset-rest-example` project in a text editor: `C:\IBM\i2analyze\SDK\sdk-projects\onyx-da-subset-rest-example\onyx-ui-project.properties`.
2.  Ensure that the properties are set for your environment. If you followed the deployment instructions for Developer Essentials, the correct values look like this example:
    ``` pre
    ui.project.dir=C:/IBM/i2analyze/SDK/sdk-projects/onyx-ui-subset-example
    msbuild.exe.path=C:/Windows/Microsoft.NET/Framework/v4.0.30319/MSBuild.exe
    ```

The procedure for extending the Intelligence Portal starts with extracting the files from the standard web application, which is the `Apollo.xap` file. Then, you add your custom functionality to the standard content before you recompile and redeploy a new version of the XAP file.

1.  In the `SDK\sdk-projects\onyx-ui-subset-example` directory, create a directory named `extractedxap`.
2.  Extract the contents of the `toolkit\application\targets\onyx-services-ar\Apollo.xap` file into the `extractedxap` directory that you created.
3.  In Visual Studio, click **Open Project** and open the solution file at `C:\IBM\i2analyze\SDK\sdk-projects\onyx-ui-subset-example\src\PortalExtensibilityExample.sln`.
    The Visual Studio solution contains two projects:
    -   `SubsettingExample` demonstrates how to add a custom HTML page and display it inside a custom tab.
    -   `CommandExtensibilityExample` demonstrates how to add and modify commands in the pop-up menus for items, and in the actions toolbar.

4.  Build the solution.
    Visual Studio puts the DLL files that it generates in the `C:\IBM\i2analyze\SDK\sdk-projects\onyx-ui-subset-example\build` directory.

5.  In the `toolkit\configuration\fragments` directory, create the `xap-supplement` directory.
6.  Copy the contents of the `SDK\sdk-projects\onyx-ui-subset-example\build` directory to the `toolkit\configuration\fragments\xap-supplement` directory.
7.  Navigate to the `toolkit\scripts` directory and run the following command to compile the new XAP file.
    ``` pre
    setup -t packXap -s onyx-server
    ```

8.  At the command prompt, run the following command to stop the server:
    ``` pre
    setup -t stop -s onyx-server
    ```

9.  At the command prompt, run the following command to redeploy the server:
    ``` pre
    setup -t deploy -s onyx-server
    ```

10. Run the following command to start the i2 Analyze server:
    ``` pre
    setup -t start -s onyx-server
    ```

11. Use the Services application in Windows ( `services.msc` ) to restart IBM HTTP Server.
12. To test the platform, open a web browser and navigate to `http://localhost/apollo`. You can log in to the Intelligence Portal with any of the default users.

Results
-------

The user interface extension example adds a **Subsetting** menu to the top of the Intelligence Portal user interface. When you click the menu, the portal displays a submenu that contains a **Launch Subsetting Tab** command. When you run the command, a **Subsetting Example** tab opens. However, the tab itself displays an error until you also deploy the server-side part of this example. To deploy the server-side part of the example, see <a href="https://github.com/IBM-i2/Analyze/blob/master/documentation/developer_essentials_example_rest.md" class="xref" title="(Opens in a new tab or window)">Configuring the &quot;two-phase data retrieval&quot; example project</a>.

Elsewhere in the user interface, the example adds commands that are named **Example command 1**, **Example command 2**, and **Example inserted command**. Depending on the current state of the application, the Intelligence Portal displays or hides these commands in different locations.

**Parent topic:** <a href="developer_essentials_welcome.md" class="link" title="IBM i2 Analyze Developer Essentials contains tools, libraries, and examples that enable development and deployment of custom extensions to i2 Analyze. Developer Essentials also includes API documentation and guides to deploying the software and the example projects.">IBM i2 Analyze Developer Essentials</a>

------------------------------------------------------------------------

© Copyright IBM Corporation 2014, 2020.


