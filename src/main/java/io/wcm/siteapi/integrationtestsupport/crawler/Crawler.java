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
package io.wcm.siteapi.integrationtestsupport.crawler;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.wcm.siteapi.integrationtestsupport.IntegrationTestContext;
import io.wcm.siteapi.integrationtestsupport.linkextractor.LinkExtractor;

/**
 * Generic Site API JSON content crawler.
 */
public final class Crawler {

  private final IntegrationTestContext context;
  private final Collection<LinkExtractor> linkExtractors;
  private final Set<String> visitedUrls = new HashSet<>();
  private final Set<String> failedUrls = new LinkedHashSet<>();
  private final UrlParser urlParser;

  private static final Logger log = LoggerFactory.getLogger(Crawler.class.getSimpleName());

  /**
   * @param context Integration test context
   * @param linkExtractors Link extractors to use for crawling links detected in JSON content.
   */
  public Crawler(@NotNull IntegrationTestContext context,
      @NotNull List<LinkExtractor> linkExtractors) {
    this.context = context;
    this.linkExtractors = Collections.unmodifiableCollection(linkExtractors);
    this.urlParser = new UrlParser(context.getPublishUrl(),
        context.getSelector(), context.getApiVersion(), context.getExtension());
  }

  /**
   * Start API crawling.
   * @param url API index URL to start crawling at.
   */
  public void start(@NotNull String url) {
    CrawlerItem item = new CrawlerItem(this, context, url);
    item.fetch();
  }

  /**
   * @return Total number of visited URLs.
   */
  public int numberOfVisits() {
    return visitedUrls.size();
  }

  /**
   * @return Number of failed visits.
   */
  public int numberOfFailedVisits() {
    return failedUrls.size();
  }

  /**
   * @return URLs of failed visits.
   */
  public @NotNull Collection<String> failedVisitUrls() {
    return Collections.unmodifiableCollection(failedUrls);
  }

  /**
   * Is called when a URL should be visited.
   * @param url Url to visit
   * @return true if the page was not already visited
   */
  boolean visitUrl(@NotNull String url) {
    boolean doVisit = visitedUrls.add(url);
    if (doVisit) {
      log.info("Visit: {}", url);
    }
    else {
      log.debug("Skip: {}", url);
    }
    return doVisit;
  }

  /**
   * Log a failed visit.
   * @param url url
   * @param message Message
   * @param fullResponse Full response
   */
  void logFailedVisitUrl(@NotNull String url, @NotNull String message, @NotNull String fullResponse) {
    log.error("Validation FAILED: {}\n{}\n\n{}\n", url, message, fullResponse);
    failedUrls.add(url);
  }

  @NotNull
  Collection<LinkExtractor> getLinkExtractors() {
    return this.linkExtractors;
  }

  @NotNull
  String parseSuffix(@NotNull String url) {
    return urlParser.parseSuffix(url);
  }

}
