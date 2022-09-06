# Setting up a development environment

The example project in this part of i2 Analyze Developer Essentials uses Java client code to call the methods of the i2 Analyze REST API.

To explore the example project, you need a development environment that includes a deployment of i2 Analyze and a compatible version of Java. The following instructions explain how to acquire both.

## Deploying i2 Analyze

To make full use of the i2 Analyze REST API, you need a deployment of i2 Analyze that includes the Information Store. The code in the example project assumes that you deployed i2 Analyze and ingested example data as described below.

**Note:** You can use a different deployment, but you might need to change some parts of the example code if you do. You might also get different results from the requests that you make.

1. Deploy i2 Analyze by following the [i2 Analyze Containers documentation](https://i2group.github.io/analyze-containers/content/deploy_config_dev.html), which explains how to create an i2 Analyze server instance that runs in a Docker container.

   **Important:** In [Creating a config](https://i2group.github.io/analyze-containers/content/deploy_config_dev.html#creating-a-config), choose a deployment pattern that includes the Information Store as part of the i2 Analyze deployment, such as `i2c_istore`. In [Specifying the schema and charting schemes files](https://i2group.github.io/analyze-containers/content/deploy_config_dev.html#specifying-the-schema-and-charting-schemes-files), choose the Law Enforcement schema.

1. Ingest the sample data as explained in the [Example ingestion process](https://i2group.github.io/analyze-containers/content/ingest_config_dev.html#example-ingestion-process), so that you can retrieve this data from the example code.   

1. Edit the `command-access-control.xml` file to give the `i2:AlertsCreate` permission to members of the Administrator group, so that they can run the example code:

   ```xml
   <CommandAccessPermissions UserGroup="Administrator">
     <Permission Value="i2:Administrator" />
     <Permission Value="i2:AlertsCreate" />
   </CommandAccessPermissions>
   ```

   After you edit this file, you must redeploy i2 Analyze, as described in [Deploying your config in the environment](https://i2group.github.io/analyze-containers/content/deploy_config_dev.html#deploying-your-config-in-the-environment).

## Installing Java

The development environment requires that Java 8 (or higher) is installed and on your system path. You can install a suitable version of Java from [https://adoptium.net](https://adoptium.net).

To check that Java is installed correctly, run ```java -version``` from the command line. The output should look similar to this:

```
$ java -version
openjdk version "11.0.15" 2022-04-19
OpenJDK Runtime Environment (build 11.0.15+10-Ubuntu-0ubuntu0.22.04.1)
OpenJDK 64-Bit Server VM (build 11.0.15+10-Ubuntu-0ubuntu0.22.04.1, mixed mode, sharing)
```

**Note:** The exact version is not important as long as the major version is 8 or higher.

With Java installed, you can now proceed with the [worked example](generating-client-code.md).
