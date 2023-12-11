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
package org.infrastructurebuilder.util.config;

import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.infrastructurebuilder.exceptions.IBException;
import org.infrastructurebuilder.util.core.JSONOutputEnabled;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONPointer;

public interface ConfigMap extends JSONOutputEnabled {

  boolean containsKey(String string);

//  Class<? extends Map> getMapType();

  /**
   * Get the value object associated with a key.
   *
   * @param key A key string.
   * @return The object associated with the key.
   * @throws IBException if the key is not found.
   */
  Object get(String key) throws IBException;

  /**
   * Get the enum value associated with a key.
   *
   * @param <E>   Enum Type
   * @param clazz The type of enum to retrieve.
   * @param key   A key string.
   * @return The enum value associated with the key
   * @throws IBException if the key is not found or if the value cannot be converted to an enum.
   */
  <E extends Enum<E>> E getEnum(Class<E> clazz, String key) throws IBException;

  /**
   * Get the boolean value associated with a key.
   *
   * @param key A key string.
   * @return The truth.
   * @throws IBException if the value is not a Boolean or the String "true" or "false".
   */
  boolean getBoolean(String key) throws IBException;

  /**
   * Get the BigInteger value associated with a key.
   *
   * @param key A key string.
   * @return The numeric value.
   * @throws IBException if the key is not found or if the value cannot be converted to BigInteger.
   */
  BigInteger getBigInteger(String key) throws IBException;

  /**
   * Get the BigDecimal value associated with a key. If the value is float or double, the
   * {@link BigDecimal#BigDecimal(double)} constructor will be used. See notes on the constructor for conversion issues
   * that may arise.
   *
   * @param key A key string.
   * @return The numeric value.
   * @throws IBException if the key is not found or if the value cannot be converted to BigDecimal.
   */
  BigDecimal getBigDecimal(String key) throws IBException;

  /**
   * Get the double value associated with a key.
   *
   * @param key A key string.
   * @return The numeric value.
   * @throws IBException if the key is not found or if the value is not a Number object and cannot be converted to a
   *                     number.
   */
  double getDouble(String key) throws IBException;

  /**
   * Get the float value associated with a key.
   *
   * @param key A key string.
   * @return The numeric value.
   * @throws IBException if the key is not found or if the value is not a Number object and cannot be converted to a
   *                     number.
   */
  float getFloat(String key) throws IBException;

  /**
   * Get the Number value associated with a key.
   *
   * @param key A key string.
   * @return The numeric value.
   * @throws IBException if the key is not found or if the value is not a Number object and cannot be converted to a
   *                     number.
   */
  Number getNumber(String key) throws IBException;

  /**
   * Get the int value associated with a key.
   *
   * @param key A key string.
   * @return The integer value.
   * @throws IBException if the key is not found or if the value cannot be converted to an integer.
   */
  int getInt(String key) throws IBException;

  /**
   * Get the JSONArray value associated with a key.
   *
   * @param key A key string.
   * @return A JSONArray which is the value.
   * @throws IBException if the key is not found or if the value is not a JSONArray.
   */
  JSONArray getJSONArray(String key) throws IBException;

  /**
   * Get the JSONObject value associated with a key.
   *
   * @param key A key string.
   * @return A JSONObject which is the value.
   * @throws IBException if the key is not found or if the value is not a JSONObject.
   */
  JSONObject getJSONObject(String key) throws IBException;

  /**
   * Get the long value associated with a key.
   *
   * @param key A key string.
   * @return The long value.
   * @throws IBException if the key is not found or if the value cannot be converted to a long.
   */
  long getLong(String key) throws IBException;

  /**
   * Get the string associated with a key.
   *
   * @param key A key string.
   * @return A string which is the value.
   * @throws IBException if there is no string value for the key.
   */
  String getString(String key) throws IBException;

  /**
   * Determine if the JSONObject contains a specific key.
   *
   * @param key A key string.
   * @return true if the key exists in the JSONObject.
   */
  boolean has(String key);

  /**
   * Determine if the value associated with the key is <code>null</code> or if there is no value.
   *
   * @param key A key string.
   * @return true if there is no value associated with the key or if the value is the JSONObject.NULL object.
   */
  boolean isNull(String key);

