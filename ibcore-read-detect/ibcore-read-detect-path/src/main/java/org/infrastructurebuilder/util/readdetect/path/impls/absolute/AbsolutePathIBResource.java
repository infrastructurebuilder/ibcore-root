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
package org.infrastructurebuilder.util.readdetect.path.impls.absolute;

import static java.lang.String.format;
import static java.nio.file.Files.newInputStream;
import static java.time.Instant.now;
import static java.util.Objects.requireNonNull;
import static java.util.Optional.empty;

import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Properties;

import org.infrastructurebuilder.util.constants.IBConstants;
import org.infrastructurebuilder.util.core.Checksum;
import org.infrastructurebuilder.util.core.IBUtils;
import org.infrastructurebuilder.util.core.OptStream;
import org.infrastructurebuilder.util.readdetect.base.base.path.IBResource;
import org.infrastructurebuilder.util.readdetect.base.base.path.IBResourceBuilder;
import org.infrastructurebuilder.util.readdetect.base.base.path.IBResourceBuilderFactory;
import org.infrastructurebuilder.util.readdetect.base.base.path.IBResourceException;
import org.infrastructurebuilder.util.readdetect.base.base.path.IBResourceIS;
import org.infrastructurebuilder.util.readdetect.base.base.path.impls.base.AbstractIBResourceIS;
import org.infrastructurebuilder.util.readdetect.model.v1_0.IBResourceModel;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The AbsolutePathIBResource is an <link>IBResource</link> that has the following propertie
 * <ol>
 * <li>The output must be backed by an <b><i>ABSOLUTE</i></b> <code>java.nio.file.Path</code></li>
 * <li>It's <code>get()</code> method returns an <code>Optional InputStream</code>, which will probably be present based
 * on the availability of the FileSystem that backs the Path.</li>
 * <li>It's <code>RelativeRoot</code> instance <i>may</i> be null, allowing for no relative paths. This could affect any
 * ability to persist the metadata.</li>
 *
 * </ol>
 */
public class AbsolutePathIBResource extends AbstractIBResourceIS implements IBResourceIS {
  private final static Logger log = LoggerFactory.getLogger(AbsolutePathIBResource.class);

  public AbsolutePathIBResource(IBResourceModel m, Path sourcePath) {
    super(null, m);
    this.m.setPath(IBResource.requireAbsolutePath(sourcePath).toString());
  }

  public AbsolutePathIBResource(IBResourceModel m) {
    this(m, Paths.get(URI.create(m.getPath().orElseThrow(() -> new IBResourceException("No [required] file path")))));
  }

  public AbsolutePathIBResource(JSONObject j) {
    super(null, IBResourceBuilder.modelFromJSON.apply(j).get()); // TODO Convert
  }

  public AbsolutePathIBResource(Path path, Checksum checksum, Optional<String> type, Optional<Properties> addlProps) {
    this(new IBResourceModel());
    m.setPath(requireNonNull(path).toAbsolutePath().toString());
    m.setStreamChecksum(requireNonNull(checksum).toString());
    IBUtils.getAttributes.apply(path).ifPresent(bfa -> {
      this.m.setCreated(bfa.creationTime().toInstant());
      this.m.setLastUpdate(bfa.lastModifiedTime().toInstant());
      this.m.setMostRecentReadTime(bfa.lastAccessTime().toInstant());
      this.m.setStreamSize(bfa.size());
    });

    requireNonNull(type).ifPresent(t -> m.setStreamType(t));
  }

  public AbsolutePathIBResource(Path path, Checksum checksum, Optional<String> type) {
    this(path, checksum, type, empty());
  }

  @Override
  public OptStream get() {
    m.setMostRecentReadTime(now());
    return new OptStream(getPath().map(path -> {
      try {
        return Files.newInputStream(path, (path.getClass().getCanonicalName().contains("Zip")) ? ZIP_OPTIONS : OPTIONS);
      } catch (Throwable t) {
        log.error("Error opening " + path, t);
        return null;
      }
    }).orElse(null));
  }

  @Override
  public boolean validate(boolean hard) {
    return getPath().map(p -> {
      if (!Files.exists(p)) {
        log.warn("validation: File %p does not exist", p.toString());
        return false;
      }
      if (hard) {
        Checksum s = this.getTChecksum();
        Checksum n = new Checksum(p); // Calculate new checksum
        if (!s.equals(n)) {
          log.warn(format("validation: expected checksum %s != actual checksum %s", s, n));
          return false;
        }
        var actualType = IBResourceBuilderFactory.toOptionalType.apply(p).orElse(IBConstants.APPLICATION_OCTET_STREAM);
        if (!this.m.getStreamType().equals(actualType)) {
          log.warn(format("validation: expected type %s != actual type %", this.m.getStreamType(), actualType));
          return false;
        }
      }
      return true;
    }).orElseGet(() -> {
      log.warn("validate: path not present in model");
      return false;
    });
  }

  @Override
  public Checksum getTChecksum() {
    return new Checksum(m.getStreamChecksum());
  }

}
