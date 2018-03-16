Configuring the "group-based default security dimension values" example project
===============================================================================

In an i2 Analyze deployment with the Opal services, you can specify the security dimension values that records in the Information Store automatically receive when they are created by users in Analyst's Notebook Premium. By default, every record is given the security dimension values. In Developer Essentials, IBM provides the `opal-default-security-example` example, which gives records different default security dimension values based on the user groups that their creator is a member of.

Before you begin
----------------

You must have a development deployment of i2 Analyze and a configured development environment. For more information, see <a href="developer_essentials_deploying.md" class="xref" title="IBM i2 Analyze Developer Essentials is a set of files and example projects that build on a standard i2 Analyze deployment. Preparing to use Developer Essentials involves installing and configuring it to work in a dedicated test environment.">Preparing to use IBM i2 Analyze Developer Essentials</a>.

About this task
---------------

When you build the default security dimension values example, you create a JAR file that contains an implementation of the `IDefaultSecurityDimensionValuesProvider` interface. To use the new implementation, redeploy i2 Analyze to include the new JAR file, and configure the platform to use the class that the JAR file contains.

Procedure
---------

The `opal-default-security-example` project contains the source code for this example. You must add this project to your developer environment. If you are using Eclipse, complete the following instructions to import the project.

1.  In Eclipse, click **File** &gt; **Import** to open the Import window.
2.  In the Import window, click **General** &gt; **Existing Projects into Workspace**, and then click **Next**.
3.  Click **Browse** at the top of the window, and then select the `SDK\sdk-projects\opal-default-security-example` directory.
4.  Click **Finish** to complete the import process.

After you add the project to your development environment, you can inspect the behavior of the example.

The example security behavior is governed by the settings file at `opal-default-security-example\fragment\WEB-INF\classes\group-based-default-security-dimension-values.xml`, which controls the default security dimension values for each user group in the example security schema. For example, the definitions for the Analyst and Clerk user groups are:

``` pre
<GroupBasedDefaultSecurityDimensionValues>

    <GroupPermissions UserGroup="Analyst">
        <Permissions Dimension="SD-SC">
            <Permission DimensionValue="OSI"/>
            <Permission DimensionValue="HI"/>
        </Permissions>
        <Permissions Dimension="SD-SL">
            <Permission DimensionValue="CON"/>
        </Permissions>
    </GroupPermissions>

    <GroupPermissions UserGroup="Clerk">
        <Permissions Dimension="SD-SC">
            <Permission DimensionValue="OSI"/>
            </Permissions>
        <Permissions Dimension="SD-SL">
            <Permission DimensionValue="UC"/>
            </Permissions Dimension>
    </GroupPermissions>
...
    <GroupPermissions UserGroup="*">
        <Permissions Dimension="SD-SC">
            <Permission DimensionValue="OSI"/>
            </Permissions>
        <Permissions Dimension="SD-SL">
            <Permission DimensionValue="UC"/>
            </Permissions Dimension>
    </GroupPermissions>

</GroupBasedDefaultSecurityDimensions>
```

In this example, records created by users that are members of the Analyst group receive the `OSI`, `HI`, and `CON` security dimension values by default. Records created by users that are members of the Clerk group receive the `OSI` and `UC` security dimension values by default. Records created by users that are not in any mentioned group receive the `OSI` and `UC` security dimension values by default.

In this implementation, if a user is a member of more than one group, records receive each of the dimension values from unordered security dimensions, and the most restrictive dimension value from ordered security dimensions.

1.  To be able to test the implementation, you must build a JAR file of the project and export it into your i2 Analyze toolkit. If you are using Eclipse, complete the following instructions.
    1.  In Eclipse, double-click the `project.jardesc` file in the `opal-default-security-example` project to open the Export window with default settings for the project applied.
    2.  In the Export window, change the **Export destination** to `toolkit\configuration\fragments\opal-default-security-example\WEB-INF\lib\opal-default-security-example.jar`.
        If the directory does not exist, a message is displayed. If you click **Yes**, the directory structure is created for you.

    3.  Click **Finish** to export the JAR file to the specified destination.

    If you are not using Eclipse, you must build the JAR file according to your development environment.

    The built JAR file must comprise the contents of the `src/main/java` directory of the `opal-default-security-example` project. Your JAR file must be named `opal-default-security-example.jar` and be in the `toolkit\configuration\fragments\opal-default-security-example\WEB-INF\lib` directory.

