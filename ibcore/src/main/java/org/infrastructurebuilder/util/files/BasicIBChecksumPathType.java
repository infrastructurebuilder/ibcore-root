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

import static java.util.Objects.requireNonNull;
import static org.infrastructurebuilder.IBConstants.APPLICATION_OCTET_STREAM;
import static org.infrastructurebuilder.IBException.cet;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;

import org.infrastructurebuilder.util.IBUtils;
import org.infrastructurebuilder.util.artifacts.Checksum;
import org.infrastructurebuilder.util.artifacts.JSONBuilder;
import org.json.JSONObject;

public class BasicIBChecksumPathType implements IBChecksumPathType {

  private static final String TYPE = "type";
  private static final String CHECKSUM = "checksum";
  private static final String PATH = "path";
  protected final Checksum checksum;
  protected final Path path;
  protected final String type;
  protected final Optional<URL> sourceURL;
  protected final Optional<String> name;
  private final OpenOption[] readOptions;
  private final JSONObject json;

  public BasicIBChecksumPathType(Path path, Checksum checksum, String type) {
    this(path, checksum, type, Optional.empty(), Optional.empty());
  }

  public BasicIBChecksumPathType(Path path, Checksum checksum, String type, Optional<URL> sourceURL, Optional<String> name) {
    super();
    this.path = requireNonNull(path);
    List<OpenOption> o = new ArrayList<>();
    o.add(StandardOpenOption.READ);
    if (this.path.getClass().getCanonicalName().contains("Zip")) {

    } else {
      o.add(LinkOption.NOFOLLOW_LINKS);
    }
    this.readOptions = o.toArray(new OpenOption[o.size()]);
    this.checksum = requireNonNull(checksum);
    this.type = requireNonNull(type);
    this.sourceURL = requireNonNull(sourceURL);
    this.name = requireNonNull(name);

    this.json = JSONBuilder.newInstance()
        //
        .addPath(PATH, this.path)
        //
        .addChecksum(CHECKSUM, this.checksum)
        //
        .addString(TYPE, this.type)
        //
        .asJSON();
  }

  public BasicIBChecksumPathType(Path path, Checksum checksum) {
    this(path, checksum, APPLICATION_OCTET_STREAM);
  }

  public BasicIBChecksumPathType(JSONObject j) {
    this(Paths.get(requireNonNull(j).getString(PATH)), new Checksum(j.getString(CHECKSUM)), j.getString(TYPE));
  }

  @Override
  public Optional<URL> getSourceURL() {
    return this.sourceURL;
  }

  @Override
  public Optional<String> getSourceName() {
    return this.name;
  }
  @Override
  public Path getPath() {
    return path;
  }

  @Override
  public Checksum getChecksum() {
    return checksum;
  }

  @Override
  public String getType() {
    return type;
  }

  @Override
  public InputStream get() {
    return cet.withReturningTranslation(() -> Files.newInputStream(this.path, readOptions));
  }

  @Override
  public String toString() {
    return new StringJoiner("|").add(getType()).add(getChecksum().asUUID().get().toString()).add(getPath().toString())
        .toString();
  }

  @Override
  public IBChecksumPathType moveTo(Path target) throws IOException {
    IBUtils.moveAtomic(this.path, target);
    return new BasicIBChecksumPathType(target, this.checksum, this.type);
  }

  @Override
  public JSONObject asJSON() {
    return this.json;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (checksum.hashCode());
    result = prime * result + (path.hashCode());
    result = prime * result + (type.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    BasicIBChecksumPathType other = (BasicIBChecksumPathType) obj;
    return checksum.equals(other.checksum) && path.equals(other.path) && type.equals(other.type);
  }

}