  /**
   * Get an enumeration of the keys of the JSONObject. Modifying this key Set will also modify the JSONObject. Use with
   * caution.
   *
   * @see Set#iterator()
   *
   * @return An iterator of the keys.
   */
  Iterator<String> keys();

  /**
   * Get a set of keys of the JSONObject. Modifying this key Set will also modify the JSONObject. Use with caution.
   *
   * @see Map#keySet()
   *
   * @return A keySet.
   */
  Set<String> keySet();

  /**
   * Get the number of keys stored in the JSONObject.
   *
   * @return The number of keys in the JSONObject.
   */
  int length();

  /**
   * Check if JSONObject is empty.
   *
   * @return true if JSONObject is empty, otherwise false.
   */
  boolean isEmpty();

  /**
   * Produce a JSONArray containing the names of the elements of this JSONObject.
   *
   * @return A JSONArray containing the key strings, or null if the JSONObject is empty.
   */
  JSONArray names();

  /**
   * Get an optional value associated with a key.
   *
   * @param key A key string.
   * @return An object which is the value, or null if there is no value.
   */
  Object opt(String key);
  
  Optional<Object> optional(String key);

  /**
   * Get the enum value associated with a key.
   *
   * @param <E>   Enum Type
   * @param clazz The type of enum to retrieve.
   * @param key   A key string.
   * @return The enum value associated with the key or null if not found
   */
  <E extends Enum<E>> Optional<E> optEnum(Class<E> clazz, String key);

  /**
   * Get the enum value associated with a key.
   *
   * @param <E>          Enum Type
   * @param clazz        The type of enum to retrieve.
   * @param key          A key string.
   * @param defaultValue The default in case the value is not found
   * @return The enum value associated with the key or defaultValue if the value is not found or cannot be assigned to
   *         <code>clazz</code>
   */
  <E extends Enum<E>> E optEnum(Class<E> clazz, String key, E defaultValue);

  /**
   * Get an optional boolean associated with a key. It returns false if there is no such key, or if the value is not
   * Boolean.TRUE or the String "true".
   *
   * @param key A key string.
   * @return The truth.
   */
  boolean optBoolean(String key);

  /**
   * Get an optional boolean associated with a key. It returns the defaultValue if there is no such key, or if it is not
   * a Boolean or the String "true" or "false" (case insensitive).
   *
   * @param key          A key string.
   * @param defaultValue The default.
   * @return The truth.
   */
  boolean optBoolean(String key, boolean defaultValue);

  /**
   * Get an optional boolean object associated with a key. It returns false if there is no such key, or if the value is
   * not Boolean.TRUE or the String "true".
   *
   * @param key A key string.
   * @return The truth.
   */
  Optional<Boolean> optBooleanObject(String key);

  /**
   * Get an optional boolean object associated with a key. It returns the defaultValue if there is no such key, or if it
   * is not a Boolean or the String "true" or "false" (case insensitive).
   *
   * @param key          A key string.
   * @param defaultValue The default.
   * @return The truth.
   */
  Boolean optBooleanObject(String key, Boolean defaultValue);

  /**
   * Get an optional BigDecimal associated with a key, or the defaultValue if there is no such key or if its value is
   * not a number. If the value is a string, an attempt will be made to evaluate it as a number. If the value is float
   * or double, then the {@link BigDecimal#BigDecimal(double)} constructor will be used. See notes on the constructor
   * for conversion issues that may arise.
   *
   * @param key          A key string.
   * @param defaultValue The default.
   * @return An object which is the value.
   */
  BigDecimal optBigDecimal(String key, BigDecimal defaultValue);

  /**
   * Get an optional BigInteger associated with a key, or the defaultValue if there is no such key or if its value is
   * not a number. If the value is a string, an attempt will be made to evaluate it as a number.
   *
   * @param key          A key string.
   * @param defaultValue The default.
   * @return An object which is the value.
   */
  BigInteger optBigInteger(String key, BigInteger defaultValue);

  /**
   * Get an optional double associated with a key, or NaN if there is no such key or if its value is not a number. If
   * the value is a string, an attempt will be made to evaluate it as a number.
   *
   * @param key A string which is the key.
   * @return An object which is the value.
   */
  double optDouble(String key);

