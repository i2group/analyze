# Configuring the 'dynamic security' example project

All deployments of i2 Analyze contain a [security schema file](https://docs.i2group.com/analyze/iap_security_schema_definitions.html) that defines the security dimensions for that deployment. The XML file can also define some or all of the values in those security dimensions, as well as the permissions that associate those values with the access levels that users receive.

It is also possible to _provide_ security dimension values and security permissions to the security schema programmatically. In Developer Essentials, i2 provides the `opal-dynamic-security-example` example, which demonstrates adding values to the Security Compartment dimension, add adding user-specific permissions when the i2 Analyze server requests them.

## Before you begin

Before you begin the example, you must have a development deployment of i2 Analyze and a configured development environment. For more information, see [Preparing to use i2 Analyze Developer Essentials](Preparing-to-use-i2-Analyze-Developer-Essentials.md).

## About this task

When you build the dynamic security example, you create a JAR file that contains implementations of the `ISecurityDimensionValuesProvider` and `ISecurityPermissionsProvider` interfaces. To use the implementations, you redeploy i2 Analyze to include the new JAR file, and configure the deployment with a security schema file that references the provider classes that the JAR file contains.

**`com.example.security.provider.SecurityCompartmentDimensionValuesProvider`**

A [security dimension values provider](https://docs.i2group.com/analyze/provide-security-dimension-values.html) must always provide at least one dimension value. In the example, the `SecurityCompartmentDimensionValuesProvider` class provides dimension values with the identifiers `HI` and `OSI` for the Security Compartment dimension.

When the server requests the dimension values, the provider has 60 seconds to respond before the request times out. You can change this period in the `DiscoServerSettingsCommon.properties` file, through the `SecurityDimensionValuesProviderTimeout` and `SecurityDimensionValuesProviderTimeoutUnits` properties.

> **Note:** The timeout period applies to the `ISecurityDimensionValuesProvider` interface's `getDimensionValues()` and `onStartup()` methods, but not to its `close()` method.

As well as the server's automatic schedule, you can force it to reload the security dimension values through the [`admin/securityschema/reload`](https://docs.i2group.com/analyze/public-rest-api.html#post-/api/v1/admin/securityschema/reload) endpoint of the i2 Analyze REST API.

**`com.example.security.provider.SecurityPermissionsProvider`**

In this example, the `SecurityPermissionsProvider` class provides permissions related to the values in the Security Compartment dimension, for each user of the system.

The [security permissions provider](https://docs.i2group.com/analyze/provide-security-permissions.html) receives a list of all the valid dimensions and values with each request. The permissions that it provides must contain only valid dimension values. If it provides a permission for a value that already has a permission in the security schema file, i2 Analyze uses the more permissive access level in its calculations.

When i2 Analyze requests the user's security permissions, the provider has 60 seconds to respond before the request times out. You can change this period in the `DiscoServerSettingsCommon.properties` file, through the `SecurityPermissionsProviderTimeout` and `SecurityPermissionsProviderTimeoutUnits` properties.

> **Note:** The timeout period applies to the `ISecurityPermissionsProvider` interface's `getSecurityPermissions()` and `onStartup()` methods, but not to its `close()` method.

## Procedure

The `opal-dynamic-security-example` project contains the source code for this example. You must add this project to your development environment. If you are using Eclipse, complete the following instructions to import the project.

1. In Eclipse, click **File** &gt; **Import** to open the Import window.

1. In the Import window, click **General** &gt; **Existing Projects into Workspace**, and then click **Next**.

1. Click **Browse** at the top of the window, and then select the `SDK\java-api-projects\opal-dynamic-security-example` directory.

1. Click **Finish** to complete the import process.

After you add the project to your development environment, you can inspect the behavior of the example, which is governed by the security schema file at `opal-dynamic-security-example\fragment\WEB-INF\classes\provider-based-security-schema.xml`.

The file specifies one `ProviderClass` for values in the Security Compartment dimension, and another `ProviderClass` for security permissions that contain those values:

```xml
<SecurityDimensions Id="812fb036-dd9b-4190-bf9e-fd89c01f84d6" Version="1">
  <AccessSecurityDimensions>
    <Dimension Id="SD-SC" DisplayName="Security Compartment"
               Description="Security Compartment" Ordered="false"
               ProviderClass="com.example.security.provider.SecurityCompartmentDimensionValuesProvider" />
  </AccessSecurityDimensions>
</SecurityDimensions>
<SecurityPermissions ProviderClass="com.example.security.provider.SecurityPermissionsProvider">
  <GroupPermissions UserGroup="Controlled">
    <Permissions Dimension="SD-SL">
      <Permission DimensionValue="CON" Level="UPDATE" />
      <Permission DimensionValue="UC" Level="UPDATE" />
    </Permissions>
  </GroupPermissions>
</SecurityPermissions>
```

> **Note:** The i2 Analyze server instantiates any configured providers at startup, and asks any security dimension values providers for their dimension values. If a provider causes an exception, directly or indirectly, then the server fails to start.

Next, to be able to test the implementation, you must build a JAR file of the project and export it into your i2 Analyze toolkit. If you are using Eclipse, complete the following instructions.

1. In Eclipse, double-click the `project.jardesc` file in the `opal-dynamic-security-example` project to open the Export window with default settings for the project applied.

1. In the Export window, change the **Export destination** to `C:\i2\i2analyze\toolkit\configuration\fragments\opal-dynamic-security-example\WEB-INF\lib\opal-dynamic-security-example.jar`.

   If the directory does not exist, a message is displayed. If you click **Yes**, the directory structure is created for you.

1. Click **Finish** to export the JAR file to the specified destination.

> **Note:** If you are not using Eclipse, you must build the JAR file according to your development environment. The JAR file must be named `opal-dynamic-security-example.jar`, it must be in the `toolkit\configuration\fragments\opal-dynamic-security-example\WEB-INF\lib` directory, and it must comprise the contents of the `src\main\java` directory of the `opal-dynamic-security-example` project.

For your deployment to _use_ the exported `opal-dynamic-security-example.jar` file, you must add a fragment to the `topology.xml` file.

1. In an XML editor, open the `toolkit\configuration\environment\topology.xml` file.

1. Edit the `<war>` element that defines the contents of the `opal-services-is` web application to include the `opal-dynamic-security-example` fragment for the dynamic security example.

   ```xml
   <war context-root="opal" i2-data-source-id="infostore"
        name="opal-services-is" target="opal-services-is">
     <data-sources>
       <data-source database-id="infostore" create-database="true"/>
     </data-sources>
     <file-store-ids>
       <file-store-id value="chart-store"/>
       <file-store-id value="job-store"/>
       <file-store-id value="recordgroup-store"/>
     </file-store-ids>
     <fragments>
       <fragment name="opal-services-is"/>
       <fragment name="opal-services"/>
       <fragment name="common"/>
       <fragment name="default-user-profile-provider"/>
       <fragment name="opal-dynamic-security-example"/>
     </fragments>
     <solr-collection-ids>
       <solr-collection-id collection-id="main_index"
                           data-dir="C:/i2/i2analyze/data/solr"
                           cluster-id="is_cluster"/>
     </solr-collection-ids>
   </war>
   ```

1. Copy the contents of the `SDK\java-api-projects\opal-dynamic-security-example\fragment` directory to the `toolkit\configuration\fragments\opal-dynamic-security-example` directory. Overwrite any old files with the new versions.

1. In a text editor, open the `configuration\fragments\common\WEB-INF\classes\ApolloServerSettingsMandatory.properties` file. Change the value of the `DynamicSecuritySchemaResource` property to point to the security schema file in this example.

   ```text
   DynamicSecuritySchemaResource=provider-based-security-schema.xml
   ```

1. Navigate to the `toolkit\scripts` directory and run the following command to redeploy i2 Analyze with all the changes that you made:

   ```sh
   setup -t deploy
   ```

1. Run the following command to start the i2 Analyze server:

   ```sh
   setup -t start
   ```

When i2 Analyze is started, connect to the Information Store using Analyst's Notebook. Log in and create items with Security Compartment dimension values and then, as different users, search for and then attempt to update the new records, to observe the dynamic security in action.

1. Optionally, debug the `opal-dynamic-security-example` project by using Eclipse and your deployment of i2 Analyze. For more information about debugging the example, see [Debugging Developer Essentials in Eclipse](Debugging-Developer-Essentials.md).

After you develop and test a dynamic security solution for i2 Analyze, the next step is to publish it to a live deployment. Complete the following steps on your live deployment of i2 Analyze.

1. Copy the `configuration\fragments\opal-dynamic-security-example` fragment directory from your development deployment to your live deployment.

1. Update the `topology.xml` and `ApolloServerSettingsMandatory.properties` files with the same changes that you made to your development environment.

1. Ensure that the `provider-based-security-schema.xml` file is a suitable replacement for the security schema file in your live deployment.

1. Redeploy the live i2 Analyze system.

## Results

The result of following all of these steps is that your development and production deployments of i2 Analyze contain the same implementations of the dynamic security providers. When users create, retrieve, and attempt to update records in the Information Store, the access levels change on a per-user basis.
