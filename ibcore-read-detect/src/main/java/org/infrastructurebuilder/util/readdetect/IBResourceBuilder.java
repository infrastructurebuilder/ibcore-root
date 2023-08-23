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

import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;
import static org.infrastructurebuilder.util.constants.IBConstants.ADDITIONAL_PROPERTIES;
import static org.infrastructurebuilder.util.constants.IBConstants.CREATE_DATE;
import static org.infrastructurebuilder.util.constants.IBConstants.DESCRIPTION;
import static org.infrastructurebuilder.util.constants.IBConstants.MIME_TYPE;
import static org.infrastructurebuilder.util.constants.IBConstants.MOST_RECENT_READ_TIME;
import static org.infrastructurebuilder.util.constants.IBConstants.NAME;
import static org.infrastructurebuilder.util.constants.IBConstants.NO_PATH_SUPPLIED;
import static org.infrastructurebuilder.util.constants.IBConstants.ORIGINAL_PATH;
import static org.infrastructurebuilder.util.constants.IBConstants.PATH;
import static org.infrastructurebuilder.util.constants.IBConstants.SIZE;
import static org.infrastructurebuilder.util.constants.IBConstants.SOURCE_URL;
import static org.infrastructurebuilder.util.constants.IBConstants.UPDATE_DATE;
import static org.infrastructurebuilder.util.constants.IBConstants.UTF_8;
import static org.infrastructurebuilder.util.core.ChecksumEnabled.CHECKSUM;
import static org.infrastructurebuilder.util.readdetect.IBResourceException.cet;
import static org.infrastructurebuilder.util.readdetect.impl.DefaultIBResource.extracted;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Optional;
import java.util.Properties;
import java.util.UUID;

