IBM i2 Analyze Developer Essentials
===================================

IBM® i2® Analyze Developer Essentials contains tools, libraries, and examples that enable development and deployment of custom extensions to i2 Analyze.

Developer Essentials includes API documentation that integrates with the Eclipse development environment. In addition, there are guides to deploying the software and the example projects.

-   [Deploying IBM i2 Analyze Developer Essentials](developer_essentials_deploying.md)

    IBM i2 Analyze Developer Essentials is a set of files and example projects that build on the standard i2 Analyze deployment toolkit. You can deploy Developer Essentials on a single workstation.

-   [Configuring the "data load direct" example project](developer_essentials_example_dld.md)

    The "data load direct" approach to data acquisition in i2 Analyze involves importing external data into the Analysis Repository. In Developer Essentials, IBM provides the `da-arload-filesystem-example` example, in which the external data source is an XML file.

-   [Configuring the "data access on-demand" example project](developer_essentials_example_daod.md)

    The "data access on-demand" approach to data acquisition in i2 Analyze involves querying external data through a new service. In Developer Essentials, IBM provides the `da-subset-filesystem-example` example, in which the external data source is an XML file.

-   [Configuring the "binary documents access" example project](developer_essentials_example_documents.md)

    The "data access on-demand" approach to data acquisition in i2 Analyze involves querying external data through a new service. In Developer Essentials, the `da-subset-documents-example` example extends the `da-subset-filesystem-example` example to include images or other binary documents in the returned data.

-   [Configuring the "user interface extension" example project](developer_essentials_example_ui.md)

    i2 Analyze supports extensions that enable users to interact with external data sources through enhancements to the Intelligence Portal. In Developer Essentials, IBM provides the `ui-subset-example` example, which includes a demonstration of how to integrate custom elements into the standard Intelligence Portal user interface.

-   [Configuring the "two-phase data retrieval" example project](developer_essentials_example_rest.md)

    i2 Analyze supports variations on the "data access on-demand" approach to data acquisition. One of the examples that IBM provides in Developer Essentials is the `da-subset-rest-example`, which demonstrates a two-phase technique for data retrieval from an external source. The example also contains code that allows it to be called and displayed by custom code in the Intelligence Portal.

-   [Configuring the "audit logging to file" example project](developer_essentials_example_audit_file.md)

    In i2 Analyze, you can use audit logging to record information about user activity in the Information Store. To do so, you must write and configure an implementation of the `IAuditLogger` interface. In Developer Essentials, IBM provides the `awc-audit-file-example` example, which writes audit logging information to a log file.

-   [Configuring the "audit logging to CSV" example project](developer_essentials_example_audit_csv.md)

    In i2 Analyze, you can use audit logging to record information about user activity in the Information Store. To do so, you must write and configure an implementation of the `IAuditLogger` interface. In Developer Essentials, IBM provides the `awc-audit-csv-example` example, which writes audit logging information to a CSV file.

-   [Configuring the "audit logging to database" example project](developer_essentials_example_audit_db.md)

    In i2 Analyze, you can use audit logging to record information about user activity in the Information Store. To do so, you must write and configure an implementation of the `IAuditLogger` interface. In Developer Essentials, IBM provides the `awc-audit-database-example` example, which writes audit logging information to a relational database.

-   [Troubleshooting the example projects](developer_essentials_troubleshooting.md)

    The example projects in i2 Analyze Developer Essentials are sensitive to changes or inconsistencies in the configuration of the Eclipse IDE. The errors that you see can be cryptic, but there are standard approaches to resolving the problems that they identify.

------------------------------------------------------------------------

© Copyright IBM Corporation 2014, 2016.


