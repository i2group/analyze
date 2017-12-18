Troubleshooting the example projects
====================================

The example projects in i2 Analyze Developer Essentials are sensitive to changes or inconsistencies in the configuration of the Eclipse IDE. The errors that you see can be cryptic, but there are standard approaches to resolving the problems that they identify.

-   <a href="#eclipse-reports-compilation-errors-after-it-imports-an-example-project" class="xref">Eclipse reports compilation errors after it imports an example project</a>
-   <a href="#server-starts-without-error-but-in-only-a-few-seconds" class="xref">Server starts without error but in only a few seconds</a>
-   <a href="#navigation-to-httplocalhostapollo-reports-http-error-404" class="xref">Navigation to http://localhost/apollo reports HTTP error 404</a>
-   <a href="#eclipse-reports-a-javalangnullpointerexception-on-clean-or-publish" class="xref">Eclipse reports a java.lang.NullpointerException on Clean or Publish</a>
-   <a href="#application-fails-to-start-and-reports-an-error-to-the-console" class="xref">Application fails to start and reports an error to the console</a>
-   <a href="#navigation-to-httplocalhostapollo-reports-an-internal-server-error" class="xref">Navigation to http://localhost/apollo reports an Internal Server Error</a>
-   <a href="#user-login-fails-and-generates-a-console-error" class="xref">User login fails and generates a console error</a>
-   <a href="#user-login-fails-with-no-obvious-console-error" class="xref">User login fails with no obvious console error</a>
-   <a href="#browse-and-search-operations-generate-server-not-found-errors" class="xref">Browse and search operations generate Server Not Found errors</a>

Eclipse reports compilation errors after it imports an example project
----------------------------------------------------------------------

For an unmodified example project, compilation errors usually occur in Eclipse when the `TOOLKIT_ROOT` variable is not set, or when it is set incorrectly.

To determine whether there is a problem with `TOOLKIT_ROOT`, expand one of the example project directories. Some of the directory icons inside the project have glyphs:

-   An arrow on the lower-right corner of a directory icon indicates a link to an external directory. If you see the arrow, then the variable is set correctly.
-   An exclamation point on the lower-right corner of a directory icon indicates a broken link. You are not able to expand an icon that is in this state.

If you have a project with broken links, there are two possibilities:

1.  If only some of the links are broken, then your `TOOLKIT_ROOT` variable is set correctly, but Developer Essentials was not extracted into the same location as the Deployment Toolkit.
2.  If all of the links are broken, then your `TOOLKIT_ROOT` variable is set incorrectly. Ensure that the variable is set to the path of Deployment Toolkit directory.

Server starts without error but in only a few seconds
-----------------------------------------------------

If the application server starts in an unusually short time, it is likely that an i2 Analyze application is not deployed correctly.

1.  In Eclipse, inside the **Servers** tab, double-click the **onyx-server**. A new **onyx-server** tab opens in the top part of the Eclipse application window.
2.  In the new tab, ensure that the **Run applications directly from the workspace** check box is cleared, and then press Ctrl+S to save the settings.

Important: This setting can cause various other problems. For i2 Analyze to behave correctly in the Eclipse IDE, you must keep these check boxes clear.

Navigation to http://localhost/apollo reports HTTP error 404
------------------------------------------------------------

HTTP error 404 can occur when one of the i2 Analyze applications is not correctly deployed, or when the HTTP server is misconfigured.

-   If the error is "Context Root Not Found", with obvious "IBM" and "IBM WebSphere Application Server" branding, then the HTTP server redirect is working and the problem is with the application.

    To resolve the problem, follow the previous procedure for clearing the **Run applications directly from the workspace** check box.

-   If the error is a plain web page with a generic "Not Found" message, then the problem is with the HTTP server.

    To resolve the problem, see the detailed procedure for checking the HTTP server configuration at the end of the final section.

Eclipse reports a java.lang.NullpointerException on Clean or Publish
--------------------------------------------------------------------

Occasionally, the representation of the project files in Eclipse can lose synchronization with the file system. When this problem happens, cleaning or publishing a project can generate an exception. To fix the project, repeat the failed operation.

Application fails to start and reports an error to the console
--------------------------------------------------------------

There are several reasons why an i2 Analyze application can fail to start. You can use the error message to diagnose the problem and choose a solution.

> `java.lang.NoClassDefFoundError: com.i2group.tracing.TracingManager`

