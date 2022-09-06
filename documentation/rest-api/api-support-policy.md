# Support policy

This document describes our support policy for the i2 Analyze REST API, and what we mean when we change the version number of the API.

## i2 Analyze REST API version numbers

The i2 Analyze REST API follows a standard pattern for releasing new versions that is similar to [_semantic versioning_](https://semver.org/).
All documentation refers to the i2 Analyze REST API by its major and minor version numbers.
In addition, the major version number appears in the API itself as part of the endpoint URIs.

For example, the first release of the API is version `1.0`.
As a result:

- The paths to the endpoints in the i2 Analyze REST API contain `v1`
- Future releases of the REST API that involve small changes will result in a different minor version (`1.1`, `1.2`, and so on)
- Future releases of the REST API that involve large changes will result in a different major version, such as `2.0`

It is also possible that we will support multiple releases of the i2 Analyze REST API at different major versions simultaneously (`1.3` and `2.0`, for example).
   
Decisions about whether a change is "major" or "minor" are made according to the semantic versioning rules. Changes that are not backwards-compatible are considered to be major changes; other changes are generally considered to be minor.

## Support for public endpoints

We plan to support all public endpoints unless and until we deprecate them.
Furthermore, we never remove public endpoints from the i2 Analyze REST API until they have been marked as deprecated in at least one major or minor version.
Assuming that we intend to replace a deprecated endpoint, we aim to have a replacement available at the point of deprecation.

After a public endpoint has been marked as deprecated, we _might_ remove it, provided that certain conditions are met. Consider the following sequence of events, in which "v1" and "v2" are major version of the API:

1. v2 of the REST API is released. It contains most of the endpoints that were in v1, plus some new ones.

2. Simultaneously, a minor version of the v1 API is released. Endpoints that don't appear in the v2 API are marked as deprecated, and descriptions of alternatives are added.

3. Six months pass, _or_ two releases of i2 Analyze take place (whichever is longer)

Only at this point do the deprecated endpoints (and the version of the API that contains them) fall out of support and become eligible for removal.

If six months pass and there has been only one release of i2 Analyze, then support for deprecated endpoints continues until the next release.
Equally, the deprecated endpoints remain supported if two releases of i2 Analyze take place within the six-month window.

For more detailed information about our support for public endpoints, [contact the i2 Support Team](https://support.i2group.com/s/support-statement).

## Support for experimental endpoints

**Important:** Do not use experimental endpoints in production code.
i2 reserves the right to withdraw support for them without notice.

Experimental endpoints can be subject to breaking changes in any major or minor release of the i2 Analyze REST API. However, i2 will make best efforts to communicate such changes before they happen, or as soon as possible afterward.

Experimental endpoints are clearly marked as such in the OpenAPI specification and the REST API documentation.
To use an experimental endpoint, you must "opt in" by providing a special parameter in the request header.

For more information, see [Using experimental endpoints](search-text.md#using-experimental-endpoints).
