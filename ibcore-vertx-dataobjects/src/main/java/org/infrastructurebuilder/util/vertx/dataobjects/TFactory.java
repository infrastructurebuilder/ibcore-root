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

import java.util.Optional;

import org.infrastructurebuilder.util.core.Identified;
import org.infrastructurebuilder.util.vertx.base.Versioned;
import org.infrastructurebuilder.util.vertx.blobstore.Blobstore;

import io.vertx.core.Future;

public interface TFactory<T> extends Identified, Versioned  {

  /**
   *
   * @param name      Short Name of object
   * @param display   Name to display to user
   * @param type
   * @param container Containing T. If null, this is the ROOT object and should
   *                  be set to <code>this</code>
   * @param tas
   * @return
   */
  Future<T> create(String name, String display, String type, String container, Optional<Tags> tags);

  Future<T> duplicate(T i);

  Future<Blobstore> getStore();

  /**
   * Query an T from whatever cache this factory holds
   *
   * @param string Id of the T in question
   * @return
   */
  Future<T> get(String id);

}