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

import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 * Object for testing the exception handling in {@link org.json.JSONObject#populateMap}.
 *
 * @author John Aylward
 */
public class ExceptionalBean {
  /**
   * @return a closeable.
   */
  public Closeable getCloseable() {
    // anonymous inner class did not work...
    return new MyCloseable();
  }

  /**
   * @return Nothing really. Just can't be void.
   * @throws IllegalAccessException always thrown
   */
  public int getIllegalAccessException() throws IllegalAccessException {
    throw new IllegalAccessException("Yup, it's illegal");
  }

  /**
   * @return Nothing really. Just can't be void.
   * @throws IllegalArgumentException always thrown
   */
  public int getIllegalArgumentException() throws IllegalArgumentException {
    throw new IllegalArgumentException("Yup, it's illegal");
  }

  /**
   * @return Nothing really. Just can't be void.
   * @throws InvocationTargetException always thrown
   */
  public int getInvocationTargetException() throws InvocationTargetException {
    throw new InvocationTargetException(new Exception("Yup, it's illegal"));
  }

  /** My closeable class. */
  public static final class MyCloseable implements Closeable {

    /**
     * @return a string
     */
    public String getString() {
      return "Yup, it's closeable";
    }

    @Override
    public void close() throws IOException {
      throw new IOException("Closing is too hard!");
    }
  }
}
