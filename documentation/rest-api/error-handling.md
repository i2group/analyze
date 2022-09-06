# Handling server errors

When the i2 Analyze server receives an invalid request, it returns a status code and a response in [RFC 7807](https://www.rfc-editor.org/rfc/rfc7807.html) format. In each response, the server sets the value of the `type` field to indicate its intended audience:

- `"tag:i2group.com,2022:problem-details"` indicates that the response is intended for developers
- `"tag:i2group.com,2022:user-problem-details"` indicates that the response is intended for end users

However, our client application doesn't currently make that detail easy to find. In this part of the walkthrough, we'll explore a couple of error scenarios, find out what information is available for us to work with, and then use that information to improve our error reporting.

**Note:** For a more detailed description of how i2 Analyze populates RFC 7807 responses, see the [REST API documentation](https://docs.i2group.com/analyze/public-rest-api.html#cmp--schemas-i2rfc7807problemdetails)

## Example error scenarios

Let's start by sending some invalid requests to the text search endpoint and examining the contents of the responses that we receive.

### Example of a developer error

To simulate the kind of mistake that a developer might make when they request a text search, we can provide a `null` search expression.

1. In the `ExampleApp.main()`, replace `app.recordTextSearch("(gene hendricks) OR Associate");` with `app.recordTextSearch(null);`.

1. Execute the `main()` method.
   The following error message is returned:

   ```
   Exception in thread "main" java.lang.RuntimeException: Error - status: 400
   ```

This message is not an obvious statement of what went wrong.
Further down the log, there's another message that indicates the type and reason of the error:

```
Caused by: com.example.generated.invoker.ApiException: {"type":"tag:i2group.com,2022:problem-details","title":"Bad Request","status":400,"detail":"'expression' must contain non-whitespace characters.","requestId":"3aaed48f-ac2c-4c33-a52b-0d8943d90345","errors":[{"message":"'expression' must contain non-whitespace characters."}]}
```

This message is in RFC 7807 format, and the `type` field contains the string `problem-details`.
The response is intended for developers.
The `errors` field contains the explanation of what made it a bad request.
Later in this topic, we'll look at how to improve the developer experience.

### Example of a user error

Next, let's see what happens in response to the kind of mistake that a user might make.

1. In the `main()` method in `ExampleApp`, replace the `null` argument that we just specified with `"(gene hendricks) AND OR NOT Associate"`.

1. Execute the `main()` method.
   You'll find a message similar to this one in the log:

   ```
   Caused by: com.example.generated.invoker.ApiException: {"type":"tag:i2group.com,2022:user-problem-details","title":"Bad Request","status":400,"detail":"The search expression is not valid.","requestId":"3e0c73d2-df4b-418b-a30b-ccc2f10e98a8","errors":[{"message":"Place the OR operator between two search terms, or remove it."},{"message":"Place the AND operator between two search terms, or remove it."}]}
   ```

This time, the `type` field contains the string `user-problem-details`, which is different from the earlier one.
The response is intended for end users.
Again, the `errors` field contains the explanation of what made it a bad request.

In both cases, it's _possible_ to identify the root cause of the issue and who it's for; but having to go through the logs is not the easiest way to do so.
The next section of this walkthrough suggests a better approach.

## Improving error handling

The details of the errors are available, but the first message is just `Error - status: 400`, and it's not obvious what the problem is until you look deeper into the logs.
We can improve our error handling to extract the interesting parts of the RFC 7807 responses and use them to provide more detailed error messages. 

We'll build the new error messages from two parts:

- A summary that indicates the target audience for the error. It will be built using the `type` field of the RFC 7807 response.
- A reason, which specifies the exact cause of the error. It will be built using the `errors` field of the RFC 7807 response.

To achieve this, follow the steps below:

1. First, you need to check that you're dealing with an RFC 7807 response.
   The `Content-Type` of such a response is `application/problem+json`.
   In the `ExampleApp.exceptionHandler()` method, add the following code before the `return` statement:

   ```java
       final List<String> contentTypes = e.getResponseHeaders().get("Content-Type");
       if (contentTypes.contains("application/problem+json")) {
           // Handle RFC 7807 error
       }
   ```

1. Now you can extract the RFC 7807 response from the response body and map it to the generated `I2RFC7807ProblemDetails` class.
   Add the following code to the `if` block:

   ```java
           try {
               final ObjectMapper objectMapper = apiClient.getObjectMapper();
               final I2RFC7807ProblemDetails problemDetails = objectMapper.readValue(e.getResponseBody(), I2RFC7807ProblemDetails.class);
           } catch (JsonProcessingException e2) {
               throw new RuntimeException(e2);
           }
   ```

1. We'll build the summary first.
   In the `try` block, after the initialization of the `problemDetails` variable, add the following code:
 
   ```java
               final String type = problemDetails.getType().toString();
               final boolean isUserProblem = type.equals("tag:i2group.com,2022:user-problem-details");
                
               final String summary;
               if (isUserProblem) {
                   summary = "Bad user input";
               } else if (problemDetails.getStatus() >= 400 && problemDetails.getStatus() < 500) {
                   summary = "Bad API request";
               } else {
                   summary = "Server error";
               }
   
               throw new RuntimeException(summary, e);
   ```

1. Execute the `ExampleApp.main()` method.
   You'll see the following message:

   ```
   Exception in thread "main" java.lang.RuntimeException: Bad user input
   ```

   We now know immediately who this message is meant for, but we still need to know why it occurred.
   To do that, let's make use of the `errors` field of the RFC 7807 response.

1. Still in the `try` block, replace `throw new RuntimeException(summary, e);` with the following code:

   ```java
               String errorMessage = summary;
               if (problemDetails.getErrors() != null && !problemDetails.getErrors().isEmpty()) {
                   String reason = problemDetails.getErrors().stream().map(RFC7807ErrorDescription::getMessage).filter(Objects::nonNull).collect(Collectors.joining(", "));
                   if (reason.isEmpty()) {
                       reason = problemDetails.getDetail();
                   }
                   if (reason.isEmpty()) {
                       reason = problemDetails.getTitle();
                   }
                   errorMessage = errorMessage.concat(": ").concat(reason);
               }

               throw new RuntimeException(errorMessage, e);
   ```

1. Execute the `main()` method again, and you'll see the following message:

   ```
   Exception in thread "main" java.lang.RuntimeException: Bad user input: Place the OR operator between two search terms, or remove it., Place the AND operator between two search terms, or remove it.
   ```

Now you can easily identify the cause and the nature of the error from the message. Let's try out the same code with the expression that caused the developer error.

1. In the `main()` method in `ExampleApp`, replace the argument to the `recordTextSearch()` method with `null`.
   When you execute the `main()` method again, you'll see the following message and know that it was caused by a bad API request:

   ```
   Exception in thread "main" java.lang.RuntimeException: Bad API request: 'expression' must contain non-whitespace characters.
   ```

   In truth, the code should be preventing requests like these from ever reaching the endpoint.
   `null` or even blank (`""`) search expressions make no sense. We can do something about that.

1. Add this code at the beginning of the `recordTextSearch()` method in the `ExampleApp` class:

   ```java
   if (expression == null || expression.trim().isEmpty()) {
       throw new IllegalArgumentException("Search expression must be specified");
   }
   ```

1. Execute the `ExampleApp` `main()` method, and you'll see the new message:

   ```
   Exception in thread "main" java.lang.IllegalArgumentException: Search expression must be specified
   ```

When you're happy with the error handling, reset the argument to the `recordTextSearch()` method to `"(Gene Hendricks) OR Associate"`, and we can move on to [retrieving the search results](result-paging.md).
