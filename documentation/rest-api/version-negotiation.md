# Negotiating a common API version

By following the walkthough to this point, you have a Java application that can make authenticated requests to an i2 Analyze server.
As a result, the rest of the REST API is now open to your application.

In general, however, before you can use the API, you need to know which version of it you're making requests to.
Over time, features are added to and removed from the REST API, and different versions of the server implement different versions of the API.
How can you find out whether a particular server supports the functionality that you need?

All versions of the REST API include a mechanism not only for determining what version of the API a server implements, but also for negotiating a version that the client and server can rely on.
By using this mechanism, you can write code to behave acceptably if it encounters an "old" server, or to provide meaningful feedback when it encounters a "new" one. 

For more information about version negotiation in the i2 Analyze REST API, see the [separate topic](version-negotiation-details.md) on this subject.
The following instructions describe how to add version negotiation to the application that we've been developing so far.

## Implementing version negotiation

Version negotiation means that a client application might need to execute different code depending on the server version.
Instead of doing everything in the `main()` method, we'll start implementing some of the logic in the `ExampleApp` class.

1. Version negotiation is fundamental, so we'll call it from the constructor.
   Insert the following code above the `main()` method in `ExampleApp.java`:

   ```java
   private static final String REQUIRED_API_VERSION = "1.0";
   private static final String CLIENT_NAME = "My Client";
   private static final String CLIENT_VERSION = "0.0.1-demo";
   
   private final ApiClient apiClient;
   
   public ExampleApp(ApiClient apiClient) {
       this.apiClient = apiClient;
       checkApiVersionIsSupported();
   }
   
   public void checkApiVersionIsSupported() {
       System.out.println("Version negotiation...");
       final VersionApi versionApi = new VersionApi(apiClient);
   }
   ```

   Like `UsersApi` and `HealthApi`, the code generator created the `VersionApi` class for endpoints that have the `"Version"` tag.
   In the specification, the `operationId` of the POST method is `versionNegotiation`, which is therefore also the name of the Java method.

1. The request we'll make here is the first you've used that requires a body rather than query parameters.
   The generated code represents the body as a request object that you can create by inserting the following code into the `checkApiVersionIsSupported()` method:

   ```java
       final VersionNegotiationRequest request = new VersionNegotiationRequest();
       final String requiredVersion = REQUIRED_API_VERSION;
       request.setMinimumVersion(requiredVersion);
   ```

   This walkthrough is written against API version `1.0`, and you generated the code from version `1.0` of the OpenAPI specification, so we've set that as the minimum version we want to use.

1. Make the request itself by inserting the following code:

   ```java
   try {
      versionApi.versionNegotiation(request);
   } catch (ApiException e) {
      throw new RuntimeException("Could not determine server version. status: " + e.getCode(), e);
   }
   ```

1. In the `ExampleApp` `main()` method, before the call to output the `Success!` message, add the following line to instantiate `ExampleApp` and call `checkApiVersionIsSupported()`:

   ```java
   final ExampleApp app = new ExampleApp(myApiClient);
   ```
   
   **Note:** You can also remove the calls that use `HealthApi` and `UsersApi` from the `main()` method.
   They are not used in the rest of this walkthrough.

1. Run the `ExampleApp` `main()` method.
   You will see an error that looks like this:

   ```
   Exception in thread "main" java.lang.RuntimeException: Could not determine server version. status: 400
   ...
   Caused by: com.example.generated.invoker.ApiException: {"type":"tag:i2group.com,2022:problem-details","title":"Bad Request","status":400,"detail":"'client' must be present.","requestId":"088d65a0-e9e0-4890-94c2-8589ba4c14c9","errors":[{"message":"'client' must be present."}]}
   ```

   What went wrong? Formatting the message makes it easier to read:

   ```json
   {
     "type": "tag:i2group.com,2022:problem-details",
     "title": "Bad Request",
     "status": 400,
     "detail": "'client' must be present.",
     "requestId": "8d4496f4-0301-4503-ab0e-62b0cc548ada",
     "errors": [
       {
         "message": "'client' must be present."
       }
     ]
   }
   ```

   The request was "bad" because `'client' must be present.`
   In other words, the request object requires both `minimumVersion` and `client` to be set, but we provided only the former.

