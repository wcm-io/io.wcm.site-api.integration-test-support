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

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonArrayBuilder;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;

import io.wcm.siteapi.integrationtest.IntegrationTestContext;
import io.wcm.siteapi.integrationtest.IntegrationTestContextBuilder;
import io.wcm.siteapi.integrationtest.linkextractor.ContentInternalLinks;
import io.wcm.siteapi.integrationtest.linkextractor.IndexLinks;
import io.wcm.siteapi.integrationtest.linkextractor.LinkExtractor;

@WireMockTest
class CrawlerTest {

  private static final String ROOT_PATH = "/content/en";
  private static final String PAGE2_PATH = "/content/en/page2";
  private static final String INDEX_PATH = ROOT_PATH + ".site.v1.api/index.json";
  private static final String NAVIGATION_PATH = ROOT_PATH + ".site.v1.api/navigation.json";
  private static final String CONTENT_ROOT_PATH = ROOT_PATH + ".site.v1.api/content.json";
  private static final String CONTENT_PAGE2_PATH = PAGE2_PATH + ".site.v1.api/content.json";

  IntegrationTestContext context;
  Crawler underTest;

  @BeforeEach
  void setUp(WireMockRuntimeInfo wm) throws Exception {
    context = new IntegrationTestContextBuilder()
        .publishUrl(wm.getHttpBaseUrl())
        .apiVersion("v1")
        .httpConnectTimeout(Duration.ofMillis(2000))
        .httpRequestTimeout(Duration.ofMillis(2000))
        .build();
    underTest = new Crawler(context, List.<LinkExtractor>of(
        new IndexLinks(),
        new ContentInternalLinks()));

    // prepare valid JSON responses for index, navigation and 2 content pages
    stubFor(get(urlPathEqualTo(INDEX_PATH)).willReturn(aResponse()
        .withBody(buildIndexJson(Map.of(
            "content", context.getPublishUrl() + CONTENT_ROOT_PATH,
            "navigation", context.getPublishUrl() + NAVIGATION_PATH
        )))));
    stubFor(get(urlPathEqualTo(NAVIGATION_PATH)).willReturn(aResponse()
        .withBody(buildNavigationJson(
            ROOT_PATH, context.getPublishUrl() + CONTENT_ROOT_PATH,
            PAGE2_PATH, context.getPublishUrl() + CONTENT_PAGE2_PATH))));
    stubFor(get(urlPathEqualTo(CONTENT_ROOT_PATH)).willReturn(aResponse()
        .withBody(buildContent())));
    stubFor(get(urlPathEqualTo(CONTENT_PAGE2_PATH)).willReturn(aResponse()
        .withBody(buildContent())));
  }

  @Test
  void testCrawl_Successful() {
    underTest.start(context.buildSiteApiUrl(ROOT_PATH, "index"));

    assertEquals(4, underTest.numberOfVisits(), "number of visits");
    assertEquals(0, underTest.numberOfFailedVisits(), "number of failed visits");
    assertTrue(underTest.failedVisitUrls().isEmpty());
  }

  @Test
  void testCrawl_OnePageInvalid() {
    stubFor(get(urlPathEqualTo(CONTENT_PAGE2_PATH)).willReturn(aResponse()
        .withBody("{}")));

    underTest.start(context.buildSiteApiUrl(ROOT_PATH, "index"));

    assertEquals(4, underTest.numberOfVisits(), "number of visits");
    assertEquals(1, underTest.numberOfFailedVisits(), "number of failed visits");
    assertEquals(List.of(context.getPublishUrl() + CONTENT_PAGE2_PATH),
        List.copyOf(underTest.failedVisitUrls()));
  }

  @Test
  void testCrawl_InvalidUrl() {
    stubFor(get(urlPathEqualTo(NAVIGATION_PATH)).willReturn(aResponse()
        .withBody(buildNavigationJson(
            ROOT_PATH, context.getPublishUrl() + CONTENT_ROOT_PATH,
            PAGE2_PATH, context.getPublishUrl() + "/invalid.json"))));

    underTest.start(context.buildSiteApiUrl(ROOT_PATH, "index"));

    assertEquals(4, underTest.numberOfVisits(), "number of visits");
    assertEquals(1, underTest.numberOfFailedVisits(), "number of failed visits");
    assertEquals(List.of(context.getPublishUrl() + "/invalid.json"),
        List.copyOf(underTest.failedVisitUrls()));
  }

  @Test
  void testCrawl_FetchFail() {
    stubFor(get(urlPathEqualTo(NAVIGATION_PATH)).willReturn(aResponse()
        .withStatus(404)));

    underTest.start(context.buildSiteApiUrl(ROOT_PATH, "index"));

    assertEquals(3, underTest.numberOfVisits(), "number of visits");
    assertEquals(1, underTest.numberOfFailedVisits(), "number of failed visits");
    assertEquals(List.of(context.getPublishUrl() + NAVIGATION_PATH),
        List.copyOf(underTest.failedVisitUrls()));
  }

  private String buildIndexJson(Map<String, String> suffixUrls) {
    JsonArrayBuilder array = Json.createArrayBuilder();
    suffixUrls.entrySet().forEach(entry -> {
      array.add(Json.createObjectBuilder()
          .add("suffix", entry.getKey())
          .add("url", entry.getValue()));
    });
    return array.build().toString();
  }

  private String buildNavigationJson(String rootPath, String rootUrl, String childPath, String childUrl) {
    return Json.createObjectBuilder()
        .add("title", StringUtils.substringAfterLast(rootPath, "/"))
        .add("link", Json.createObjectBuilder()
            .add("path", rootPath)
            .add("type", "internal")
            .add("url", rootUrl))
        .add("children", Json.createArrayBuilder().add(Json.createObjectBuilder()
            .add("title", StringUtils.substringAfterLast(childPath, "/"))
            .add("link", Json.createObjectBuilder()
                .add("path", childPath)
                .add("type", "internal")
                .add("url", childUrl))))
        .build().toString();
  }

  private String buildContent() {
    return Json.createObjectBuilder()
        .add(":items", Json.createObjectBuilder())
        .add(":itemsOrder", Json.createArrayBuilder())
        .add(":type", "siteapi-test/core/components/global/page")
        .build().toString();
  }

}
