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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class IntegrationTestContextTest {

  IntegrationTestContext underTest;

  @BeforeEach
  void setUp() {
    underTest = new IntegrationTestContextBuilder()
        .publishUrl("http://localhost:8080/")
        .selector("sel1")
        .apiVersion("v1")
        .extension("ext1")
        .build();
  }

  @Test
  void testProperties() {
    assertEquals("http://localhost:8080", underTest.getPublishUrl());
    assertEquals("sel1", underTest.getSelector());
    assertEquals("v1", underTest.getApiVersion());
    assertEquals("ext1", underTest.getExtension());
  }

  @Test
  void testGetAllApiVersions() {
    assertEquals(List.of("v1", "v2"), List.copyOf(underTest.getAllApiVersions()));
  }

  @Test
  void testGetValidator() {
    assertNotNull(underTest.getValidator("content"));
  }

  @Test
  void testBuildSiteApiUrl() {
    assertEquals("http://localhost:8080/content/page1.sel1.v1.ext1/content.json",
        underTest.buildSiteApiUrl("/content/page1", "content"));
  }

  @Test
  void testBuildSiteApiUrl_NoVersion_Defaults() {
    underTest = new IntegrationTestContextBuilder()
        .publishUrl("http://localhost:8080/")
        .build();
    assertEquals("http://localhost:8080/content/page1.site.api/content.json",
        underTest.buildSiteApiUrl("/content/page1", "content"));
  }

}
