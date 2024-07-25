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
package org.infrastructurebuilder.pathref.classpath;

import static java.util.Optional.ofNullable;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.Optional;

import javax.inject.Named;

import org.infrastructurebuilder.pathref.AbstractBasePathRef;
import org.infrastructurebuilder.pathref.PathRef;
import org.infrastructurebuilder.pathref.PathRefProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named(ClasspathPathRefSupplier.NAME)
public class ClasspathPathRefSupplier implements PathRefProducer<String> {
  private static final Logger log = LoggerFactory.getLogger(ClasspathPathRefSupplier.class);

  public static final String NAME = "classpath:/";

  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public Class<String> withClass() {
    return String.class;
  }

  @Override
  public Optional<PathRef> with(Object data) {
    return Optional.of(new AbstractBasePathRef<String>(NAME) {
      @Override
      public Optional<PathRef> extendAsPathRef(Path newPath) {
        return Optional.of(this); // "extend" doesn't work for classpath types
      }

      @Override
      public Optional<InputStream> getInputStreamFrom(String path) {
        return ofNullable(this.getClass().getResourceAsStream(path)); // TODO is this OK?
      }
    });

  }
}
