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
package io.wcm.siteapi.integrationtestsupport.httpclient;

/**
 * HTTP request failed.
 */
public final class HttpRequestFailedException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  /**
   * @param message Message
   */
  public HttpRequestFailedException(String message) {
    super(message);
  }

  /**
   * @param message Message
   * @param cause Cause
   */
  public HttpRequestFailedException(String message, Throwable cause) {
    super(message, cause);
  }

}
