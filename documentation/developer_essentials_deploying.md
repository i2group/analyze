Deploying IBM i2 Analyze Developer Essentials
=============================================

IBM® i2® Analyze Developer Essentials is a set of files and example projects that build on the standard i2 Analyze deployment toolkit. You can deploy Developer Essentials on a single workstation.

Before you begin
----------------

IBM i2 Analyze Developer Essentials has a longer set of requirements than the platform itself. Before you begin, you must have access to all of the following software:
-   IBM Installation Manager 1.8
-   IBM i2 Analyze 4.1.5
-   IBM DB2® 10.5 Express® Edition (or Express-C Edition) with spatial extender support.
-   Web Server Plug-ins for IBM WebSphere® Application Server 8.5.5.4 or above
-   IBM HTTP Server 8.5.5.4 or above
-   Eclipse Neon (Eclipse IDE for Java™ EE Developers)

About this task
---------------

The deployment process for i2 Analyze Developer Essentials follows the same sequence as the deployment process for i2 Analyze. As a result, it is helpful to keep the <a href="http://www.ibm.com/support/knowledgecenter/SSXVXZ/com.ibm.i2.deploy.example.doc/c_deploy_intro.html" class="xref">Deployment patterns and examples</a> documentation open as you read the steps in this document.

Procedure
---------

In the first part of the procedure, you use Developer Essentials to create a custom version of the deployment toolkit. The libraries and settings that you add enable development for i2 Analyze on a single computer.

1.  Install IBM i2 Analyze 4.1.5 according to the instructions in the release notes. If you accept all of the default settings, the deployment toolkit is installed to `C:\IBM\i2analyze`.
2.  Follow the instructions in <a href="http://www.ibm.com/support/knowledgecenter/SSXVXZ/com.ibm.i2.eia.install.doc/software_prerequisites.html" class="xref">Software prerequisites</a> to install all the other prerequisite software.
    Note: Developer Essentials uses IBM DB2 Express Edition, which does not create Windows groups when you install it. Record the details of the `db2admin` user that DB2 creates; you need them later in this procedure.

