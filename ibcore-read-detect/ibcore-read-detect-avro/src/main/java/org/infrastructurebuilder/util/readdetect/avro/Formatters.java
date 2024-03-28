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
package org.infrastructurebuilder.util.readdetect.avro;

import static java.time.format.DateTimeFormatter.ofPattern;
import static java.util.Objects.requireNonNull;
import static org.infrastructurebuilder.util.constants.IBConstants.DATE_FORMATTER;
import static org.infrastructurebuilder.util.constants.IBConstants.TIMESTAMP_FORMATTER;
import static org.infrastructurebuilder.util.constants.IBConstants.TIME_FORMATTER;

import java.time.format.DateTimeFormatter;

import org.infrastructurebuilder.util.config.ConfigMap;

public class Formatters {
  public final static String DEFAULT_TIME_FORMATTER = "HH:mm";
  public final static String DEFAULT_DATE_FORMATTER = "MM-dd-yy"; // TODO Really?
  public final static DateTimeFormatter DEFAULT_TIMESTAMP_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

  private String df = DEFAULT_DATE_FORMATTER;
  private String tf = DEFAULT_TIME_FORMATTER;
  private DateTimeFormatter ts = DEFAULT_TIMESTAMP_FORMATTER;

  public Formatters() {
  }

  public Formatters(ConfigMap config) {
    df = requireNonNull(config).optString(DATE_FORMATTER, DEFAULT_DATE_FORMATTER);
    tf = config.optString(TIME_FORMATTER, DEFAULT_TIME_FORMATTER);
    ts = config.optString(TIMESTAMP_FORMATTER).map(DateTimeFormatter::ofPattern).orElse(DEFAULT_TIMESTAMP_FORMATTER);

  }

  public DateTimeFormatter getDateFormatter() {
    return ofPattern(df);
  }

  public DateTimeFormatter getTimeFormatter() {
    return ofPattern(tf);
  }

  public DateTimeFormatter getTimestampFormatter() {
    return ts;
  }

  public boolean isBlankFieldNullInUnion() {
    return true;
  }

}
