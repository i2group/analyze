# Preparing to use IBM i2 Analyze Developer Essentials

IBM® i2® Analyze Developer Essentials is a set of files and example projects that build on a standard i2 Analyze deployment. Preparing to use Developer Essentials involves installing and configuring it to work in a dedicated test environment.

## Before you begin

You must have a deployment of i2 Analyze that you can use to develop and test the example projects. If you do not already have a deployment of i2 Analyze, you can create a deployment to use. For more information about creating a deployment, see <a href="https://www.ibm.com/docs/en/i2-analyze/latest?topic=deploying" class="xref" title="(Opens in a new tab or window)">Deploying i2 Analyze</a>.

## About this task

The following procedure explains how to link the examples in Developer Essentials to the i2 Analyze libraries, how to modify the i2 Analyze user registry and a path variable, and how to set up Java.

## Procedure

1.  Extract i2 Analyze Developer Essentials, and copy the `SDK` directory into the `C:\IBM\i2analyze\` directory of your development deployment.

2.  Copy the contents of the `SDK\wlp-overrides` directory to the `i2analyze\deploy\wlp` directory and accept any overwrites.
    This action copies a modified user registry into the `wlp` directory for i2 Analyze to use. The registry contains more users and groups that help to demonstrate the functions of some of the examples.

To access the i2 Analyze libraries, the example projects rely on the presence of a variable named `TOOLKIT_ROOT` on the class path of your development environment. You can configure this in Eclipse by completing the following instructions.

1.  In Eclipse, click **Window** &gt; **Preferences** to open the Preferences window.

2.  In the Preferences window, select **General** &gt; **Workspace** &gt; **Linked Resources**.

3.  Add an entry to the **Defined path variables** list:
    -   Name: `TOOLKIT_ROOT`
    -   Location: `C:\IBM\i2analyze\toolkit`

The examples in Developer Essentials require links to the i2 Analyze libraries and the API documentation. These links are configured in a separate project directory that you must import into your development environment before you can build any of the example projects. You can configure Eclipse to work with the examples by completing the following instructions.

1.  In Eclipse, click **File** &gt; **Import** to open the Import window.

2.  In the Import window, click **General** &gt; **Existing Projects into Workspace**, and then click **Next**.

3.  Click **Browse** at the top of the window, and then select the `C:\IBM\i2analyze\SDK\sdk-projects\i2analyze-toolkit-libraries` directory.

4.  Click **Finish** to complete the import process.

The i2 Analyze application uses OpenJDK, and the OpenJDK is installed by the i2 Analyze deployment toolkit. If the JRE that is installed by the toolkit is not the default JRE in your environment, then you can configure it as the default JRE. If you are using Eclipse, configure the default JRE by completing the following instructions.

1.  In the Preferences window, click **Java** &gt; **Installed JREs** &gt; **Add** to open the Add JRE window.

2.  Use the Add JRE window to add a **Standard VM** named **Toolkit Java**. The home directory for this JRE is `C:\IBM\i2analyze\deploy\java`.

3.  Select the check box that makes **Toolkit Java** the default JRE in Eclipse.