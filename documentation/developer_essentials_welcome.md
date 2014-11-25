IBM i2 Intelligence Analysis Platform Developer Essentials
==========================================================

IBM i2 Intelligence Analysis Platform Developer Essentials contains tools, libraries, and examples that enable development and deployment of custom extensions to the Intelligence Analysis Platform.

Developer Essentials includes API documentation that integrates with the Eclipse development environment. In addition, there are guides to deploying the software and the example projects.

-   [Deploying IBM i2 Intelligence Analysis Platform Developer Essentials](developer_essentials_deploying.md)

     IBM i2 Intelligence Analysis Platform Developer Essentials is a set of files and example projects that build on the standard Deployment Toolkit. You can deploy Developer Essentials on a single workstation.
-   [Configuring the "data load direct" example project](developer_essentials_example_dld.md)

     The "data load direct" approach to data acquisition in the Intelligence Analysis Platform involves importing external data into the Analysis Repository. In Developer Essentials, IBM provides the `da-arload-filesystem-example` example, in which the external data source is an XML file.
-   [Configuring the "data load ELP stage" example project](developer_essentials_example_delps.md)

     The "data load ELP stage" approach to data acquisition in the Intelligence Analysis Platform involves importing external data into an additional, semi-permanent data store. In Developer Essentials, IBM provides the `da-elpsload-filesystem-example` example, in which the external data source is an XML file.
-   [Adding an ELP stage to a live deployment](developer_essentials_deploying_delps.md)

     After you develop and test a solution for the Intelligence Analysis Platform that involves an ELP stage, the next step is to add an ELP stage to a live deployment.
-   [Configuring the "data access on-demand" example project](developer_essentials_example_daod.md)

     The "data access on-demand" approach to data acquisition in the Intelligence Analysis Platform involves querying external data through a new service on the read side of the platform. In Developer Essentials, IBM provides the `da-subset-filesystem-example` example, in which the external data source is an XML file.
-   [Publishing a data access on-demand solution to a live deployment](developer_essentials_going_live.md)

     After you develop and test a data access on-demand solution for the Intelligence Analysis Platform, the next step is to publish it to a live deployment.
-   [Configuring the "user interface extension" example project](developer_essentials_example_ui.md)

     The Intelligence Analysis Platform supports extensions that enable users to interact with external data sources through enhancements to the Intelligence Portal. In Developer Essentials, IBM provides the `ui-subset-example` example, which includes a demonstration of how to integrate custom elements into the standard portal user interface.
-   [Configuring the "two-phase data retrieval" example project](developer_essentials_example_rest.md)

     The Intelligence Analysis Platform supports variations on the "data access on-demand" approach to data acquisition. One of the examples that IBM provides in Developer Essentials is the `da-subset-rest-example`, which demonstrates a two-phase technique for data retrieval from an external source. The example also contains code that allows it to be called and displayed by custom code in the Intelligence Portal.
-   [Publishing a solution with UI and REST components to a live deployment](developer_essentials_deploying_uirest.md)

     When the user interface extension and two-phase data retrieval examples are running in a development environment, the next step is to publish them to a live deployment of the platform.
-   [Troubleshooting the example projects](developer_essentials_troubleshooting.md)

     The example projects in Intelligence Analysis Platform Developer Essentials are sensitive to changes or inconsistencies in the configuration of the Eclipse IDE. The errors that you see can be cryptic, but there are standard approaches to resolving the problems that they identify.

* * * * *

© Copyright IBM Corporation 2014.