1. Populate the `client` field on the request by inserting the following code after the call to `setMinimumVersion()`:

   ```java
   final ClientInfo clientInfo = new ClientInfo();
   clientInfo.setName(CLIENT_NAME);
   clientInfo.setVersion(CLIENT_VERSION);
   request.setClient(clientInfo);
   ```

   **Note:** The details of the name and the version don't affect the behavior of the API, but they do appear in the server logs.
   Setting them to values that are meaningful might help you or a server administrator to identify problems related to your client application in the future.

1. Run the `main()` method of the `ExampleApp` class again.
   
   This time, there are no errors, but there's also nothing to say that the request worked.
   With a bit more work, we can do something about that.

1. Replace the `try-catch` block with this code, which outputs the server's version and indicates whether it is deprecated.

   ```java
   try {
      final VersionNegotiationResponse responseBody = versionApi.versionNegotiation(request);
      String message = "The server implements API version " + responseBody.getVersion();
      if (Boolean.TRUE.equals(responseBody.getDeprecated())) {
          message += " but it is deprecated";
      }
      System.out.println(message);
   } catch (ApiException e) {
      throw new RuntimeException("Could not determine server version. status: " + e.getCode(), e);
   }
   ```

1. Run the `main()` method once again, and you'll see the server's API version on the console.
   You've successfully verified that the server supports the API version required for this walkthrough.

## Version negotiation error handling

So far, we've looked at what happens when the server supports the version of the API that we require.
Next, we'll find out what happens when the server does not support the requested version, and add some graceful error handling.

1. Set the `REQUIRED_API_VERSION` constant to `0.9` and run the `main()` method again.
   Then do the same thing twice more, with the constant set to `99.9` and `1.0.1`.

   The three different values produce three different response codes:

   - `0.9` generates a `410` response
   - `99.9` generates a `404` response
   - `1.0.1` generates a `400` response

   In all three cases, the client application throws an exception. You can handle the exception more cleanly from the `catch` block by reacting to the server.

1. In the `catch` block of the `checkApiVersionIsSupported()` method, insert the following above the `throw new RuntimeException...` statement:

   ```java
   if (e.getCode() == 404) {
       throw new RuntimeException("API version " + requiredVersion + " is not supported. The server is too old.");
   }
   if (e.getCode() == 410) {
       throw new RuntimeException("API version " + requiredVersion + " is not supported. The server is too new.");
   }
   ```

   You now have explicit handling for two of the cases where negotiation fails because the server cannot satisfy the version that the client requires.
   The client falls back to generic error handling for any other case with the existing `throw` statement.
   Let's clean it up slightly, as we're going to have errors to handle when making other requests.
   
1. Add a method that extracts the status code from exceptions, which we've been doing manually so far:

   ```java
   private RuntimeException exceptionHandler(ApiException e) {
       return new RuntimeException("Error - status: " + e.getCode(), e);
   }
   ```
1. In the `catch` block of the `checkApiVersionIsSupported()` method, replace the existing `throw` statement with this more generic approach:

   ```java
   throw new RuntimeException("Could not determine server version", exceptionHandler(e));
   ```
   
   Later in the walkthrough, we'll extend the capabilities of the `exceptionHandler()` method to extract more information from unexpected errors.

1. Modify `REQUIRED_API_VERSION` again to try each scenario, and then set it back to `1.0` when you are done.

## Summary

The code that you added to the example in this part of the walkthrough has added simple version negotiation to the client application.
Remember, however, that the client is just requesting the _minimum_ version that it supports. 
The i2 Analyze server might still return a higher compatible version.

We've now verified that the server supports the version of the API that we're writing code against, and we can start making requests against the remainder of the API with confidence that the server supports the functionality that we require.

In the next topic, we'll continue to develop the client by using the [alerting API](sending-alerts.md) to send notifications to i2 Analyze users.
