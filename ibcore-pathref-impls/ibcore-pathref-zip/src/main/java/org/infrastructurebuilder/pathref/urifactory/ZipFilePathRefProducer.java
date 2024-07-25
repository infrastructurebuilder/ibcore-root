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

import java.io.InputStream;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import javax.inject.Inject;
import javax.inject.Named;

import org.infrastructurebuilder.pathref.AbstractBasePathRef;
import org.infrastructurebuilder.pathref.PathRef;
import org.infrastructurebuilder.pathref.PathRefProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is (mostly) a testing instance of RelativeRootSupplier, allowing for the creation of RelativeRootSupplier
 * instances in the same manner as the Named instances
 */
@Named(ZipFilePathRefProducer.NAME)
// NOT a singleton
public class ZipFilePathRefProducer implements PathRefProducer<String> {
  private static final Logger log = LoggerFactory.getLogger(ZipFilePathRefProducer.class);
  public static final String NAME = "uri-supplier";
  private final AtomicReference<String> path = new AtomicReference<>();
  private AtomicReference<Path> localPath = new AtomicReference<>();

  public String getName() {
    return NAME;
  }

  /**
   * Get a new one every time
   */
  @Inject
  public ZipFilePathRefProducer() {
  }

  @Override
  public Class<String> withClass() {
    return String.class;
  }

  protected Logger getLog() {
    return log;
  }

  public Optional<PathRef> with(Object data) {
    if (data == null || !(data instanceof String))
      return empty();
    try {
      return Optional.of(new ZipFilePathRef((String) data));
    } catch (Throwable t) {
      log.error(String.format("Error creating ZipFilePathRef from {}", data), t);
      return empty();
    }
  }

  public final static class ZipFilePathRef extends AbstractBasePathRef<Path> {

    protected ZipFilePathRef(String u) {
      super(u);
    }

    @Override
    public Optional<InputStream> getInputStreamFrom(String path) {
      // TODO Auto-generated method stub
      return Optional.empty();
    }

  }

}
