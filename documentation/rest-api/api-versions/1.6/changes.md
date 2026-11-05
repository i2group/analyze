# Changelog for i2 Analyze REST API 1.6

Version 1.6 contains minor changes to the i2 Analyze REST API.

## Added

N/A

## Modified

At this release, the following public endpoints are modified in the i2 Analyze REST API:

- [`alerts`](https://docs.i2group.com/analyze/4.4.8/public-rest-api.html#post-/api/v1/alerts)

  The `sendTo` object in the request body has two new fields: `userIds` and `userPrincipalNames`.
  You can use `userIds` to send an alert to users by their unique identifiers, and `userPrincipalNames` to send an alert to users by their principal names.

  The existing `users` field is deprecated, and will be removed in a future release. Use `userPrincipalNames` instead.

## Removed

N/A
