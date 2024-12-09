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
package org.infrastructurebuilder.pathref.urifactory;

import static java.util.Optional.empty;
import static org.infrastructurebuilder.exceptions.IBException.cet;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import javax.inject.Inject;
import javax.inject.Named;

import org.infrastructurebuilder.exceptions.IBException;
import org.infrastructurebuilder.pathref.AbstractBasePathRef;
import org.infrastructurebuilder.pathref.PathRef;
import org.infrastructurebuilder.pathref.PathRefProducer;
import org.infrastructurebuilder.pathref.base.AbstractBasicPathPropertiesPathRefSupplier;
import org.infrastructurebuilder.util.core.IBUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named(URIPathRefProducer.NAME)
// NOT a singleton
public class URIPathRefProducer implements PathRefProducer {
  private static final Logger log = LoggerFactory.getLogger(URIPathRefProducer.class);
  public static final String NAME = "uri-supplier";

  public String getName() {
    return NAME;
  }

  /**
   * Get a new one every time
   */
  @Inject
  public URIPathRefProducer() {
  }

  public Optional<PathRef> with(String data) {
    if (data == null || !(data instanceof String))
      return empty();
    try {
      return Optional.of(new URIPathRef(cet.returns(() -> URI.create((String) data))));
    } catch (Throwable t) {
      log.error(String.format("Error creating URIPathRef from {}", data), t);
      return empty();
    }
  }

  protected Logger getLog() {
    return log;
  }

  public final static class URIPathRef extends AbstractBasePathRef<URI> {

    protected URIPathRef(URI u) {
      super(u);
    }

  }

}
