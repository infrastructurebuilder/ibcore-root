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
package org.infrastructurebuilder.util.readdetect.avro;

import java.util.Optional;
import java.util.function.Supplier;

import org.infrastructurebuilder.util.core.RelativeRoot;
import org.infrastructurebuilder.util.readdetect.IBResourceBuilder;
import org.infrastructurebuilder.util.readdetect.IBResourceIS;
import org.infrastructurebuilder.util.readdetect.impls.absolute.AbsolutePathIBResourceBuilderFactory;
import org.infrastructurebuilder.util.readdetect.impls.relative.RelativePathIBResourceISBuilderFactory;

public class RelativePathAvroIBResourceBuilderFactory extends RelativePathIBResourceISBuilderFactory {
  private static final long serialVersionUID = 8394943566089224494L;

  public RelativePathAvroIBResourceBuilderFactory(RelativeRoot relRoot) {
    super(relRoot);
  }

  @Override
  protected Supplier<IBResourceBuilder<Optional<IBResourceIS>>> getBuilder() {
    return () -> new RelativePathAvroIBResourceBuilder(getRelativeRoot());
  }

}
