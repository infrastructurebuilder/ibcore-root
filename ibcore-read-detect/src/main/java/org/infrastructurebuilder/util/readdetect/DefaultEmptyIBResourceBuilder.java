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
package org.infrastructurebuilder.util.readdetect;

import java.util.Optional;

import org.infrastructurebuilder.util.core.PathAndChecksum;
import org.infrastructurebuilder.util.core.RelativeRoot;

public class DefaultEmptyIBResourceBuilder<T> extends AbstractIBResourceBuilder<Optional<T>> {

  public DefaultEmptyIBResourceBuilder(RelativeRoot root) {
    super(null);
  }

  @Override
  public IBResourceBuilder<Optional<T>> fromPathAndChecksum(PathAndChecksum path) {
    return this;
  }

  @Override
  public IBResourceBuilder<Optional<T>> fromURL(String url) {
    // TODO Auto-generated method stub
    return this;
  }

  @Override
  public Optional<T> build(boolean hard) {
    return Optional.empty();
  }

}
