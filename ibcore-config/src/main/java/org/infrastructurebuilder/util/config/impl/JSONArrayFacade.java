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

import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONPointer;

// Not currently used
@Deprecated
public interface JSONArrayFacade {

  Iterator<Object> iterator();

  /**
   * Get the object value associated with an index.
   *
   * @param index The index must be between 0 and length() - 1.
   * @return An object value.
   * @throws JSONException If there is no value for the index.
   */
  Object get(int index) throws JSONException;

  /**
   * Get the boolean value associated with an index. The string values "true" and "false" are converted to boolean.
   *
   * @param index The index must be between 0 and length() - 1.
   * @return The truth.
   * @throws JSONException If there is no value for the index or if the value is not convertible to boolean.
   */
  boolean getBoolean(int index) throws JSONException;

  /**
   * Get the double value associated with an index.
   *
   * @param index The index must be between 0 and length() - 1.
   * @return The value.
   * @throws JSONException If the key is not found or if the value cannot be converted to a number.
   */
  double getDouble(int index) throws JSONException;

  /**
   * Get the float value associated with a key.
   *
   * @param index The index must be between 0 and length() - 1.
   * @return The numeric value.
   * @throws JSONException if the key is not found or if the value is not a Number object and cannot be converted to a
   *                       number.
   */
  float getFloat(int index) throws JSONException;

  /**
   * Get the Number value associated with a key.
   *
   * @param index The index must be between 0 and length() - 1.
   * @return The numeric value.
   * @throws JSONException if the key is not found or if the value is not a Number object and cannot be converted to a
   *                       number.
   */
  Number getNumber(int index) throws JSONException;

  /**
   * Get the enum value associated with an index.
   *
   * @param <E>   Enum Type
   * @param clazz The type of enum to retrieve.
   * @param index The index must be between 0 and length() - 1.
   * @return The enum value at the index location
   * @throws JSONException if the key is not found or if the value cannot be converted to an enum.
   */
  <E extends Enum<E>> E getEnum(Class<E> clazz, int index) throws JSONException;

  /**
   * Get the BigDecimal value associated with an index. If the value is float or double, the
   * {@link BigDecimal#BigDecimal(double)} constructor will be used. See notes on the constructor for conversion issues
   * that may arise.
   *
   * @param index The index must be between 0 and length() - 1.
   * @return The value.
   * @throws JSONException If the key is not found or if the value cannot be converted to a BigDecimal.
   */
  BigDecimal getBigDecimal(int index) throws JSONException;

  /**
   * Get the BigInteger value associated with an index.
   *
   * @param index The index must be between 0 and length() - 1.
   * @return The value.
   * @throws JSONException If the key is not found or if the value cannot be converted to a BigInteger.
   */
  BigInteger getBigInteger(int index) throws JSONException;

  /**
   * Get the int value associated with an index.
   *
   * @param index The index must be between 0 and length() - 1.
   * @return The value.
   * @throws JSONException If the key is not found or if the value is not a number.
   */
  int getInt(int index) throws JSONException;

  /**
   * Get the JSONArray associated with an index.
   *
   * @param index The index must be between 0 and length() - 1.
   * @return A JSONArray value.
   * @throws JSONException If there is no value for the index. or if the value is not a JSONArray
   */
  JSONArray getJSONArray(int index) throws JSONException;

  /**
   * Get the JSONObject associated with an index.
   *
   * @param index subscript
   * @return A JSONObject value.
   * @throws JSONException If there is no value for the index or if the value is not a JSONObject
   */
  JSONObject getJSONObject(int index) throws JSONException;

  /**
   * Get the long value associated with an index.
   *
   * @param index The index must be between 0 and length() - 1.
   * @return The value.
   * @throws JSONException If the key is not found or if the value cannot be converted to a number.
   */
  long getLong(int index) throws JSONException;

  /**
   * Get the string associated with an index.
   *
   * @param index The index must be between 0 and length() - 1.
   * @return A string value.
   * @throws JSONException If there is no string value for the index.
   */
  String getString(int index) throws JSONException;

