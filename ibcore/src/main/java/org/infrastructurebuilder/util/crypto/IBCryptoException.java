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
package org.infrastructurebuilder.util.crypto;

import com.mscharhag.et.ET;
import com.mscharhag.et.ExceptionTranslator;

public class IBCryptoException extends RuntimeException {
  /**
   *
   */
  private static final long serialVersionUID = -2115861405599391442L;
  public static ExceptionTranslator et = ET.newConfiguration().translate(Exception.class).to(IBCryptoException.class)
      .done();

  public IBCryptoException() {
  }

  public IBCryptoException(final String message) {
    super(message);
  }

  public IBCryptoException(final String message, final Throwable cause) {
    super(message, cause);
  }

  public IBCryptoException(final String message, final Throwable cause, final boolean enableSuppression,
      final boolean writableStackTrace)
  {
    super(message, cause, enableSuppression, writableStackTrace);
  }

  public IBCryptoException(final Throwable cause) {
    super(cause);
  }

}
