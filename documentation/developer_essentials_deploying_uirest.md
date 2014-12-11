Publishing a solution with UI and REST components to a live deployment
======================================================================

When the user interface extension and two-phase data retrieval examples are running in a development environment, the next step is to publish them to a live deployment of the platform.

Before you begin
----------------

These instructions assume that you have access to two instances of IBM i2 Intelligence Analysis Platform:

-   The development version of the platform, deployed according to the instructions in IBM i2 Intelligence Analysis Platform Developer Essentials. This instance contains the solution that you want to publish.
-   The production version of the platform, deployed according to the instructions in the IBM i2 Intelligence Analysis Platform Deployment Guide. This instance has all of its default settings.

About this task
---------------

The publication process for a data acquisition solution with UI and REST components has several phases:

1.  Set up a new server to host the REST-enabled data access on-demand solution.
2.  Create a fragment that contains the REST component and all of its requirements.
3.  Add the UI component to the Deployment Toolkit on the write side of the live deployment
4.  Update the Deployment Toolkit on the write side of the live deployment to include the new fragment.
5.  Run the scripts to update the live deployment with the data acquisition solution.

Procedure
---------

In an Intelligence Analysis Platform deployment of this type, the REST service and external data source are typically co-located on a separate server from the standard read and write sides. You need to configure a new server on the logical read side of the platform.

1.  Set up a server with the same software prerequisites as a standard Intelligence Analysis Platform read server, except that the example solution does not require a database management system.

To prepare an application for the read side of the platform, the scripts copy the fragments for that application into one place, and create a WAR file from them. To publish a custom application, you must create a fragment that contains your code and configuration, and then reference that fragment from the topology file.

In this example, Eclipse automatically compiles all code into its `sdk-fragment` directory, which is linked to a real directory in the file system. You can create the deployment fragment by following these instructions.

1.  In the Deployment Toolkit for the development version of the platform, navigate to `TOOLKIT_ROOT\configuration\fragments\sdk-da-subset-rest-example`. This directory contains the code with which to populate the new WAR file.

    Attention: Notice that the fragment directory has "`sdk`" in its name. Do not confuse this directory with other children of the `fragments` directory.

2.  Copy the `sdk-da-subset-rest-example` directory from the Deployment Toolkit for the development deployment to the same location on the write side of the production deployment.
3.  Copy the `configuration\environment\newread` directory from the Deployment Toolkit for the development deployment to the same location on the write side of the production deployment.

    You copy the solution to the write server first, so that you can use the deployment scripts to add and register the new data source with the platform. After you update the write side, you can deploy the service to the new server.

4.  Amend `environment.properties` and `port-def.props` in the `newread` directory to reflect the configuration of the WebSphere Application Server that hosts the new services.

    Note: On this occasion, the only important setting in `environment.properties` is `was.home.dir`, which must reflect the path to WebSphere Application Server on the new server.

5.  To add the custom extension to the Intelligence Portal, use the contents of `configuration\fragments\write` from the Deployment Toolkit for the development deployment to update the production deployment.

    1.  Copy the `Apollo.xap` file from the development deployment, and use it to replace the file with the same name in the production deployment.
    2.  Copy the `<SubsettingExample...>` elements from the bottom of the `ApolloClientSettings.xml` file in the development deployment, and add them to the file with the same name in the production deployment.

    These operations ensure that the updated XAP file is present when you update the deployment.

After you add all the pieces of the solution to the Deployment Toolkit for the production deployment, you must update the configuration of the platform itself.

1.  Open the topology file that represents the production deployment of the Intelligence Analysis Platform in an XML editor. By default, this file is at `IAP-Deployment-Toolkit\configuration\environment\topology.xml`.
2.  Edit the topology file to add the following `<application>` element immediately before the closing tag of the `<applications>` element:

    ``` {.pre .codeblock}
       ...
       <application name="newread" host-name="hostname">
          <wars>
          </wars>
       </application>
    </applications>
    ```

    Important: *hostname* must be the fully qualified name of the new server that hosts the REST-enabled data access on-demand solution.

