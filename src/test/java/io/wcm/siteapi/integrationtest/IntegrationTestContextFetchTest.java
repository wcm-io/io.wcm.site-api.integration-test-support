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

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.matching;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.net.http.HttpResponse;
import java.time.Duration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;

@WireMockTest
class IntegrationTestContextFetchTest {

  static final String TEST_PATH = "/test1.json";
  static final String TEST_JSON = "{}";

  IntegrationTestContext underTest;
  String url;

  @BeforeEach
  void setUp(WireMockRuntimeInfo wm) {
    underTest = new IntegrationTestContextBuilder()
        .publishUrl(wm.getHttpBaseUrl())
        .httpConnectTimeout(Duration.ofMillis(2000))
        .httpRequestTimeout(Duration.ofMillis(2000))
        .build();
    url = wm.getHttpBaseUrl() + TEST_PATH;
  }

  @Test
  void testFetch() {
    stubFor(get(urlPathEqualTo(TEST_PATH)).willReturn(aResponse()
        .withBody(TEST_JSON)));

    HttpResponse<String> response = underTest.fetch(url);
    assertEquals(200, response.statusCode());
    assertEquals(TEST_JSON, response.body());

    // ensure timestamp parameter was added
    verify(getRequestedFor(urlPathEqualTo(TEST_PATH))
        .withQueryParam("timestamp", matching("\\d+")));
  }

  @Test
  void testFetch_Timeout() {
    stubFor(get(urlPathEqualTo(TEST_PATH)).willReturn(aResponse()
        .withFixedDelay(3000)
        .withStatus(404)));
    assertThrows(HttpRequestFailedException.class, () -> underTest.fetch(url));
  }

  @Test
  void testFetchBody() {
    stubFor(get(urlPathEqualTo(TEST_PATH)).willReturn(aResponse()
        .withBody(TEST_JSON)));

    assertEquals(TEST_JSON, underTest.fetchBody(url));
  }

  @Test
  void testFetchBody_NotFound() {
    stubFor(get(urlPathEqualTo(TEST_PATH)).willReturn(aResponse()
        .withStatus(404)));

    assertThrows(HttpRequestFailedException.class, () -> underTest.fetchBody(url));
  }

}