This error implies that the application cannot access the shared Java libraries. Sometimes, the problem is that you ran `build` multiple times for the same Data Acquisition endpoint. In this situation:

1.  In a text editor, open the `topology.xml` file.
2.  If the file contains multiple entries for the same Data Acquisition endpoint:
    1.  Remove the duplicate entries, leaving only the original entry in place.
    2.  Run `build` again for your i2 Analyze deployment.

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
2.  Use `build` to re-create `server.xml` from `topology.xml`:

    ``` pre
    build -t deploy
    ```

3.  Refresh all the projects and all the servers in the Eclipse IDE.

Navigation to http://localhost/apollo reports an Internal Server Error
----------------------------------------------------------------------

If the HTTP server reports an internal server error, then the **i2analyze** server did not start. Start the server and try the operation again.

User login fails and generates a console error
----------------------------------------------

If users cannot log in to your deployment of i2 Analyze, a missing user repository is often the cause. The platform generates the following error:

> `WebTrustAssociationFailedException: No UserRegistry found to authenticate with`

To resolve this problem, ensure that you configured example users according to the Developer Essentials deployment instructions.

User login fails with no obvious console error
----------------------------------------------

If users cannot log in to your deployment of i2 Analyze, and all they see is a "failed login" message, then there are several possible causes.

First, make sure that users are providing credentials accurately. In i2 Analyze, user names and passwords are all case-sensitive.

Second, it is possible that event replay is still in progress. In the console, check for a `Replay complete` message:
``` pre
2014-03-19 17:31:50,100 - Replayed 3 of approximately 3 events (100%).
2014-03-19 17:31:50,101 - Waiting for read services to acknowledge remaining events
                          (expecting progress within 60 seconds)
2014-03-19 17:31:50,103 - Event queue not empty (1 events in queue),
                          waiting until all are received.
2014-03-19 17:31:50,355 - Event queue not empty (4 events in queue),
                          waiting until all are received.
2014-03-19 17:32:29,971 - Read services should now be up to date.
2014-03-19 17:32:30,039 - Updated EventBusConfigurationJPA.JMS_QUEUE_CAPACITY=2100
                          (was 2100)
2014-03-19 17:32:30,041 - Replay complete.
```

To understand any other problem, you must look in the log. Refresh Eclipse, and examine the log file at `servers/server-name`/apps/logs/<span class="keyword parmname">war-name</span>/IBM\_i2\_Analysis\_Repository.log.

Note: i2 Analyze error messages contain contributions from several parts of the system. Usually, the first and last sections of a message are the most useful.
-   If the top-level error contains "`SystemResourceRuntimeException`", check the message for an explanation of the problem.
-   Check the bottom-level exception, which contains most detail about the cause of the problem.

Browse and search operations generate Server Not Found errors
-------------------------------------------------------------

Often, you can resolve a "Server Not Found" error by restarting the HTTP server. The procedure takes only a few seconds, so it makes sense to try this approach first.

If a restart does not fix the problem, "Server Not Found" errors have three possible causes. Either one of the i2 Analyze applications failed, or it is not correctly deployed, or the HTTP server is misconfigured.

To determine whether an application failed, check the console log for the server, and the application log for the application that you were trying to use. If there is an exception, you must resolve the problem that caused it.

To determine whether there is a problem with HTTP server configuration or application deployment, attempt to view the application directly in your web browser. For example, navigate to http://localhost:9082/<span class="keyword parmname">datasource-guid</span>. A correct deployment displays a basic HTML page.

Note: You can obtain <span class="keyword parmname">datasource-guid</span> by opening `server.xml`. In the `<webApplication>` element, the `war-name` attribute is human-readable, while the `context-root` attribute contains the GUID that you need.

If you see HTTP error 404 instead of a basic HTML page, then the application is not deployed. In most cases, you can resolve the problem by following the procedure for clearing the **Run applications directly from the workspace** check box. However, you might have to intervene more forcefully:

1.  In Eclipse, refresh the project that represents WebSphere Application Server Liberty Profile.
2.  In that project, expand the `servers/i2analyze/apps` directory.

    If the directory contains a `project-name`.war.xml file instead of a `project-name`.war subdirectory, then the application is not deployed.

3.  In the **Servers** tab, double-click the **i2analyze** server to open the **i2analyze** tab in the top part of the Eclipse application window.
4.  Make certain of the **Run applications directly from the workspace** setting:
    1.  If the check box is cleared, select it, and then save the settings.
    2.  Clear the check box, and then save the settings again.
    3.  Clean the server. The directory, rather than the file, is added to the project.

