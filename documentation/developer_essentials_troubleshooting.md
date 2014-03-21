Troubleshooting the example projects
====================================

The example projects in Intelligence Analysis Platform Developer Essentials are sensitive to changes or inconsistencies in the configuration of the Eclipse IDE. The errors that you see can be cryptic, but there are standard approaches to resolving the problems that they identify.

Eclipse reports compilation errors after it imports an example project
----------------------------------------------------------------------

For an unmodified example project, compilation errors usually occur in Eclipse when the `TOOLKIT_ROOT` variable is not set, or when it is set incorrectly.

To determine whether there is a problem with `TOOLKIT_ROOT`, expand one of the example project directories. Some of the directory icons inside the project have glyphs:

-   An arrow on the lower-right corner of a directory icon indicates a link to an external directory. If you see the arrow, then the variable is set correctly.
-   An exclamation point on the lower-right corner of a directory icon indicates a broken link. You are not able to expand an icon that is in this state. In this case, the variable is not set correctly.

If you have a project with broken links, there are two things to check:

1.  Verify that you extracted Developer Essentials into the same location as the Deployment Toolkit.
2.  Verify that your `TOOLKIT_ROOT` variable is set to the path of Deployment Toolkit directory.

Server starts without error, but in only a few seconds
------------------------------------------------------

If one or both of the application servers start in an unusually short time, it is likely that an Intelligence Analysis Platform application is not deployed correctly.

1.  In Eclipse, inside the **Servers** tab, double-click the **read** and **write** servers in turn. New **read** and **write** tabs open in the top part of the Eclipse application window.
2.  In both new tabs, ensure that the **Run applications directly from the workspace** check box is cleared, and then press Ctrl+s to save the settings.

Important: This setting can cause various other problems. For the Intelligence Analysis Platform to behave correctly in the Eclipse IDE, you must keep these check boxes clear.

Application fails to start and reports an error
-----------------------------------------------

There are several reasons why an Intelligence Analysis Platform application can fail to start. You can use the error message to diagnose the problem and choose a solution.

> `java.lang.NoClassDefFoundError: com.i2group.tracing.TracingManager`

This error implies that the application cannot access the shared Java libraries. Sometimes, the problem is that you ran `da-setup.py` multiple times for the same Data Acquisition endpoint. In this situation:

1.  In a text editor, open the `topology.xml` file.
2.  If the file contains multiple entries for the same Data Acquisition endpoint:
    1.  Remove the duplicate entries, leaving only the original entry in place.
    2.  Run `deploy.py` again for your Intelligence Analysis Platform deployment.

If multiple endpoint definitions are not the issue, then references to the shared libraries are faulty, which is a symptom of two slightly different problems:

1.  A server configuration file does not contain all the information that a particular application requires.
2.  A server configuration file contains the information that a particular application requires, but there is a mismatch between the Eclipse web project name and the application name.

For each server in your deployment, you must examine the `server.xml` configuration file.

1.  In Eclipse, inside the **Servers** tab, double-click the server name to open a tab in the top part of the Eclipse application window.
2.  In the new tab, click **Open server configuration** to open the relevant `server.xml` file.
3.  Examine the `<webApplication>` elements in the file. When the file is correct, each of these elements has multiple child elements, one of which is `<classloader>`.
    -   If there is an empty `<webApplication>` element, then you have the first problem.
    -   If there is an empty element and a `<webApplication>` element that appears to be correct, then you have the second problem.

The solution to both problems is to re-create the `server.xml` file. Solving the second problem requires some additional work beforehand.

1.  If you have the name mismatch problem, edit `topology.xml` for your deployment so that the web application name matches the Eclipse project name.
2.  Use `deploy.py` to re-create `server.xml` from `topology.xml`:

    ``` {.pre .codeblock}
    python deploy.py -s server-name -t update-liberty
    ```

3.  Refresh all the projects and all the servers in the Eclipse IDE.

Eclipse reports a java.lang.NullpointerException on "Clean" or "Publish"
------------------------------------------------------------------------

Occasionally, the representation of the project files in Eclipse can lose synchronization with the file system. When this problem happens, cleaning or publishing a project can generate an exception. To fix the project, repeat the failed operation.

User login fails and generates a console error
----------------------------------------------

If users cannot log in to your deployment of the Intelligence Analysis Platform, a missing user repository is often the cause. The platform generates the following error:

