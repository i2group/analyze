Adding an ELP stage to a live deployment
========================================

After you develop and test a solution for the Intelligence Analysis Platform that involves an ELP stage, the next step is to add an ELP stage to a live deployment.

Before you begin
----------------

These instructions assume that you have access to two instances of the Intelligence Analysis Platform:

-   The development version of the platform, deployed according to the instructions in IBM i2 Intelligence Analysis Platform Developer Essentials. This instance contains an ELP stage.
-   The production version of the platform, deployed according to the instructions in the IBM i2 Intelligence Analysis Platform Deployment Guide. This instance has all of its default settings.

About this task
---------------

The process for adding an ELP stage to a live deployment of the Intelligence Analysis Platform involves modifying the configuration files, and then running the deployment scripts.

Procedure
---------

1.  On the write server, open a command prompt as Administrator, and navigate to `IAP-Deployment-Toolkit\scripts`.
2.  Run the following command:

    ``` {.pre .codeblock}
    python da-setup.py -t add-delps-data-source -a read -d data-source-name
    ```

    When the command runs successfully, it modifies `topology.xml` to add information about a new ELP stage data source. In the user interface, the ELP stage gets the same name as the value that you provide to the `-d` option.

3.  Open the topology file that represents your deployment of the Intelligence Analysis Platform in an XML editor. By default, this file is at `IAP-Deployment-Toolkit\configuration\environment\topology.xml`.
4.  In the `<databases>` element, find the new `<database>` element with `database-name="delps1"`. Set the value of the `host-name` attribute to match the production deployment. Save and close the topology file.
5.  In the same directory as `topology.xml`, modify the `credentials.properties` file to add credential information for the database that hosts the new ELP stage.
6.  Stop WebSphere Application Server on the write side, and then also stop it on the read side.
7.  Run the following commands on the write-side server:

    ``` {.pre .codeblock}
    python deploy.py -s write -t provision-storage-and-messaging
    python deploy.py -s write -t start-was-server
    python deploy.py -s write -t update-application
    python deploy.py -s write -t install-http-server-config
    ```

    Note: Sometimes, the `provision-storage-and-messaging` command can fail. It displays the following error message:

    `AMQ6004: An error occurred during WebSphere MQ initialization or ending.`

    The failure occurs when the command attempts to delete and re-create the WebSphere MQ queue manager. To resolve the problem, use WebSphere MQ Explorer to delete the queue manager, and then rerun the `provision-storage-and-messaging` command.

8.  Restart the HTTP server.
9.  Copy the Deployment Toolkit to the read side.
10. Run the following commands on the read-side server:

    ``` {.pre .codeblock}
    python deploy.py -s read -t provision-storage-and-messaging
    python deploy.py -s read -t start-was-server
    python deploy.py -s read -t recreate-was-data-sources-and-jms-topics
    python deploy.py -s read -t update-application
    ```

Results
-------

After you run the final sequence of commands, the ELP stage is available to users of your Intelligence Analysis Platform deployment.

* * * * *

© Copyright IBM Corporation 2015.


