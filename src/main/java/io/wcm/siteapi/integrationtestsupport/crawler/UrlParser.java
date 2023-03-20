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
package io.wcm.siteapi.integrationtestsupport.crawler;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

/**
 * Parses and validates Site API URLs.
 */
class UrlParser {

  private static final int GROUP_SUFFIX = 1;
  private final Pattern urlPattern;

  UrlParser(@NotNull String publishUrl,
      @NotNull String selector, @NotNull String apiVersion, @NotNull String extension) {
    urlPattern = Pattern.compile(
        "^" + Pattern.quote(publishUrl) + "/.+"
        + "\\." + Pattern.quote(selector)
        + (StringUtils.isNotBlank(apiVersion) ? ("\\." + Pattern.quote(apiVersion)) : "")
        + "\\." + Pattern.quote(extension)
        + "/([^/]+)(/(.*))?\\.json$");
  }

  String parseSuffix(@NotNull String url) {
    Matcher matcher = urlPattern.matcher(url);
    if (!matcher.matches()) {
      throw new IllegalArgumentException("Invalid URL: " + url + ", expected pattern: " + urlPattern);
    }
    return matcher.group(GROUP_SUFFIX);
  }

}
