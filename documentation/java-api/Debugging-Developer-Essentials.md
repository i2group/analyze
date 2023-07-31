# Debugging Developer Essentials

After you deploy your code to your development deployment of i2 Analyze, you can debug your code. To debug your code, you must configure your IDE to connect to your development deployment.

## About this task

First, configure i2 Analyze for debugging. Then debug your project from your IDE. Instructions are provided for debugging in Eclipse on your development deployment of i2 Analyze.

**Note:** Do not enable i2 Analyze for debugging on a production system.

## Procedure

1. Configure i2 Analyze for debugging.

   1. By default, port number 7777 is used for debugging with i2 Analyze. To use a different port, open `toolkit\configuration\environment\<server>\environment-advanced.properties` and change the value of the `was.debug.port` property.

   1. Navigate to the `toolkit\scripts` directory, and run the following command to enable debugging on the specified port:

      ```sh
      setup -t enableDebug
      ```

      The following line is added to the `wlp\usr\servers\<server>\jvm.options` file:

      ```sh
      -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=<host name>:<port number>
      ```

      **Note:** To disable debugging, remove this line from the file.

   1. Run the following command to start i2 Analyze:

      ```sh
      setup -t start
      ```

1. In Eclipse, select the project to debug and leave it highlighted.

   For example, if you are configuring the audit logging to CSV example, select the `opal-audit-csv-example` project.

1. Click **Run** &gt; **Debug Configurations** to open the Debug Configurations window.

1. Right-click **Remote Java Application** and click **New** to create a new debug configuration with the same name as your project.

1. Change the **Port** to match the value that is specified in the `environment-advanced.properties` file. By default, port number 7777 is used.

1. Click **Apply**.

1. Click **Debug**.

   Eclipse connects to the Liberty server that is running i2 Analyze, and you can debug the project in the usual manner.
