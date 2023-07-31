# Changelog for i2 Analyze REST API 1.0

Version 1.0 is the first public release of the i2 Analyze REST API.
As such, all endpoints and schemas are new at this version of the API.

## Added

At this release, both public and experimental endpoints were added to the REST API.

### Public endpoints

The following endpoints are new and fully supported at this version of the i2 Analyze REST API:

- A version endpoint, for [version negotiation](../../version-negotiation-details.md)

- A range of administrative endpoints:

  - For managing the behavior of privacy agreements
  - For reloading the server configuration after a change
  - For enabling and disabling server metrics
  - For retrieving highlight query configuration information
  - For determining the status of the Solr indexes

- An endpoint for reloading configuration settings for the i2 Connect gateway and any connectors

  **Note:** This endpoint requires the caller to have the `i2:Administrator` command access control permission.

- Endpoints for determining the health of the server, and for retrieving server metrics

- An endpoint for sending alert notifications to users

- Other supporting endpoints

### Experimental endpoints

The following endpoints are new and _experimental_ at this version of the i2 Analyze REST API:

- A text search endpoint, for finding records in the Information Store according to their text contents

- A geospatial search endpoint, for finding records in the Information Store according to the location data that they contain

- A "record groups" endpoint, for retrieving records that were found by either of the search endpoints

For an explanation of what "experimental" means in this context, see our [API support policy](../../api-support-policy#support-for-experimental-endpoints).

## Modified

N/A

## Removed

N/A
