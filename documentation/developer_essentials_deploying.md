Deploying IBM i2 Analyze Developer Essentials
=============================================

IBM® i2® Analyze Developer Essentials is a set of files and example projects that build on the standard Deployment Toolkit. You can deploy Developer Essentials on a single workstation.

Before you begin
----------------

IBM i2 Analyze Developer Essentials has a longer and more specific set of requirements than the platform itself. Before you begin, you must have access to all of the following software:
-   IBM Installation Manager 1.8
-   IBM i2 Analyze 4.1.1
-   IBM DB2® 10.5 Express® Edition (or Express-C Edition)
-   Web Server Plug-ins for IBM WebSphere® Application Server 8.5.5
-   IBM HTTP Server 8.5
-   Eclipse Mars (Eclipse IDE for Java™ EE Developers)

About this task
---------------

The deployment process for i2 Analyze Developer Essentials follows the same sequence as the deployment process for i2 Analyze. As a result, many of the steps in these instructions make reference to the i2 Analyze Deployment Guide. It is helpful to keep that guide open as you read the steps in this document.

Procedure
---------

In the first part of the procedure, you use Developer Essentials to create a custom version of the Deployment Toolkit. The libraries and settings that you add enable development for i2 Analyze on a single computer.

1.  Install IBM i2 Analyze 4.1.1 according to the instructions in the release notes. If you accept all of the default settings, the Deployment Toolkit is installed to `C:\IBM\i2analyze`.
2.  Install all the other prerequisite software and accept all of the default settings, apart from the target directory for IBM HTTP Server that should be set to `C:\IBM\HTTPServer`, and the Web Server Plug-ins for IBM WebSphere Application Server that should be set to `C:\IBM\WebSphere\Plugins`.
    Note: Developer Essentials uses IBM DB2 Express Edition, which does not create Windows groups when you install it. Record the details of the `db2admin` user that DB2 creates; you need them later in this procedure.

3.  Extract i2 Analyze Developer Essentials, and copy the `SDK` directory into the `C:\IBM\i2analyze\` directory.
4.  Check the following files to ensure that the default settings match your environment:
    -   `SDK\sdk-projects\master\toolkit-overides\configuration\environment\i2analyze\environment.properties`
    -   `SDK\sdk-projects\master\toolkit-overides\configuration\environment\http-server.properties`
    -   `SDK\sdk-projects\master\toolkit-overides\configuration\environment\topology.xml`

    Note: These files refer to `deploy-dev` and `data-dev` directories that do not exist until after you deploy the platform. When paths contain these directories, ensure that the parent directories are present on your computer.

5.  Open the file at `SDK\sdk-projects\master\toolkit-overrides\configuration\environment\credentials.properties` in a text editor, and add the user name and passwords for DB2 and the LTPA keys file.

At this stage, configuration of the development version of i2 Analyze is complete. You can now deploy the platform.

1.  Open a command prompt as Administrator, and navigate to the `SDK\sdk-projects\master` directory.
2.  Run the following command:

    ``` pre
    build -t deployExample
    ```

    Note: The development version of i2 Analyze uses a file-based registry of users and groups. Developer Essentials includes definitions of users and groups that correspond to the example security schema. The default users are:
    -   Analyst
    -   Manager
    -   Demo

    For each of these users, the password is password.

Now, the development version of i2 Analyze is ready for use. The remaining steps configure the Eclipse development environment to interact with the platform.

1.  Start Eclipse, and then open a web browser and navigate to https://developer.ibm.com/wasdev/. This website contains tools and information for developing web applications for WebSphere Application Server Liberty Profile.
2.  Click **Downloads** and then **Download in Eclipse** to open the tools download page. Follow the instructions on the page to install the tools for **WAS Liberty Profile**.
    Note: Do not follow the instructions to create a WAS Liberty server at this time.

3.  In Eclipse, click **Window** &gt; **Preferences** to open the Preferences window.
4.  Optional: If IBM Java is not the default JRE on your computer, then you must configure it as the default JRE in Eclipse.
    1.  In the Preferences window, click **Java** &gt; **Installed JREs** &gt; **Add** to open the Add JRE window.
    2.  Use the Add JRE window to add a **Standard VM** named **IBM Java**. The default home directory for this JRE is `C:\IBM\i2analyze\deploy-dev\java`.
    3.  Select the check box that makes IBM Java the default JRE in Eclipse.

5.  Create a server runtime environment in which the i2 Analyze application will run.
    1.  In the Preferences window, click **Server** &gt; **Runtime Environments** &gt; **Add** to open the New Server Runtime Environment window.
    2.  In the New Server Runtime Environment window, click **IBM** &gt; **WebSphere Application Server Liberty Profile**, and then click **Next**.
    3.  Set the **Path** of the new server runtime environment to `C:\IBM\i2analyze\deploy-dev\wlp`, instruct it to use the default JRE, and then click **Finish**.

6.  Click **OK** to close the Preferences window.
7.  Create a server in Eclipse that represents the i2 Analyze deployment.
    1.  In the Eclipse application window, click the **Servers** tab.

        Note: If the **Servers** tab is not visible, click **Window** &gt; **Show View** &gt; **Servers** to open it.

    2.  Right-click inside the tab, and then click **New** &gt; **Server** to open the New Server window.
    3.  In the New Server window, click **IBM** &gt; **WebSphere Application Server Liberty Profile** to select the server type.
    4.  Change the **Server name** to `i2analyze`, and then click **Next**.
    5.  From the **Liberty profile server** list, select **i2analyze**, and then click **Finish**.

8.  Start the development version of i2 Analyze.
    1.  Inside the **Servers** tab, double-click the **i2analyze** server. A new **i2analyze** tab opens in the top part of the Eclipse application window.
    2.  Modify the settings in the **i2analyze** tab:
        -   Clear the **Run applications directly from the workspace** check box
        -   Click **Publishing** &gt; **Never publish automatically**
        -   Press Ctrl+S to save the settings

        Note: These settings mean that in order for any changes that you make to become apparent, you must restart the server.

    3.  Back inside the **Servers** tab, start the server. When the startup process completes, you have a running instance of i2 Analyze that you can control through Eclipse.

9.  If you have not done so recently, use the Services application in Windows (`services.msc`) to restart IBM HTTP Server.
10. To test the platform, open a web browser and navigate to http://localhost/apollo. You can log in to the Intelligence Portal with any of the default users.

------------------------------------------------------------------------

© Copyright IBM Corporation 2014, 2016.


