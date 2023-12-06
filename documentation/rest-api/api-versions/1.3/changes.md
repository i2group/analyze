# Changelog for i2 Analyze REST API 1.3

Version 1.3 contains minor changes to the i2 Analyze REST API.

## Added

At this release, the following public endpoints are new and fully supported in the i2 Analyze REST API:

- [`admin/indexes/config/reload`](https://docs.i2group.com/analyze/4.4.3/public-rest-api.html#post-/api/v1/admin/indexes/config/reload)

  This administrative POST endpoint is for reloading the index configuration from disk. Any changes from the existing settings are applied without requiring a server restart.

- [`admin/indexes/retryfailed`](https://docs.i2group.com/analyze/4.4.3/public-rest-api.html#post-/api/v1/admin/indexes/retryfailed)

  This administrative POST endpoint is for re-queuing batches of ingested data that failed indexing, after action has been taken to correct problems.

- [`admin/indexes/{name}/clean`](https://docs.i2group.com/analyze/4.4.3/public-rest-api.html#post-/api/v1/admin/indexes/-name-/clean)

  This administrative POST endpoint is for clearing all information from the specified index, which then repopulates automatically.

- [`admin/indexes/{name}/reindex`](https://docs.i2group.com/analyze/4.4.3/public-rest-api.html#post-/api/v1/admin/indexes/-name-/reindex)

  This administrative POST endpoint is for repopulating the specified index to reflect data source changes since a specified point in time. Existing data in the index is overwritten but not removed.

- [`admin/indexes/{name}/repopulate`](https://docs.i2group.com/analyze/4.4.3/public-rest-api.html#post-/api/v1/admin/indexes/-name-/repopulate)

  This administrative POST endpoint is for repopulating the specified index from its data source. Existing data in the index is overwritten but not removed.

## Modified

At this release, the following public endpoints are modified in the i2 Analyze REST API:

- [`admin/indexes/status`](https://docs.i2group.com/analyze/4.4.3/public-rest-api.html#get-/api/v1/admin/indexes/status)

  A `FAILED` enumeration value has been added to the `state` field of the `IndexStatusAll` response type. The value indicates that all batches were processed, but one or more batches contained invalid data.

- [`admin/indexes/{name}/status`](https://docs.i2group.com/analyze/4.4.3/public-rest-api.html#get-/api/v1/admin/indexes/-name-/status)

  A `failedBatches` field has been added to the `IndexStatus` response type, which indicates the number of ingestion batches that failed indexing because they contained invalid properties.

  A `FAILED` enumeration value has been added to the `state` field, which indicates that all batches were processed, but one or more batches contained invalid data.

- [`gateway/reload`](https://docs.i2group.com/analyze/4.4.3/public-rest-api.html#post-/api/v1/gateway/reload)

  This endpoint no longer supports the `capabilities` query parameter. Instead, information about services of all types is always included in the response.

## Removed

N/A