import org.infrastructurebuilder.util.core.Checksum;
import org.infrastructurebuilder.util.core.IBUtils;
import org.infrastructurebuilder.util.core.PathSupplier;
import org.infrastructurebuilder.util.core.RelativeRoot;
import org.infrastructurebuilder.util.readdetect.impl.DefaultIBResource;
import org.infrastructurebuilder.util.readdetect.model.IBResourceModel;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IBResourceBuilder {

  private final static Logger log = LoggerFactory.getLogger(IBResourceBuilder.class);
  private IBResourceModel model = new IBResourceModel();
  private Checksum targetChecksum;
  private Path sourcePath;
  private RelativeRoot root;

  public IBResourceBuilder(RelativeRoot rRoot) {
    this.root = requireNonNull(rRoot);
    // This dir must be a dir and read/writeable
    getTargetDir().ifPresent(td -> {
      if (!Files.isDirectory(td, LinkOption.NOFOLLOW_LINKS))
        throw new IBResourceException("not.unlinked.dir");
      if (!Files.isReadable(td))
        throw new IBResourceException("not.readable");
      if (!Files.isWritable(td))
        throw new IBResourceException("not.writeable");
    });
  }

  private Optional<Path> getTargetDir() {
    return root.getPath();
  }

  public IBResourceBuilder fromModel(IBResourceModel model) {
    this.model = requireNonNull(model);
    return this;
  }

  public IBResourceBuilder from(JSONObject j) {
    var m = new IBResourceModel();
    m.setModelEncoding(UTF_8);
    m.setCreated(requireNonNull(j).optString(CREATE_DATE, null));
    m.setFileChecksum(j.getString(CHECKSUM));
    m.setSize(j.getLong(SIZE));
    m.setType(j.getString(MIME_TYPE));
    m.setFilePath(j.optString(PATH, null));
    m.setLastUpdate(j.optString(UPDATE_DATE, null));
    m.setMostRecentReadTime(j.optString(MOST_RECENT_READ_TIME, null));
    m.setName(j.optString(NAME, null));
    m.setSource(j.optString(SOURCE_URL, null));
    m.setDescription(j.optString(DESCRIPTION, null));
    ofNullable(j.optJSONObject(ADDITIONAL_PROPERTIES)).ifPresent(jo -> {
      jo.toMap().forEach((k, v) -> {
        m.addAdditionalProperty(k, v.toString());
      });
    });

    var p = ofNullable(m.getFilePath()).map(extracted).orElseThrow(() -> new IBResourceException(NO_PATH_SUPPLIED));
    var originalPath = ofNullable(j.optString(ORIGINAL_PATH, null)).map(extracted).orElse(null);
    throw new IBResourceException("FIXORIGINALPATH");
//    return this.fromModel(m).from(p).fromOriginalPath(originalPath);

  }

  public IBResourceBuilder with(Checksum csum) {
    this.targetChecksum = requireNonNull(csum);
    return this;
  }

  public IBResourceBuilder copyFrom(Path path) {
    Path targetPath = getTargetDir().map(p -> p.resolve(UUID.randomUUID().toString()))
        .orElseThrow(() -> new IBResourceException("no.target.dir"));
    Checksum cs;
    try (InputStream ins = Files.newInputStream(requireNonNull(path));
        OutputStream outs = Files.newOutputStream(targetPath)) {
      cs = IBUtils.copyAndDigest(ins, outs);

    } catch (IOException | NoSuchAlgorithmException e) {
      log.error("Error in copyFrom", e);
      throw new IBResourceException("error.in.copyfrom", e);
    }
    this.sourcePath = cet
        .returns(() -> IBUtils.moveFileToNewIdPath(targetPath, cs.asUUID().get()).relativize(getTargetDir().get()));
    this.model.setFileChecksum(cs.toString());
    this.model.setFilePath(this.sourcePath.toString());
    this.model.setType(IBResourceFactory.toType.apply(this.sourcePath));
    return this;
  }

  public IBResourceBuilder from(Path path) {
    this.sourcePath = requireNonNull(path);
    return this;
  }

  public IBResourceBuilder withName(String name) {
    this.model.setName(requireNonNull(name));
    return this;
  }

  public IBResourceBuilder withDescription(String desc) {
    this.model.setDescription(requireNonNull(desc));
    return this;
  }

  public IBResourceBuilder withType(String type) {
    this.model.setType(requireNonNull(type));
    return this;
  }

  public IBResourceBuilder withAdditionalProperties(Properties p) {
    Properties p1 = new Properties();
    p1.putAll(p);
    this.model.setAdditionalProperties(p1);
    return this;
  }

  public IBResourceBuilder withLastUpdated(Instant last) {
    this.model.setLastUpdate(requireNonNull(last).toString());
    return this;
  }

  public IBResourceBuilder withSource(String source) {
    this.model.setSource(requireNonNull(source));
    return this;
  }

  public IBResourceBuilder withCreateDate(Instant create) {
    this.model.setCreated(requireNonNull(create).toString());
    return this;
  }

  /**
   * validate checks the values provided so far and throws IBResourceException if
   * anything is off. You can call validate whenever you set any value and if it
   * returns your data is still possibly OK
   *
   * @param hard if true, then assume nothing and re-validate the existence and
   *             checksums of the paths and sources
   * @throws IBResourceException if validation fails
   * @return this builder
   */
  public IBResourceBuilder validate(boolean hard) {
    getTargetDir().ifPresent(targetDir -> {
      // there is a targetDir, so the file we're referencing should be in that
      // directory
      if (this.sourcePath != null)
        if (!IBUtils.isParent(targetDir, this.sourcePath)) {
          // We have a source, and a place it should be
        }
    });

    if (this.sourcePath == null) {

      // There has been no source path set.
    }
    if (this.targetChecksum == null) {
      this.targetChecksum = Checksum.ofPath.apply(this.sourcePath)
          .orElseThrow(() -> new IBResourceException("unreadable.path"));
    }
    if (this.model.getType() == null) {
      this.model.setType(IBResourceFactory.toType.apply(this.sourcePath));
    }
    return this;
  }

  public IBResource build(boolean hard) {
    validate(hard);
    return new DefaultIBResource(this.model);
  }

  public IBResource build() {
    return build(false);
  }
}