  /**
   * Determine if the value is <code>null</code>.
   *
   * @param index The index must be between 0 and length() - 1.
   * @return true if the value at the index is <code>null</code>, or if there is no value.
   */
  boolean isNull(int index);

  /**
   * Make a string from the contents of this JSONArray. The <code>separator</code> string is inserted between each
   * element. Warning: This method assumes that the data structure is acyclical.
   *
   * @param separator A string that will be inserted between the elements.
   * @return a string.
   * @throws JSONException If the array contains an invalid number.
   */
  String join(String separator) throws JSONException;

  /**
   * Get the number of elements in the JSONArray, included nulls.
   *
   * @return The length (or size).
   */
  int length();

  /**
   * Get the optional object value associated with an index.
   *
   * @param index The index must be between 0 and length() - 1. If not, null is returned.
   * @return An object value, or null if there is no object at that index.
   */
  Object opt(int index);

  /**
   * Get the optional boolean value associated with an index. It returns false if there is no value at that index, or if
   * the value is not Boolean.TRUE or the String "true".
   *
   * @param index The index must be between 0 and length() - 1.
   * @return The truth.
   */
  boolean optBoolean(int index);

  /**
   * Get the optional boolean value associated with an index. It returns the defaultValue if there is no value at that
   * index or if it is not a Boolean or the String "true" or "false" (case insensitive).
   *
   * @param index        The index must be between 0 and length() - 1.
   * @param defaultValue A boolean default.
   * @return The truth.
   */
  boolean optBoolean(int index, boolean defaultValue);

  /**
   * Get the optional Boolean object associated with an index. It returns false if there is no value at that index, or
   * if the value is not Boolean.TRUE or the String "true".
   *
   * @param index The index must be between 0 and length() - 1.
   * @return The truth.
   */
  Boolean optBooleanObject(int index);

  /**
   * Get the optional Boolean object associated with an index. It returns the defaultValue if there is no value at that
   * index or if it is not a Boolean or the String "true" or "false" (case insensitive).
   *
   * @param index        The index must be between 0 and length() - 1.
   * @param defaultValue A boolean default.
   * @return The truth.
   */
  Boolean optBooleanObject(int index, Boolean defaultValue);

  /**
   * Get the optional double value associated with an index. NaN is returned if there is no value for the index, or if
   * the value is not a number and cannot be converted to a number.
   *
   * @param index The index must be between 0 and length() - 1.
   * @return The value.
   */
  double optDouble(int index);

  /**
   * Get the optional double value associated with an index. The defaultValue is returned if there is no value for the
   * index, or if the value is not a number and cannot be converted to a number.
   *
   * @param index        subscript
   * @param defaultValue The default value.
   * @return The value.
   */
  double optDouble(int index, double defaultValue);

  /**
   * Get the optional Double object associated with an index. NaN is returned if there is no value for the index, or if
   * the value is not a number and cannot be converted to a number.
   *
   * @param index The index must be between 0 and length() - 1.
   * @return The object.
   */
  Double optDoubleObject(int index);

  /**
   * Get the optional double value associated with an index. The defaultValue is returned if there is no value for the
   * index, or if the value is not a number and cannot be converted to a number.
   *
   * @param index        subscript
   * @param defaultValue The default object.
   * @return The object.
   */
  Double optDoubleObject(int index, Double defaultValue);

  /**
   * Get the optional float value associated with an index. NaN is returned if there is no value for the index, or if
   * the value is not a number and cannot be converted to a number.
   *
   * @param index The index must be between 0 and length() - 1.
   * @return The value.
   */
  float optFloat(int index);

  /**
   * Get the optional float value associated with an index. The defaultValue is returned if there is no value for the
   * index, or if the value is not a number and cannot be converted to a number.
   *
   * @param index        subscript
   * @param defaultValue The default value.
   * @return The value.
   */
  float optFloat(int index, float defaultValue);

  /**
   * Get the optional Float object associated with an index. NaN is returned if there is no value for the index, or if
   * the value is not a number and cannot be converted to a number.
   *
   * @param index The index must be between 0 and length() - 1.
   * @return The object.
   */
  Float optFloatObject(int index);