> `WebTrustAssociationFailedException: No UserRegistry found to authenticate with`

To resolve this problem, ensure that you configured example users according to the Developer Essentials deployment instructions.

User login fails with no obvious console error
----------------------------------------------

If users cannot log in to your deployment of the Intelligence Analysis Platform, and all they see is a "failed login" message, then any error is in the log.

To understand the problem, refresh Eclipse, and examine the log file at `servers/server-name`/apps/logs/war-name/IBM\_i2\_Analysis\_Repository.log.

Note: Intelligence Analysis Platform error messages contain contributions from several parts of the system. Usually, the first and last sections of a message are the most useful.

-   If the top-level error contains "`SystemResourceRuntimeException`", check the message for an explanation of the problem.
-   Check the bottom-level exception, which contains most detail about the cause of the problem.

Navigation to http://localhost/apollo reports an Internal Server Error
----------------------------------------------------------------------

If the platform reports an internal server error, then the write-side server did not start. Start the server and try the operation again.

Navigation to http://localhost/apollo reports HTTP error 404
------------------------------------------------------------

HTTP error 404 can occur when one of the Intelligence Analysis Platform applications is not correctly deployed, or when the HTTP server is misconfigured.

-   If the error is "Context Root Not Found", with obvious "IBM" and "IBM WebSphere Application Server" branding, then the HTTP server redirect is working and the problem is with the application.

    To resolve the problem, follow the previous procedure for clearing the **Run applications directly from the workspace** check box.

-   If the error is a plain web page with a generic "Not Found" message, then the problem is with the HTTP server.

    To resolve the problem, see the detailed procedure at the end of the next section.

Browse and search operations generate Server Not Found errors
-------------------------------------------------------------

Often, you can resolve a "Server Not Found" error by restarting the HTTP server. The procedure takes only a few seconds, so it makes sense to try this approach first.

If a restart does not fix the problem, "Server Not Found" errors have three possible causes. Either one of the Intelligence Analysis Platform applications failed, or it is not correctly deployed, or the HTTP server is misconfigured.

To determine whether an application failed, check the console log for the read server, and the application log for the application that you were trying to use. If there is an exception, you must resolve the problem that caused it.

To determine whether there is a problem with HTTP server configuration or application deployment, attempt to view the application directly in your web browser:

-   For the standard read side of an Intelligence Analysis Platform deployment, navigate to http://localhost:9082/iap-datasource-guid. A correct deployment displays a basic HTML page.
-   For a data access on-demand solution, navigate to http://localhost:9082/daod-datasource-guid. A correct deployment displays a basic HTML page.
-   For a data load ELP stage, navigate to http://localhost:9082/delps-datasource-guid. A correct deployment displays a basic HTML page.

Note: In all cases, you can obtain datasource-guid by opening `server.xml`. In the `<webApplication>` element, the `war-name` attribute is human-readable, while the `context-root` attribute contains the GUID that you need.

If you see HTTP error 404 instead of a basic HTML page, then the application is not deployed. In most cases, you can resolve the problem by following the procedure for clearing the **Run applications directly from the workspace** check box. For a data access on-demand project, you might have to intervene more forcefully:

1.  In Eclipse, refresh the project that represents WebSphere Application Server Liberty Profile.
2.  In that project, expand the `servers/read/apps` folder.

    If the folder contains `project-name`.war.xml file instead of a `project-name`.war subfolder, then the application is not deployed.

3.  In the **Servers** tab, double-click the **read** server to open the **read** tab in the top part of the Eclipse application window.
4.  Make certain of the **Run applications directly from the workspace** setting:
    1.  If the check box is cleared, select it, and then save the settings.
    2.  Clear the check box, and then save the settings again.
    3.  Clean the server. The folder, rather than the file, is added to the project.

If you see a basic HTML page, then the application is deployed but you have an HTTP server problem. When a simple restart does not work, you must check the server configuration:

