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
package io.wcm.siteapi.integrationtest;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.util.Collection;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import io.wcm.siteapi.openapi.validator.OpenApiSchemaValidator;
import io.wcm.siteapi.openapi.validator.OpenApiSpec;
import io.wcm.siteapi.openapi.validator.OpenApiSpecVersions;

/**
 * Context for Site API integration tests.
 */
public final class IntegrationTestContext {

  private final String publishUrl;
  private final String selector;
  private final String apiVersion;
  private final String extension;
  private final OpenApiSpecVersions specVersions;
  private final HttpClient httpClient;
  private final Duration httpRequestTimeout;

  IntegrationTestContext(IntegrationTestContextBuilder builder) {
    this.publishUrl = builder.getPublishUrl();
    this.selector = builder.getSelector();
    this.apiVersion = builder.getApiVersion();
    this.extension = builder.getExtension();
    this.specVersions = builder.getSpecVersions();
    this.httpClient = HttpClient.newBuilder()
        // stick with HTTP 1.1 for AEMaaCS CM integration tests
        .version(Version.HTTP_1_1)
        .followRedirects(Redirect.NORMAL)
        .connectTimeout(builder.getHttpConnectTimeout())
        .build();
    this.httpRequestTimeout = builder.getHttpRequestTimeout();
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
   * Fetch HTTP content. Check status code of response for success.
   * @param url URL
   * @return HTTP response.
   */
  @SuppressWarnings("CQRules:CWE-676")
  public @NotNull HttpResponse<String> fetch(@NotNull String url) {
    String urlWithTimestamp = appendTimestamp(url);
    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create(urlWithTimestamp))
        .timeout(httpRequestTimeout)
        .build();
    try {
      return httpClient.send(request, BodyHandlers.ofString());
    }
    catch (IOException ex) {
      throw new HttpRequestFailedException("Unable to fetch " + urlWithTimestamp + ": " + ex.getMessage(), ex);
    }
    catch (InterruptedException ex) {
      Thread.currentThread().interrupt();
      throw new IllegalStateException(ex);
    }
  }

  /**
   * Fetch HTTP content. Fails with exception when request does not return successfully.
   * @param url URL
   * @return Body string.
   */
  public @NotNull String fetchBody(@NotNull String url) {
    String urlWithTimestamp = appendTimestamp(url);
    HttpResponse<String> response = fetch(urlWithTimestamp);
    if (response.statusCode() == 200) {
      return response.body();
    }
    else {
      throw new HttpRequestFailedException(urlWithTimestamp + " returned HTTP " + response.statusCode());
    }
  }

  /**
   * Attach timestamp to skip CDN and dispatcher cache layers
   * @param url URL with or without timestamp parameter.
   * @return URL with timestamp parameter.
   */
  private String appendTimestamp(String url) {
    if (!StringUtils.contains(url, "?timestamp=")) {
      return url + "?timestamp=" + System.currentTimeMillis();
    }
    return url;
  }

  /**
   * @return Get all available API versions.
   */
  public Collection<String> getAllApiVersions() {
    return specVersions.getAllVersions();
  }

  /**
   * Get OAS3 schema validator for given processor/suffix.
   * @param suffix Suffix e.g. "content", "navigation"
   * @return Validator
   */
  public OpenApiSchemaValidator getValidator(@NotNull String suffix) {
    OpenApiSpec spec = specVersions.get(apiVersion);
    return spec.getSchemaValidator(suffix);
  }

  /**
   * @return Publish URL (without trailing /)
   */
  public String getPublishUrl() {
    return this.publishUrl;
  }

  /**
   * @return Site API Selector
   */
  public String getSelector() {
    return this.selector;
  }

  /**
   * @return Site API version
   */
  public String getApiVersion() {
    return this.apiVersion;
  }

  /**
   * @return Site API Extension
   */
  public String getExtension() {
    return this.extension;
  }

}