  /**
   * Get the optional Float object associated with an index. The defaultValue is returned if there is no value for the
   * index, or if the value is not a number and cannot be converted to a number.
   *
   * @param index        subscript
   * @param defaultValue The default object.
   * @return The object.
   */
  Float optFloatObject(int index, Float defaultValue);

  /**
   * Get the optional int value associated with an index. Zero is returned if there is no value for the index, or if the
   * value is not a number and cannot be converted to a number.
   *
   * @param index The index must be between 0 and length() - 1.
   * @return The value.
   */
  int optInt(int index);

  /**
   * Get the optional int value associated with an index. The defaultValue is returned if there is no value for the
   * index, or if the value is not a number and cannot be converted to a number.
   *
   * @param index        The index must be between 0 and length() - 1.
   * @param defaultValue The default value.
   * @return The value.
   */
  int optInt(int index, int defaultValue);

  /**
   * Get the optional Integer object associated with an index. Zero is returned if there is no value for the index, or
   * if the value is not a number and cannot be converted to a number.
   *
   * @param index The index must be between 0 and length() - 1.
   * @return The object.
   */
  Integer optIntegerObject(int index);

  /**
   * Get the optional Integer object associated with an index. The defaultValue is returned if there is no value for the
   * index, or if the value is not a number and cannot be converted to a number.
   *
   * @param index        The index must be between 0 and length() - 1.
   * @param defaultValue The default object.
   * @return The object.
   */
  Integer optIntegerObject(int index, Integer defaultValue);

  /**
   * Get the enum value associated with a key.
   *
   * @param <E>   Enum Type
   * @param clazz The type of enum to retrieve.
   * @param index The index must be between 0 and length() - 1.
   * @return The enum value at the index location or null if not found
   */
  <E extends Enum<E>> E optEnum(Class<E> clazz, int index);

  /**
   * Get the enum value associated with a key.
   *
   * @param <E>          Enum Type
   * @param clazz        The type of enum to retrieve.
   * @param index        The index must be between 0 and length() - 1.
   * @param defaultValue The default in case the value is not found
   * @return The enum value at the index location or defaultValue if the value is not found or cannot be assigned to
   *         clazz
   */
  <E extends Enum<E>> E optEnum(Class<E> clazz, int index, E defaultValue);

  /**
   * Get the optional BigInteger value associated with an index. The defaultValue is returned if there is no value for
   * the index, or if the value is not a number and cannot be converted to a number.
   *
   * @param index        The index must be between 0 and length() - 1.
   * @param defaultValue The default value.
   * @return The value.
   */
  BigInteger optBigInteger(int index, BigInteger defaultValue);

  /**
   * Get the optional BigDecimal value associated with an index. The defaultValue is returned if there is no value for
   * the index, or if the value is not a number and cannot be converted to a number. If the value is float or double,
   * the {@link BigDecimal#BigDecimal(double)} constructor will be used. See notes on the constructor for conversion
   * issues that may arise.
   *
   * @param index        The index must be between 0 and length() - 1.
   * @param defaultValue The default value.
   * @return The value.
   */
  BigDecimal optBigDecimal(int index, BigDecimal defaultValue);

  /**
   * Get the optional JSONArray associated with an index. Null is returned if there is no value at that index or if the
   * value is not a JSONArray.
   *
   * @param index The index must be between 0 and length() - 1.
   * @return A JSONArray value.
   */
  JSONArray optJSONArray(int index);

  /**
   * Get the optional JSONArray associated with an index. The defaultValue is returned if there is no value at that
   * index or if the value is not a JSONArray.
   *
   * @param index        The index must be between 0 and length() - 1.
   * @param defaultValue The default.
   * @return A JSONArray value.
   */
  JSONArray optJSONArray(int index, JSONArray defaultValue);

  /**
   * Get the optional JSONObject associated with an index. Null is returned if there is no value at that index or if the
   * value is not a JSONObject.
   *
   * @param index The index must be between 0 and length() - 1.
   * @return A JSONObject value.
   */
  JSONObject optJSONObject(int index);

