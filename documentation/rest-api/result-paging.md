# Paging search results

When you added code to the client application to [search for records by their contents](search-text.md), we said that we'd return later to interact with the record groups that the text search endpoint returns.
In this part of the walkthrough, we'll do exactly that.

From client code, you can call the endpoint at `recordgroups/{recordGroupId}/pagedrecords` to access the contents of record groups.
The endpoint provides access to the contents of the record group with a cursor-based paging API, allowing a client to process a manageable volume of data on each request, rather than receiving everything in a single response.

For more detailed information about the paged record groups endpoint, see the [REST API documentation](https://docs.i2group.com/analyze/public-rest-api.html#get-/api/v1/recordgroups/-recordGroupId-/pagedrecords).
At this release of the i2 Analyze REST API, it is an [_experimental endpoint_](api-support-policy.md#support-for-experimental-endpoints), which means that it is not ready for use in production code.
We'd also welcome your feedback on it.

## Getting a reference to the record group

Our existing code that calls the text search endpoint just displays a count of the results and an explanation of the query.
Now, we're going to get the record group from the search response and see what it contains.

1. In the `ExampleApp` class, create a `getRecords()` method that will eventually process the record group, but just makes sure that it exists for now:

   ```java
   private void getRecords(RecordGroupInfo recordGroup) {
       if (recordGroup == null) {
           System.out.println("No results");
           return;
       }
   
       System.out.println("Fetching results...");
   }
   ```

1. Call the new `getRecords()` method from the end of the `recordTextSearch()` method, after the final `System.out.println()`:

   ```java
   getRecords(response.getRecordGroup());
   ```

1. Run the `ExampleApp.main()` method with search expressions that do and do not match records in the Information Store.
   The output from `getRecords()` should be appropriate in both cases.

## Processing the record group

The record group in the response from the search request contains pages.
Each time you request a page from the record group, _that_ response contains the records in the page, and a cursor that points to the next page.

To navigate the whole record group, you repeatedly call the endpoint for retrieving a page, passing the cursor that you received in the previous response, until there are no more pages left to consume.
The cursor for retrieving the first page of results is `*`.

In pseudocode, the process for retrieving all pages of data for a record group looks like this:

1. Set `cursor` to `*`
1. Loop:
   1. Make a request for the current page of records
   1. Process any records on the current page
   1. Check if there is a subsequent page
      - If none, then exit
      - If so, then set `cursor` to `nextPage.cursor`

**Note:** The endpoint for retrieving pages also takes a `pageSize` query parameter, which limits the number of records on the page that matched the original search expression.
However, if the search matches a link record, then the entities at the ends of that link always appear on the same page. A page of *N* matching link records can also contain up to 2*N* entity records.

If the total number of matching records is exactly divisible by `pageSize`, it's possible for the final page in the record group to contain no records.

Let's put all of that logic into a new method for retrieving records from a record group, which we can call from our `ExampleApp.getRecords()` method.

1. Add the following code to the `ExampleApp` class:

   ```java
   private void retrieveRecords(String recordGroupId, Integer pageSize, BiConsumer<Records, Collection<String>> responseConsumer) {
       final RecordGroupsApi recordGroupsApi = new RecordGroupsApi(apiClient);
       String cursor = "*";
       int pageNumber = 1;
       while (cursor != null) {
           System.out.println("Retrieving results page " + pageNumber++);
           final String thisCursor = cursor;

           PagedRecordGroupResponse recordGroupResponse = executeRequest(() -> recordGroupsApi.pagedRecordsResults(recordGroupId, thisCursor, pageSize, null, OPT_IN_TO_EXPERIMENTAL_API));
           Records records = recordGroupResponse.getRecords();
           responseConsumer.accept(records, recordGroupResponse.getMatchedRecordIds());
           PagedRecordsPage nextPage = recordGroupResponse.getNextPage();

           if (nextPage == null) {
               cursor = null;
           } else {
               cursor = nextPage.getCursor();
           }
       }
   }
   ```

   Inside the loop, the call to the generated `RecordGroupsApi.pagedRecordsResults()` method fetches a page of results.

   We then pass the records on the page (`PagedRecordGroupResponse.getRecords()`) - together with a list of which records actually matched the search - to a function for further processing, in `responseConsumer.accept()`.

   Last, we call `PagedRecordGroupResponse.getNextPage()` to retrieve information about the next page.
   If there _is_ another page in the record group, we set the cursor accordingly, and loop again.

1. Call the new method by adding this code at the end of the `ExampleApp.getRecords()` method:

   ```java
   final String recordGroupId = recordGroup.getId();
   final Integer recordGroupPageSize = null; // Defaults to 500

   retrieveRecords(recordGroupId, recordGroupPageSize, (records, matchedRecordIds) -> {
       System.out.println("Page has " + records.getEntities().size() + " entities and " + records.getLinks().size() + " links");
   });
   ```

1. Run the `ExampleApp.main()` method, and you'll see the count of entity and link records being returned for each page.

   Setting the page size to `null` makes it default to 500, which might be larger than the number of matching records.
   Try changing the `pageSize` value and seeing its effect on the results.

## Cleaning up record groups

Record groups consume resources on the server.
You are free to process a record group as many times as you need, but when you have finished with it, it's good practice to delete it at the earliest opportunity.

**Note:** i2 Analyze does clean up record groups that haven't been accessed for a period of time (15 minutes by default), but it's better to be explicit when possible.

The REST API includes an [_experimental_](api-support-policy.md#support-for-experimental-endpoints) `DELETE` method for record groups that you call with the identifier of the group to be deleted.
For more detailed information, see the [documentation](https://docs.i2group.com/analyze/public-rest-api.html#delete-/api/v1/recordgroups/-recordGroupId-).

In this walkthrough, we only want to process the record group once, so we can delete it after we've retrieved the records.

1. Insert the following code at the end of the `ExampleApp.retrieveRecords()` method, outside the `while` loop:

   ```java
   executeRequest(() -> { recordGroupsApi.deleteRecordGroup(recordGroupId, OPT_IN_TO_EXPERIMENTAL_API); return null; });
   ```

After this call, the record group is no longer available for processing, and any attempt that you make to do so will fail.

## Processing records

So far, we've determined the number of entity and link records on each page of the record group, but we haven't seen any actual record data.
Let's start to extract some basic information by displaying the identifier and label of each record in the record group.

1. Add the following method to the `ExampleApp` class:

   ```java
   private void processRecord(Collection<String> matchedRecordIds, Set<String> seenRecordIds, Record record) {
      System.out.println(record.getId() + " " + record.getLabel());
   }
   ```

1. In the `getRecords()` method, replace the existing call to `retrieveRecords()` with the following:

   ```java
   final Set<String> seenRecordIds = new HashSet<>();
   retrieveRecords(recordGroupId, recordGroupPageSize, (records, matchedRecordIds) -> {
       Stream.concat(records.getEntities().stream(), records.getLinks().stream()).forEach(record -> processRecord(matchedRecordIds, seenRecordIds, record));
   });
   ```

   This implementation streams all the entity and link records from each page to the `processRecord()` method that we just added.
   (We'll use the `matchedRecordIds` and `seenRecordIds` soon.)

1. Execute the `ExampleApp.main()` method, and you will see the record identifiers and labels printed like this:

   ```
   Retrieving results page 1
   ABQdjN4ASuZ7iia25sb7aqDxoB Cocaine Distribution,
   FAXGStLK183TvCUPwaW4yQFqaR Cocaine Import,
   cYXPxZfpEzpB42Ft3sWASA7S8y Cocaine Import,
   UJ1NY7z9JMoVHtenNWoQd9sorH Setting up Hit,
   arTVZnWVjvgcrpJ2nwgpjWgmtG Gene HENDRICKS
   EKkk2E9orVYeoKWZEU6meLxktt Fenella TYLER
   NZU1rzrQmFCrsCGc53K4f6MU7W Tina CHURCHILL
   ...
   2YDjbC6CG4EaNakPeQWyvQSGURpc Associate
   HUgS9GG3xQU2B6Jryp9AQPbNYo2 Associate
   ```

### Matching and non-matching records

If you used the same data set and the same search expression as this walkthrough, then the output contained 42 records.
However, the query itself matched only 20 records.
To distinguish matching and non-matching records, the record group includes a `matchedRecordIds` collection that contains the identifiers of the matching records.

You can use the collection to establish which results matched the search and which did not.

1. Replace the `println()` statement in `processRecord()` with this code:

   ```java
   final String recordId = record.getId();
   System.out.println(recordId + " " + record.getLabel() + " - matched? " + matchedRecordIds.contains(recordId));
   ```

1. Run the `ExampleApp.main()` method again, and the output now looks like this:

   ```
   ABQdjN4ASuZ7iia25sb7aqDxoB Cocaine Distribution,  - matched? true
   FAXGStLK183TvCUPwaW4yQFqaR Cocaine Import,  - matched? true
   cYXPxZfpEzpB42Ft3sWASA7S8y Cocaine Import,  - matched? true
   UJ1NY7z9JMoVHtenNWoQd9sorH Setting up Hit,  - matched? true
   arTVZnWVjvgcrpJ2nwgpjWgmtG Gene HENDRICKS - matched? true
   EKkk2E9orVYeoKWZEU6meLxktt Fenella TYLER - matched? false
   NZU1rzrQmFCrsCGc53K4f6MU7W Tina CHURCHILL - matched? false
   ...
   2YDjbC6CG4EaNakPeQWyvQSGURpc Associate - matched? true
   HUgS9GG3xQU2B6Jryp9AQPbNYo2 Associate - matched? true
   ```

   You can see that `Fenella TYLER` and `Tina CHURCHILL` were present only as supporting link ends.

### Page size

Another consequence of link ends being returned to support links, is that the same entity can appear on several pages (but it can never appear more than once on a single page).
We can demonstrate the effect by reducing the page size.

1. In the `getRecords()` method, set `recordGroupPageSize` to `5`, and then run `ExampleApp.main()`.
   You will see output like this:

   ```
   Retrieving results page 1
   ...
   arTVZnWVjvgcrpJ2nwgpjWgmtG Gene HENDRICKS - matched? true
   ...
   Retrieving results page 4
   arTVZnWVjvgcrpJ2nwgpjWgmtG Gene HENDRICKS - matched? false
   ```

   Gene Hendricks appears on two pages.
   On page 1, he's a match for the search, so we output `matched? true`.
   On page 4, he's being returned as a supporting link end, so we output `matched? false` for that entry.

   **Note:** Nick Haley, Ramon Lopez, and Charlie Lien also appear on more than one page of the record group, but only ever as supporting link ends.

1. We don't want to print the same record information repeatedly, so update the `processRecord()` method to ignore duplicate entities by updating and checking the `seenRecordIds` set that we passed to the method:

   ```java
   final String recordId = record.getId();
   // Link ends might have appeared on earlier pages so check if we've seen them before
   if (!seenRecordIds.add(recordId)) {
       return;
   }
   System.out.println(recordId + " " + record.getLabel() + " - matched? " + matchedRecordIds.contains(recordId));
   ```

   You might not want to keep track of all the entities that have been returned in memory.
   But because supporting entities are always on the same page as the link they support, the record information is always available if you need it.

1. Run the `ExampleApp.main()` method again, and Gene Hendricks will appear only once in the results.

### Additional record information

We're now extracting each record's identifier, its label, and whether it matched the search.
For every link record in the results, we can also retrieve information about its ends.

The response from the server separates entity and link records into arrays that we've chosen to concatenate as we retrieve them.
However, that's not a problem, because link records also have an `isLink` flag.

1. Update the `processRecord()` method so that it prints link end information by inserting the following at the end:

   ```java
   final boolean isLink = Boolean.TRUE.equals(record.getIsLink());
   if (isLink) {
       System.out.println("\t\tFrom - " + record.getFromEnd().getId() + " - " + record.getFromEnd().getLabel());
       System.out.println("\t\tTo - " + record.getToEnd().getId() + " - " + record.getToEnd().getLabel());
   }
   ```

1. Run the `ExampleApp.main()` method again and take a look at the output:

   ```
   Retrieving results page 1
   ...
   arTVZnWVjvgcrpJ2nwgpjWgmtG Gene HENDRICKS - matched? true
   Retrieving results page 2
   ...
   XcZLzxiNC1HnTMJUGH76qQawyN Nick Haley - matched? false
   a28RNZaDWwZxAMVU7K9eGoQ6VJ Ramon LOPEZ - matched? false
   Retrieving results page 4
   ...
   FfmuxQ4qFSzPPJsGM7dgDHH1Vtt Associate - matched? true
                   From - a28RNZaDWwZxAMVU7K9eGoQ6VJ - Ramon LOPEZ
                   To - XcZLzxiNC1HnTMJUGH76qQawyN - Nick Haley
   2ashfF7vQgpfhVnTWPsyYnWWMSXJ Associate - matched? true
                   From - arTVZnWVjvgcrpJ2nwgpjWgmtG - Gene HENDRICKS
                   To - XcZLzxiNC1HnTMJUGH76qQawyN - Nick Haley
   ```

   The link end information contains the record identifier and label of the supporting entity record, which match the details in the page's array of entity records.
   You can use the identifier to retrieve more information about the entity record if you need it while you're processing the link record.

1. Records also contain type information, which we'll talk about more in [Supporting Endpoints](supporting-endpoints.md).
   For now, let's just output the item type identifier of each record and whether it's an entity or a link.
   Add the following to the `processRecord()` method, before the `if (isLink) {` check.

   ```java
   final String type = record.getItemTypeId() + (isLink ? " [Link]" : " [Entity]");
   System.out.println("\t" + type);
   ```

1. Run `ExampleApp.main()`, and you will see output like this:

   ```
   Retrieving results page 1
   ABQdjN4ASuZ7iia25sb7aqDxoB Cocaine Distribution,  - matched? true
           ET2 [Entity]
   ...
   arTVZnWVjvgcrpJ2nwgpjWgmtG Gene  HENDRICKS - matched? true
           ET5 [Entity]
   ...
   2vZZPt1TsrktEYNjekZ1U2wLk9ap Associate - matched? true
           LAS1 [Link]
                   From - gAMpN3SxDkgvAjs5Td8FDp9hDn - Mark KINGSTON
                   To - XcZLzxiNC1HnTMJUGH76qQawyN - Nick Haley
   ```

   `ET2` is an `Event` entity type in the example law enforcement schema; `ET5` is a `Person` entity type; and `LAS1` is an `Associate` link type.

1. To improve the output a little, remove the original `println()` statement and add these individual statements instead, after the output of the item type identifier:

   ```java
   System.out.println("\t\tRecord Identifier: " + recordId);
   System.out.println("\t\tLabel: " + record.getLabel());
   System.out.println("\t\tMatched Query: " + (matchedRecordIds.contains(recordId) ? "Yes" : "No (supporting record only)"));
   ```

   The output should now look like this:

   ```
           ET5 [Entity]
                   Record Identifier: arTVZnWVjvgcrpJ2nwgpjWgmtG
                   Label: Gene HENDRICKS
                   Matched Query: Yes
           ...
           LAS1 [Link]
                   Record Identifier: HUgS9GG3xQU2B6Jryp9AQPbNYo2
                   Label: Associate
                   Matched Query: Yes
                   From - HfV9VvR7Tr7nfWmG47ja9qxh4G - Nigel PARKIN
                   To - d1iZRgehA5QjbsV4T6rUGbfzL - Vernon Myers
   ```

### Record properties

The last thing to look at in this part of the walkthrough is how to retrieve the actual property values of the records in the record group.

In the OpenAPI specification, record properties are modeled as self-describing "polymorphic" types that take advantage of inheritance and composition.
You can interpret their values without reference to the i2 Analyze schema.

For example, a `SINGLE_LINE_STRING` property looks like this:

```json
{
  "value": "Location 1",
  "logicalType": "SINGLE_LINE_STRING",
  "typeId": "PT1"
}
```

While a `GEOSPATIAL` property looks like this:

```json
{
  "value": {
    "type": "Point",
    "coordinates": [
      -77.0728081,
      38.9949801
    ]
  },
  "logicalType": "GEOSPATIAL",
  "typeId": "PT2"
}
```

And an `INTEGER` property looks like this:

```json
{
  "value": 2002,
  "logicalType": "INTEGER",
  "typeId": "PT8"
}
```

The `value` is interpreted differently according to the `logicalType`.
This translates directly into the generated code, since JSON libraries use the `logicalType` as a discriminator.

We will now extract the properties of the records we're retrieving.

1. In `ExampleApp`, add this method that extracts the value of any property as a `String` and prints it to the console along with the property type identifier:

   ```java
   private void processProperty(RecordPropertyBase property) {
       final String value;
       switch (property.getLogicalType()) {
           case SINGLE_LINE_STRING:
           case MULTIPLE_LINE_STRING:
           case SELECTED_FROM:
           case SUGGESTED_FROM:
               value = ((RecordStringProperty) property).getValue();
               break;
           case DATE:
               value = ((RecordDateProperty) property).getValue().toString();
               break;
           case TIME:
               value = ((RecordTimeProperty) property).getValue();
               break;
           case DATE_AND_TIME:
               final DateAndTime dateAndTime = ((RecordDateAndTimeProperty) property).getValue();
               value = dateAndTime.getLocalDateAndTime() + " " + dateAndTime.getTimeZoneId();
               break;
           case BOOLEAN:
               value = ((RecordBooleanProperty) property).getValue().toString();
               break;
           case INTEGER:
               value = ((RecordIntegerProperty) property).getValue().toString();
               break;
           case DOUBLE:
               value = ((RecordDoubleProperty) property).getValue().toString();
               break;
           case DECIMAL:
               value = ((RecordDecimalProperty) property).getValue().toString();
               break;
           case GEOSPATIAL:
               value = ((RecordGeospatialProperty) property).getValue().toString();
               break;
           default:
               throw new IllegalArgumentException("Unknown property type " + property.getLogicalType());
       }
       System.out.println("\t\t" + property.getTypeId() + " - " + value);
   }
   ```

   **Note:** The method is a bit verbose because of the way the code generator created the transport classes.
   All the subclasses of `RecordPropertyBase` have a `getValue()` method, but `RecordPropertyBase` itself does not.
   We could modify the generated class or customize the generator, but doing so is not important for the walkthrough.

1. Insert the following code at the end of the `processRecord()` method to print all the properties for each record:

   ```java
   System.out.println("\t\t-------------");
   record.getProperties().forEach(this::processProperty);
   System.out.println();
   ```

1. Execute the `ExampleApp.main()` method. The output for Gene Hendricks looks like this:

   ```
           ET5 [Entity]
                   Record Identifier: arTVZnWVjvgcrpJ2nwgpjWgmtG
                   Label: Gene HENDRICKS
                   Matched Query: Yes
                   -------------
                   PER4 - Gene
                   PER5 - Alan
                   PER6 - HENDRICKS
                   PER9 - 1968-09-24
                   PER15 - Male
                   PER93 - American
                   PER14 - Southwest
                   PER17 - Slim
                   PER71 - 180.0
                   PER72 - 180.0
                   PER92 - Green
                   PER23 - Short
                   PER55 - Brown
                   PER28 - Company President
                   PER34 - Tattoo of dragon on his left upper arm
   ```

The record object contains other information, including source reference identifiers and notes.
See the [REST API documentation](https://docs.i2group.com/analyze/public-rest-api.html#get-/api/v1/recordgroups/-recordGroupId-/pagedrecords) for more details.

Now that we can retrieve records from a search, we will start [searching for records by their locations](search-geospatial.md).
