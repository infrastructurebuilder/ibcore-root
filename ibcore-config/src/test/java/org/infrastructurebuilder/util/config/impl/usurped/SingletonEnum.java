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
package org.infrastructurebuilder.util.config.impl.usurped;

/**
 * Sample singleton done as an Enum for use with bean testing.
 *
 * @author John Aylward
 *
 */
public enum SingletonEnum {
  /**
   * the singleton instance.
   */
  INSTANCE;

  /** */
  private int someInt;
  /** */
  private String someString;

  /** single instance. */

  /**
   * @return the singleton instance. In a real application, I'd hope no one did this to an enum singleton.
   */
  public static final SingletonEnum getInstance() {
    return INSTANCE;
  }

  /** */
  private SingletonEnum() {
  }

  /** @return someInt */
  public int getSomeInt() {
    return this.someInt;
  }

  /**
   * sets someInt.
   *
   * @param someInt the someInt to set
   */
  public void setSomeInt(int someInt) {
    this.someInt = someInt;
  }

  /** @return someString */
  public String getSomeString() {
    return this.someString;
  }

  /**
   * sets someString.
   *
   * @param someString the someString to set
   */
  public void setSomeString(String someString) {
    this.someString = someString;
  }
}
