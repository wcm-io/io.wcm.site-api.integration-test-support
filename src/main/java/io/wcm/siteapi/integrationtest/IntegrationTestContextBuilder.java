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

import java.time.Duration;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import io.wcm.siteapi.openapi.validator.OpenApiSpecVersions;

/**
 * Integration test context builder.
 */
public final class IntegrationTestContextBuilder {

  private String publishUrl;
  private String selector = "site";
  private String apiVersion = "";
  private String extension = "api";
  private OpenApiSpecVersions specVersions;
  private Duration httpConnectTimeout = Duration.ofSeconds(20);
  private Duration httpRequestTimeout = Duration.ofSeconds(60);

  /**
   * @return Publish URL
   */
  public @Nullable String getPublishUrl() {
    return this.publishUrl;
  }

  /**
   * @param value Publish URL
   * @return this
   */
  public @NotNull IntegrationTestContextBuilder publishUrl(@NotNull String value) {
    // strip "/" from end of publish URL if present
    this.publishUrl = StringUtils.removeEnd(value, "/");
    return this;
  }

  /**
   * @return Site API selector
   */
  public @NotNull String getSelector() {
    return this.selector;
  }

  /**
   * @param value Site API selector
   * @return this
   */
  public @NotNull IntegrationTestContextBuilder selector(@NotNull String value) {
    this.selector = value;
    return this;
  }

  /**
   * @return Site API version or empty string
   */
  public String getApiVersion() {
    return this.apiVersion;
  }

  /**
   * @param value Site API version or empty string
   * @return this
   */
  public @NotNull IntegrationTestContextBuilder apiVersion(@NotNull String value) {
    this.apiVersion = value;
    return this;
  }

  /**
   * @return Site API extension
   */
  public @NotNull String getExtension() {
    return this.extension;
  }

  /**
   * @param value Site API extension
   * @return this
   */
  public @NotNull IntegrationTestContextBuilder extension(@NotNull String value) {
    this.extension = value;
    return this;
  }

  /**
   * @return Open API Spec versions to be used for validation.
   */
  public @NotNull OpenApiSpecVersions getSpecVersions() {
    return this.specVersions;
  }

  /**
   * @param value Open API Spec versions to be used for validation.
   * @return this
   */
  public @NotNull IntegrationTestContextBuilder specVersions(@NotNull OpenApiSpecVersions value) {
    this.specVersions = value;
    return this;
  }

  /**
   * @return HTTP connect timeout
   */
  public @NotNull Duration getHttpConnectTimeout() {
    return this.httpConnectTimeout;
  }

  /**
   * @param value HTTP connect timeout
   * @return this
   */
  public @NotNull IntegrationTestContextBuilder httpConnectTimeout(@NotNull Duration value) {
    this.httpConnectTimeout = value;
    return this;
  }

  /**
   * @return HTTP request timeout
   */
  public @NotNull Duration getHttpRequestTimeout() {
    return this.httpRequestTimeout;
  }

  /**
   * @param value HTTP request timeout
   * @return this
   */
  public @NotNull IntegrationTestContextBuilder httpRequestTimeout(@NotNull Duration value) {
    this.httpRequestTimeout = value;
    return this;
  }

  /**
   * @return Integration test context
   */
  public @NotNull IntegrationTestContext build() {
    if (this.publishUrl == null) {
      throw new IllegalArgumentException("No publish URL given.");
    }
    if (this.specVersions == null) {
      this.specVersions = new OpenApiSpecVersions();
    }
    return new IntegrationTestContext(this);
  }

}
