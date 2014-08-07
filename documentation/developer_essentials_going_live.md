Publishing a data access on-demand solution to a live deployment
================================================================

After you develop and test a data access on-demand solution for the Intelligence Analysis Platform, the next step is to publish it to a live deployment.

Before you begin
----------------

These instructions assume that you have access to two instances of the Intelligence Analysis Platform:

-   The development version of the platform, deployed according to the instructions in IBM i2 Intelligence Analysis Platform Developer Essentials. This instance contains the custom application that you want to publish.
-   The production version of the platform, deployed according to the instructions in the IBM i2 Intelligence Analysis Platform Deployment Guide. This instance has all of its default settings.

About this task
---------------

The publication process for a custom Intelligence Analysis Platform application has three main phases:

1.  Create a fragment that contains the application and all of its requirements.
2.  Update the Deployment Toolkit on the write side of the live deployment to include the new fragment.
3.  Run the scripts to update the live deployment with the custom application.

Procedure
---------

When you deploy an application, the scripts copy all the fragments for that application into one place, and then turn the result into a WAR file. To publish a custom application, you must create a fragment that contains your code and configuration, and then reference that fragment from the topology file.

The procedure that you follow depends on the complexity of the project in Eclipse.

1.  If all the project code is in source folders of the dynamic web project, then you have a simple project. In this case, Eclipse automatically compiles all code into its `sdk-fragment` directory, which is linked to a real directory in the file system. You can create the deployment fragment by following these instructions:
    1.  In the Deployment Toolkit, navigate to `TOOLKIT_ROOT/configuration/fragments/fragment-name`. For example, the *fragment-name* for the `da-subset-filesystem-example` project is `sdk-da-subset-filesystem-example`.
    2.  Verify that any web content for the project is in the root of the `fragment-name` directory.
    3.  Verify that the `WEB-INF/classes` directory contains all the files that you expect.
    4.  Copy the `fragment-name` directory from the Deployment Toolkit for the development deployment to the same location on the write side of the production deployment.

2.  If your project code references other Eclipse projects, external libraries, or external resources, then you have a complex project. In this case, you must build the code in your Eclipse projects, and then place the completed artifacts into a directory that looks like a deployment fragment.

    Warning: The approach that this procedure describes is quick, but inefficient. The fragment that you create contains all the code from the common fragments and the target that are configured in Eclipse. It therefore contains duplicates of standard Intelligence Analysis Platform JAR files and security settings, and the common server settings. Subsequent changes to these parts of the project through the standard mechanisms do not apply to a fragment that you create in this way.

    1.  Export the project from Eclipse as a WAR file. Eclipse automatically pulls in the dependencies for the WAR file as it exports the project.
    2.  Extract the exported WAR file so that it looks like a new fragment.
    3.  Copy the new fragment directory to the `configuration/fragments` directory of the Deployment Toolkit on the write side of the production deployment.

After you create the fragment and add it to the Deployment Toolkit for the production deployment, you must update the configuration of the live platform.

1.  If you changed the platform schema in the development version, copy the modified schema to the `configuration/fragments/common/WEB-INF/classes` directory in the Deployment Toolkit on the write side of the production deployment.
2.  Run `da-setup.py` on the write-side server of the production deployment:

    ``` {.pre .codeblock}
    python da-setup.py -t add-data-source
                       -a read
                       -d data-source-name
    ```

    Note: In the production deployment, *data-source-name* does not have to match the name of an Eclipse project. You can give it any name, except the name of one of the fragment directories.

3.  Open `topology.xml` and add information about your new fragment to the `<fragments>` child of the new `<war>` element that references it. The name of the fragment must match the name of the directory in `configuration/fragments`.

    Important: Fragments are processed in order from top to bottom. Later fragments override earlier fragments. Therefore, add your fragment to the foot of the list.

After you update the configuration files, you can run the scripts to redeploy your new version of the Intelligence Analysis Platform.

1.  On the write side, run the following commands:

    ``` {.pre .codeblock}
    python deploy.py -s write -t update-application
    python deploy.py -s write -t install-http-server-config
    ```

2.  Stay on the write side, and restart the HTTP server.
3.  Copy the Deployment Toolkit from the write-side server to the read-side server.
4.  On the read side, run the following commands:

    ``` {.pre .codeblock}
    python deploy.py -s read -t update-application
    python deploy.py -s read -t provision-storage-and-messaging
    ```

5.  Back on the write side, run the following command to start all the Intelligence Analysis Platform applications:

    ``` {.pre .codeblock}
    python deploy.py -s write -t start-all-applications
    ```

* * * * *

© Copyright IBM Corporation 2014.