  /**
   * Get an optional double associated with a key, or the defaultValue if there is no such key or if its value is not a
   * number. If the value is a string, an attempt will be made to evaluate it as a number.
   *
   * @param key          A key string.
   * @param defaultValue The default.
   * @return An object which is the value.
   */
  double optDouble(String key, double defaultValue);

  /**
   * Get an optional Double object associated with a key, or NaN if there is no such key or if its value is not a
   * number. If the value is a string, an attempt will be made to evaluate it as a number.
   *
   * @param key A string which is the key.
   * @return An object which is the value.
   */
  Optional<Double> optDoubleObject(String key);

  /**
   * Get an optional Double object associated with a key, or the defaultValue if there is no such key or if its value is
   * not a number. If the value is a string, an attempt will be made to evaluate it as a number.
   *
   * @param key          A key string.
   * @param defaultValue The default.
   * @return An object which is the value.
   */
  Double optDoubleObject(String key, Double defaultValue);

  /**
   * Get the optional float value associated with an index. NaN is returned if there is no value for the index, or if
   * the value is not a number and cannot be converted to a number.
   *
   * @param key A key string.
   * @return The value.
   */
  float optFloat(String key);

  /**
   * Get the optional float value associated with an index. The defaultValue is returned if there is no value for the
   * index, or if the value is not a number and cannot be converted to a number.
   *
   * @param key          A key string.
   * @param defaultValue The default value.
   * @return The value.
   */
  float optFloat(String key, float defaultValue);

  /**
   * Get the optional Float object associated with an index. NaN is returned if there is no value for the index, or if
   * the value is not a number and cannot be converted to a number.
   *
   * @param key A key string.
   * @return The object.
   */
  Optional<Float> optFloatObject(String key);

  /**
   * Get the optional Float object associated with an index. The defaultValue is returned if there is no value for the
   * index, or if the value is not a number and cannot be converted to a number.
   *
   * @param key          A key string.
   * @param defaultValue The default object.
   * @return The object.
   */
  Float optFloatObject(String key, Float defaultValue);

  /**
   * Get an optional int value associated with a key, or zero if there is no such key or if the value is not a number.
   * If the value is a string, an attempt will be made to evaluate it as a number.
   *
   * @param key A key string.
   * @return An object which is the value.
   */
  int optInt(String key);

  /**
   * Get an optional int value associated with a key, or the default if there is no such key or if the value is not a
   * number. If the value is a string, an attempt will be made to evaluate it as a number.
   *
   * @param key          A key string.
   * @param defaultValue The default.
   * @return An object which is the value.
   */
  int optInt(String key, int defaultValue);

  /**
   * Get an optional Integer object associated with a key, or zero if there is no such key or if the value is not a
   * number. If the value is a string, an attempt will be made to evaluate it as a number.
   *
   * @param key A key string.
   * @return An object which is the value.
   */
  Optional<Integer> optIntegerObject(String key);

  /**
   * Get an optional Integer object associated with a key, or the default if there is no such key or if the value is not
   * a number. If the value is a string, an attempt will be made to evaluate it as a number.
   *
   * @param key          A key string.
   * @param defaultValue The default.
   * @return An object which is the value.
   */
  Integer optIntegerObject(String key, Integer defaultValue);

  /**
   * Get an optional JSONArray associated with a key. It returns null if there is no such key, or if its value is not a
   * JSONArray.
   *
   * @param key A key string.
   * @return A JSONArray which is the value.
   */
  Optional<JSONArray> optJSONArray(String key);

  /**
   * Get an optional JSONArray associated with a key, or the default if there is no such key, or if its value is not a
   * JSONArray.
   *
   * @param key          A key string.
   * @param defaultValue The default.
   * @return A JSONArray which is the value.
   */
  JSONArray optJSONArray(String key, JSONArray defaultValue);

  /**
   * Get an optional JSONObject associated with a key. It returns null if there is no such key, or if its value is not a
   * JSONObject.
   *
   * @param key A key string.
   * @return A JSONObject which is the value.
   */
  Optional<JSONObject> optJSONObject(String key);

