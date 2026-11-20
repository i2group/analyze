# Configuring the 'audit logging to database' example project

In i2 Analyze, you can use audit logging to record information about user activity in the Information Store. To do so, you must write and configure an implementation of the `IAuditLogger` interface. In Developer Essentials, i2 provides the `opal-audit-database-example` example, which writes audit logging information to a relational database.

## Before you begin

You must have a development deployment of i2 Analyze that uses PostgreSQL or Db2 as its relational database, and a configured development environment. For more information, see [Preparing to use i2 Analyze Developer Essentials](Preparing-to-use-i2-Analyze-Developer-Essentials.md).

For its database, the example uses the same instance as i2 Analyze, and as a result it has no additional requirements.

## About this task

When you build the audit logging to database example, you create a JAR file that contains an implementation of the `IAuditLogger` interface. To enable audit functionality, you must redeploy i2 Analyze to include the new JAR file, and configure the platform to use the class that the JAR file contains.

Because this example involves writing information to a database, you must create the audit logging database itself and also add information about that database to the server configuration.

## Procedure

Before you can run the audit logging to database example, you must create and then configure the database that the example uses to record the logged information. The first part of the procedure is different depending on whether your RDBMS is PostgreSQL or IBM Db2.

### PostgreSQL

If you deployed the Information Store on PostgreSQL:

1. Open a command window as administrator, and navigate to the `SDK\java-api-projects\opal-audit-database-example\scripts\database\postgres` directory.

1. Run the following command to create the database:

   ```sh
   psql -f create-audit-database.sql
   ```

   This file contains the definition of the database that receives audit logging information from the example. The code assumes that a database user named `pguser` exists, and creates the database schema and the tables in that context.

   If your PostgreSQL database does not have an account named `pguser`, edit the SQL file accordingly. The name appears in two locations, and you must change it to the name of a user in your system. The name of the account that you use for the Information Store in `credentials.properties` is a reasonable choice.

1. In an XML editor, open the `i2analyze\deploy\wlp\usr\servers\opal-server\server.datasources.xml` file.

1. Add the following `<dataSource>` element alongside the existing one that configures the Information Store:

   ```xml
   <dataSource id="Audit" jdbcDriverRef="postgres_postgresql-*_jar" jndiName="jdbc/audit" isolationLevel="TRANSACTION_READ_COMMITTED" type="javax.sql.ConnectionPoolDataSource">
       <properties serverName="localhost" portNumber="5432" databaseName="audit" user="UserName" password="Password" ssl="false" sslrootcert=""/>
   </dataSource>
   ```

   In the `<properties>` element, edit _UserName_ and _Password_ to match the database user that you specified in the `create-audit-database.sql` file.

### IBM Db2

If you deployed the Information Store on Db2:

1. Open a Db2 command window as administrator, and navigate to the `SDK\java-api-projects\opal-audit-database-example\scripts\database\db2` directory.

1. Run the following command to create the database:

   ```sh
   db2 -stvf create-audit-database.sql
   ```

   This file contains the definition of the database that receives audit logging information from the example. The code assumes that a database user named `db2user` exists, and creates the database schema and the tables in that context.

   If your Db2 database does not have an account named `db2user`, edit the SQL file accordingly. The name appears in two locations, and you must change it to the name of a user in your system. The name of the account that you use for the Information Store in `credentials.properties` is a reasonable choice.

1. In an XML editor, open the `i2analyze\deploy\wlp\usr\servers\opal-server\server.datasources.xml` file.

1. Add the following `<dataSource>` element alongside the existing one that configures the Information Store:

   ```xml
   <dataSource id="Audit" jndiName="jdbc/audit" jdbcDriverRef="db2_db2jcc4_jar">
       <properties.db2.jcc databaseName="AUDIT"
                           serverName="localhost" portNumber="50000"
                           user="UserName" password="Password"/>
   </dataSource>
   ```

   In the `<properties.db2.jcc>` element, edit _UserName_ and _Password_ to match the database user that you specified in the `create-audit-database.sql` file.

### Common setup tasks

The remainder of this procedure does not depend on your choice of RDBMS.

The audit logging examples in Developer Essentials require the contents of the `opal-audit-example-common` project, which you must add to your development environment. The audit logging to database example also requires the `opal-audit-database-example` project, which contains the source code that is specific to this example. Import the `SDK\java-api-projects` directory into your IDE as a Gradle project, as described in [Preparing to use Developer Essentials](Preparing-to-use-i2-Analyze-Developer-Essentials.md). This adds both projects to your development environment.

After you add the projects to your development environment, you can inspect the behavior of the example.