  /**
   * Get the optional JSONObject associated with an index. The defaultValue is returned if there is no value at that
   * index or if the value is not a JSONObject.
   *
   * @param index        The index must be between 0 and length() - 1.
   * @param defaultValue The default.
   * @return A JSONObject value.
   */
  JSONObject optJSONObject(int index, JSONObject defaultValue);

  /**
   * Get the optional long value associated with an index. Zero is returned if there is no value for the index, or if
   * the value is not a number and cannot be converted to a number.
   *
   * @param index The index must be between 0 and length() - 1.
   * @return The value.
   */
  long optLong(int index);

  /**
   * Get the optional long value associated with an index. The defaultValue is returned if there is no value for the
   * index, or if the value is not a number and cannot be converted to a number.
   *
   * @param index        The index must be between 0 and length() - 1.
   * @param defaultValue The default value.
   * @return The value.
   */
  long optLong(int index, long defaultValue);

  /**
   * Get the optional Long object associated with an index. Zero is returned if there is no value for the index, or if
   * the value is not a number and cannot be converted to a number.
   *
   * @param index The index must be between 0 and length() - 1.
   * @return The object.
   */
  Long optLongObject(int index);

  /**
   * Get the optional Long object associated with an index. The defaultValue is returned if there is no value for the
   * index, or if the value is not a number and cannot be converted to a number.
   *
   * @param index        The index must be between 0 and length() - 1.
   * @param defaultValue The default object.
   * @return The object.
   */
  Long optLongObject(int index, Long defaultValue);

  /**
   * Get an optional {@link Number} value associated with a key, or <code>null</code> if there is no such key or if the
   * value is not a number. If the value is a string, an attempt will be made to evaluate it as a number
   * ({@link BigDecimal}). This method would be used in cases where type coercion of the number value is unwanted.
   *
   * @param index The index must be between 0 and length() - 1.
   * @return An object which is the value.
   */
  Number optNumber(int index);

  /**
   * Get an optional {@link Number} value associated with a key, or the default if there is no such key or if the value
   * is not a number. If the value is a string, an attempt will be made to evaluate it as a number ({@link BigDecimal}).
   * This method would be used in cases where type coercion of the number value is unwanted.
   *
   * @param index        The index must be between 0 and length() - 1.
   * @param defaultValue The default.
   * @return An object which is the value.
   */
  Number optNumber(int index, Number defaultValue);

  /**
   * Get the optional string value associated with an index. It returns an empty string if there is no value at that
   * index. If the value is not a string and is not null, then it is converted to a string.
   *
   * @param index The index must be between 0 and length() - 1.
   * @return A String value.
   */
  String optString(int index);

  /**
   * Get the optional string associated with an index. The defaultValue is returned if the key is not found.
   *
   * @param index        The index must be between 0 and length() - 1.
   * @param defaultValue The default value.
   * @return A String value.
   */
  String optString(int index, String defaultValue);

  /**
   * Creates a JSONPointer using an initialization string and tries to match it to an item within this JSONArray. For
   * example, given a JSONArray initialized with this document:
   *
   * <pre>
   * [
   *     {"b":"c"}
   * ]
   * </pre>
   *
   * and this JSONPointer string:
   *
   * <pre>
   * "/0/b"
   * </pre>
   *
   * Then this method will return the String "c" A JSONPointerException may be thrown from code called by this method.
   *
   * @param jsonPointer string that can be used to create a JSONPointer
   * @return the item matched by the JSONPointer, otherwise null
   */
  Object query(String jsonPointer);

  /**
   * Uses a user initialized JSONPointer and tries to match it to an item within this JSONArray. For example, given a
   * JSONArray initialized with this document:
   *
   * <pre>
   * [
   *     {"b":"c"}
   * ]
   * </pre>
   *
   * and this JSONPointer:
   *
   * <pre>
   * "/0/b"
   * </pre>
   *
   * Then this method will return the String "c" A JSONPointerException may be thrown from code called by this method.
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
  Object optQuery(String jsonPointer);

  /**
   * Queries and returns a value from this object using {@code jsonPointer}, or returns null if the query fails due to a
   * missing key.
   *
   * @param jsonPointer The JSON pointer
   * @return the queried value or {@code null}
   * @throws IllegalArgumentException if {@code jsonPointer} has invalid syntax
   */
  Object optQuery(JSONPointer jsonPointer);

