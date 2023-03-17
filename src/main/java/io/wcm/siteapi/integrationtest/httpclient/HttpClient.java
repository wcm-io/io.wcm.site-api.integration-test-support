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
package io.wcm.siteapi.integrationtest.httpclient;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import io.wcm.siteapi.integrationtest.IntegrationTestContextBuilder;

/**
 * Context for Site API integration tests.
 */
public final class HttpClient {

  private final java.net.http.HttpClient httpClient;
  private final Duration requestTimeout;

  /**
   * @param builder Integration test context builder.
   */
  public HttpClient(IntegrationTestContextBuilder builder) {
    this.httpClient = java.net.http.HttpClient.newBuilder()
        // stick with HTTP 1.1 for AEMaaCS CM integration tests
        .version(Version.HTTP_1_1)
        .followRedirects(Redirect.NORMAL)
        .connectTimeout(builder.getHttpConnectTimeout())
        .build();
    this.requestTimeout = builder.getHttpRequestTimeout();
  }

  /**
   * Fetch HTTP content. Check status code of response for success.
   * @param url URL
   * @return HTTP response.
   */
  @SuppressWarnings("CQRules:CWE-676")
  public @NotNull HttpResponse<String> get(@NotNull String url) {
    String urlWithTimestamp = appendTimestamp(url);
    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create(urlWithTimestamp))
        .timeout(requestTimeout)
        .build();
    try {
      return new StringHttpResponse(httpClient.send(request, BodyHandlers.ofString()));
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
  public @NotNull String getBody(@NotNull String url) {
    String urlWithTimestamp = appendTimestamp(url);
    HttpResponse<String> response = get(urlWithTimestamp);
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

}
