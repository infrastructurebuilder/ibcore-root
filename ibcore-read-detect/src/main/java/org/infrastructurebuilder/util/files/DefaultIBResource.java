/**
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
 */
package org.infrastructurebuilder.util.files;

import static java.nio.file.Files.createTempFile;
import static java.util.Objects.requireNonNull;
import static java.util.Optional.empty;
import static org.infrastructurebuilder.IBException.cet;
import static org.infrastructurebuilder.util.IBUtils.copy;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Function;

import org.apache.tika.Tika;
import org.infrastructurebuilder.IBException;
import org.infrastructurebuilder.util.artifacts.Checksum;
import org.infrastructurebuilder.util.files.model.IBResourceModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultIBResource extends IBResourceModel {
  private final static Logger log = LoggerFactory.getLogger(DefaultIBResource.class);
  private static final long serialVersionUID = 5978749189830232137L;
  private final static Tika tika = new Tika();

  public final static IBResource copyToTempChecksumAndPath(Path targetDir, final Path source,
      final Optional<String> oSource, final String pString) throws IOException {
    DefaultIBResource d = (DefaultIBResource) copyToTempChecksumAndPath(targetDir, source);
    requireNonNull(oSource).ifPresent(o -> {
      d.setSource(o + "!/" + pString);
    });
    return d;
  }

  public final static IBResource copyToTempChecksumAndPath(Path targetDir, final Path source) throws IOException {

    String localType = toType.apply(requireNonNull(source));
    Checksum cSum = new Checksum(source);
    Path newTarget = targetDir.resolve(cSum.asUUID().get().toString());
    cet.withReturningTranslation(() -> copy(source, newTarget));
    return new DefaultIBResource(newTarget, cSum, Optional.of(localType));
  }

  public final static IBResource copyToDeletedOnExitTempChecksumAndPath(Path targetDir, String prefix, String suffix,
      final InputStream source) {
    return cet.withReturningTranslation(() -> {
      Path target = createTempFile(requireNonNull(targetDir), prefix, suffix);
      try (OutputStream outs = Files.newOutputStream(target)) {
        copy(source, outs);
        source.close();
      }
      return copyToTempChecksumAndPath(targetDir, target);
    });
  }

  public final static Function<Path, String> toType = (path) -> {
    synchronized (tika) { // FIXME Unnecessary to synchronize?
      log.debug("Detecting path " + path);
      org.apache.tika.metadata.Metadata md = new org.apache.tika.metadata.Metadata();
      md.set(org.apache.tika.metadata.Metadata.RESOURCE_NAME_KEY, path.toAbsolutePath().toString());
      try (Reader p = tika.parse(path, md)) {
        log.debug(" Metadata is " + md);
        return tika.detect(path);
      } catch (IOException e) {
        throw new IBException("Failed during attempt to get tika type", e);
      }
    }
  };

  public final static IBResource from(Path p, Checksum c, String type) {
    return new IBResourceModel(p, c, type);
  }

  public DefaultIBResource(Path path, Checksum checksum, Optional<String> type) {
    super(path, checksum, requireNonNull(type).orElseGet(() -> toType.apply(path)));
  }

  public final static IBResource fromPath(Path path) {
    return new DefaultIBResource(path, new Checksum(path), empty());
  }
}
