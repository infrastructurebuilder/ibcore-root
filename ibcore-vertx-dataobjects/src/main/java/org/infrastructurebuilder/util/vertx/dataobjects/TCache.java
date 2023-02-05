/*
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
 */
package org.infrastructurebuilder.util.vertx.dataobjects;

import static io.vertx.core.Future.succeededFuture;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.infrastructurebuilder.util.core.Typed;

import io.vertx.core.Future;

public interface TCache<T extends Typed> {

  void start();

  void stop();

  boolean isRunning();

  Future<Map<String, T>> getAllAsMap();

  /**
   *
   * @param type
   * @return
   */
  Future<List<T>> getAllOfType(String type);

  Future<Map<String, List<T>>> getAllAsTypeMap();

  Future<T> get(String id);

  default Future<List<T>> getAll() {
    return getAllAsMap().compose(m -> succeededFuture(new ArrayList<>(m.values())));
  }


  /**
   * Add an T. Will not add an T with a duplicate ID!
   *
   * @param i T to add
   * @return true if added, false if T exists
   */
  Future<Boolean> put(T i);

  /**
   * Marks an T as deleted
   *
   * @param i T to mark as deleted
   * @return
   */
  Future<Boolean> remove(T i);

  Future<Boolean> update(T i);

  Future<List<T>> getAllRemoved();

  Future<Void> gc(boolean removeDeleted);

  Future<Boolean> setTFactoryReference(TFactory<T> factoryImpl);

  //
  TFactory<T> getFactory();

}