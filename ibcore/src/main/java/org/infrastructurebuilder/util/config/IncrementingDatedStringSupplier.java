/**
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
 */
package org.infrastructurebuilder.util.config;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Named;

@Named(IncrementingDatedStringSupplier.INCREMENTING_DATED_STRING_SUPPLIER)
public class IncrementingDatedStringSupplier implements IdentifierSupplier {
  public static final String INCREMENTING_DATED_STRING_SUPPLIER = "incrementing-dated-string-supplier";
  public static final String DEFAULT_DATE_FORMAT = "yyyyMMddHHmm";
  private final AtomicInteger i = new AtomicInteger(0);
  private String format;

  @Override
  public synchronized String get() {
    {
      if (format == null)
        format = new SimpleDateFormat(DEFAULT_DATE_FORMAT).format(new Date()) + "-%04d";
    }
    return String.format(format, i.addAndGet(1));
  }
}
