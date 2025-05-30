package io.wcm.siteapi.integrationtestsupport.linkextractor;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

final class LinkExtractorTestUtils {

  private LinkExtractorTestUtils() {
    // static methods only
  }

  /**
   * Assert extracted link URLs matches with expected list of URLs
   * @param linkExtractor Link extractor
   * @param classpath Classpath reference to JSON file
   * @param expectedUrls Expected URLs
   */
  public static void assertLinks(LinkExtractor linkExtractor, String classpath, String... expectedUrls) {
    DocumentContext context = getJsonPathContext(classpath);
    List<String> urls = linkExtractor.getLinks(context).collect(Collectors.toList());
    assertEquals(List.of(expectedUrls), urls);
  }

  private static DocumentContext getJsonPathContext(String classpath) {
    try (InputStream is = LinkExtractorTestUtils.class.getClassLoader().getResourceAsStream(classpath)) {
      String json = IOUtils.toString(is, StandardCharsets.UTF_8);
      return JsonPath.parse(json);
    }
    catch (IOException ex) {
      throw new IllegalArgumentException("Unable to read from classpath: " + classpath, ex);
    }
  }

}
