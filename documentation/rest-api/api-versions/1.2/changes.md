# Changelog for i2 Analyze REST API 1.2

Version 1.2 contains minor changes to the i2 Analyze REST API.

## Added

At this release, the following public endpoints are new and fully supported in the i2 Analyze REST API:

- [`admin/securityschema/reload`](https://docs.i2group.com/analyze/4.4.2/public-rest-api.html#post-/api/v1/admin/securityschema/reload)

  This administrative POST endpoint is for reloading the deployment's security schema, which includes reloading information from all security dimension values providers.

- [`admin/share/chartlets`](https://docs.i2group.com/analyze/4.4.2/public-rest-api.html#delete-/api/v1/admin/share/chartlets)

  This administrative DELETE endpoint is for deleting all chartlets in the system.

## Modified

At this release, the following public endpoints are modified in the i2 Analyze REST API:

- [`admin/config/reload`](https://docs.i2group.com/analyze/4.4.2/public-rest-api.html#post-/api/v1/admin/config/reload)

  The Visual Query configuration can now be updated without requiring a server restart.

- [`core/securityschema`](https://docs.i2group.com/analyze/4.4.2/public-rest-api.html#get-/api/v1/core/securityschema)

  The optional `resolutionMode` field was added to the `SecuritySchemaResponse` response type, while the `SecurityDimensionValue` type now contains a `suspended` field.

- [`gateway/reload`](https://docs.i2group.com/analyze/4.4.2/public-rest-api.html#post-/api/v1/gateway/reload)

  The behavior of this endpoint is subject to a number of changes:

  - The `capabilities` query parameter can be set to `VISUAL_QUERY`, to include services that support Visual Query in the response.

    **Note:** The limitation on the `capabilities` parameter in i2 Analyze 4.4.1 is corrected in i2 Analyze 4.4.2.
    The server ignores all `capabilities` values other than `SEMANTIC_SEARCH` and `VISUAL_QUERY`.

  - The client configuration for a connector service now accepts a `VISUAL_QUERY` type.

    **Note:** This type is supported for connectors that were developed against at least version 1.2 of the i2 Analyze REST SPI.
  
  - If the client configuration for the connector service is of type `VISUAL_QUERY`, the resource links for the service include the `connectorVisualQuerySchema` and `validateConnectorVisualQueryShape` fields.

## Removed

N/A
