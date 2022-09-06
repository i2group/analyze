# Searching for records by their contents

The i2 Analyze REST API provides programmatic access to the same functionality that Analyst's Notebook and i2 Notebook users enjoy when they run a "Quick Search" of the Information Store.

From client code, you can call the endpoint at `infostore/search/text`, which searches the full text contents of records for the terms that you specify.
(You can choose to search the contents of notes as well as or instead of property values.
You can also choose to constrain the search to records of specific item types.)

For more detailed information about the text search endpoint, see
the [REST API documentation](https://docs.i2group.com/analyze/public-rest-api.html#post-/api/v1/infostore/search/text).

## Using experimental endpoints

At this release of the i2 Analyze REST API, `infostore/search/text` is an [_experimental endpoint_](api-support-policy.md#support-for-experimental-endpoints), which means that it is not ready for use in production code.
We'd also welcome your feedback on it.

In order to use experimental endpoints, you must acknowledge in your code that you are doing so by providing an extra parameter when you call them.

The OpenAPI specification requires that requests to experimental endpoints include `I2-Experimental-Apis` header.
In this release, you must set the value of the header to `SEARCH_V1`.

1. In the `ExampleApp` class, add a constant above the constructor:

   ```java
    // You must set this to "SEARCH_V1" to use the experimental APIs.
    private static final String OPT_IN_TO_EXPERIMENTAL_API = "SEARCH_V1";
   ```

We'll make use of this constant when we call the generated method to run the text search.

## Running a text search

In the i2 Analyze REST API, performing a text search and retrieving the results of that search are separate operations. Running the search generates a _record group_ on the server that you can interact with later.

In this part of the walkthrough, we're going to run a search and check that it completed correctly, but we're not yet going to look at the results. That's a subject for a later step!

1. Add a method named `recordTextSearch()` to the `ExampleApp` class:

   ```java
   public void recordTextSearch(String expression) {
       System.out.println("Executing record text search...");
       final SearchApi searchApi = new SearchApi(apiClient);

       final TextSearchRequest textSearchRequest = new TextSearchRequest();
       textSearchRequest.expression(expression);
       
       final TextSearchResponse response = executeRequest(() -> searchApi.recordTextSearch(textSearchRequest, OPT_IN_TO_EXPERIMENTAL_API));
       System.out.println("Got " + response.getMatchCount() + " results for query: " + response.getExplain().getFormatted());
   }
   ```

   The form of this code is starting to become familiar.
   In the specification, the text search endpoint has the `"Search"` tag, and so the generated class is named `SearchApi`.

   Like `AlertsApi.createAlert()`, which you used to [send alerts to users](sending-alerts.md), the call to `SearchApi.recordTextSearch()` is wrapped in a call to `executeRequest()`.
   The parameters to `recordTextSearch()` are the request object and the `OPT_IN_TO_EXPERIMENTAL_API` constant.

   The response to the search contains several parts.
   For now, we are extracting the number of matching records (`getMatchCount()`) and retrieving an explanation of how the server interpreted the query (`getExplain().getFormatted()`).

1. In the `main()` method, insert a call to the method you just added:

   ```java
   app.recordTextSearch("(gene hendricks) OR Associate");
   ```

1. Run the `main()` method, and you'll see the following output:

   ```
   Executing record text search...
   Got 20 results for query: (gene AND hendricks) OR Associate
   ```

   Different searches and different data sets will produce different result counts.
   Notice how the _formatted_ expression shows that the server interpreted "gene hendricks" as "gene AND hendricks", clarifying the search behavior.

1. Experiment with other queries by modifying the query string and running the application again.

   For information about the full syntax of the query string, see the [Quick Search documentation](https://docs.i2group.com/anb/info_store_searching.html).

In the next topic, before we start to examine the search results, we'll look at what happens if requests to the server go wrong, and [how to handle the errors](error-handling.md) that arise when they do.
