# Sending alerts to users

At this point in the walkthrough, you have a client application that can log in to an i2 Analyze server and confirm that it supports a compatible version of the REST API.
We're ready to make the server do something a bit more interesting.

i2 Analyze actually provides three ways of [sending alerts to users](https://docs.i2group.com/analyze/third-party-alerting.html).
You can run stored procedures in the database that contains the Information Store (or the Chart Store); you can use the native Java API; or you can use the REST API.

## Implementing alerts

The i2 Analyze REST API includes an `alerts` endpoint that enables you to create alerts and send them to users.
For detailed information about it, see the [REST API documentation](https://docs.i2group.com/analyze/public-rest-api.html#post-/api/v1/alerts).

In this part of the walkthrough, we'll add code to the client application that uses the `alerts` endpoint to create a message alert and send it to a range of recipients.

1. In `ExampleApp`, insert the following method to construct a message alert with the mandatory fields `title` and `sendTo` specified:

   ```java
   public void createAlert(String title, String message) {
       System.out.println("Creating alert...");

       final CreateAlertRequest request = new CreateAlertRequest();
       request.setTitle(title);
       request.setMessage(message);

       final AlertSendTo sendTo = new AlertSendTo();
       sendTo.setAllUsers(true);
       request.setSendTo(sendTo);

       final AlertsApi alertsApi = new AlertsApi(apiClient);
       final CreateAlertResponse response = alertsApi.createAlert(request);
       System.out.println("Alert sent to " + response.getSentToCount() + " users");
   }
   ```

   The `alerts` endpoint has the `"Alerts"` tag in the API specification, and so the generated class is named `AlertsApi`, as you've seen for other tags.
   The `operationId` of the POST method is `createAlert`, which becomes the name of the Java method.

   Calling the generated `AlertSendTo.setAllUsers()` method has the same effect as setting the `allUsers` field in the REST API: the alert is sent to any user who has logged into the system.

1. If you try to compile the example at this point, it won't work.
   `AlertsApi.createAlert()` can throw an `ApiException` that the code needs to handle.

   Add the following code to `ExampleApp`:

   ```java
   @FunctionalInterface
   private interface IApiRequest<T> {
       T execute() throws ApiException;
   }

   private <T> T executeRequest(IApiRequest<T> apiRequest) {
       try {
           return apiRequest.execute();
       } catch (ApiException e) {
           throw exceptionHandler(e);
       }
   }
   ```
   
   These lines create a common method that can execute requests and handle any `ApiException`s they throw by delegating to the `exceptionHandler()` method that you added in the previous topic.

   **Note:** We _could_ use this new method in `checkApiVersionIsSupported()`, but since that method has some specialized handling for `404` and `410` status codes that doesn't apply to other APIs, we'll leave it as it is.

1. Now, you can wrap the call to `AlertsApi.createAlert()` in a call to `executeRequest()`.
   In `createAlert()`, replace this line:

   ```java
       final CreateAlertResponse response = alertsApi.createAlert(request);
   ```

   With this:

   ```java
       final CreateAlertResponse response = executeRequest(() -> alertsApi.createAlert(request));
   ```

1. In the `ExampleApp.main()` method, after the call to construct `ExampleApp`, insert the following code:

   ```java
   app.createAlert("Hello, I'm a REST API client", String.format("(name: %s, version: %s)", CLIENT_NAME, CLIENT_VERSION));
   ```

1. Execute the `ExampleApp.main()` method, and you'll see the following message:

   ```
   Alert sent to 2 users
   ```

   The alert was sent to all users that have logged into the system.
   
   As part of the i2 Analyze Containers deployment process, an administrative user named `I2AnalyzeConfigDevAdmin` was used to make requests to the server. We're making requests as `Jenny`, and so the count is 2.

   If you also logged into the server as `Imogen` or another user, then the count will be higher.

   **Note:** If you see an `Error - status: 403` message, then the user making the request does not have permission to create alerts.
   The user must have the `i2:AlertsCreate` permission, as described in the [deployment instructions](setting-up-dev-environment.md#deploying-i2-analyze).

## Viewing and changing alerts

Our Java application is now sending alerts, but what does that look like for the users that we want to receive them?
Let's take a look.

1. In Analyst's Notebook Premium, log in as `Jenny` and inspect the Alerts icon at the top-right corner of the application window.
   You'll see the alert with its message.
   
   If you don't see the alert, try logging out and logging back in again.
   (By default, the application polls for alerts every 60 seconds.)

1. Next, try to log in as `Imogen`.
   
   If this is the first time you have logged in as `Imogen`, there will be no alert.

1. To change the example again, you can modify the `AlertSendTo` object in `createAlert()`.
   Instead of sending the alert to all users, you can send it to named individuals.
   Replace `sendTo.setAllUsers(true)` with the following code:

   ```java
   final Set<String> users = Collections.singleton("Imogen");
   sendTo.setUsers(users);
   ```

1. Run the `main()` method of the `ExampleApp` class again.
   You'll see the following message:

   ```
   Alert sent to 1 users
   ```

   If you log in as `Imogen` this time, you'll see that you received the alert.
   If you log in as `Jenny`, on the other hand, you'll see that no new alert arrived.

1. Optionally, add users and groups to your i2 Analyze instance, and try different combinations of property values in the `AlertSendTo` object.

   The API provides the ability to identify the recipients of an alert by specifying lists of users, groups, and command access control permissions.
   The criteria are ORed together inclusively, so only one criterion must be met in order for a user to receive an alert.

   As you experiment with these settings, pay close attention to the console output.
   Check that the number of users being logged to the console matches the number of alerts that you expect to be created.

1. A feature of message alerts is that you can add a URL that links to an external resource.
   Add the following line to the `createAlert()` method:
   
   ```java
   request.setHref("https://github.com/i2group/analyze");
   ```
   
1. Run the `main()` method again and view the alert in Analyst's Notebook.
   When you _click_ the alert, the specified URL opens in your web browser.

1. Instead of linking to an external resource, you can create a "record alert", which links to one or more records in the Information Store. To do so, replace the call to `setHref()` with one to `setRecordIdentifiers()`, providing a list of record identifiers as the argument.
   
   If you're already familiar with retrieving record identifiers from the Record Inspector, you can try this now.
   Otherwise, you can come back later when we start to [retrieve records from search results](result-paging.md).
   
   **Note**: If any of the record identifiers that you provide on the request do not exist, then no record is returned for that identifier when the user receives the alert and clicks it.

## Summary

This walkthrough provided a quick demonstration of how to use the `alerts` REST API endpoint from your project, including how to target alerts at different users.

We also established a common pattern for executing requests with exception handling, which we'll use for future API calls.

In the next topic, we'll take a look at using the i2 Analyze REST API to perform [searches for records in the Information Store by their contents](search-text.md).