If you see a basic HTML page, then the application is deployed but you have an HTTP server problem. When a simple restart does not work, you must check the server configuration:

1.  In a text editor, open the file at `http-server-dir`/Plugins/i2analyze/config/plugin-cfg.xml.
2.  Find the `<UriGroup Name="core_URIs">` element. Depending on the topology of your deployment, the contents look like this example:

    ``` pre
    <Uri AffinityCookie="JSESSIONID" AffinityURLIdentifier="jsessionid"
         Name="/d1f28093-143d-4ebe-a9d4-e1202f755e6e/services/ExternalDataSubsetCreationService/*"/>
    <Uri AffinityCookie="JSESSIONID" AffinityURLIdentifier="jsessionid"
         Name="/d1f28093-143d-4ebe-a9d4-e1202f755e6e/services/ExternalDataSubsetExplorationService/*"/>
    <Uri AffinityCookie="JSESSIONID" AffinityURLIdentifier="jsessionid"
         Name="/d1f28093-143d-4ebe-a9d4-e1202f755e6e/services/ExternalDataRetrievalService/*"/>

    <Uri AffinityCookie="JSESSIONID" AffinityURLIdentifier="jsessionid"
         Name="/5fa4d7fb-0487-49a8-a16f-4293771b0e6d/services/AlertFeedRetrievalService/*"/>
    <Uri AffinityCookie="JSESSIONID" AffinityURLIdentifier="jsessionid"
         Name="/5fa4d7fb-0487-49a8-a16f-4293771b0e6d/services/AlertRetrievalService/*"/>
    <Uri AffinityCookie="JSESSIONID" AffinityURLIdentifier="jsessionid"
         Name="/5fa4d7fb-0487-49a8-a16f-4293771b0e6d/services/AuditService/*"/>
    <Uri AffinityCookie="JSESSIONID" AffinityURLIdentifier="jsessionid"
         Name="/5fa4d7fb-0487-49a8-a16f-4293771b0e6d/services/IndexAdminService/*"/>
    <Uri AffinityCookie="JSESSIONID" AffinityURLIdentifier="jsessionid"
         Name="/5fa4d7fb-0487-49a8-a16f-4293771b0e6d/services/ItemHistoryService/*"/>
    <Uri AffinityCookie="JSESSIONID" AffinityURLIdentifier="jsessionid"
         Name="/5fa4d7fb-0487-49a8-a16f-4293771b0e6d/services/ItemRetrievalService/*"/>
    <Uri AffinityCookie="JSESSIONID" AffinityURLIdentifier="jsessionid"
         Name="/5fa4d7fb-0487-49a8-a16f-4293771b0e6d/services/NetworkSearchService/*"/>
    <Uri AffinityCookie="JSESSIONID" AffinityURLIdentifier="jsessionid"
         Name="/5fa4d7fb-0487-49a8-a16f-4293771b0e6d/services/SearchService/*"/>
    <Uri AffinityCookie="JSESSIONID" AffinityURLIdentifier="jsessionid"
         Name="/5fa4d7fb-0487-49a8-a16f-4293771b0e6d/services/download/*"/>
    <Uri AffinityCookie="JSESSIONID" AffinityURLIdentifier="jsessionid"
         Name="/5fa4d7fb-0487-49a8-a16f-4293771b0e6d/services/upload/*"/>
    ```

    Here, the first block defines a data access on-demand server, and the second block defines the i2 Analyze server. The GUIDs in the `Name` attributes must match the GUIDs in the URLs to which Eclipse deployed the applications.

If the `Name` attributes do not contain the correct GUIDs, or the block for one of your servers is missing:

1.  Run `build -t configureHTTPServer`
2.  Check that the `plugin-cfg.xml` file now contains the correct GUIDs.
3.  Restart the HTTP server.

If the previous procedure does not resolve the problem, or the GUIDs appear to be correct:

1.  Stop all the application servers.
2.  Run the following commands on all the servers:

    ``` pre
    build -t clearData
    build -t deploy
    ```

3.  Restart the HTTP server.
4.  Start all the application servers.

**Parent topic:** <a href="developer_essentials_welcome.md" class="link" title="IBM i2 Analyze Developer Essentials contains tools, libraries, and examples that enable development and deployment of custom extensions to i2 Analyze.">IBM i2 Analyze Developer Essentials</a>

------------------------------------------------------------------------

© Copyright IBM Corporation 2014, 2017.