  /**
   * Get an optional JSONObject associated with a key, or the default if there is no such key or if the value is not a
   * JSONObject.
   *
   * @param key          A key string.
   * @param defaultValue The default.
   * @return An JSONObject which is the value.
   */
  JSONObject optJSONObject(String key, JSONObject defaultValue);

  /**
   * Get an optional long value associated with a key, or zero if there is no such key or if the value is not a number.
   * If the value is a string, an attempt will be made to evaluate it as a number.
   *
   * @param key A key string.
   * @return An object which is the value.
   */
  long optLong(String key);

  /**
   * Get an optional long value associated with a key, or the default if there is no such key or if the value is not a
   * number. If the value is a string, an attempt will be made to evaluate it as a number.
   *
   * @param key          A key string.
   * @param defaultValue The default.
   * @return An object which is the value.
   */
  long optLong(String key, long defaultValue);

  /**
   * Get an optional Long object associated with a key, or zero if there is no such key or if the value is not a number.
   * If the value is a string, an attempt will be made to evaluate it as a number.
   *
   * @param key A key string.
   * @return An object which is the value.
   */
  Optional<Long> optLongObject(String key);

  /**
   * Get an optional Long object associated with a key, or the default if there is no such key or if the value is not a
   * number. If the value is a string, an attempt will be made to evaluate it as a number.
   *
   * @param key          A key string.
   * @param defaultValue The default.
   * @return An object which is the value.
   */
  Long optLongObject(String key, Long defaultValue);

  /**
   * Get an optional {@link Number} value associated with a key, or <code>null</code> if there is no such key or if the
   * value is not a number. If the value is a string, an attempt will be made to evaluate it as a number
   * ({@link BigDecimal}). This method would be used in cases where type coercion of the number value is unwanted.
   *
   * @param key A key string.
   * @return An object which is the value.
   */
  Optional<Number> optNumber(String key);

  /**
   * Get an optional {@link Number} value associated with a key, or the default if there is no such key or if the value
   * is not a number. If the value is a string, an attempt will be made to evaluate it as a number. This method would be
   * used in cases where type coercion of the number value is unwanted.
   *
   * @param key          A key string.
   * @param defaultValue The default.
   * @return An object which is the value.
   */
  Number optNumber(String key, Number defaultValue);

  /**
   * Get an optional string associated with a key. It returns an empty string if there is no such key. If the value is
   * not a string and is not null, then it is converted to a string.
   *
   * @param key A key string.
   * @return A string which is the value.
   */
  Optional<String> optString(String key);

  /**
   * Get an optional string associated with a key. It returns the defaultValue if there is no such key.
   *
   * @param key          A key string.
   * @param defaultValue The default.
   * @return A string which is the value.
   */
  String optString(String key, String defaultValue);

  /**
   * Creates a JSONPointer using an initialization string and tries to match it to an item within this JSONObject. For
   * example, given a JSONObject initialized with this document:
   * 
   * <pre>
   * {
   *     "a":{"b":"c"}
   * }
   * </pre>
   * 
   * and this JSONPointer string:
   * 
   * <pre>
   * "/a/b"
   * </pre>
   * 
   * Then this method will return the String "c". A JSONPointerException may be thrown from code called by this method.
   *
   * @param jsonPointer string that can be used to create a JSONPointer
   * @return the item matched by the JSONPointer, otherwise null
   */
  Object query(String jsonPointer);

  /**
   * Uses a user initialized JSONPointer and tries to match it to an item within this JSONObject. For example, given a
   * JSONObject initialized with this document:
   * 
   * <pre>
   * {
   *     "a":{"b":"c"}
   * }
   * </pre>
   * 
   * and this JSONPointer:
   * 
   * <pre>
   * "/a/b"
   * </pre>
   * 
   * Then this method will return the String "c". A JSONPointerException may be thrown from code called by this method.
   *
   * @param jsonPointer string that can be used to create a JSONPointer
   * @return the item matched by the JSONPointer, otherwise null
   */
  Object query(JSONPointer jsonPointer);

  /**
   * Queries and returns a value from this object using {@code jsonPointer}, or returns null if the query fails due to a
   * missing key.
   *
   * @param jsonPointer the string representation of the JSON pointer
   * @return the queried value or {@code null}
   * @throws IllegalArgumentException if {@code jsonPointer} has invalid syntax
   */
  Optional<Object> optQuery(String jsonPointer);