3.  Copy the `configuration\environment\daod` directory from the Deployment Toolkit for the development deployment to the same location on the write side of the production deployment.
4.  Run `da-setup.py` on the write server of the production deployment:

    ``` {.pre .codeblock}
    python da-setup.py -t add-daod-data-source -a newread -d data-source-name
    ```

    Note: In the production deployment, *data-source-name* does not have to match the name of an Eclipse project. The setting controls the name that users see in the Intelligence Portal. You can supply any *data-source-name*, but do not use the name of one of the fragment directories.

5.  Back in `topology.xml`, modify the `<DataSource>` element that the `add-daod-data-source` command created so that its attributes have values that match the equivalent element in the development version of the platform.
6.  Still in the topology file, add information about your new fragment to the `<fragments>` child of the new `<war>` element that references it. The name of the fragment must match the name of the directory in `configuration\fragments`.

    Important: Fragments are processed in order from top to bottom, and later fragments override earlier ones. The previous step added a fragment that is named after the data source. Add your fragment to the foot of the list:

    ``` {.pre .codeblock}
    <fragments>
       ...
       <fragment name="data-source-name" />
       <fragment name="sdk-da-subset-rest-example" />
    </fragments>
    ```

7.  On the write side of the production deployment, run the following command:

    ``` {.pre .codeblock}
    python deploy.py -s write -t register-data-sources
    ```

    This command generates a GUID for the data source in `environment\dsid\dsid.data-source-name-id.properties`.

8.  In the Deployment Toolkit for the production deployment, update the `<SubsettingExample...>` elements at the bottom of `ApolloClientSettings.xml` to reference the generated DSID. Also, change the host name to reference the server that runs the HTTP proxy.

    ``` {.pre .codeblock}
    <SubsettingExampleGenerationUILocation>
    http://proxy-server/data-source-id/SubsettingHtml.html</SubsettingExampleGenerationUILocation>
    <SubsettingExampleDisplayRecordUrl>
    http://proxy-server/data-source-id/ItemDetails.jsp?id={0}&amp;source={1}</SubsettingExampleDisplayRecordUrl>
    ```

9.  Add entries for `websphere.newread.user-name` and `websphere.newread.password` to `credentials.properties`. These entries enable the deployment scripts to interact with the instance of WebSphere Application Server on the new server.

After you update the configuration files, you can run the scripts to deploy your updated version of the Intelligence Analysis Platform.

1.  On the write side, run the following commands:

    ``` {.pre .codeblock}
    python deploy.py -s write -t update-application
    python deploy.py -s write -t install-http-server-config
    ```

2.  Still on the write side, update the configuration of the proxy server so that communication with the new server uses the same authentication cookies as the rest of the platform:
    1.  Open the file at `C:\IBM\HTTPServer\plugins\iap\config\plugin-cfg.xml` in an XML editor.
    2.  Find the `<UriGroup>` element whose name contains the name of the new data source, and then add three child elements to it:

        ``` {.pre .codeblock}
           ...
           <Uri AffinityCookie="JSESSIONID" AffinityURLIdentifier="jsessionid"
                Name="/data-source-id/services/ExternalDataRetrievalService/*"/>
           <Uri AffinityCookie="JSESSIONID" AffinityURLIdentifier="jsessionid"
                Name="/data-source-id/SubsettingHtml.html"/>
           <Uri AffinityCookie="JSESSIONID" AffinityURLIdentifier="jsessionid"
                Name="/data-source-id/ItemDetails.jsp"/>
           <Uri AffinityCookie="JSESSIONID" AffinityURLIdentifier="jsessionid"
                Name="/data-source-id/rest/daodSubsets"/>
        </UriGroup>
        ```

3.  Stay on the write side, and restart the HTTP server.

At this point, you are ready to deploy the application to the new server on the read side of the platform.

1.  Copy the Deployment Toolkit from the write server to the server that hosts the `newread` application.
2.  On that server, run the following command:

    ``` {.pre .codeblock}
    python deploy.py -s newread
    ```

3.  Set up security in the instance of WebSphere Application Server on the new server in the same way that you configured it on the standard read and write servers.

    If your Intelligence Analysis Platform deployment uses WebSphere federated repositories, then the information in the new repository must match the information in the existing repositories.

4.  Finally, start the server that hosts the `newread` application:

    ``` {.pre .codeblock}
    python deploy.py -s newread -t start-was-server
    ```

Results
-------

After you run the final sequence of commands, the new data source is available to users of your Intelligence Analysis Platform deployment.

* * * * *

© Copyright IBM Corporation 2014.


