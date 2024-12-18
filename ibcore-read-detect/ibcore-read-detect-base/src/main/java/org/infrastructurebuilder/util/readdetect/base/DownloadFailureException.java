/*
 * @formatter:off
 * Copyright © 2019 admin (admin@infrastructurebuilder.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * @formatter:on
 */
package org.infrastructurebuilder.util.readdetect.base;

/**
 * Represents a download failure exception, thrown when the requested resource returns a non-20x HTTP code.
 */
public final class DownloadFailureException extends RuntimeException {

  private static final long serialVersionUID = 9188784105763259184L;

  private final int statusCode;

  private final String statusLine;

  /**
   * @return the HTTP status code.
   */
  public int getHttpCode() {
    return statusCode;
  }

  /**
   * @return the HTTP status line.
   */
  public String getStatusLine() {
    return statusLine;
  }

  /**
   * Creates a new instance.
   *
   * @param statusCode HTTP code
   * @param statusLine status line
   */
  public DownloadFailureException(int statusCode, String statusLine) {
    this.statusCode = statusCode;
    this.statusLine = statusLine;
  }

  @Override
  public String getMessage() {
    return "Download failed with code " + getHttpCode() + ": " + getStatusLine();
  }
}
