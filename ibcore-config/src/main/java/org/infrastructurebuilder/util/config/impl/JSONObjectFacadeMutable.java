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

import java.util.Collection;
import java.util.Map;

import org.infrastructurebuilder.util.config.ConfigMap;
import org.json.JSONException;
import org.json.JSONObject;

public interface JSONObjectFacadeMutable extends ConfigMap {

  /**
   * Accumulate values under a key. It is similar to the put method except that if there is already an object stored
   * under the key then a JSONArray is stored under the key to hold all of the accumulated values. If there is already a
   * JSONArray, then the new value is appended to it. In contrast, the put method replaces the previous value.
   *
   * If only one value is accumulated that is not a JSONArray, then the result will be the same as using put. But if
   * multiple values are accumulated, then the result will be like append.
   *
   * @param key   A key string.
   * @param value An object to be accumulated under the key.
   * @return this.
   * @throws JSONException        If the value is non-finite number.
   * @throws NullPointerException If the key is <code>null</code>.
   */
  JSONObject accumulate(String key, Object value) throws JSONException;

  /**
   * Append values to the array under a key. If the key does not exist in the JSONObject, then the key is put in the
   * JSONObject with its value being a JSONArray containing the value parameter. If the key was already associated with
   * a JSONArray, then the value parameter is appended to it.
   *
   * @param key   A key string.
   * @param value An object to be accumulated under the key.
   * @return this.
   * @throws JSONException        If the value is non-finite number or if the current value associated with the key is
   *                              not a JSONArray.
   * @throws NullPointerException If the key is <code>null</code>.
   */
  JSONObject append(String key, Object value) throws JSONException;

  /**
   * Increment a property of a JSONObject. If there is no such property, create one with a value of 1 (Integer). If
   * there is such a property, and if it is an Integer, Long, Double, Float, BigInteger, or BigDecimal then add one to
   * it. No overflow bounds checking is performed, so callers should initialize the key prior to this call with an
   * appropriate type that can handle the maximum expected value.
   *
   * @param key A key string.
   * @return this.
   * @throws JSONException If there is already a property with this name that is not an Integer, Long, Double, or Float.
   */
  JSONObject increment(String key) throws JSONException;

  /**
   * Removes all of the elements from this JSONObject. The JSONObject will be empty after this call returns.
   */
  void clear();

  /**
   * Put a key/boolean pair in the JSONObject.
   *
   * @param key   A key string.
   * @param value A boolean which is the value.
   * @return this.
   * @throws JSONException        If the value is non-finite number.
   * @throws NullPointerException If the key is <code>null</code>.
   */
  JSONObject put(String key, boolean value) throws JSONException;

  /**
   * Put a key/value pair in the JSONObject, where the value will be a JSONArray which is produced from a Collection.
   *
   * @param key   A key string.
   * @param value A Collection value.
   * @return this.
   * @throws JSONException        If the value is non-finite number.
   * @throws NullPointerException If the key is <code>null</code>.
   */
  JSONObject put(String key, Collection<?> value) throws JSONException;

  /**
   * Put a key/double pair in the JSONObject.
   *
   * @param key   A key string.
   * @param value A double which is the value.
   * @return this.
   * @throws JSONException        If the value is non-finite number.
   * @throws NullPointerException If the key is <code>null</code>.
   */
  JSONObject put(String key, double value) throws JSONException;

  /**
   * Put a key/float pair in the JSONObject.
   *
   * @param key   A key string.
   * @param value A float which is the value.
   * @return this.
   * @throws JSONException        If the value is non-finite number.
   * @throws NullPointerException If the key is <code>null</code>.
   */
  JSONObject put(String key, float value) throws JSONException;

  /**
   * Put a key/int pair in the JSONObject.
   *
   * @param key   A key string.
   * @param value An int which is the value.
   * @return this.
   * @throws JSONException        If the value is non-finite number.
   * @throws NullPointerException If the key is <code>null</code>.
   */
  JSONObject put(String key, int value) throws JSONException;

  /**
   * Put a key/long pair in the JSONObject.
   *
   * @param key   A key string.
   * @param value A long which is the value.
   * @return this.
   * @throws JSONException        If the value is non-finite number.
   * @throws NullPointerException If the key is <code>null</code>.
   */
  JSONObject put(String key, long value) throws JSONException;

  /**
   * Put a key/value pair in the JSONObject, where the value will be a JSONObject which is produced from a Map.
   *
   * @param key   A key string.
   * @param value A Map value.
   * @return this.
   * @throws JSONException        If the value is non-finite number.
   * @throws NullPointerException If the key is <code>null</code>.
   */
  JSONObject put(String key, Map<?, ?> value) throws JSONException;

  /**
   * Put a key/value pair in the JSONObject. If the value is <code>null</code>, then the key will be removed from the
   * JSONObject if it is present.
   *
   * @param key   A key string.
   * @param value An object which is the value. It should be of one of these types: Boolean, Double, Integer, JSONArray,
   *              JSONObject, Long, String, or the JSONObject.NULL object.
   * @return this.
   * @throws JSONException        If the value is non-finite number.
   * @throws NullPointerException If the key is <code>null</code>.
   */
  JSONObject put(String key, Object value) throws JSONException;

  /**
   * Put a key/value pair in the JSONObject, but only if the key and the value are both non-null, and only if there is
   * not already a member with that name.
   *
   * @param key   key to insert into
   * @param value value to insert
   * @return this.
   * @throws JSONException if the key is a duplicate
   */
  JSONObject putOnce(String key, Object value) throws JSONException;

  /**
   * Put a key/value pair in the JSONObject, but only if the key and the value are both non-null.
   *
   * @param key   A key string.
   * @param value An object which is the value. It should be of one of these types: Boolean, Double, Integer, JSONArray,
   *              JSONObject, Long, String, or the JSONObject.NULL object.
   * @return this.
   * @throws JSONException If the value is a non-finite number.
   */
  JSONObject putOpt(String key, Object value) throws JSONException;

  /**
   * Remove a name and its value, if present.
   *
   * @param key The name to be removed.
   * @return The value that was associated with the name, or null if there was no value.
   */
  Object remove(String key);

}
