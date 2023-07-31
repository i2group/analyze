# The i2 Analyze REST API

i2 provides the i2 Analyze REST API as an [OpenAPI version 3](https://github.com/OAI/OpenAPI-Specification/blob/main/versions/3.0.0.md) specification document, in JSON format.
The OpenAPI specification is supported by a wide range of tools, including documentation viewers and code generators.

i2 Analyze Developer Essentials does not itself contain the REST API documentation, but the information is available both online and from i2 Analyze servers.
When you use the i2 Analyze server-hosted documentation viewer, you can also make API requests directly from the viewer.

## Viewing the REST API documentation on docs.i2group.com

The latest version of the i2 Analyze REST API documentation is available on the i2 Product Documentation website, as part of the documentation for the version of i2 Analyze that supports it.

For example, the documentation for version 1.1 of the REST API is available at [https://docs.i2group.com/analyze/4.4.1/public-rest-api.html](https://docs.i2group.com/analyze/4.4.1/public-rest-api.html).

Documentation for future versions of the REST API will be hosted in a location that corresponds to the version of i2 Analyze that the API was released with.

## Viewing the REST API documentation on an i2 Analyze server

It's also possible to view the REST API documentation without an internet connection.
You need a [deployment of i2 Analyze](setting-up-dev-environment.md) instead.

When i2 Analyze is deployed and started, you can view the documentation for the latest version of the API that the server supports by logging in and browsing to `https://<hostname>:<port>/opal/doc`.
(For example, [https://i2analyze.eia:9443/opal/doc](https://i2analyze.eia:9443/opal/doc).)

You can then use the interactive features of the viewer to execute requests against the server, allowing you to gain rapid insight into the REST API.

## API version information

New versions of the REST API are always released as part of new i2 Analyze releases.
Documentation for a particular version of the API is always available alongside the associated i2 Analyze documentation.

| API version 🡆 <br/> i2 Analyze version 🡇 | [1.0](https://docs.i2group.com/analyze/4.4.0/public-rest-api.html) | [1.1](https://docs.i2group.com/analyze/4.4.1/public-rest-api.html) | [1.2](https://docs.i2group.com/analyze/4.4.2/public-rest-api.html) |
| :-----------------------:|:--:|:--:|:--:|
| 4.4.0                    | ✔️ | ❌ | ❌ |
| 4.4.1                    | ✔️ | ✔️ | ❌ |
| 4.4.2                    | ✔️ | ✔️ | ✔️ |

### API changelog

- [1.0 changes](api-versions/1.0/changes.md)
- [1.1 changes](api-versions/1.1/changes.md)
- [1.2 changes](api-versions/1.2/changes.md)

## Developing for the REST API

i2 Analyze Developer essentials contains a walkthrough that demonstrates the process of writing a client for the API, beginning with running a code generator against the specification.
To start following the walkthrough, [set up a development environment](setting-up-dev-environment.md).
