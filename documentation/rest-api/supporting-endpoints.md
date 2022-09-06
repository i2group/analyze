# Using other endpoints

The example that we've built so far can search and retrieve records from the Information Store without knowing much about the configuration of the server or the structure of the data.
However, the i2 Analyze REST API also contains some endpoints from which you can retrieve information to improve the user experience of your client code.

In this final part of the walkthrough, we'll take a quick look at some of these endpoints, and then use some of them to enhance the example application.

## Information Store schema

Until now, in all references to item types and property types in the code or the output, we've used their identifers: `ET5` and `PER93` and so on.
The schema endpoint at `infostore/schema` allows you to get information about the types with those identifiers, including their names and descriptions.

As the [API documentation](https://docs.i2group.com/analyze/public-rest-api.html#get-/api/v1/infostore/schema) describes, the GET method on the schema endpoint retrieves the deployed i2 Analyze schema in JSON format.

For more information about i2 Analyze schemas, see the [data model documentation](https://docs.i2group.com/analyze/i2_analyze_schemas.html) and the [Schema Designer user help](https://docs.i2group.com/analyze/schemas.html).

### Using the schema in the client

As an example of using the schema to enhance the client experience, we can update the client to fetch the schema and look up the display names of the item and property types that appear in pages of records from the server.

1. As a reminder, run the `ExampleApp.main()` method and look at any of the records in the output:

   ```
        ET1 [Entity]
                Record Identifier: HuyEBJZ6tAenrUE79goLSPYjMi
                Label: 6329, Dartmouth Drive
                Matched Query: Yes
                -------------
                ADD3 - 6329
                ADD5 - Dartmouth Drive
                ADD6 - Bethesda
                ADD17 - MD
                ADD26 - View on a map: https://www.openstreetmap.org/?mlon=-77.119977&mlat=38.98978&zoom=15
   ```

   Unless you know what `ET1` and `ADD3` mean, this information is not easy to read.
   We can start to improve that by using the schema to create a lookup table that associates the type identifiers with the types themselves.

1. Add these fields above the constructor in `ExampleApp` to create maps that hold the entity types, link types, and property types:

   ```java
       private Map<String, SchemaEntityType> entityTypes;
       private Map<String, SchemaLinkType> linkTypes;
       private Map<String, SchemaPropertyType> propertyTypes;
   ```

1. Add a method that fetches the schema and populates the maps:

   ```java
   private void fetchSchema() {
       final Schema schema = executeRequest(() -> new InformationStoreApi(apiClient).getInfoStoreSchema());
       entityTypes = schema.getItemTypes().getEntityTypes().stream().collect(Collectors.toMap(SchemaEntityType::getId, Function.identity()));
       linkTypes = schema.getItemTypes().getLinkTypes().stream().collect(Collectors.toMap(SchemaLinkType::getId, Function.identity()));
       propertyTypes = new HashMap<>();

       Collector<SchemaPropertyType, ?, Map<String, SchemaPropertyType>> collectProperties = Collectors.toMap(SchemaPropertyType::getId, Function.identity());
       propertyTypes.putAll(entityTypes.values().stream().flatMap(type -> Stream.concat(type.getPropertyGroupTypes().stream().map(SchemaPropertyGroupType::getPropertyTypes).flatMap(Collection::stream), type.getPropertyTypes().stream())).collect(collectProperties));
       propertyTypes.putAll(linkTypes.values().stream().flatMap(type -> Stream.concat(type.getPropertyGroupTypes().stream().map(SchemaPropertyGroupType::getPropertyTypes).flatMap(Collection::stream), type.getPropertyTypes().stream())).collect(collectProperties));
   }
   ```

   Notice that this code gathers property types from within item types and from within property _group_ types.
   The example schema contains both top-level property types, and property types that are nested inside groups.

1. Call `fetchSchema()` from the `ExampleApp` constructor, after the API version check:

   ```java
   public ExampleApp(ApiClient apiClient) {
       this.apiClient = apiClient;
       checkApiVersionIsSupported();
       fetchSchema();
   }
   ```

1. Now you can use the maps to look up type information from the schema.
   In the `processRecord()` method, replace `final String type = record.getItemTypeId() + (isLink ?  " [Link]" : " [Entity]");` with the following:

   ```java
   final String type = (isLink ? linkTypes.get(record.getItemTypeId()).getDisplayName() + " [Link]" : entityTypes.get(record.getItemTypeId()).getDisplayName() + " [Entity]");
   ```

1. In the `processProperty()` method, replace `System.out.println("\t\t" + property.getTypeId() + " - " + value);` with this:

   ```java
   System.out.println("\t\t" + propertyTypes.get(property.getTypeId()).getDisplayName() + " - " + value);
   ```

1. Execute the `main()` method again.
   The output now includes the display names of the item and property types, making it more useful than what we had before:

   ```
           Address [Entity]
                   Record Identifier: HuyEBJZ6tAenrUE79goLSPYjMi
                   Label: 6329, Dartmouth Drive
                   Matched Query: Yes
                   -------------
                   Building Number - 6329
                   Street Name - Dartmouth Drive
                   Town/City - Bethesda
                   State - MD
                   Geographic Location - View on a map: https://www.openstreetmap.org/?mlon=-77.119977&mlat=38.98978&zoom=15
   ```

The example code here demonstrates one way in which you can use one of these endpoints to improve a client application that uses the REST API.
In the remainder of the topic, we'll take a quick look at some similar endpoints, and suggest some ways in which you might use them too.

## Security schema

Because it only _retrieves_ data from the Information Store, which already enforces security on the records that it returns, the example application has not considered the security dimension values that records contain.

If you need to provide users with more security information, you can retrieve the security schema in JSON format from the endpoint at `core/securityschema` and use it to look up the names and descriptions of dimensions and their values.

You can read more about the endpoint in the [REST API documentation](https://docs.i2group.com/analyze/public-rest-api.html#get-/api/v1/core/securityschema), and more about the security schema in the [security model documentation](https://docs.i2group.com/analyze/security_model.html).

## Time zones

The time zones endpoint at `core/temporal/timezones` retrieves a JSON array containing the time zones that the i2 Analyze server supports.
This information can be useful in making sense of the date and time values that the server returns in record property data, for example.

You can also provide the identifier of a time zone to the server to change the time zone that it uses when it returns record metadata.

For more detailed information about the time zones endpoint, see the [REST API documentation](https://docs.i2group.com/analyze/public-rest-api.html#get-/api/v1/core/temporal/timezones).

## Users

When you wrote the code to [log in to the i2 Analyze server](logging-in.md), you retrieved user information from the users endpoint at `users/{userId}`.
That information includes the [command access control permissions](https://docs.i2group.com/analyze/controlling_access.html) that determine which i2 Analyze features the user has access to.

If a user does not have access to all features, then some REST API endpoints might have restricted functionality, or might not work at all.
For example:

- The `infostore/search/text` endpoint can search within the notes that records contain, but if a user who doesn't have the `i2:Notes` permission attempts to do so, a "permission denied" response is the result.

- All `admin` endpoints require the user to have the `i2:Administrator` permission.

- The `alerts` endpoint requires the user to have the `i2:AlertsCreate` permission.

For more detailed information about the users endpoint, see the [REST API documentation](https://docs.i2group.com/analyze/public-rest-api.html#get-/api/v1/users/-userId-).
