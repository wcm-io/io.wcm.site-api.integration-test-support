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
package io.wcm.siteapi.integrationtest.linkextractor;

import static com.jayway.jsonpath.Criteria.where;
import static com.jayway.jsonpath.Filter.filter;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

/**
 * Extracts internal links from rich text fragments in JSON.
 * Looks for components with specific resource types and a "text" property containing the HTML fragment.
 */
public final class RichTextInternalLinks implements LinkExtractor {

  private static final Set<String> SUFFIXES = Set.of("content");
  private final JsonPath jsonPath;

  /**
   * @param resourceTypes Resource type(s) for components containing rich text.
   */
  public RichTextInternalLinks(String... resourceTypes) {
    this(Arrays.asList(resourceTypes));
  }

  /**
   * @param resourceTypes Resource type(s) for components containing rich text.
   */
  public RichTextInternalLinks(List<String> resourceTypes) {
    jsonPath = JsonPath.compile("$..[?]",
        filter(where(":type").in(resourceTypes).and("text").exists(true)));
  }

  @Override
  public boolean accept(String suffix) {
    return SUFFIXES.contains(suffix);
  }

  @Override
  public Stream<String> getLinks(DocumentContext jsonPathContext) {
    // find all rich text components and parse rich text from "text" property
    final List<Map<String, String>> richTextComponents = jsonPathContext.read(jsonPath);
    return richTextComponents.stream()
        .map(entry -> entry.get("text"))
        .flatMap(this::extractLinksFromHtmlFragment);
  }

  private Stream<String> extractLinksFromHtmlFragment(String html) {
    final Document document = Jsoup.parse(html);
    return document.select("a[data-type='internal']").stream()
        .map(a -> a.attr("href"));
  }

}
