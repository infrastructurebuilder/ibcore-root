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

//import static java.nio.file.Files.createTempFile;
//import static java.nio.file.Files.readAttributes;
//import static java.util.Objects.requireNonNull;
//import static java.util.Optional.empty;
//import static java.util.Optional.of;
//import static java.util.Optional.ofNullable;
//import static org.infrastructurebuilder.exceptions.IBException.cet;
//import static org.infrastructurebuilder.util.core.IBUtils.copy;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.OutputStream;
//import java.io.Reader;
//import java.net.URL;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.nio.file.attribute.BasicFileAttributes;
//import java.time.Instant;
//import java.util.Optional;
//import java.util.Properties;
//import java.util.concurrent.atomic.AtomicReference;
//import java.util.function.BiFunction;
//import java.util.function.Function;
//
//import org.apache.tika.Tika;
//import org.apache.tika.metadata.TikaCoreProperties;
//import org.infrastructurebuilder.exceptions.IBException;
//import org.infrastructurebuilder.util.constants.IBConstants;
//import org.infrastructurebuilder.util.core.Checksum;
//import org.infrastructurebuilder.util.core.RelativeRoot;
//import org.infrastructurebuilder.util.readdetect.impl.DefaultIBResource;
//import org.infrastructurebuilder.util.readdetect.model.IBResourceModel;
//import org.json.JSONObject;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

/**
 * The IBResourceFactory produces various forms of IBResource, usually by copying and internally checksumming them to
 * some local space.
 *
 * @author mykelalvis
 *
 */
public class IBResourceFactory {
//  private final static AtomicReference<RelativeRoot> root = new AtomicReference<>();
//
//  public final static void setRelativeRoot(RelativeRoot r) {
//    root.set(r);
//  }


//  private final static BiFunction<Path, Path, Optional<IBResource>> toIBResource = (targetDir, source) -> {
//    try {
//      return Optional.of(IBResourceFactory.copyToTempChecksumAndPath(targetDir, source));
//    } catch (IOException e) {
//      // TODO ??
//    }
//    return empty();
//  };
//
//  private final static BiFunction<Path, Optional<String>, String> nameMapper = (p, on) -> {
//    var str = requireNonNull(p).toString();
//    return requireNonNull(on).orElse(str.substring(0, str.lastIndexOf('.')));
//  };
//
//  public final static IBResource from(IBResourceModel m) {
//    return new DefaultIBResource(m);
//  }
//
//  public final static IBResource fromJSON(JSONObject j) {
//    return new DefaultIBResource(j);
//  }
//
  IBResourceFactory() {
  }

//  /**
//   * Produces an IBResource by making a copy of some named source into a target directly. The resulting source is
//   * indicated to be "contained" within an archive using the !/ syntax of path managenment if the original values are
//   * presented .
//   *
//   * @param targetDir
//   * @param source
//   * @param oSource
//   * @param pString
//   * @return
//   * @throws IOException
//   */
//  public final static IBResource copyToTempChecksumAndPath(Path targetDir, final Path source,
//      final Optional<String> oSource, final String pString) throws IOException {
//    DefaultIBResource d = (DefaultIBResource) copyToTempChecksumAndPath(targetDir, source);
//    requireNonNull(oSource).ifPresent(o -> {
//      d.setSource(o + "!/" + pString);
//    });
//    return d;
//  }
//
//  private final static IBResource copyToTempChecksumAndPath(Path targetDir, final Path source) throws IOException {
//
//    String localType = IBResourceCacheFactory.toType.apply(requireNonNull(source));
//    Checksum cSum = new Checksum(source);
//    Path newTarget = targetDir.resolve(cSum.asUUID().get().toString());
//    cet.returns(() -> copy(source, newTarget));
//    return new DefaultIBResource(newTarget, cSum, Optional.of(localType), empty());
//  }
//
//  private final static IBResource copyToDeletedOnExitTempChecksumAndPath(Path targetDir, String prefix, String suffix,
//      final InputStream source) {
//    return cet.returns(() -> {
//      Path target = createTempFile(requireNonNull(targetDir), prefix, suffix);
//      try (OutputStream outs = Files.newOutputStream(target)) {
//        copy(source, outs);
//        source.close();
//      }
//      return copyToTempChecksumAndPath(targetDir, target);
//    });
//  }

//  public final static IBResource from(Path p, Optional<String> name, Optional<String> desc) {
//    return new DefaultIBResource(requireNonNull(p), name, desc,
//        Checksum.ofPath.apply(p).orElseThrow(() -> new RuntimeException("unreadable.path")), empty());
//  }
//
//  public final static IBResource from(Path p, Checksum c, String type, String source) {
//    IBResourceModel m = new IBResourceModel();
//    m.setFilePath(requireNonNull(p).toAbsolutePath().toString());
//    m.setFileChecksum(c.toString());
//    m.setType(type);
//    m.setSource(source);
//    return new DefaultIBResource(p, c, Optional.of(type), empty());
//
//  }

//  public final static IBResource from(Path p, Checksum c, String type) {
//    return from(p, c, type, null);
//  }

//  public final static IBResource fromPath(Path path) {
//    return new DefaultIBResource(path, new Checksum(path), empty(), empty());
//  }

//  /**
//   * Dont (at) me bro. The string is a JSON string
//   *
//   * @param json
//   * @return
//   */
//  public static IBResource fromJSONString(String json) {
//    return fromJSON(new JSONObject(json));
//  }
//
//  public static IBResource from(Path path, Optional<String> originalName, Optional<String> desc, Instant createDate,
//      Instant lastUpdated, Optional<Properties> addlProps) {
//    Path rpath = root.get().relativize(path).orElseThrow(() -> new IBException(String.format("Invalid path {}", path)));
//    return new DefaultIBResource(rpath, new Checksum(path), of(IBResourceCacheFactory.toType.apply(path)), addlProps).setCreateDate(createDate)
//        .setLastUpdated(lastUpdated);
//  }
}
