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
package org.infrastructurebuilder.util.config.impl;

import static java.util.Objects.requireNonNull;
import static java.util.Objects.requireNonNullElseGet;
import static java.util.Optional.ofNullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.Stack;
import java.util.StringJoiner;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import org.infrastructurebuilder.exceptions.IBException;
import org.infrastructurebuilder.util.config.ConfigMap;
import org.infrastructurebuilder.util.config.ConfigMapBuilder;
import org.infrastructurebuilder.util.core.IBUtils;
import org.infrastructurebuilder.util.core.JSONOutputEnabled;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONPointer;
import org.json.JSONPointerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IBJSONObject implements ConfigMap, ConfigMapBuilder {
  private static final Logger log = LoggerFactory.getLogger(IBJSONObject.class);
  protected final Stack<JSONObject> s = new Stack<JSONObject>();

  private IBJSONObject(Stack<JSONObject> h, JSONOutputEnabled g) {
    this(h);
    this.s.add(g.asJSON());
  }

  private IBJSONObject(Stack<JSONObject> j) {
    ofNullable(j).ifPresent(j1 -> {
      this.s.addAll(j1);
    });
  }

  public IBJSONObject() {
    this(new JSONObject());
  }

  public IBJSONObject(JSONObject j) {
    withJSONObject(j);
  }

  public IBJSONObject(Map<String, Object> m) {
    withJSONObject(new JSONObject(m));
  }

  @Override
  public ConfigMapBuilder copy() {
    return new IBJSONObject(s);
  }

  @Override
  public ConfigMapBuilder withProperties(Properties p) {
    var props = requireNonNullElseGet(p, () -> new Properties());
    var j = new JSONObject();
    p.propertyNames().asIterator().forEachRemaining(n -> j.put(n.toString(), props.getProperty(n.toString())));
    return withJSONObject(j);
  }

  @Override
  public ConfigMapBuilder withPropertiesFile(Path file, boolean optional) {
    Properties p = new Properties();
    try (BufferedReader r = Files.newBufferedReader(file)) {
      p.load(r);
    } catch (IOException e) {
      if (optional) {
        log.error("Unable to load properties file " + file, e);
        return this;
      } else
        throw new IBException(e);
    }
    return withProperties(p);
  }

  @Override
  public ConfigMapBuilder withPropertiesResource(String file, boolean optional) {
    Properties p = new Properties();
    try (InputStream r = getClass().getResourceAsStream(file)) {
      p.load(r);
    } catch (IOException e) {
      if (optional) {
        log.error("Unable to read properties resource " + file, e);
        return this;
      } else
        throw new IBException(e);
    }
    return withProperties(p);
  }

  private ConfigMapBuilder withStack(Stack<JSONObject> newStack) {
    var b = new IBJSONObject(this.s);
    b.s.addAll(newStack);
    return b;
  }

  private ConfigMapBuilder withCopiedStack(JSONObject j) {
    var b = new IBJSONObject(this.s);
    b.s.add(Objects.requireNonNull(j));
    return b;
  }

  @Override
  public ConfigMapBuilder withConfigMapBuilder(ConfigMapBuilder c) {
    if (c instanceof IBJSONObject j) {
      return withStack(j.s);
    } else
      return withJSONObjectFacade(c.get());
  }

  @Override
  public ConfigMapBuilder withJSONFile(Path file, boolean optional) {
    try {
      String r = Files.readString(file);
      return withCopiedStack(new JSONObject(r));
    } catch (IOException e) {
      if (optional) {
        log.error("Unable to load JSON file " + file, e);
        return this;
      } else
        throw new IBException(e);
    }
  }

  @Override
  public ConfigMapBuilder withJSONResource(String file, boolean optional) {

    try (InputStream ins = getClass().getResourceAsStream(file)) {
      return withCopiedStack(new JSONObject(IBUtils.readToString(ins)));
    } catch (IOException e) {
      if (optional) {
        log.error("Unable to load JSON resource " + file, e);
        return this;
      } else
        throw new IBException(e);
    }
  }

  @Override
  public ConfigMapBuilder withJSONObject(JSONObject j) {
    return withCopiedStack(j);
  }

  @Override
  public ConfigMapBuilder withJSONObjectFacade(ConfigMap j) {
    return withCopiedStack(Objects.requireNonNullElseGet(j, new IBJSONObject()).asJSON());
  }

  @Override
  public ConfigMapBuilder withMapStringString(Map<String, String> m) {
    var j = new JSONObject();
    requireNonNullElseGet(m, () -> new HashMap<String, String>()).forEach((k, v) -> j.put(k, v));
    return withCopiedStack(j);
  }

  @Override
  public ConfigMapBuilder withMapStringObject(Map<String, Object> m) {
    return withCopiedStack(new JSONObject(requireNonNullElseGet(m, () -> new HashMap<String, Object>())));
  }

  @Override
  public ConfigMapBuilder withKeyValue(String key, Object value) {
    return withCopiedStack(new JSONObject()

        .put(requireNonNull(key), requireNonNullElseGet(value, () -> JSONObject.NULL)));
  }

  @Override
  public ConfigMap get() {
    return this;
  }

  @Override
  public JSONObject asJSON() {
    return collapse();
  }

  private synchronized JSONObject collapse() {
    JSONObject j = new JSONObject();
    List<JSONObject> items = this.s.stream().collect(Collectors.toList());
    Collections.reverse(items);
    items.forEach(jo -> jo.keySet().forEach(k -> j.put(k, jo.get(k))));
    return j;
  }

  @Override
  public boolean containsKey(String key) {
    return this.s.stream().filter(e -> e.has(key)).findAny().isPresent();
  }

  // --------------------
  // Following lifted directly from JSONObject. It's definitely their code, not IB's.

  /**
   * Tests if the value should be tried as a decimal. It makes no test if there are actual digits.
   *
   * @param val value to test
   * @return true if the string is "-0" or if it contains '.', 'e', or 'E', false otherwise.
   */
  protected static boolean isDecimalNotation(final String val) {
    return val.indexOf('.') > -1 || val.indexOf('e') > -1 || val.indexOf('E') > -1 || "-0".equals(val);
  }

  /**
   * For a prospective number, remove the leading zeros
   *
   * @param value prospective number
   * @return number without leading zeros
   */
  private static String removeLeadingZerosOfNumber(String value) {
    if (value.equals("-")) {
      return value;
    }
    boolean negativeFirstChar = (value.charAt(0) == '-');
    int counter = negativeFirstChar ? 1 : 0;
    while (counter < value.length()) {
      if (value.charAt(counter) != '0') {
        if (negativeFirstChar) {
          return "-".concat(value.substring(counter));
        }
        return value.substring(counter);
      }
      ++counter;
    }
    if (negativeFirstChar) {
      return "-0";
    }
    return "0";
  }

  /**
   * Converts a string to a number using the narrowest possible type. Possible returns for this function are BigDecimal,
   * Double, BigInteger, Long, and Integer. When a Double is returned, it should always be a valid Double and not NaN or
   * +-infinity.
   *
   * @param input value to convert
   * @return Number representation of the value.
   * @throws NumberFormatException thrown if the value is not a valid number. A public caller should catch this and wrap
   *                               it in a {@link JSONException} if applicable.
   */
  protected static Number stringToNumber(final String input) throws NumberFormatException {
    String val = input;
    if (val.startsWith(".")) {
      val = "0" + val;
    }
    if (val.startsWith("-.")) {
      val = "-0." + val.substring(2);
    }
    char initial = val.charAt(0);
    if ((initial >= '0' && initial <= '9') || initial == '-') {
      // decimal representation
      if (isDecimalNotation(val)) {
        // Use a BigDecimal all the time so we keep the original
        // representation. BigDecimal doesn't support -0.0, ensure we
        // keep that by forcing a decimal.
        try {
          BigDecimal bd = new BigDecimal(val);
          if (initial == '-' && BigDecimal.ZERO.compareTo(bd) == 0) {
            return Double.valueOf(-0.0);
          }
          return bd;
        } catch (NumberFormatException retryAsDouble) {
          // this is to support "Hex Floats" like this: 0x1.0P-1074
          try {
            Double d = Double.valueOf(val);
            if (d.isNaN() || d.isInfinite()) {
              throw new NumberFormatException("val [" + input + "] is not a valid number.");
            }
            return d;
          } catch (NumberFormatException ignore) {
            throw new NumberFormatException("val [" + input + "] is not a valid number.");
          }
        }
      }
      val = removeLeadingZerosOfNumber(input);
      initial = val.charAt(0);
      if (initial == '0' && val.length() > 1) {
        char at1 = val.charAt(1);
        if (at1 >= '0' && at1 <= '9') {
          throw new NumberFormatException("val [" + input + "] is not a valid number.");
        }
      } else if (initial == '-' && val.length() > 2) {
        char at1 = val.charAt(1);
        char at2 = val.charAt(2);
        if (at1 == '0' && at2 >= '0' && at2 <= '9') {
          throw new NumberFormatException("val [" + input + "] is not a valid number.");
        }
      }
      // integer representation.
      // This will narrow any values to the smallest reasonable Object representation
      // (Integer, Long, or BigInteger)

      // BigInteger down conversion: We use a similar bitLength compare as
      // BigInteger#intValueExact uses. Increases GC, but objects hold
      // only what they need. i.e. Less runtime overhead if the value is
      // long lived.
      BigInteger bi = new BigInteger(val);
      if (bi.bitLength() <= 31) {
        return Integer.valueOf(bi.intValue());
      }
      if (bi.bitLength() <= 63) {
        return Long.valueOf(bi.longValue());
      }
      return bi;
    }
    throw new NumberFormatException("val [" + input + "] is not a valid number.");
  }

  // ====================
  private Object process(BiFunction<JSONObject, String, Object> func, String key) {
    Object q = null;
    for (int i = s.size(); i > 0; i--) {
//    for (JSONObject j: Collections.reverse(s)) {
      JSONObject j = s.get(i - 1);
      try {
        q = func.apply(j, key);
      } catch (Throwable t) {
        continue;
      }
      return q;
    }
    return Objects.requireNonNull(q);
//    var q=  s.stream().map(j -> {
//      try {
//        return func.apply(j, key);
//      } catch (JSONException p) {
//        return Stream.empty();
//      }
//    });
//    return q.findFirst().orElseThrow(() -> new IBException());
  }

  private Object processPointerNull(BiFunction<JSONObject, JSONPointer, Object> func, JSONPointer pp) {
    Object q = null;
    for (int i = s.size(); i > 0; i--) {
      JSONObject j = s.get(i - 1);
      try {
        q = func.apply(j, pp);
      } catch (Throwable t) {
        continue;
      }
    }
    return q;
  }

  private <E extends Enum<E>> E processEnumNull(BiFunction<JSONObject, Class<E>, E> func, Class<E> pp) {
    E q = null;
    for (int i = s.size(); i > 0; i--) {
      JSONObject j = s.get(i - 1);
      try {
        q = func.apply(j, pp);
      } catch (Throwable t) {
        continue;
      }
    }
    return q;
  }

  private Object processNull(BiFunction<JSONObject, String, Object> func, String key) {
    Object q = null;
    for (int i = s.size(); i > 0; i--) {
      JSONObject j = s.get(i - 1);
      try {
        q = func.apply(j, key);
      } catch (Throwable t) {
        continue;
      }
    }
    return JSONObject.NULL.equals(q) ? null : q;
  }

  @Override
  public Object get(String key) throws IBException {
    return process((jj, kk) -> jj.get(kk), key);
  }

  @Override
  public Object opt(String key) {
    return processNull((jj, kk) -> jj.get(kk), key);
  }

  private static Object opt(JSONObject jj, String kk) {
    Object val = jj.opt(kk);
    if (JSONObject.NULL.equals(val) || val == null) {
      return null;
    }
    return val;
  }

  @Override
  public <E extends Enum<E>> E getEnum(Class<E> clazz, String key) throws IBException {
    E val = optEnum(clazz, key).orElse(null);
    if (val == null) {
      // JSONException should really take a throwable argument.
      // If it did, I would re-implement this with the Enum.valueOf
      // method and place any thrown exception in the JSONException
      throw new IBException(key + " / enum of type " + JSONObject.quote(clazz.getSimpleName()));
    }
    return val;
  }

  @Override
  public <E extends Enum<E>> Optional<E> optEnum(Class<E> clazz, String key) {
    return ofNullable(this.optEnum(clazz, key, null));
  }

  @Override
  public <E extends Enum<E>> E optEnum(Class<E> clazz, String key, E defaultValue) {
    return Objects.requireNonNullElse(processEnumNull((jj, cl) -> {
      try {
        Object val = jj.opt(key);
        if (JSONObject.NULL.equals(val)) {
          return null;
        }
        if (clazz.isAssignableFrom(val.getClass())) {
          // we just checked it!
          @SuppressWarnings("unchecked")
          E myE = (E) val;
          return myE;
        }
        return Enum.valueOf(clazz, val.toString());
      } catch (IllegalArgumentException | NullPointerException e) {
        return null;
      }

    }, clazz), defaultValue);
  }

  @Override
  public boolean getBoolean(String key) throws IBException {
    return (boolean) process((jj, kk) -> jj.getBoolean(kk), key);
  }

  @Override
  public boolean optBoolean(String key) {
    return optBoolean(key, false);
  }

  @Override
  public boolean optBoolean(String key, boolean defaultValue) {
    return (boolean) Objects.requireNonNullElse(processNull((jj, kk) -> {
      var val = opt(jj, kk);
      if (val == null || JSONObject.NULL.equals(val))
        return defaultValue;
      try {
        return jj.getBoolean(kk);
      } catch (JSONException j) {
        return defaultValue;
      }
    }, key), defaultValue);
  }

  @Override
  public Optional<Boolean> optBooleanObject(String key) {
    return ofNullable(optBooleanObject(key, null));
  }

  @Override
  public Boolean optBooleanObject(String key, Boolean defaultValue) {
    return (Boolean) Objects.requireNonNullElse(processNull((jj, kk) -> {
      var val = opt(jj, kk);
      if (val == null || JSONObject.NULL.equals(val))
        return defaultValue;
      try {
        return jj.optBooleanObject(kk);
      } catch (JSONException j) {
        return defaultValue;
      }
    }, key), defaultValue);
  }

  @Override
  public BigInteger getBigInteger(String key) throws IBException {
    return (BigInteger) process((jj, kk) -> jj.getBigInteger(kk), key);
  }

  @Override
  public BigInteger optBigInteger(String key, BigInteger defaultValue) {
    return Objects.requireNonNullElse(getBigInteger(key), defaultValue);
  }

  @Override
  public BigDecimal getBigDecimal(String key) throws IBException {
    return (BigDecimal) process((jj, kk) -> jj.getBigDecimal(kk), key);
  }

  @Override
  public BigDecimal optBigDecimal(String key, BigDecimal defaultValue) {
    return Objects.requireNonNullElse(getBigDecimal(key), defaultValue);
  }

  @Override
  public double getDouble(String key) throws IBException {
    return (double) process((jj, kk) -> jj.getDouble(kk), key);
  }

  @Override
  public double optDouble(String key) {
    return optDouble(key, Double.NaN);
  }

  @Override
  public double optDouble(String key, double defaultValue) {
    return (double) processNull((jj, kk) -> {
      var val = jj.optNumber(key);
      if (val == null)
        return defaultValue;
      return val.doubleValue();
    }, key);
  }

  @Override
  public Optional<Double> optDoubleObject(String key) {
    return ofNullable(optDoubleObject(key, null));
  }

  @Override
  public Double optDoubleObject(String key, Double defaultValue) {
    return optDouble(key, defaultValue);
  }

  @Override
  public float getFloat(String key) throws IBException {
    return optFloat(key, Float.NaN);
  }

  @Override
  public float optFloat(String key) {
    return optFloat(key, Float.NaN);
  }

  @Override
  public float optFloat(String key, float defaultValue) {
    return (float) processNull((jj, kk) -> {
      var val = jj.optNumber(key);
      if (val == null)
        return defaultValue;
      return val.floatValue();
    }, key);
  }

  @Override
  public Optional<Float> optFloatObject(String key) {
    return ofNullable(optFloatObject(key, null));
  }

  @Override
  public Float optFloatObject(String key, Float defaultValue) {
    return optFloat(key, defaultValue);
  }

  @Override
  public Number getNumber(String key) throws IBException {
    return (Number) process((jj, kk) -> jj.getNumber(kk), key);
  }

  @Override
  public Optional<Number> optNumber(String key) {
    return ofNullable(optNumber(key, null));
  }

  @Override
  public Number optNumber(String key, Number defaultValue) {
    return (Number) processNull((jj, kk) -> {
      var val = opt(jj, key);
      if (val == null)
        return defaultValue;
      if (val instanceof Number)
        return (Number) val;
      try {
        return stringToNumber(val.toString());
      } catch (Exception e) {
        return defaultValue;
      }
    }, key);
  }

  @Override
  public int getInt(String key) throws IBException {
    return (int) process((jj, kk) -> jj.getInt(kk), key);
  }

  @Override
  public int optInt(String key) {
    return optInt(key, 0);
  }

  @Override
  public int optInt(String key, int defaultValue) {
    final Number val = this.optNumber(key, null);
    if (val == null) {
      return defaultValue;
    }
    return val.intValue();
  }

  @Override
  public Optional<Integer> optIntegerObject(String key) {
    return ofNullable(optIntegerObject(key, null));
  }

  @Override
  public Integer optIntegerObject(String key, Integer defaultValue) {
    return optInt(key, defaultValue);
  }

  @Override
  public JSONArray getJSONArray(String key) throws IBException {
    return (JSONArray) process((jj, kk) -> jj.getJSONArray(kk), key);
  }

  @Override
  public Optional<JSONArray> optJSONArray(String key) {
    return ofNullable(this.optJSONArray(key, null));
  }

  @Override
  public JSONArray optJSONArray(String key, JSONArray defaultValue) {
    Object object = this.opt(key);
    return object instanceof JSONArray ? (JSONArray) object : defaultValue;
  }

  @Override
  public JSONObject getJSONObject(String key) throws IBException {
    return (JSONObject) process((jj, kk) -> jj.getJSONObject(kk), key);
  }

  @Override
  public Optional<JSONObject> optJSONObject(String key) {
    return ofNullable(this.optJSONObject(key, null));
  }

  @Override
  public JSONObject optJSONObject(String key, JSONObject defaultValue) {
    Object object = this.opt(key);
    return object instanceof JSONObject ? (JSONObject) object : defaultValue;
  }

  @Override
  public long getLong(String key) throws IBException {
    return (long) process((jj, kk) -> jj.getLong(kk), key);
  }

  @Override
  public long optLong(String key) {
    return this.optLong(key, 0L);
  }

  @Override
  public long optLong(String key, long defaultValue) {
    final Number val = this.optNumber(key, null);
    if (val == null) {
      return defaultValue;
    }

    return val.longValue();
  }

  @Override
  public Optional<Long> optLongObject(String key) {
    return ofNullable(optLong(key));
  }

  @Override
  public Long optLongObject(String key, Long defaultValue) {
    return optLong(key, defaultValue);
  }

  @Override
  public String getString(String key) throws IBException {
    return (String) process((jj, kk) -> jj.getString(kk), key);
  }

  @Override
  public Optional<String> optString(String key) {
    return ofNullable(this.optString(key, null));
  }

  @Override
  public String optString(String key, String defaultValue) {
    Object object = this.opt(key);
    return JSONObject.NULL.equals(object) ? defaultValue : object.toString();
  }

  @Override
  public boolean has(String key) {
    return (boolean) process((jj, kk) -> jj.has(kk), key);
  }

  @Override
  public boolean isNull(String key) {
    return (boolean) process((jj, kk) -> jj.isNull(kk), key);
  }

  @Override
  public Iterator<String> keys() {
    return keySet().iterator();
  }

  @Override
  public Set<String> keySet() {
    return s.stream().map(JSONObject::keySet).flatMap(Set::stream).collect(Collectors.toSet());
  }

  @Override
  public int length() {
    return keySet().size();
  }

  @Override
  public boolean isEmpty() {
    return length() == 0;
  }

  @Override
  public JSONArray names() {
    var names = keySet();
    return names.size() == 0 ? null : new JSONArray(names);
  }

  @Override
  public Object query(String jsonPointer) {
    return query(new JSONPointer(jsonPointer));
  }

  @Override
  public Object query(JSONPointer jsonPointer) {
    return processPointerNull((jj, pp) -> {
      try {
        return Objects.requireNonNullElse(jsonPointer.queryFrom(jj), null);
      } catch (JSONPointerException pe) {
        return null;
      }
    }, jsonPointer);
  }

  @Override
  public Optional<Object> optQuery(String jsonPointer) {
    return optQuery(new JSONPointer(jsonPointer));
  }

  @Override
  public Optional<Object> optQuery(JSONPointer jsonPointer) {
    return ofNullable(query(jsonPointer));
  }

  @Override
  public boolean similar(Object other) {
    var o = other;
    if (other instanceof IBJSONObject) {
      o = ((IBJSONObject) other).collapse();
    }

    return collapse().similar(o);
  }

  @Override
  public JSONArray toJSONArray(JSONArray names) throws IBException {
    var s = new ArrayList<Object>();
    names.forEach(name -> s.add(this.get(name.toString())));
    return new JSONArray(s);
  }

  @Override
  public String toString(int indentFactor) throws IBException {
    return collapse().toString(indentFactor);
  }

  @Override
  public String toString() {
    StringJoiner sj = new StringJoiner("\n", "[", "]");

    this.s.forEach(j -> {
      sj.add(j.toString(2));
    });
    return sj.toString();
  }

  @Override
  public Writer write(Writer writer) throws IBException {
    return write(writer, 0, 0);
  }

  @Override
  public Writer write(Writer writer, int indentFactor, int indent) throws IBException {
    return collapse().write(writer, indentFactor, indent);
  }

  @Override
  public Map<String, Object> toMap() {
    return collapse().toMap();
  }

  @Override
  public Optional<Object> optional(String key) {
    return ofNullable(opt(key));
  }

  // -------------------------------------------------------------------------------------

}