3.  Extract i2 Analyze Developer Essentials, and copy the `SDK` directory into the `C:\IBM\i2analyze\` directory.
4.  Navigate to the `SDK\sdk-projects\master\toolkit-overrides\configuration\environment` directory, and check the following files to ensure that the default settings match your environment:
    -   In `onyx-server\environment.properties`, verify that the installation path for DB2 is correct, and that the first part of the installation path for i2 Analyze is also correct.
    -   In `opal-server\environment.properties`, verify that the installation path for DB2 is correct, and that the first part of the installation path for i2 Analyze is also correct.
    -   In `http-server.properties`, verify that the paths to the HTTP server installation and WebSphere plugin directories are correct.
    -   In `topology.xml`, verify that the first part of the installation path for i2 Analyze is correct. To allow you to run all the examples, this version of the topology file includes configuration settings for both types of i2 Analyze application.

    Note: These files refer to `deploy-dev` and `data-dev` subdirectories that do not exist until after you deploy the platform.

5.  Open the file at `SDK\sdk-projects\master\toolkit-overrides\configuration\environment\credentials.properties` in a text editor, and add the user names and passwords for the databases and the Solr index, and the password for the LTPA keys file.
    Note: The user name and passwords used for your databases should match the `db2admin` details that you recorded earlier.

At this stage, configuration of the development version of i2 Analyze is complete. You can now deploy it.

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

Now, the development version of i2 Analyze is ready for use. The remaining steps configure the Eclipse development environment to interact with it.

1.  Start Eclipse, and then open a web browser and navigate to <a href="https://developer.ibm.com/wasdev" class="uri" class="xref">https://developer.ibm.com/wasdev</a>. This website contains tools and information for developing web applications for WebSphere Application Server Liberty Profile.
2.  Click **Downloads** and then **Download Liberty in Eclipse** to open the tools download page. Follow the instructions to install the WebSphere Developer Tools by dragging the **Install** icon. Accept all of the default settings.
3.  In Eclipse, click **Window** &gt; **Preferences** to open the Preferences window.
4.  If IBM Java is not the default JRE on your computer, then you must configure it as the default JRE in Eclipse.
    1.  In the Preferences window, click **Java** &gt; **Installed JREs** &gt; **Add** to open the Add JRE window.
    2.  Use the Add JRE window to add a **Standard VM** named **IBM Java**. The home directory for this JRE is `C:\IBM\i2analyze\deploy-dev\java`.
    3.  Select the check box that makes IBM Java the default JRE in Eclipse.

5.  Create the server runtime environments in which the i2 Analyze applications will run:
    1.  In the Preferences window, click **Server** &gt; **Runtime Environments** &gt; **Add** to open the New Server Runtime Environment window.
    2.  In the New Server Runtime Environment window, click **IBM** &gt; **WebSphere Application Server Liberty**, and then click **Next**.
    3.  Set up the server runtime environment:
        -   Set the **Name** to WebSphere Application Server Liberty Profile
        -   Set the **Path** to `C:\IBM\i2analyze\deploy-dev\wlp`
        -   Use the default JRE

    4.  Click **Finish**.

6.  Click **OK** to close the Preferences window.
7.  Create a server in Eclipse that represents the `onyx-services-ar` application:
    1.  In the Eclipse application window, click the **Servers** tab.

        Note: If the **Servers** tab is not visible, click **Window** &gt; **Show View** &gt; **Servers** to open it.

    2.  Right-click inside the tab, and then click **New** &gt; **Server** to open the New Server window.
    3.  In the New Server window, click **IBM** &gt; **WebSphere Application Server Liberty** to select the server type.
    4.  Change the **Server name** to `onyx-server`, and then click **Next**.
    5.  From the **Liberty server** list, select **onyx-server**, and then click **Finish**.

8.  Create a server in Eclipse that represents the `opal-services-is` application by repeating the steps above, and creating a server named `opal-server`.
9.  Start the development version of the `onyx-services-ar` application:
    1.  Inside the **Servers** tab, double-click the **onyx-server** server. A new **onyx-server** tab opens in the top part of the Eclipse application window.
    2.  Modify the settings in the **onyx-server** tab:
        -   Clear the **Run applications directly from the workspace** check box
        -   Click **Publishing** &gt; **Never publish automatically**
        -   Press Ctrl+S to save the settings

        Note: These settings mean that in order for any changes that you make to become apparent, you must manually publish your changes before you restart the server.

    3.  Back inside the **Servers** tab, start the server. When the startup process completes, you have a running instance of i2 Analyze that you can control through Eclipse.

10. Repeat the previous step to start the development version of the `opal-services-is` application.
    Note: When you start the `opal-services-is` application, the **Console** tab displays error messages about problems with `SLF4J`. These errors are benign, and you can safely ignore them here.
    If you see error messages about the Solr indexing service, run the following command at your command prompt to ensure that the service is running:

    ``` pre
    build -t startSolrAndZk -s opal-server
    ```

11. If you have not done so recently, use the Services application in Windows (`services.msc`) to restart IBM HTTP Server.
12. To test the applications, you must connect to the deployment using one of the supported clients. See <a href="http://www.ibm.com/support/knowledgecenter/SSXVXZ/com.ibm.i2.deploy.example.doc/connecting_to_clients.html" class="xref">Connecting to clients</a> for more information.

**Parent topic:** <a href="developer_essentials_welcome.md" class="link" title="IBM i2 Analyze Developer Essentials contains tools, libraries, and examples that enable development and deployment of custom extensions to i2 Analyze.">IBM i2 Analyze Developer Essentials</a>

------------------------------------------------------------------------

© Copyright IBM Corporation 2014, 2017.


