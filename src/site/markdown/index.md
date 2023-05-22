## About Site API Integration Test Support

Run Cloud Manager integration tests for Site API.

[![Maven Central](https://img.shields.io/maven-central/v/io.wcm/io.wcm.site-api.integration-test-support)](https://repo1.maven.org/maven2/io/wcm/io.wcm.site-api.integration-test-support/)


### Documentation

* [Usage][usage]
* [API documentation][apidocs]
* [Changelog][changelog]


### Overview

* Support module for AEMaaCS integration tests (Custom Functional Tests in Adobe Cloud Manager) and AEM projects based on [Site API][site-api].
* Provides a generic crawler that crawls the whole site content represented as JSON serialization with hyperlinks
* Validates each JSON response against the OAS3 schema using the [Site API Open API Validator][site-api-openapi-validator]
* Provides a simple set of [LinkExtractor][link-extractor] implementations and the capability to implement custom ones to detect JSON hyperlinks within any project-specific JSON schema


### GitHub Repository

Sources: https://github.com/wcm-io/io.wcm.site-api.integration-test-support


[usage]: usage.html
[apidocs]: apidocs/
[changelog]: changes-report.html
[site-api]: https://wcm.io/site-api
[site-api-openapi-validator]: https://wcm.io/site-api/openapi-validator/
[link-extractor]: apidocs/io/wcm/siteapi/integrationtestsupport/linkextractor/LinkExtractor.html
