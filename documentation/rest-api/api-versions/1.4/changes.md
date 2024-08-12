# Changelog for i2 Analyze REST API 1.4

Version 1.4 contains minor changes to the i2 Analyze REST API.

## Added

At this release, the following public endpoints are new and fully supported in the i2 Analyze REST API:

- [`admin/provisioning/reload`](https://docs.i2group.com/analyze/4.4.4/public-rest-api.html#post-/api/v1/admin/provisioning/reload)

  [User provisioning](https://docs.i2group.com/analyze/4.4.4/user_provisioning.html) can be triggered by calling this API.

## Modified

At this release, the following public endpoints are modified in the i2 Analyze REST API:

- [`core/securityschema`](https://docs.i2group.com/analyze/4.4.4/public-rest-api.html#get-/api/v1/core/securityschema)

  The response from the `securityschema` endpoint now includes a `userSecurityPermissions` field that contains the resolved security permissions for the current user. These permissions can be used to evaluate the access level that a user should have to data secured using the [i2 Analyze security model](https://docs.i2group.com/analyze/4.4.4/security_model.html), outside of i2 Analyze itself.
  
  The rules of the security model can change over time, so the response _also_ includes a `securityModelVersion` field that can be used to determine whether a change has taken place that requires security to be evaluated differently. Currently the field is set to `1.0`, but if you implement a client that evaluates data using the security model, you should react to changes in this value.

## Removed

N/A
