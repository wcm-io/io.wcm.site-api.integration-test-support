/*
 * #%L
 * wcm.io
 * %%
 * Copyright (C) 2026 wcm.io
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
package io.wcm.siteapi.integrationtestsupport.linkextractor;

import static io.wcm.siteapi.integrationtestsupport.linkextractor.LinkExtractorTestUtils.assertLinks;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class IndexLinksTest {

  private LinkExtractor underTest;

  @BeforeEach
  void setUp() {
    underTest = new IndexLinks();
  }

  @Test
  void testAccept() {
    assertTrue(underTest.accept("index"));
  }

  @Test
  void testGetLinks() {
    assertLinks(underTest, "linkextractor/index.json",
        "http://localhost:4502/content/siteapi-test/en.site.api/navigation.json",
        "http://localhost:4502/content/siteapi-test/en.site.api/content.json",
        "http://localhost:4502/content/siteapi-test/en.site.api/config.json");
  }

}
