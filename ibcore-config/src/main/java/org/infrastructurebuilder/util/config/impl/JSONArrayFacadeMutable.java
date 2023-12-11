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

import org.json.JSONArray;
import org.json.JSONException;

@Deprecated
public interface JSONArrayFacadeMutable extends JSONArrayFacade {

  /**
   * Removes all of the elements from this JSONArray. The JSONArray will be empty after this call returns.
   */
  void clear();

  /**
   * Append a boolean value. This increases the array's length by one.
   *
   * @param value A boolean value.
   * @return this.
   */
  JSONArray put(boolean value);

  /**
   * Put a value in the JSONArray, where the value will be a JSONArray which is produced from a Collection.
   *
   * @param value A Collection value.
   * @return this.
   * @throws JSONException If the value is non-finite number.
   */
  JSONArray put(Collection<?> value);

  /**
   * Append a double value. This increases the array's length by one.
   *
   * @param value A double value.
   * @return this.
   * @throws JSONException if the value is not finite.
   */
  JSONArray put(double value) throws JSONException;

  /**
   * Append a float value. This increases the array's length by one.
   *
   * @param value A float value.
   * @return this.
   * @throws JSONException if the value is not finite.
   */
  JSONArray put(float value) throws JSONException;

  /**
   * Append an int value. This increases the array's length by one.
   *
   * @param value An int value.
   * @return this.
   */
  JSONArray put(int value);

  /**
   * Append an long value. This increases the array's length by one.
   *
   * @param value A long value.
   * @return this.
   */
  JSONArray put(long value);

  /**
   * Put a value in the JSONArray, where the value will be a JSONObject which is produced from a Map.
   *
   * @param value A Map value.
   * @return this.
   * @throws JSONException        If a value in the map is non-finite number.
   * @throws NullPointerException If a key in the map is <code>null</code>
   */
  JSONArray put(Map<?, ?> value);

  /**
   * Append an object value. This increases the array's length by one.
   *
   * @param value An object value. The value should be a Boolean, Double, Integer, JSONArray, JSONObject, Long, or
   *              String, or the JSONObject.NULL object.
   * @return this.
   * @throws JSONException If the value is non-finite number.
   */
  JSONArray put(Object value);

  /**
   * Put or replace a boolean value in the JSONArray. If the index is greater than the length of the JSONArray, then
   * null elements will be added as necessary to pad it out.
   *
   * @param index The subscript.
   * @param value A boolean value.
   * @return this.
   * @throws JSONException If the index is negative.
   */
  JSONArray put(int index, boolean value) throws JSONException;

  /**
   * Put a value in the JSONArray, where the value will be a JSONArray which is produced from a Collection.
   *
   * @param index The subscript.
   * @param value A Collection value.
   * @return this.
   * @throws JSONException If the index is negative or if the value is non-finite.
   */
  JSONArray put(int index, Collection<?> value) throws JSONException;

  /**
   * Put or replace a double value. If the index is greater than the length of the JSONArray, then null elements will be
   * added as necessary to pad it out.
   *
   * @param index The subscript.
   * @param value A double value.
   * @return this.
   * @throws JSONException If the index is negative or if the value is non-finite.
   */
  JSONArray put(int index, double value) throws JSONException;

  /**
   * Put or replace a float value. If the index is greater than the length of the JSONArray, then null elements will be
   * added as necessary to pad it out.
   *
   * @param index The subscript.
   * @param value A float value.
   * @return this.
   * @throws JSONException If the index is negative or if the value is non-finite.
   */
  JSONArray put(int index, float value) throws JSONException;

  /**
   * Put or replace an int value. If the index is greater than the length of the JSONArray, then null elements will be
   * added as necessary to pad it out.
   *
   * @param index The subscript.
   * @param value An int value.
   * @return this.
   * @throws JSONException If the index is negative.
   */
  JSONArray put(int index, int value) throws JSONException;

  /**
   * Put or replace a long value. If the index is greater than the length of the JSONArray, then null elements will be
   * added as necessary to pad it out.
   *
   * @param index The subscript.
   * @param value A long value.
   * @return this.
   * @throws JSONException If the index is negative.
   */
  JSONArray put(int index, long value) throws JSONException;

  /**
   * Put a value in the JSONArray, where the value will be a JSONObject that is produced from a Map.
   *
   * @param index The subscript.
   * @param value The Map value.
   * @return this.
   * @throws JSONException        If the index is negative or if the value is an invalid number.
   * @throws NullPointerException If a key in the map is <code>null</code>
   */
  JSONArray put(int index, Map<?, ?> value) throws JSONException;

  /**
   * Put or replace an object value in the JSONArray. If the index is greater than the length of the JSONArray, then
   * null elements will be added as necessary to pad it out.
   *
   * @param index The subscript.
   * @param value The value to put into the array. The value should be a Boolean, Double, Integer, JSONArray,
   *              JSONObject, Long, or String, or the JSONObject.NULL object.
   * @return this.
   * @throws JSONException If the index is negative or if the value is an invalid number.
   */
  JSONArray put(int index, Object value) throws JSONException;

  /**
   * Put a collection's elements in to the JSONArray.
   *
   * @param collection A Collection.
   * @return this.
   */
  JSONArray putAll(Collection<?> collection);

  /**
   * Put an Iterable's elements in to the JSONArray.
   *
   * @param iter An Iterable.
   * @return this.
   */
  JSONArray putAll(Iterable<?> iter);

  /**
   * Put a JSONArray's elements in to the JSONArray.
   *
   * @param array A JSONArray.
   * @return this.
   */
  JSONArray putAll(JSONArray array);

  /**
   * Put an array's elements in to the JSONArray.
   *
   * @param array Array. If the parameter passed is null, or not an array or Iterable, an exception will be thrown.
   * @return this.
   *
   * @throws JSONException        If not an array, JSONArray, Iterable or if an value is non-finite number.
   * @throws NullPointerException Thrown if the array parameter is null.
   */
  JSONArray putAll(Object array) throws JSONException;

}
