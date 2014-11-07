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

The process for adding an ELP stage to a live deployment of the Intelligence Analysis Platform involves editing the configuration files, and then running the deployment scripts.

Procedure
---------

1.  Copy all the configuration elements for the ELP stage from `topology.xml` in the development version of the Deployment Toolkit to the same file in the production version.

    Note: Remember always to start by modifying the Deployment Toolkit on the write-side server.

2.  In the topology file, modify the database settings to match the production deployment.
3.  In the same directory as `topology.xml`, modify the `credentials.properties` file to add credential information for the database that hosts the new ELP stage.
4.  Stop WebSphere Application Server on the write side, and then also stop it on the read side.
5.  Run the following commands on the write-side server:

    ``` {.pre .codeblock}
    python deploy.py -s write -t provision-storage-and-messaging
    python deploy.py -s write -t start-was-server
    python deploy.py -s write -t update-application
    python deploy.py -s write -t install-http-server-config
    ```

    Note: Sometimes, the `provision-storage-and-messaging` command can fail. It displays the following error message:

    `AMQ6004: An error occurred during WebSphere MQ initialization or ending.`

    The failure occurs when the command attempts to delete and re-create the WebSphere MQ queue manager. To resolve the problem, use WebSphere MQ Explorer to delete the queue manager, and then rerun the `provision-storage-and-messaging` command.

6.  Restart the HTTP server.
7.  Copy the Deployment Toolkit to the read side.
8.  Run the following commands on the read-side server:

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

© Copyright IBM Corporation 2014.


