/*
 * #%L
 * wcm.io
 * %%
 * Copyright (C) 2023 wcm.io
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package io.wcm.siteapi.integrationtestsupport;

import java.util.Collection;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import io.wcm.siteapi.integrationtestsupport.httpclient.HttpClient;
import io.wcm.siteapi.openapi.validator.OpenApiSchemaValidator;
import io.wcm.siteapi.openapi.validator.OpenApiSpec;
import io.wcm.siteapi.openapi.validator.OpenApiSpecVersions;

/**
 * Context for Site API integration tests. To build an instance use {@link IntegrationTestContextBuilder}.
 * An instances is always associated with a publish URL to test against, and a specific Site API OAS3 spec version
 * to validate the JSON content against.
 */
public final class IntegrationTestContext {

  private final String publishUrl;
  private final String selector;
  private final String apiVersion;
  private final String extension;
  private final OpenApiSpecVersions specVersions;
  private final HttpClient httpClient;

  IntegrationTestContext(IntegrationTestContextBuilder builder) {
    this.publishUrl = builder.getPublishUrl();
    this.selector = builder.getSelector();
    this.apiVersion = builder.getApiVersion();
    this.extension = builder.getExtension();
    this.specVersions = builder.getSpecVersions();
    this.httpClient = new HttpClient(builder);
  }

  /**
   * Build full Site API URL.
   * @param path Content path
   * @param suffix Suffix
   * @return Site API URL
   */
  public String buildSiteApiUrl(@NotNull String path, @NotNull String suffix) {
    StringBuilder result = new StringBuilder();
    result.append(publishUrl)
        .append(path)
        .append(".").append(selector);
    if (!StringUtils.isEmpty(apiVersion)) {
      result.append(".").append(apiVersion);
    }
    result.append(".").append(extension).append("/").append(suffix).append(".json");
    return result.toString();
  }

  /**
   * @return Simple HTTP client for integration tests.
   */
  public @NotNull HttpClient getHttpClient() {
    return this.httpClient;
  }

  /**
   * @return Get all available API versions.
   */
  public @NotNull Collection<String> getAllApiVersions() {
    return specVersions.getAllVersions();
  }

  /**
   * Get OAS3 schema validator for given processor/suffix.
   * @param suffix Suffix e.g. "content", "navigation"
   * @return Validator
   */
  public @NotNull OpenApiSchemaValidator getValidator(@NotNull String suffix) {
    OpenApiSpec spec = specVersions.get(apiVersion);
    return spec.getSchemaValidator(suffix);
  }

  /**
   * @return Publish URL (without trailing /)
   */
  public @NotNull String getPublishUrl() {
    return this.publishUrl;
  }

  /**
   * @return Site API Selector
   */
  public @NotNull String getSelector() {
    return this.selector;
  }

  /**
   * @return Site API version or empty string
   */
  public @NotNull String getApiVersion() {
    return this.apiVersion;
  }

  /**
   * @return Site API Extension
   */
  public @NotNull String getExtension() {
    return this.extension;
  }

}
