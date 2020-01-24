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

import static java.nio.file.Files.newInputStream;
import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static java.nio.file.StandardOpenOption.READ;
import static java.util.Objects.hash;
import static java.util.Optional.empty;
import static org.infrastructurebuilder.IBException.cet;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.function.Supplier;

import org.infrastructurebuilder.IBException;
import org.infrastructurebuilder.util.artifacts.Checksum;

public interface IBResource extends Supplier<InputStream> {

  /**
   * @return Non-null Path to this result
   * @throws IBException (runtime) if not available
   */
  Path getPath();

  /**
   * @return Equivalent to calculated Checksum of the contents of the file at getPath()
   */
  Checksum getChecksum();

  /**
   * @return Non-null MIME type for the file at getPath()
   */
  String getType();

  /**
   * Relocate underlying path to new path.  The target path is meant to be a
   * normal filesystem, not a ZipFileSystem.
   *
   * @param target
   * @return
   * @throws IOException
   */
  IBResource moveTo(Path target) throws IOException;

  default InputStream get() {
    java.util.List<java.nio.file.OpenOption> o = new java.util.ArrayList<>();
    o.add(READ);
    if (getPath().getClass().getCanonicalName().contains("Zip")) {

    } else {
      o.add(NOFOLLOW_LINKS);
    }
    OpenOption[] readOptions = o.toArray(new java.nio.file.OpenOption[o.size()]);

    return cet.withReturningTranslation(() -> newInputStream(getPath(), readOptions));
  }

  default Optional<URL> getSourceURL() {
    return empty();
  }

  default Optional<String> getSourceName() {
    return empty();
  }

  default int defaultHashCode() {
    return hash(getChecksum(), getPath(), getSourceName(), getSourceURL(), getType());
  }

  default boolean defaultEquals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof IBResource))
      return false;
    IBResource other = (IBResource) obj;
    return Objects.equals(getChecksum(), other.getChecksum())
        && Objects.equals(getPath(), other.getPath())
        && Objects.equals(getSourceName(), other.getSourceName())
        && Objects.equals(getSourceURL(), other.getSourceURL())
        && Objects.equals(getType(), other.getType());
  }

  default String defaultToString() {
    StringJoiner sj = new StringJoiner("|").add(getType()).add(getChecksum().asUUID().get().toString())
        .add(getPath().toString());
    getSourceURL().ifPresent(u -> sj.add(u.toExternalForm()));
    getSourceName().ifPresent(sj::add);
    return sj.toString();
  }

  default Long size() {
    return cet.withReturningTranslation(() -> Files.size(getPath()));

  }

}