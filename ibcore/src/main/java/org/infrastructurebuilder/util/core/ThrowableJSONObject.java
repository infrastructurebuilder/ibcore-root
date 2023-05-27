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
package org.infrastructurebuilder.util.core;

import static org.infrastructurebuilder.util.constants.IBConstants.CAUSE;
import static org.infrastructurebuilder.util.constants.IBConstants.CLASS;
import static org.infrastructurebuilder.util.constants.IBConstants.MESSAGE;
import static org.infrastructurebuilder.util.constants.IBConstants.STACK_TRACE;
import static org.infrastructurebuilder.util.constants.IBConstants.UNKNOWN_THROWABLE_CLASS;

import java.util.Objects;
import java.util.Optional;

import org.json.JSONArray;
import org.json.JSONObject;

public class ThrowableJSONObject implements JSONOutputEnabled {
  // Cant recurse static Function, so this is a method
  public static JSONObject getThrowableJson(Throwable t) {
    final JSONObject j2 = new JSONObject();
    if (t != null) {
      j2.put(CLASS, t.getClass().getCanonicalName());
      Optional.ofNullable(t.getCause()).ifPresent(cause -> {
        j2.put(CAUSE, getThrowableJson(cause)); // Recurses
      });
      Optional.ofNullable(t.getMessage()).ifPresent(message -> {
        j2.put(MESSAGE, message);
      });
      var st = t.getStackTrace();
      if (st.length > 0) {
        var l = new JSONArray();
        for (StackTraceElement ste : st) {
          l.put(ste.toString());
        }
        j2.put(STACK_TRACE, l);
      }
    } else {
      j2.put(CLASS, UNKNOWN_THROWABLE_CLASS);
    }
    return j2;
  }

  private final Throwable t;;

  public ThrowableJSONObject(Throwable t) {
    this.t = Objects.requireNonNull(t);
  }

  public final void throwIt() throws Throwable {
    throw this.t;
  }

  @Override
  public JSONObject asJSON() {
    return getThrowableJson(this.t);
  }

}
