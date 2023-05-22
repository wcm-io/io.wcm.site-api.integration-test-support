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

import java.util.stream.Stream;

import com.jayway.jsonpath.DocumentContext;

/**
 * A link extractor is used to fetch all hyperlinks from a JSON content response that point to other
 * parts of the Site API of the same site to continue crawling with.
 * <p>
 * Link extractors should ignore external URLs or URLs pointing to assets.
 * </p>
 */
public interface LinkExtractor {

  /**
   * Returns true if the link extractor accepts the given suffix (processor mapped to this suffix).
   * @param suffix Suffix
   * @return true if JSON response of this processor is supported
   */
  boolean accept(String suffix);

  /**
   * Retrieves links from the JSON document via JSON path.
   * @param jsonPathContext Document context
   * @return Link URLs
   */
  Stream<String> getLinks(DocumentContext jsonPathContext);

}
