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

import org.json.JSONPropertyName;

/**
 * Test bean for the {@link JSONPropertyName} annotation.
 */
public class MyBeanCustomName implements MyBeanCustomNameInterface {
  public int getSomeInt() {
    return 42;
  }

  @JSONPropertyName("")
  public long getSomeLong() {
    return 42L;
  }

  @JSONPropertyName("myStringField")
  public String getSomeString() {
    return "someStringValue";
  }

  @JSONPropertyName("Some Weird NAme that Normally Wouldn't be possible!")
  public double getMyDouble() {
    return 0.0d;
  }

  @Override
  public float getSomeFloat() {
    return 2.0f;
  }

  @Override
  public int getIgnoredInt() {
    return 40;
  }
}
