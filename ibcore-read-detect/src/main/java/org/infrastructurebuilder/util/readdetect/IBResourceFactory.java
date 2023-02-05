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
import static java.nio.file.Files.readAttributes;
import static java.util.Objects.requireNonNull;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.infrastructurebuilder.exceptions.IBException.cet;
import static org.infrastructurebuilder.util.core.Checksum.ofPath;
import static org.infrastructurebuilder.util.core.IBUtils.copy;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.lang.System.Logger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.apache.tika.Tika;
import org.apache.tika.metadata.TikaCoreProperties;
import org.infrastructurebuilder.exceptions.IBException;
import org.infrastructurebuilder.util.constants.IBConstants;
import org.infrastructurebuilder.util.core.Checksum;
import org.infrastructurebuilder.util.core.RelativeRoot;
import org.infrastructurebuilder.util.readdetect.impl.DefaultIBResource;
import org.infrastructurebuilder.util.readdetect.model.IBResourceModel;
import org.json.JSONObject;

public class IBResourceFactory {
  private static final long serialVersionUID = 5978749189830232137L;
  private final static Logger log = System.getLogger(IBResourceFactory.class.getName());
  private final static Tika tika = new Tika();
  private final static AtomicReference<RelativeRoot> root = new AtomicReference<>();

  public final static void setRelativeRoot(RelativeRoot r) {
    root.set(r);
  }

  public final static Function<Path, String> toType = (path) -> {
    if (!Files.exists(requireNonNull(path)))
      throw new IBException("file.does.not.exist");
    if (!Files.isRegularFile(path))
      throw new IBException("file.not.regular.file");

    synchronized (tika) {
      log.log(Logger.Level.DEBUG, "Detecting path " + path);
      org.apache.tika.metadata.Metadata md = new org.apache.tika.metadata.Metadata();
      md.set(TikaCoreProperties.RESOURCE_NAME_KEY, path.toAbsolutePath().toString());
      try (Reader p = tika.parse(path, md)) {
        log.log(Logger.Level.DEBUG, " Metadata is " + md);
        return tika.detect(path);
      } catch (IOException e) {
        log.log(Logger.Level.ERROR, "Failed during attempt to get tika type", e);
        return IBConstants.APPLICATION_OCTET_STREAM;
      }
    }
  };
  public final static Function<Path, Optional<BasicFileAttributes>> getAttributes = (i) -> {
    Optional<BasicFileAttributes> retVal = empty();
    try {
      retVal = of(readAttributes(requireNonNull(i), BasicFileAttributes.class));
    } catch (IOException e) {
      // Do nothing
    }
    return retVal;
  };

  public final static BiFunction<Path, Path, Optional<IBResource>> toIBResource = (targetDir, source) -> {
    try {
      return Optional.of(IBResourceFactory.copyToTempChecksumAndPath(targetDir, source));
    } catch (IOException e) {
      // TODO ??
    }
    return empty();
  };

  public final static BiFunction<Path, Optional<String>, String> nameMapper = (p, on) -> {
    var str = requireNonNull(p).toString();
    return requireNonNull(on).orElse(str.substring(0, str.lastIndexOf('.')));
  };

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
    return new DefaultIBResource(newTarget, cSum, Optional.of(localType), empty());
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

  public final static IBResource from(Path p, Optional<String> name, Optional<String> desc) {
    return new DefaultIBResource(requireNonNull(p), name, desc,
        Checksum.ofPath.apply(p).orElseThrow(() -> new RuntimeException("unreadable.path")), empty());
  }

  public final static IBResource from(Path p, Checksum c, String type, String source) {
    IBResourceModel m = new IBResourceModel();
    m.setFilePath(requireNonNull(p).toAbsolutePath().toString());
    m.setFileChecksum(c.toString());
    m.setType(type);
    m.setSource(source);
    return new DefaultIBResource(p, c, Optional.of(type), empty());

  }
  public final static IBResource from(Path p, Checksum c, String type) {
    return from(p,c,type,null);
  }

  public final static IBResource fromPath(Path path) {
    return new DefaultIBResource(path, new Checksum(path), empty(), empty());
  }

  /**
   * Dont (at) me bro. The string is a JSON string
   *
   * @param json
   * @return
   */
  public static IBResource from(String json) {
    return fromJSON(new JSONObject(json));
  }

  public static IBResource from(Path path, Optional<String> name, Optional<String> desc, Instant createDate,
      Instant lastUpdated, Optional<Properties> addlProps) {
    return new DefaultIBResource(path, new Checksum(path), of(toType.apply(path)), addlProps).setCreateDate(createDate)
        .setLastUpdated(lastUpdated);
  }
}