# Searching for records by their locations

At this point in the walkthrough, we've successfully used the i2 Analyze REST API to run a search against the Information Store and process the results that it returned.
However, searching for records by their text contents is not the only kind of search that the API supports.
The geospatial search endpoint at `infostore/search/geospatial` enables searching for records based on geospatial criteria.

The functionality is the same as the geospatial search that Analyst's Notebook Premium provides in Visual Query, except that by default, the REST API searches records of _all_ types that have `GEOSPATIAL` properties.
(You can limit the search to records with particular types if you want to.)

For more detailed information about the geospatial search endpoint, see the [REST API documentation](https://docs.i2group.com/analyze/public-rest-api.html#post-/api/v1/infostore/search/geospatial).
At this release of the i2 Analyze REST API, it is an [_experimental endpoint_](api-support-policy.md#support-for-experimental-endpoints), which means that it is not ready for use in production code.
We'd also welcome your feedback on it.

## Running a geospatial search

Let's see what it takes to run a geospatial search with the code that you generated from the OpenAPI specification.

**Note:** The code in this part of the example looks more complicated than it really is, because it has to construct the JSON object that represents the search area.
We'll explore a different approach later in the topic.

1. Add this pair of methods to create a query that searches the area around Washington DC; execute that query; and then fetch the results using our existing `ExampleApp.getRecords()` method:

   ```java
   public void geoAreaSearch() {
       System.out.println("Executing geospatial search...");
       final SearchApi searchApi = new SearchApi(apiClient);
   
       final GeospatialSearchRequest geospatialSearchRequest = new GeospatialSearchRequest();
       final FeatureCollection featureCollection = new FeatureCollection();
       featureCollection.setType(FeatureCollection.TypeEnum.FEATURECOLLECTION);
       final Feature feature = new Feature();
       feature.setType(Feature.TypeEnum.FEATURE);
       final GeoJSONPolygon polygon = new GeoJSONPolygon();
       polygon.addCoordinatesItem(listOf(listOf(-77.254486, 38.75087), listOf(-77.254486, 39.030919), listOf(-76.779327, 39.030919), listOf(-76.779327, 38.75087), listOf(-77.254486, 38.75087)));
       feature.setGeometry(polygon);
       featureCollection.addFeaturesItem(feature);
       geospatialSearchRequest.setArea(featureCollection);
   
       final GeospatialSearchResponse response = executeRequest(() -> searchApi.geospatial(geospatialSearchRequest, OPT_IN_TO_EXPERIMENTAL_API));
       System.out.println("Got " + response.getMatchCount() + " results for query");
       getRecords(response.getRecordGroup());
   }
   
   // If you are using Java 9+ then you can just use List.of() instead
   private <T> List<T> listOf(T... ts) {
       return Arrays.asList(ts);
   }
   ```

   The geospatial search endpoint supports [GeoJSON](https://geojson.org/) `Polygon` and `MultiPolygon` types that `FeatureCollection`s provide, including holes and arbitrary shapes within the polygons.

1. Now add a call to `geoAreaSearch()` from the `ExampleApp.main()` method:

   ```java
   app.geoAreaSearch();
   ```

1. Run the `main()` method and verify that the output looks like this:

   ```
   Executing geospatial search...
   Got 11 results for query
   Fetching results...
   Retrieving results page 1
           ET1 [Entity]
                   Record Identifier: RMvBYPtHE4PA982zAfL7cNM47S
                   Label: 114, State Park Drive
                   Matched Query: Yes
                   -------------
                   ADD3 - 114
                   ADD5 - State Park Drive
                   ADD6 - Chevy Chase
                   ADD17 - MD
                   ADD26 - class GeoJSONPoint {
       class Geometry {
           type: Point
       }
       coordinates: [-77.072808, 38.99498]
   }
   ...
   ```

1. The presentation could be better.
   Enhance the `processProperty()` method by replacing the `case` statement for `GEOSPATIAL` properties with the following code, which outputs a link to the location on a map instead:

   ```java
   case GEOSPATIAL:
       final RecordGeospatialProperty geoProp = (RecordGeospatialProperty) property;
       
       // Currently records only have Point properties, but that might change in the future without the REST API changing.
       // It is safer to check rather than cast directly to GeoJSONPoint.
       if (geoProp.getValue().getType() == Geometry.TypeEnum.POINT) {
           final GeoJSONPoint point = (GeoJSONPoint) geoProp.getValue();
           final List<Double> coordinates = point.getCoordinates();
           value = String.format("View on a map: https://www.openstreetmap.org/?mlon=%s&mlat=%s&zoom=15", coordinates.get(0), coordinates.get(1));
       } else {
           value = geoProp.getValue().toString();
       }
       break;
   ```

   This code demonstrates the value of the [polymorphic types](result-paging.md#record-properties) in the OpenAPI specification.
   You can interpret the `value` of the property based on its logical type, without having to serialize it explicitly to longitude and latitude.

   **Note:** The addresses in the example data are fake, and do not match the geospatial location information.
   As a result, the links open to different (real) addresses than you might expect.

1. Run the `ExampleApp.main()` method again. 
   The output now looks like this:

   ```
   Executing geospatial search...
   Got 11 results for query
   Fetching results...
   Retrieving results page 1
           ET1 [Entity]
                   Record Identifier: RMvBYPtHE4PA982zAfL7cNM47S
                   Label: 114, State Park Drive
                   Matched Query: Yes
                   -------------
                   ADD3 - 114
                   ADD5 - State Park Drive
                   ADD6 - Chevy Chase
                   ADD17 - MD
                   ADD26 - View on a map: https://www.openstreetmap.org/?mlon=-77.072808&mlat=38.99498&zoom=15
   ...
   ```
 
   You should be able to follow the link from the VS Code terminal (press Ctrl and click). If that doesn't work, copy and paste the link into a browser.

## Importing a feature collection

If you want to experiment more freely with the endpoint from Java, then instead of constructing the geometry directly, you can import it from a file.

1. Go to http://geojson.io and draw a shape around Washington DC.

1. Copy the definition of your shape from the **JSON Source** tab and save it to a new file called `myFile.txt` in the root of your project (alongside the `src` directory).

1. Remove the following lines from the `geoAreaSearch()` method:

   ```java
   final FeatureCollection featureCollection = new FeatureCollection();
   featureCollection.setType(FeatureCollection.TypeEnum.FEATURECOLLECTION);
   final Feature feature = new Feature();
   feature.setType(Feature.TypeEnum.FEATURE);
   final GeoJSONPolygon polygon = new GeoJSONPolygon();
   polygon.addCoordinatesItem(listOf(listOf(-77.254486, 38.75087), listOf(-77.254486, 39.030919), listOf(-76.779327, 39.030919), listOf(-76.779327, 38.75087), listOf(-77.254486, 38.75087)));
   feature.setGeometry(polygon);
   featureCollection.addFeaturesItem(feature);
   ```

1. Replace the deleted lines with this code, which loads a `FeatureCollection` from `myFile.txt` and serializes it to a Java object.

   ```java
   final FeatureCollection featureCollection; 
   try { 
       featureCollection = apiClient.getObjectMapper().readValue(new java.io.File("myFile.txt"), FeatureCollection.class);
   } catch (java.io.IOException e) { 
       throw new RuntimeException(e); 
   }
   ```

1. Now when you run the `ExampleApp.main()` method, the search criteria will come from the file that you created.

In the next topic, as a final step in the walkthrough, we can see how to enhance the client with information provided by some [supporting APIs](supporting-endpoints.md).
