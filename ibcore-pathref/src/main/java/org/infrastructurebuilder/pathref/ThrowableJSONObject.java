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

import static org.infrastructurebuilder.constants.IBConstants.CAUSE;
import static org.infrastructurebuilder.constants.IBConstants.CLASS;
import static org.infrastructurebuilder.constants.IBConstants.MESSAGE;
import static org.infrastructurebuilder.constants.IBConstants.STACK_TRACE;
import static org.infrastructurebuilder.constants.IBConstants.UNKNOWN_THROWABLE_CLASS;

import java.lang.reflect.Constructor;
import java.util.Objects;
import java.util.Optional;

import org.infrastructurebuilder.exceptions.IBException;
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

  private final Throwable t;
  private final JSONObject jsonObject;

  public ThrowableJSONObject(Throwable t) {
    this.t = Objects.requireNonNull(t);
    this.jsonObject = getThrowableJson(t);
  }

  public ThrowableJSONObject(JSONObject j) {
    this.jsonObject = Objects.requireNonNull(j);
    var cname = j.optString(CLASS);
    var msg = Optional.ofNullable(j.optString(MESSAGE));
    Optional<JSONObject> c = Optional.ofNullable(j.optString(CAUSE)).map(JSONObject::new);
    Optional<ThrowableJSONObject> cause = c.map(ThrowableJSONObject::new);

    Throwable tt;
    try {
      if (UNKNOWN_THROWABLE_CLASS.equals(cname))
        tt = new IBException("Unknown throwable class (null, most likely)");
      else {
        var clazz = Class.forName(cname);
        Constructor<?> cons;
        if (msg.isPresent()) {
          if (cause.isPresent()) {
            cons = clazz.getConstructor(String.class, Throwable.class);
            tt = (Throwable) cons.newInstance(msg.get(), cause.get().getThrowable());
          } else {
            cons = clazz.getConstructor(String.class);
            tt = (Throwable) cons.newInstance(msg.get());
          }
        } else {
          if (cause.isPresent()) {
            cons = clazz.getConstructor(Throwable.class);
            tt = (Throwable) cons.newInstance(cause.get().getThrowable());
          } else {
            cons = clazz.getConstructor();
            tt = (Throwable) cons.newInstance();
          }
        }
      }
      this.t = tt;
    } catch (Throwable e) {
      throw new IBException("Could not find class " + cname, e);
    }
  }

  public final Throwable getThrowable() {
    return this.t;
  }

  public final void throwIt() throws Throwable {
    throw this.t;
  }

  @Override
  public JSONObject asJSON() {
    return this.jsonObject;
  }

}
