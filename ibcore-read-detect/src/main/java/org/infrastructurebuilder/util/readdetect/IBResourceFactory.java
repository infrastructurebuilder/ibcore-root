/*
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
package org.infrastructurebuilder.util.readdetect;

import static java.nio.file.Files.createTempFile;
import static java.util.Objects.requireNonNull;
import static java.util.Optional.empty;
import static org.infrastructurebuilder.exceptions.IBException.cet;
import static org.infrastructurebuilder.util.core.IBUtils.copy;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.System.Logger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Function;

import org.infrastructurebuilder.util.core.Checksum;
import org.infrastructurebuilder.util.readdetect.impl.DefaultIBResource;
import org.infrastructurebuilder.util.readdetect.model.IBResourceModel;
import org.json.JSONObject;

public class IBResourceFactory {
  private static final long serialVersionUID = 5978749189830232137L;
  private final static Logger log = System.getLogger(IBResourceFactory.class.getName());

  private Path p;

  public final static IBResource from(IBResourceModel m) {
    return new DefaultIBResource(m);
  }

  public final static IBResource fromJSON(JSONObject j) {
    return new DefaultIBResource(j);
  }

  IBResourceFactory() {
  }

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
    cet.returns(() -> copy(source, newTarget));
    return new DefaultIBResource(newTarget, cSum, Optional.of(localType));
  }

  public final static IBResource copyToDeletedOnExitTempChecksumAndPath(Path targetDir, String prefix, String suffix,
      final InputStream source) {
    return cet.returns(() -> {
      Path target = createTempFile(requireNonNull(targetDir), prefix, suffix);
      try (OutputStream outs = Files.newOutputStream(target)) {
        copy(source, outs);
        source.close();
      }
      return copyToTempChecksumAndPath(targetDir, target);
    });
  }

  public final static Function<Path, String> toType = DefaultIBResource.toType;

  public final static IBResource from(Path p, Checksum c, String type) {
    IBResourceModel m = new IBResourceModel();
    m.setFilePath(requireNonNull(p).toAbsolutePath().toString());
    m.setFileChecksum(c.toString());
    m.setType(type);
    return new DefaultIBResource(p, c, Optional.of(type));
  }

  public final static IBResource fromPath(Path path) {
    return new DefaultIBResource(path, new Checksum(path), empty());
  }
}