  /**
   * Queries and returns a value from this object using {@code jsonPointer}, or returns null if the query fails due to a
   * missing key.
   *
   * @param jsonPointer The JSON pointer
   * @return the queried value or {@code null}
   * @throws IllegalArgumentException if {@code jsonPointer} has invalid syntax
   */
  Optional<Object> optQuery(JSONPointer jsonPointer);

  /**
   * Determine if two JSONObjects are similar. They must contain the same set of names which must be associated with
   * similar values.
   *
   * @param other The other JSONObject
   * @return true if they are equal
   */
  boolean similar(Object other);

  /**
   * Produce a JSONArray containing the values of the members of this JSONObject.
   *
   * @param names A JSONArray containing a list of key strings. This determines the sequence of the values in the
   *              result.
   * @return A JSONArray of values.
   * @throws IBException If any of the values are non-finite numbers.
   */
  JSONArray toJSONArray(JSONArray names) throws IBException;

  /**
   * Make a JSON text of this JSONObject. For compactness, no whitespace is added. If this would not result in a
   * syntactically correct JSON text, then null will be returned instead.
   * <p>
   * <b> Warning: This method assumes that the data structure is acyclical. </b>
   *
   * @return a printable, displayable, portable, transmittable representation of the object, beginning with
   *         <code>{</code>&nbsp;<small>(left brace)</small> and ending with <code>}</code>&nbsp;<small>(right
   *         brace)</small>.
   */
  String toString();

  /**
   * Make a pretty-printed JSON text of this JSONObject.
   *
   * <p>
   * If
   * 
   * <pre>{@code
   * indentFactor > 0
   * }</pre>
   * 
   * and the {@link JSONObject} has only one key, then the object will be output on a single line:
   * 
   * <pre>{@code {"key": 1}}</pre>
   *
   * <p>
   * If an object has 2 or more keys, then it will be output across multiple lines:
   * 
   * <pre>{@code {
   *  "key1": 1,
   *  "key2": "value 2",
   *  "key3": 3
   * }}</pre>
   * <p>
   * <b> Warning: This method assumes that the data structure is acyclical. </b>
   *
   * @param indentFactor The number of spaces to add to each level of indentation.
   * @return a printable, displayable, portable, transmittable representation of the object, beginning with
   *         <code>{</code>&nbsp;<small>(left brace)</small> and ending with <code>}</code>&nbsp;<small>(right
   *         brace)</small>.
   * @throws IBException If the object contains an invalid number.
   */
  String toString(int indentFactor) throws IBException;

  /**
   * Write the contents of the JSONObject as JSON text to a writer. For compactness, no whitespace is added.
   * <p>
   * <b> Warning: This method assumes that the data structure is acyclical. </b>
   * 
   * @param writer the writer object
   * @return The writer.
   * @throws IBException if a called function has an error
   */
  Writer write(Writer writer) throws IBException;

  /**
   * Write the contents of the JSONObject as JSON text to a writer.
   *
   * <p>
   * If
   * 
   * <pre>{@code
   * indentFactor > 0
   * }</pre>
   * 
   * and the {@link JSONObject} has only one key, then the object will be output on a single line:
   * 
   * <pre>{@code {"key": 1}}</pre>
   *
   * <p>
   * If an object has 2 or more keys, then it will be output across multiple lines:
   * 
   * <pre>{@code {
   *  "key1": 1,
   *  "key2": "value 2",
   *  "key3": 3
   * }}</pre>
   * <p>
   * <b> Warning: This method assumes that the data structure is acyclical. </b>
   *
   * @param writer       Writes the serialized JSON
   * @param indentFactor The number of spaces to add to each level of indentation.
   * @param indent       The indentation of the top level.
   * @return The writer.
   * @throws IBException if a called function has an error or a write error occurs
   */
  Writer write(Writer writer, int indentFactor, int indent) throws IBException;

  /**
   * Returns a java.util.Map containing all of the entries in this object. If an entry in the object is a JSONArray or
   * JSONObject it will also be converted.
   * <p>
   * Warning: This method assumes that the data structure is acyclical.
   *
   * @return a java.util.Map containing the entries of this object
   */
  Map<String, Object> toMap();

}
