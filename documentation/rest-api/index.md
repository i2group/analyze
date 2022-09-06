# Interacting with i2 Analyze

This part of i2 Analyze Developer Essentials introduces the i2 Analyze REST API, explains how to generate client code from the API specification, and includes examples of some common patterns for interacting with the i2 Analyze server through the REST API.

The documentation here is structured as a walkthrough that guides you through these topics.

The example in this part of the documentation illustrates the principles of using a code generator and the OpenAPI specification file to bootstrap the development of a client, and introduces some core features of the i2 Analyze REST API.
The example is _not_ a reference for all the functionality that the API provides (for that, see the [REST API documentation](https://docs.i2group.com/analyze/public-rest-api.html)), and it does not provide a fully featured client application.

In particular, the example demonstrates the small amount of code you need to write in order to make requests to the server and be able to:

- authenticate with a standard i2 Analyze deployment
- ensure that the server provides the APIs you require
- handle errors from requests
- search for and retrieve record data

The example is implemented using Java, but the general principles apply across all languages.

## Getting started

1. [The i2 Analyze REST API](api-reference.md)

1. [Setting up a development environment](setting-up-dev-environment.md)

## Worked example

1. [Generating client code from the API specification](generating-client-code.md)

1. [Making a request with the generated client](making-a-request.md)

1. [Logging in to the i2 Analyze server](logging-in.md)

1. [Negotiating a common API version](version-negotiation.md)

1. [Sending alerts to users](sending-alerts.md)

1. [Searching for records by their contents](search-text.md)

1. [Handling server errors](error-handling.md)

1. [Paging search results](result-paging.md)

1. [Searching for records by their locations](search-geospatial.md)

1. [Other REST API endpoints](supporting-endpoints.md)

## Further development

1. [Auditing REST API operations](auditing.md)
