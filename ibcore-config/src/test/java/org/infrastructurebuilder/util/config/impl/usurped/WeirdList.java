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
/**
 *
 */
package org.infrastructurebuilder.util.config.impl.usurped;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author John Aylward
 */
public class WeirdList {
  /** */
  private final List<Integer> list = new ArrayList();

  /**
   * @param vals
   */
  public WeirdList(Integer... vals) {
    this.list.addAll(Arrays.asList(vals));
  }

  /**
   * @return a copy of the list
   */
  public List<Integer> get() {
    return new ArrayList(this.list);
  }

  /**
   * @return a copy of the list
   */
  public List<Integer> getALL() {
    return new ArrayList(this.list);
  }

  /**
   * get a value at an index.
   *
   * @param i index to get
   * @return the value at the index
   */
  public Integer get(int i) {
    return this.list.get(i);
  }

  /**
   * get a value at an index.
   *
   * @param i index to get
   * @return the value at the index
   */
  @SuppressWarnings("boxing")
  public int getInt(int i) {
    return this.list.get(i);
  }

  /**
   * @param value new value to add to the end of the list
   */
  public void add(Integer value) {
    this.list.add(value);
  }
}
