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
package org.infrastructurebuilder.pathref;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Named;

import org.infrastructurebuilder.api.base.IdentifierSupplier;

@Named(IncrementingDatedStringSupplier.NAME)
public class IncrementingDatedStringSupplier implements IdentifierSupplier {
  public static final String NAME = "incrementing-dated-string-supplier";
  public static final String DEFAULT_DATE_FORMAT = "yyyyMMddHHmm";
  private final AtomicInteger i = new AtomicInteger(0);
  private String format;

  @Override
  public synchronized String get() {
    {
      if (format == null) // TODO Currently always true
        format = new SimpleDateFormat(DEFAULT_DATE_FORMAT).format(new Date()) + "-%05d";
    }
    return String.format(format, i.addAndGet(1));
  }
}