1. To be able to test the implementation, you must build a JAR file of the project and copy it into your i2 Analyze toolkit.

   1. In the `SDK\java-api-projects` directory, run `.\gradlew :opal-audit-database-example:jar`. Gradle creates `opal-audit-database-example.jar` in the `opal-audit-database-example\build\libs` directory. The JAR file includes the code from `opal-audit-example-common`.

   1. Copy the JAR file to `C:\i2\i2analyze\toolkit\configuration\fragments\opal-audit-database-example\WEB-INF\lib\opal-audit-database-example.jar`. Create the directory structure if it does not exist.

   The built JAR file comprises the contents of the `src/main/java` directories of the `opal-audit-database-example` and `opal-audit-example-common` projects. Your JAR file must be named `opal-audit-database-example.jar` and be in the `toolkit\configuration\fragments\opal-audit-database-example\WEB-INF\lib` directory.

1. For your deployment to use the built `opal-audit-database-example.jar` file, you must add the `opal-audit-database-example` fragment to the `topology.xml` file.

   1. In an XML editor, open the `toolkit\configuration\environment\topology.xml` file.

   1. Edit the `<war>` element that defines the contents of the `opal-services-is` web application to include the `opal-audit-database-example` fragment.

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
              <fragment name="opal-audit-database-example"/>
          </fragments>
          <solr-collection-ids>
              <solr-collection-id collection-id="main_index"
                                  data-dir="C:/i2/i2analyze/data/solr"
                                  cluster-id="is_cluster"/>
          </solr-collection-ids>
      </war>
      ```

1. Copy the contents of the `SDK\java-api-projects\opal-audit-database-example\fragment` directory to the `toolkit\configuration\fragments\opal-audit-database-example` directory. Overwrite any old files with the new versions.

1. In a text editor, open the `configuration\fragments\common\WEB-INF\classes\ApolloServerSettingsMandatory.properties` file. Change the value of the `AuditLogger` property to use the new class as the `IAuditLogger` implementation in this example project.

   ```text
   AuditLogger=com.example.audit.DatabaseAuditLogger
   ```

1. Navigate to the `toolkit\scripts` directory and run the following command to redeploy i2 Analyze with all the changes that you made:

   ```sh
   setup -t deploy
   ```

1. Run the following command to start the i2 Analyze server:

   ```sh
   setup -t start
   ```

   This action starts the server with audit logging to database enabled. The logging behavior is governed by the settings file at `opal-audit-database-example\fragment\WEB-INF\classes\DatabaseAudit.properties`, which specifies the names of the database schema and the tables that store the information. Typical data looks like this:

   | Column                      | Contents |
   |-----------------------------|----------|
   | `ID`                        | `1` |
   | `USER_NAME`                 | `Analyst` |
   | `EVENT_TIME`                | `2016-10-04 17:14:53.309` |
   | `USER_SECURITY_GROUPS`      | `Analyst, Security Controller, Controlled` |
   | `USER_SECURITY_PERMISSIONS` | `[SD-SL: [CON: UPDATE, UC: UPDATE], SD-SC: [HI: UPDATE, OSI: READ_ONLY]]` |
   | `CLIENT_USER_AGENT`         | `Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.116 Safari/537.36` |
   | `CLIENT_IP_ADDRESS`         | `127.0.0.1` |
   | `EXPRESSION`                | `white` |
   | `FILTERS`                   | `[]` |
   | `DATASTORES`                | `InfoStore` |

   The supplied version of `DatabaseAudit.properties` specifies the `I2AUDIT` schema as the target for audit logging data. The tables are named `QUICK_SEARCH`, `EXPAND`, `VISUAL_QUERY` and `RECORD_RETRIEVAL`.

1. Debug the `opal-audit-database-example` project by using your IDE and your deployment of i2 Analyze. For more information about debugging the example, see [Debugging Developer Essentials](Debugging-Developer-Essentials.md).

After you develop and test an audit logging solution for i2 Analyze, the next step is to publish it to a live deployment. Complete the following steps on your live deployment of i2 Analyze.

1. Copy the `toolkit\configuration\fragments\opal-audit-database-example` fragment directory from your development deployment to your live deployment.

1. Update the `topology.xml`, `server.datasources.xml`, and `ApolloServerSettingsMandatory.properties` files with the same changes that you made to your development environment.

   > **Note:**
   > To use a different database server in a live deployment, modify the `create-audit-database.sql` script and the `server.datasources.xml` file accordingly.

1. Ensure that the settings in the `DatabaseAudit.properties` file are suitable for your live deployment.

1. Redeploy the live i2 Analyze system.

## Results

The result of following all of these steps is that your development and production deployments of i2 Analyze contain the same implementation of audit logging to database. When users interact with the Information Store, the platform writes data about those interactions to the database tables that you specified.
