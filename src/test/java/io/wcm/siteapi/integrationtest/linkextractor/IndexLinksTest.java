package io.wcm.siteapi.integrationtest.linkextractor;

import static io.wcm.siteapi.integrationtest.linkextractor.LinkExtractorTestUtils.assertLinks;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class IndexLinksTest {

  private LinkExtractor underTest;

  @BeforeEach
  void setUp() throws Exception {
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
