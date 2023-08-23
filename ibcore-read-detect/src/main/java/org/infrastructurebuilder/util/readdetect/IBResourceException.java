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
package org.infrastructurebuilder.util.readdetect;

import org.infrastructurebuilder.exceptions.IBException;

import com.mscharhag.et.ET;
import com.mscharhag.et.ExceptionTranslator;

public class IBResourceException extends IBException {

  private static final long serialVersionUID = -912369588505080668L;
  public static ExceptionTranslator cet = ET.newConfiguration().translate(Exception.class).to(IBResourceException.class)
      .done();

  public IBResourceException(String string) {
    super(string);
  }

  public IBResourceException(String string, Throwable e) {
    super(string, e);
  }

}
