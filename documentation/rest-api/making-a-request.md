# Making a request with the generated client

The [generated client code](generating-client-code.md) contains most of the logic for making requests to an i2 Analyze server.
However, we still need to provide some configuration settings, as well as the code that drives the requests.

This part of the walkthrough demonstrates how to establish a secure connection to the server and make a simple request to an endpoint that requires no parameters and no authentication.

## Configuring the client

The generated client includes a class named `ApiClient` that contains generic code for interacting with a server that supports the API in the specification.
To interact with our particular i2 Analyze server, you can extend that class to customize its functionality.

1. Create a directory named `myclient` at the same level as the `generated` directory in `src/main/java`. (`src/main/java/com/example/myclient`).

1. Inside the `myclient` directory, create a file named `MyApiClient.java`, and populate it with this code:

   ```java
   package com.example.myclient;
   
   import com.example.generated.invoker.*;
   import com.fasterxml.jackson.core.type.TypeReference;
   import org.apache.http.client.CookieStore;
   import org.apache.http.impl.client.*;
   
   import java.net.URI;
   import java.net.URISyntaxException;
   import java.util.*;
   
   public class MyApiClient extends ApiClient {
       public MyApiClient(String basePath, String username, String password) {
           super.setBasePath(basePath);
       }
   }
   ```

   Here, the `MyApiClient` constructor enables us to override the default `basePath` in the generated code (`http://localhost/opal`) with the path to the server that we want to connect to.
   We'll deal with the `username` and `password` parameters later.

   **Note:** Code that you add in later steps depends on the `import`s at the top of this new file.

The other class that you need to create contains the entry point for your client application.
We'll call this class `ExampleApp`.

1. In the same `myclient` directory, create a file named `ExampleApp.java`, and populate it with the following code:

   ```java
   package com.example.myclient;
   
   import com.example.generated.api.*;
   import com.example.generated.invoker.*;
   import com.example.generated.model.*;
   import com.fasterxml.jackson.core.JsonProcessingException;
   import com.fasterxml.jackson.databind.ObjectMapper;
   
   import java.util.*;
   import java.util.function.*;
   import java.util.stream.*;
   
   public class ExampleApp {
       private static final String SERVER_URL = "https://i2analyze.eia:9443/opal";
   
       public static void main(String[] args) throws ApiException {
           final MyApiClient myApiClient = new MyApiClient(SERVER_URL, "Jenny", "Jenny");
   
           System.out.println("Success!");
       }
   }
   ```

   This code creates an instance of the `MyApiClient` class and provides it with the location of the i2 Analyze server and the credentials of a user that has access to all of the APIs and data in the system.
   (These values are correct for the development server that you deployed in [Setting up a development environment](setting-up-dev-environment.md).
   A different server will require different values.)

1. Inspect the `main()` method in VS Code. 
   As the entry point for a Java application, the IDE displays **Run** and **Debug** buttons above it.

1. Click the **Run** button.
   The application runs and outputs the `Success!` message to the console.

This early success is welcome, but your application has not yet attempted to communicate with the i2 Analyze server.
Let's see what happens when you do that.

## Making a request to a server with HTTPS

Even to make an unauthenticated request, the client application must trust the server that it connects to.
We'll need to set up a trust chain between the client and the server, but you can start by making the request and seeing what happens if you don't.

The first step is to add a call to the `GET /api/v1/health/live` endpoint.

**Note:** Most of the endpoints in the i2 Analyze REST API require the caller to be authenticated.
The `live` endpoint is the exception, because it just indicates whether the server is available.
(It is primarily intended for use by load balancers.)

1. In the `main()` method, after the instantiation of `myApiClient`, insert the following code to call the `live` endpoint:

   ```java
           new HealthApi(myApiClient).live();
   ```

   Let's break that call down to understand how it works:

   - The OpenAPI specification contains _tags_ that group related endpoints together.
   
   - The code generator groups methods together based on their tags, and creates classes named `<TagName>Api` for them.
   
   - The `/api/v1/health/live` endpoint is tagged with `"Health"`, and so the generator created the `HealthApi` class.

   - The call to `new HealthApi(myApiClient)` creates an instance of the class.
     The constructor accepts an `ApiClient` object as a parameter.
     By passing `myApiClient`, we arrange for requests to the server to use the configuration that we set up.

   - Each endpoint in the OpenAPI specification has a unique `operationId`, which the generator uses for the names of the Java methods that it creates.

   - The `live` endpoint's `operationId` is simply `live`. Its `GET` method has no parameters, and so the method call is just `live()`.

1. Save and recompile the project, and then use VS Code to execute the `main()` method of `ExampleApp` again.
   This time, the code produces an exception:

   ```
   Caused by: com.example.generated.invoker.ApiException: javax.net.ssl.SSLHandshakeException: PKIX path building failed: sun.security.provider.certpath.SunCertPathBuilderException: unable to find valid certification path to requested target.
   ```

   **Note:** If, instead of the message above, you see the following error:

   ```
   Caused by: com.example.generated.invoker.ApiException: org.apache.http.conn.HttpHostConnectException: Connect to i2analyze.eia:9443 [i2analyze.eia/127.0.0.1] failed: Connection refused: connect
   ```

   Then the server you are attempting to connect to is not running or otherwise not accessible.
   Ensure that you specified the hostname and port correctly, and that the server is definitely running.

The connection fails because we've specified a URL using HTTPS, but Java does not use the operating system's certificate store for negotiating SSL connections.
It cannot verify the authenticity of the server's certificate.

Even if you can succesfully access the i2 Analyze server from Analyst's Notebook Premium or your browser, you need to establish trust _from Java_ to communicate with it securely in this context.
Java provides a default trust store for CA certificates named `cacerts` that you can add certificates to.

1. Locate the CA certificate for your server.
   In the i2 Analyze Containers deployment, it is in [`analyze-containers/dev-environment-secrets/generated-secrets/certificates/externalCA/CA.cer`](https://i2group.github.io/analyze-containers/content/deploy_config_dev.html#installing-the-certificate)

1. Add your server's CA certificate to the `cacerts` store by running the following command, replacing `<PATH_CA_CERT>` with the path to your certificate.
   The command requires elevated privileges:

   - On Windows, launch a command prompt as an Administrator.
   - On Linux or macOS, prefix the command with `sudo`.

   ```
   keytool -cacerts -importcert -noprompt -alias i2a_ca -file <PATH_TO_CA_CERT>\CA.cer
   ```
 
   If you get an error with "Access is denied" or "Permission denied", then you did not run the command with elevated privileges.

1. When the command prompts you for a password, the default password for the `cacerts` store is `changeit`.

   **Note:** You can change the password using the command `keytool -cacerts -storepasswd`.
   You can also delete the certificate from the store with the command `keytool -cacerts -delete -noprompt -alias i2a_ca` - but don't do that now!

1. Once again, execute the `main()` method of `ExampleApp`.
   This time, the application connects to the server and receives a response from the `live` endpoint.
   The `Success!` message appears on the command line.

   **Note:** If you continue to see the `SSLHandshakeException`, your IDE might be using a different copy of Java from the one that you configured with `keytool`.
   Check the VS Code settings to find out which Java it's using.

At this point, you have a Java client that can call the least interesting method in the i2 Analyze REST API!
To do more, the application must be able to authenticate on behalf of a user.
Logging in to the server is the [next step](logging-in.md) in this walkthrough.
