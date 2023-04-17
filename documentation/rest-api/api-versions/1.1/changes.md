# Changelog for i2 Analyze REST API 1.1

Version 1.1 contains minor changes to the i2 Analyze REST API.

## Added

N/A

## Modified

At this release, some public endpoints were modified in the REST API.

### Public endpoints

The following endpoints were modified at this version of the i2 Analyze REST API:

- [`gateway/reload`](https://docs.i2group.com/analyze/4.4.1/public-rest-api.html#post-/api/v1/gateway/reload)

  Future versions of the API might support new types of i2 Connect connector services that require backwards-incompatible changes to the response body.
  
  To allow these changes to be made without affecting existing clients, a new `capabilities` query parameter has been added to allow clients to opt in to receiving those new entries in the response. Omitting the parameter causes the response to contain only the services that are supported by API 1.0, providing backwards compatibility by default.
  
  In this release, the parameter can be set to `SEMANTIC_SEARCH` to include new services that support semantic property type constraints.
 
  **Note:** A limitation of the i2 Analyze 4.4.1 server is that it accepts _only_ `SEMANTIC_SEARCH` as the capabilities parameter. Any other value results in a `404` response code instead of a limited response. This limitation will be corrected in subsequent releases.

- [`infostore/schema`](https://docs.i2group.com/analyze/4.4.1/public-rest-api.html#get-/api/v1/infostore/schema)

  The optional `semanticTypeLibrary` field was removed from the `Schema` response type and will not be returned by the server.

## Removed

N/A