  /**
   * Remove an index and close the hole.
   *
   * @param index The index of the element to be removed.
   * @return The value that was associated with the index, or null if there was no value.
   */
  Object remove(int index);

  /**
   * Determine if two JSONArrays are similar. They must contain similar sequences.
   *
   * @param other The other JSONArray
   * @return true if they are equal
   */
  boolean similar(Object other);

  /**
   * Produce a JSONObject by combining a JSONArray of names with the values of this JSONArray.
   *
   * @param names A JSONArray containing a list of key strings. These will be paired with the values.
   * @return A JSONObject, or null if there are no names or if this JSONArray has no values.
   * @throws JSONException If any of the names are null.
   */
  JSONObject toJSONObject(JSONArray names) throws JSONException;

  /**
   * Make a JSON text of this JSONArray. For compactness, no unnecessary whitespace is added. If it is not possible to
   * produce a syntactically correct JSON text then null will be returned instead. This could occur if the array
   * contains an invalid number.
   * <p>
   * <b> Warning: This method assumes that the data structure is acyclical. </b>
   *
   * @return a printable, displayable, transmittable representation of the array.
   */
  String toString();

  /**
   * Make a pretty-printed JSON text of this JSONArray.
   *
   * <p>
   * If
   *
   * <pre> {@code
   * indentFactor > 0
   * }</pre>
   *
   * and the {@link JSONArray} has only one element, then the array will be output on a single line:
   *
   * <pre>{@code [1]}</pre>
   *
   * <p>
   * If an array has 2 or more elements, then it will be output across multiple lines:
   *
   * <pre>{@code
   * [
   * 1,
   * "value 2",
   * 3
   * ]
   * }</pre>
   * <p>
   * <b> Warning: This method assumes that the data structure is acyclical. </b>
   *
   * @param indentFactor The number of spaces to add to each level of indentation.
   * @return a printable, displayable, transmittable representation of the object, beginning with
   *         <code>[</code>&nbsp;<small>(left bracket)</small> and ending with <code>]</code> &nbsp;<small>(right
   *         bracket)</small>.
   * @throws JSONException if a called function fails
   */
  String toString(int indentFactor) throws JSONException;

  /**
   * Write the contents of the JSONArray as JSON text to a writer. For compactness, no whitespace is added.
   * <p>
   * <b> Warning: This method assumes that the data structure is acyclical. </b>
   *
   * @param writer the writer object
   * @return The writer.
   * @throws JSONException if a called function fails
   */
  Writer write(Writer writer) throws JSONException;

  /**
   * Write the contents of the JSONArray as JSON text to a writer.
   *
   * <p>
   * If
   *
   * <pre>{@code
   * indentFactor > 0
   * }</pre>
   *
   * and the {@link JSONArray} has only one element, then the array will be output on a single line:
   *
   * <pre>{@code [1]}</pre>
   *
   * <p>
   * If an array has 2 or more elements, then it will be output across multiple lines:
   *
   * <pre>{@code
   * [
   * 1,
   * "value 2",
   * 3
   * ]
   * }</pre>
   * <p>
   * <b> Warning: This method assumes that the data structure is acyclical. </b>
   *
   * @param writer       Writes the serialized JSON
   * @param indentFactor The number of spaces to add to each level of indentation.
   * @param indent       The indentation of the top level.
   * @return The writer.
   * @throws JSONException if a called function fails or unable to write
   */
  Writer write(Writer writer, int indentFactor, int indent) throws JSONException;

  /**
   * Returns a java.util.List containing all of the elements in this array. If an element in the array is a JSONArray or
   * JSONObject it will also be converted to a List and a Map respectively.
   * <p>
   * Warning: This method assumes that the data structure is acyclical.
   *
   * @return a java.util.List containing the elements of this array
   */
  List<Object> toList();

  /**
   * Check if JSONArray is empty.
   *
   * @return true if JSONArray is empty, otherwise false.
   */
  boolean isEmpty();

}
