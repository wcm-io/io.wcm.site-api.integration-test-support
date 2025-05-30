package io.wcm.siteapi.integrationtestsupport.linkextractor;

import static io.wcm.siteapi.integrationtestsupport.linkextractor.LinkExtractorTestUtils.assertLinks;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RichTextInternalLinksTest {

  private LinkExtractor underTest;

  @BeforeEach
  void setUp() throws Exception {
    underTest = new RichTextInternalLinks("siteapi-test/core/components/content/text");
  }

  @Test
  void testAccept() {
    assertTrue(underTest.accept("content"));
  }

  @Test
  void testGetLinks() {
    assertLinks(underTest, "linkextractor/content.json",
        "http://localhost:4502/content/siteapi-test/en/page-1.site.api/content.json");
  }

}