2.  For your deployment to use the exported `opal-default-security-example.jar` file, you must add a fragment to the `topology.xml` file.
    1.  In an XML editor, open the `toolkit\configuration\environment\topology.xml` file.
    2.  Edit the `<war>` element that defines the contents of the `opal-services-is` web application to include the `opal-default-security-example` fragment for the default security example.
        ``` pre
        <war context-root="opal" i2-data-source-id="infostore"
             name="opal-services-is" target="opal-services-is">
           <data-sources>
              <data-source database-id="infostore" create-database="true"/>
           </data-sources>
           <file-store-ids>
              <file-store-id value="job-store"/>
              <file-store-id value="recordgroup-store"/>
           </file-store-ids>
           <fragments>
              <fragment name="opal-services-is"/>
              <fragment name="common"/>
              <fragment name="default-user-profile-provider"/>
              <fragment name="opal-default-security-example"/>
           </fragments>
           <solr-collection-ids>
              <solr-collection-id collection-id="main_index"
                                  data-dir="C:/IBM/i2analyze/data/solr"
                                  cluster-id="is_cluster"/>
           </solr-collection-ids>
        </war>
        ```

3.  Copy the contents of the `SDK\sdk-projects\opal-default-security-example\fragment` directory to the `toolkit\configuration\fragments\opal-default-security-example` directory. Overwrite any old files with the new versions.
4.  In a text editor, open the `configuration\fragments\common\WEB-INF\classes\ApolloServerSettingsMandatory.properties` file. Change the value of the `DefaultSecurityDimensionValuesProvider` property to use the new class as the `IDefaultSecurityDimensionValuesProvider` implementation in this example project.
    ``` pre
    DefaultSecurityDimensionValuesProvider=
    com.example.security.GroupBasedDefaultSecurityDimensionValuesProvider
    ```

5.  Navigate to the `toolkit\scripts` directory and run the following command to redeploy i2 Analyze with all the changes that you made:
    ``` pre
    setup -t deploy -s opal-server
    ```

6.  Run the following command to start the i2 Analyze server:
    ``` pre
    setup -t start -s opal-server
    ```

    When i2 Analyze is started, connect to the Information Store using Analyst's Notebook Premium. Log in and create items as different users, and observe the default security dimension values that the records receive.

7.  Debug the `opal-default-security-example` project by using Eclipse and your deployment of i2 Analyze. For more information about debugging the example, see <a href="https://github.com/IBM-i2/Analyze/blob/master/documentation/developer_essentials_debug.md" class="xref" title="(Opens in a new tab or window)">Debugging Developer Essentials in Eclipse</a>.

After you develop and test a default security dimension values solution for i2 Analyze, the next step is to publish it to a live deployment. Complete the following steps on your live deployment of i2 Analyze.

1.  Copy the `toolkit\configuration\fragments\opal-default-security-example` fragment directory from your development deployment to your live deployment.
2.  Update the `topology.xml` and `ApolloServerSettingsMandatory.properties` files with the same changes that you made to your development environment.
3.  Ensure that the `group-based-default-security-dimension-values.xml` file is suitable for the user groups that are in your live deployment.
4.  Redeploy the live i2 Analyze system.

Results
-------

The result of following all of these steps is that your development and production deployments of i2 Analyze contain the same implementation of the default security dimension values provider. When users create records in the Information Store, the default security dimension values that are applied depend on the user's group membership.

**Parent topic:** <a href="developer_essentials_welcome.md" class="link" title="IBM i2 Analyze Developer Essentials contains tools, libraries, and examples that enable development and deployment of custom extensions to i2 Analyze. Developer Essentials also includes API documentation and guides to deploying the software and the example projects.">IBM i2 Analyze Developer Essentials</a>

------------------------------------------------------------------------

© Copyright IBM Corporation 2014, 2017.