1.  In a text editor, open the file at `http-server-dir`/Plugins/iap/config/plugin-cfg.xml.
2.  Find the `<UriGroup Name="read_Cluster_URIs">` element. Depending on the topology of your deployment, the contents look like this example:

    ``` {.pre .codeblock}
    <Uri AffinityCookie="JSESSIONID" AffinityURLIdentifier="jsessionid" Name="/6bab453d-101d-4984-b6ec-888d7f7f2814/services/IndexAdminService/*"/>
    <Uri AffinityCookie="JSESSIONID" AffinityURLIdentifier="jsessionid" Name="/6bab453d-101d-4984-b6ec-888d7f7f2814/services/ItemRetrievalService/*"/>
    <Uri AffinityCookie="JSESSIONID" AffinityURLIdentifier="jsessionid" Name="/6bab453d-101d-4984-b6ec-888d7f7f2814/services/NetworkSearchService/*"/>
    <Uri AffinityCookie="JSESSIONID" AffinityURLIdentifier="jsessionid" Name="/6bab453d-101d-4984-b6ec-888d7f7f2814/services/SearchService/*"/>

    <Uri AffinityCookie="JSESSIONID" AffinityURLIdentifier="jsessionid" Name="/d1f28093-143d-4ebe-a9d4-e1202f755e6e/services/ExternalDataSubsetCreationService/*"/>
    <Uri AffinityCookie="JSESSIONID" AffinityURLIdentifier="jsessionid" Name="/d1f28093-143d-4ebe-a9d4-e1202f755e6e/services/ExternalDataSubsetExplorationService/*"/>
    <Uri AffinityCookie="JSESSIONID" AffinityURLIdentifier="jsessionid" Name="/d1f28093-143d-4ebe-a9d4-e1202f755e6e/services/ExternalDataRetrievalService/*"/>

    <Uri AffinityCookie="JSESSIONID" AffinityURLIdentifier="jsessionid" Name="/5fa4d7fb-0487-49a8-a16f-4293771b0e6d/services/AlertFeedRetrievalService/*"/>
    <Uri AffinityCookie="JSESSIONID" AffinityURLIdentifier="jsessionid" Name="/5fa4d7fb-0487-49a8-a16f-4293771b0e6d/services/AlertRetrievalService/*"/>
    <Uri AffinityCookie="JSESSIONID" AffinityURLIdentifier="jsessionid" Name="/5fa4d7fb-0487-49a8-a16f-4293771b0e6d/services/AuditService/*"/>
    <Uri AffinityCookie="JSESSIONID" AffinityURLIdentifier="jsessionid" Name="/5fa4d7fb-0487-49a8-a16f-4293771b0e6d/services/IndexAdminService/*"/>
    <Uri AffinityCookie="JSESSIONID" AffinityURLIdentifier="jsessionid" Name="/5fa4d7fb-0487-49a8-a16f-4293771b0e6d/services/ItemHistoryService/*"/>
    <Uri AffinityCookie="JSESSIONID" AffinityURLIdentifier="jsessionid" Name="/5fa4d7fb-0487-49a8-a16f-4293771b0e6d/services/ItemRetrievalService/*"/>
    <Uri AffinityCookie="JSESSIONID" AffinityURLIdentifier="jsessionid" Name="/5fa4d7fb-0487-49a8-a16f-4293771b0e6d/services/NetworkSearchService/*"/>
    <Uri AffinityCookie="JSESSIONID" AffinityURLIdentifier="jsessionid" Name="/5fa4d7fb-0487-49a8-a16f-4293771b0e6d/services/SearchService/*"/>
    <Uri AffinityCookie="JSESSIONID" AffinityURLIdentifier="jsessionid" Name="/5fa4d7fb-0487-49a8-a16f-4293771b0e6d/services/download/*"/>
    <Uri AffinityCookie="JSESSIONID" AffinityURLIdentifier="jsessionid" Name="/5fa4d7fb-0487-49a8-a16f-4293771b0e6d/services/upload/*"/>
    ```

    Here, the first block defines a data load ELP stage. The second block defines a data access on-demand server, and the third block defines the Intelligence Analysis Platform read server. The GUIDs in the `Name` attributes must match the GUIDs in the URLs to which Eclipse deployed the applications.

If the `Name` attributes do not contain the correct GUIDs, or the block for one of your servers is missing:

1.  Run `deploy.py -s write -t install-http-server-config`
2.  Check that the `plugin-cfg.xml` file now contains the correct GUIDs.
3.  Restart the HTTP server.

If the previous procedure does not resolve the problem, or the GUIDs appear to be correct:

1.  Stop all the application servers.
2.  Run the following commands on all the servers:

    ``` {.pre .codeblock}
    deploy.py -s <server> -t clear-data
    deploy.py -s <server> -t update-liberty
    ```

3.  Restart the HTTP server.
4.  Start all the application servers.

* * * * *

© Copyright IBM Corporation 2014.


