# Logging in to the i2 Analyze server

So far, you've made a request to the `live` endpoint, which does not require authentication.
The other endpoints in the i2 Analyze REST API require an authenticated user to make the request.
The procedure in this topic demonstrates what happens when you attempt to make a request without authentication, and then goes on to implement form-based authentication for this and future requests.

1. In `ExampleApp`, insert the following lines after the call to `live()`, but before the call to output the `Success!` message:

   ```java
       try {
           new UsersApi(myApiClient).getUser("Jenny");
       } catch (ApiException ex) {
           throw new RuntimeException("getUser status: " + ex.getCode() + " " + ex.getMessage());
       }
   ```

   By default, the `user` endpoint provides access to information only if the user making the request is the same as the specified user.
   Otherwise, it returns a `404` response.

   In the REST API, the `user` endpoint has a `userId` path parameter.
   In the generated code, the path parameter becomes an argument to the `getUser()` method.

1. Execute the `main()` method, and you'll get an exception with an error message that looks like this:

   ```
   status: 200 Deserialization for content type 'text/html' not supported for type 'com.example.generated.api.UsersApi$1@25aca718'
   ```

   The error occurs because i2 Analyze uses form-based authentication by default.
   When a request is made to an endpoint that requires authentication, and the authentication is not provided or not valid, the server defaults to redirecting a browser to the login page (which has `text/html` content rather than the `application/json` that the client expects).
   You can change this behavior by adding a query parameter (`redirectForAuthentication=false`) to requests.

   **Note:** When the target i2 Analyze deployment uses a different authentication method, this approach does not work.
   You must use a compatible way to authenticate your client.
   For more information on the authentication methods that Liberty supports, [refer to its documentation](https://www.ibm.com/docs/en/was-liberty/base?topic=security-authentication).

1. In `MyApiClient`, insert the following method definition, which overrides the `invokeAPI()` method in the generated `ApiClient`:

   ```java
       @Override
       public <T> T invokeAPI(String path, String method, List<Pair> queryParams, List<Pair> collectionQueryParams, String urlQueryDeepObject, Object body, Map<String, String> headerParams, Map<String, String> cookieParams, Map<String, Object> formParams, String accept, String contentType, String[] authNames, TypeReference<T> returnType) throws ApiException {
           // Ensure a 401 rather than a 302 if authentication is required
           queryParams.add(new Pair("redirectForAuthentication", "false"));
           return super.invokeAPI(path, method, queryParams, collectionQueryParams, urlQueryDeepObject, body, headerParams, cookieParams, formParams, accept, contentType, authNames, returnType);
       }
   ```

   Now, all HTTP requests that you make through `MyApiClient` will include the new query parameter.

1. Execute the `main()` method again, and you'll see a different error:

   ```
   Exception in thread "main" java.lang.RuntimeException: getUser status: 401 
   ```


   We will implement better error handling later, but the `401` status clearly indicates that authentication is required when you call `getUser()`.

## Using form-based authentication

Form-based authentication is a standard mechanism in which the caller sends credentials to a specific endpoint.
On a successful request, the server returns a cookie for the caller to use in place of credentials on subsequent requests.

The form-based approach is best suited to browser-based applications rather than REST clients, and so the OpenAPI specification and the code generator do not provide direct support for it.
However, it is the default authentication mechanism in i2 Analyze, and so we need to be able to work with it.

You can perform form-based authentication against a default deployment of i2 Analyze by making a `POST` request to `/j_security_check` with a
`content-type` of `application/x-www-form-urlencoded` and the form parameters `j_username` and `j_password`.
All successful responses contain a `Set-Cookie` header with an `LtpaToken2` cookie.

We'll take this approach in `MyApiClient` and execute it from the constructor.

1. Add this method to `MyApiClient`:

   ```java
   private void formBasedAuthLogin(String username, String password) {
       final String contentType = "application/x-www-form-urlencoded";
       final Map<String, Object> formParams = new HashMap<>();
       formParams.put("j_username", username);
       formParams.put("j_password", password);
       try {
           invokeAPI("/j_security_check", "POST", new ArrayList<>(), new ArrayList<>(), null, null, new HashMap<>(), new HashMap<>(), formParams, null, contentType, new String[0], null);
       } catch (ApiException ex) {
           if (ex.getCode() > 0) {
               throw new RuntimeException("formBasedAuthLogin status: " + ex.getCode() + " " + ex.getMessage(), ex);
           }
           throw new RuntimeException(ex);
       }
   }
   ```

1. Call the new method from the `MyApiClient` constructor _after_ the call to `super.setBasePath(basePath)` by adding this line:

   ```java
   formBasedAuthLogin(username, password);
   ```

1. Execute the `main()` method of `ExampleApp` again.
   You'll still see an error from the call to `getUser()`:

   ```
   Exception in thread "main" java.lang.RuntimeException: getUser status: 401
   ```

   The generated `ApiClient` creates a separate cookie store for each request, so the authentication cookie is not available to the call to `getUser()`.

1. To capture the `LtpaToken2` cookie and supply it in requests, `MyApiClient` needs a `CookieStore`.
   Add this line above the constructor:

   ```java
   private final CookieStore cookieStore = new BasicCookieStore();
   ```

1. In `formBasedAuthLogin()`, replace the lines before the `try` block with the following code to prepare the login request and supply our cookie store:

   ```java
   final HttpPost request = new HttpPost(URI.create(getBasePath() + "/j_security_check"));
   request.setEntity(new UrlEncodedFormEntity(Arrays.asList(
           new BasicNameValuePair("j_username", username),
           new BasicNameValuePair("j_password", password))));
   final HttpClientContext context = HttpClientContext.create();
   context.setCookieStore(cookieStore);
   ```

1. Replace the `try` and `catch` blocks to send the request through the underlying HTTP client:

   ```java
   try {
       getHttpClient().execute(request, context, response -> {
           if (response.getCode() < 200 || response.getCode() >= 300) {
               throw new RuntimeException("Authentication failed: HTTP " + response.getCode());
           }
           return null;
       });
   } catch (IOException ex) {
       throw new RuntimeException("Authentication failed", ex);
   }
   ```

   This code retains cookies from the login response, including any intermediate redirects, in the `CookieStore`.
   The response handler lets the HTTP client release response resources automatically.

1. After the `catch` block, add this check for the authentication cookie:

   ```java
   if (cookieStore.getCookies().stream().noneMatch(cookie -> "LtpaToken2".equals(cookie.getName()))) {
       throw new RuntimeException("Authentication failed: no LtpaToken2 cookie received");
   }
   ```

   Checking for the `LtpaToken2` cookie is important because a failed login can also redirect to an HTML page with a final `200` status.

1. Execute the `main()` method again.
   You'll still see `getUser status: 401`.
   We're retrieving the cookie, but we haven't yet set it on the call to `getUser()`.

1. In the overridden `invokeAPI()` method, insert the following line before the call to `super.invokeAPI()`:

   ```java
   cookieStore.getCookies().forEach(cookie -> cookieParams.put(cookie.getName(), cookie.getValue()));
   ```

1. Execute the `main()` method yet again, and at last you'll see the `Success!` message on the command line.

1. To confirm that the authentication mechanism is truly authenticating the credentials we supply, change the password supplied to the `MyApiClient` constructor from `"Jenny"` to `"NotJenny"` and execute the `main()` method again.
   This time you'll see an error:

   ```
   Exception in thread "main" java.lang.RuntimeException: Authentication failed: no LtpaToken2 cookie received
   ```

   If the server instead returns a non-success status, the error includes that HTTP status.

   Revert the change to the password before continuing.

We can also take a look at the response that is being returned from the `user` endpoint, because the `getUser()` method returns a `SessionUser` object.

1. In the `main()` method, wrap the call to `getUser()` with a `println()` statement:

   ```java
   System.out.println(new UsersApi(myApiClient).getUser("Jenny"));
   ```

1. Execute the `main()` method one last time to see output that contains the user information retrieved from the server, marshaled into a Java class:

   ```
   class SessionUser {
       commandPermissions: [i2:RecordsUpload, i2:RecordsDelete, i2:Connectors, i2:RecordsExport, i2:ChartsDelete, i2:ChartsUpload, i2:Administrator, i2:ChartsRead, i2:Notebook, i2:Notes, i2:Connectors:example-connector]
       displayName: Jenny
       principalName: Jenny
       id: 48d0a868-6bcb-4856-88db-a11dbae2712f
   }
   ```

## Summary

In this part of the walkthrough, you've started to consume the generated client code and extended it to deal with form-based authentication.
You've also used a trust store to establish a secure HTTPS connection to the server in order to verify the identity of the server you're communicating with.
Finally, you've made successful requests to some of the REST API endpoints.

In the next topic, you'll learn how to verify that the server [supports the APIs](version-negotiation.md) that your client depends on.
