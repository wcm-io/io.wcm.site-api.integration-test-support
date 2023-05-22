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
package io.wcm.siteapi.integrationtestsupport.linkextractor;

import static com.jayway.jsonpath.Criteria.where;
import static com.jayway.jsonpath.Filter.filter;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

/**
 * Generic extractor that is able to detect any "internal" links produced by
 * <a href="https://wcm.io/handler/link/">wcm.io Link Handler</a>.
 * <p>
 * It used very basic heuristic and looks for any JSON elements with a properties "url" property,
 * and a "type=internal" property.
 * </p>
 */
public final class ContentInternalLinks implements LinkExtractor {

  private static final Set<String> SUFFIXES = Set.of("content", "navigation", "config");
  private static final JsonPath JSON_PATH = JsonPath.compile("$..[?]",
      filter(where("url").exists(true).and("type").is("internal")));

  @Override
  public boolean accept(String suffix) {
    return SUFFIXES.contains(suffix);
  }

  @Override
  public Stream<String> getLinks(DocumentContext jsonPathContext) {
    final List<Map<String, String>> contentLinkObjects = jsonPathContext.read(JSON_PATH);
    return contentLinkObjects
        .stream()
        .map(item -> item.get("url"));
  }

}
