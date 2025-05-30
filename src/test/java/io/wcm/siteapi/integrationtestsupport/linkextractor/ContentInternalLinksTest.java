package io.wcm.siteapi.integrationtestsupport.linkextractor;

import static io.wcm.siteapi.integrationtestsupport.linkextractor.LinkExtractorTestUtils.assertLinks;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ContentInternalLinksTest {

  private LinkExtractor underTest;

  @BeforeEach
  void setUp() throws Exception {
    underTest = new ContentInternalLinks();
  }

  @Test
  void testAccept() {
    assertTrue(underTest.accept("content"));
    assertTrue(underTest.accept("navigation"));
    assertTrue(underTest.accept("config"));
  }

  @Test
  void testGetLinks_content() {
    assertLinks(underTest, "linkextractor/content.json");
  }

  @Test
  void testGetLinks_navigation() {
    assertLinks(underTest, "linkextractor/navigation.json",
        "http://localhost:4502/content/siteapi-test/en/page-1/page-1-1.site.api/content.json",
        "http://localhost:4502/content/siteapi-test/en/page-1/page-1-2.site.api/content.json",
        "http://localhost:4502/content/siteapi-test/en/page-1.site.api/content.json",
        "http://localhost:4502/content/siteapi-test/en/page-2.site.api/content.json",
        "http://localhost:4502/content/siteapi-test/en.site.api/content.json");
  }

  @Test
  void testGetLinks_config() {
    assertLinks(underTest, "linkextractor/config.json",
        "http://localhost:4502/content/siteapi-test/en/page-1.site.api/content.json");
  }

}
