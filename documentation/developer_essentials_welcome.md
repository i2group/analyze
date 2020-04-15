IBM i2 Analyze Developer Essentials
===================================

IBM® i2® Analyze Developer Essentials contains tools, libraries, and examples that enable development and deployment of custom extensions to i2 Analyze. Developer Essentials also includes API documentation and guides to deploying the software and the example projects.

Some of the example projects extend Opal deployments of i2 Analyze, and some extend Onyx deployments. The names of the example projects indicate which deployment type they apply to.

The Developer Essentials examples are supplied in the form of Eclipse projects. You can use other editors to interact with the examples, but the instructions here assume that you are using Eclipse Neon (Eclipse IDE for Java™ EE Developers).

Most of the examples culminate in building a JAR file and copying it to the `toolkit` directory of an i2 Analyze deployment. After you modify the configuration, you redeploy i2 Analyze to add the example to the system. Other examples run separately from i2 Analyze as clients.

Important: When you develop a custom extension, use it in a test deployment of i2 Analyze first. After you are satisfied that the extension is complete, you can move it from the test deployment to your production environment.

-   **[Preparing to use IBM i2 Analyze Developer Essentials](developer_essentials_deploying.md)**
    IBM i2 Analyze Developer Essentials is a set of files and example projects that build on a standard i2 Analyze deployment. Preparing to use Developer Essentials involves installing and configuring it to work in a dedicated test environment.
-   **[Configuring the "group-based default security dimension values" example project](developer_essentials_example_default_security.md)**
    In an i2 Analyze deployment with the Opal services, you can specify the security dimension values that records in the Information Store automatically receive when they are created by users in Analyst's Notebook Premium. By default, every record is given the security dimension values. In Developer Essentials, IBM provides the `opal-default-security-example` example, which gives records different default security dimension values based on the user groups that their creator is a member of.
-   **[Configuring the "audit logging to file" example project](developer_essentials_example_audit_file.md)**
    In i2 Analyze, you can use audit logging to record information about user activity in the Information Store. To do so, you must write and configure an implementation of the `IAuditLogger` interface. In Developer Essentials, IBM provides the `opal-audit-file-example` example, which writes audit logging information to a log file.
-   **[Configuring the "audit logging to CSV" example project](developer_essentials_example_audit_csv.md)**
    In i2 Analyze, you can use audit logging to record information about user activity in the Information Store. To do so, you must write and configure an implementation of the `IAuditLogger` interface. In Developer Essentials, IBM provides the `opal-audit-csv-example` example, which writes audit logging information to a CSV file.
-   **[Configuring the "audit logging to database" example project](developer_essentials_example_audit_db.md)**
    In i2 Analyze, you can use audit logging to record information about user activity in the Information Store. To do so, you must write and configure an implementation of the `IAuditLogger` interface. In Developer Essentials, IBM provides the `opal-audit-database-example` example, which writes audit logging information to a relational database.
-   **[Configuring the "data load direct" example project](developer_essentials_example_dld.md)**
    The "data load direct" approach to data acquisition in an i2 Analyze deployment with the Onyx services involves importing external data into the Analysis Repository. In Developer Essentials, IBM provides the `onyx-da-arload-filesystem-example` example, in which the external data source is an XML file.
-   **[Configuring the "data access on-demand" example project](developer_essentials_example_daod.md)**
    The "data access on-demand" approach to data acquisition in an i2 Analyze deployment with the Onyx services involves querying external data through a new service. In Developer Essentials, IBM provides the `onyx-da-subset-filesystem-example` example, in which the external data source is an XML file.
-   **[Configuring the "binary documents access" example project](developer_essentials_example_documents.md)**
    The "data access on-demand" approach to data acquisition in an i2 Analyze deployment with the Onyx services involves querying external data through a new service. In Developer Essentials, the `onyx-da-subset-documents-example` example extends the `onyx-da-subset-filesystem-example` example to include images or other binary documents in the returned data.
-   **[Configuring the "user interface extension" example project](developer_essentials_example_ui.md)**
    i2 Analyze supports extensions that enable users to interact with external data sources through enhancements to the Intelligence Portal. In Developer Essentials, IBM provides the `onyx-ui-subset-example` example, which includes a demonstration of how to integrate custom elements into the standard Intelligence Portal user interface.
-   **[Configuring the "two-phase data retrieval" example project](developer_essentials_example_rest.md)**
    i2 Analyze supports variations on the "data access on-demand" approach to data acquisition. One of the examples that IBM provides in Developer Essentials is the `onyx-da-subset-rest-example`, which demonstrates a two-phase technique for data retrieval from an external source. The example also contains code that allows it to be called and displayed by custom code in the Intelligence Portal.
-   **[Debugging Developer Essentials](developer_essentials_debug.md)**
    After you deploy your code to your development deployment of i2 Analyze, you can debug your code. To debug your code, you must configure your IDE to connect to your development deployment.
-   **[Troubleshooting the example projects](developer_essentials_troubleshooting.md)**
    The example projects in i2 Analyze Developer Essentials are sensitive to changes or inconsistencies in the configuration of the development environment. The errors that you see can be cryptic, but there are standard approaches to resolving the problems that they identify.

------------------------------------------------------------------------

© Copyright IBM Corporation 2014, 2020.


