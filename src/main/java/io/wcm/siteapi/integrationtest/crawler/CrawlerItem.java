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
package io.wcm.siteapi.integrationtest.crawler;

import java.util.stream.Stream;

import org.jetbrains.annotations.NotNull;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

import io.wcm.siteapi.integrationtest.HttpRequestFailedException;
import io.wcm.siteapi.integrationtest.IntegrationTestContext;
import io.wcm.siteapi.openapi.validator.ContentValidationException;
import io.wcm.siteapi.openapi.validator.OpenApiSchemaValidator;

/**
 * Validates the JSON response of a single URL.
 */
class CrawlerItem {

  private final Crawler crawler;
  private final IntegrationTestContext context;
  private final String url;
  private final String refererUrl;

  CrawlerItem(Crawler crawler, IntegrationTestContext context, String url) {
    this(crawler, context, url, null);
  }

  CrawlerItem(Crawler crawler, IntegrationTestContext context, String url, String refererUrl) {
    this.crawler = crawler;
    this.context = context;
    this.url = url;
    this.refererUrl = refererUrl;
  }

  void fetch() {
    // skip processing if page was already crawled
    if (!crawler.visitUrl(url)) {
      return;
    }

    // parse and validate URL
    String suffix;
    try {
      suffix = crawler.parseSuffix(url);
    }
    catch (IllegalArgumentException ex) {
      crawler.logFailedVisitUrl(url, appendReferer(ex.getMessage()), "");
      return;
    }

    // load JSON from URL
    OpenApiSchemaValidator validator = context.getValidator(suffix);
    String json;
    try {
      json = context.fetchBody(url);
    }
    catch (HttpRequestFailedException ex) {
      crawler.logFailedVisitUrl(url, appendReferer(ex.getMessage()), "");
      return;
    }

    // validate JSON against OAS3 spec
    try {
      validator.validate(json);
    }
    catch (ContentValidationException ex) {
      crawler.logFailedVisitUrl(url, "Validator(" + context.getApiVersion() + "," + suffix + ") " + ex.getMessage(), json);
      return;
    }

    // continue crawling with all link URLs found
    getAllLinks(JsonPath.parse(json), suffix)
        .forEach(followUrl -> new CrawlerItem(crawler, context, followUrl, url).fetch());
  }

  private Stream<String> getAllLinks(@NotNull DocumentContext jsonPathContext, @NotNull String suffix) {
    return crawler.getLinkExtractors().stream()
        .filter(extractor -> extractor.accept(suffix))
        .flatMap(extractor -> extractor.getLinks(jsonPathContext))
        .distinct();
  }

  private String appendReferer(String message) {
    if (refererUrl != null) {
      return message + ", refererer: " + refererUrl;
    }
    return message;
  }

}
