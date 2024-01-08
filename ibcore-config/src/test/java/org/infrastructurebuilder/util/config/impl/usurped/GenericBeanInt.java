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

/**
 * @author john
 *
 */
public class GenericBeanInt extends GenericBean<Integer> {
  /** */
  final char a = 'A';

  /** @return the a */
  public char getA() {
    return this.a;
  }

  /**
   * Should not be beanable
   *
   * @return false
   */
  public boolean getable() {
    return false;
  }

  /**
   * Should not be beanable
   *
   * @return false
   */
  public boolean get() {
    return false;
  }

  /**
   * Should not be beanable
   *
   * @return false
   */
  public boolean is() {
    return false;
  }

  /**
   * Should be beanable
   *
   * @return false
   */
  public boolean isB() {
    return this.genericValue.equals((Integer.valueOf(this.a + 1)));
  }

  /**
   * @param genericValue the value to initiate with.
   */
  public GenericBeanInt(Integer genericValue) {
    super(genericValue);
  }

  /** override to generate a bridge method */
  @Override
  public Integer getGenericValue() {
    return super.getGenericValue();
  }

}
