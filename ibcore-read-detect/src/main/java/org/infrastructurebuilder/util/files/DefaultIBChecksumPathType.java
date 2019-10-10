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

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.function.Function;

import org.apache.tika.Tika;
import org.infrastructurebuilder.IBException;
import org.infrastructurebuilder.util.artifacts.Checksum;

public class DefaultIBChecksumPathType extends BasicIBChecksumPathType implements IBChecksumPathType {
  public final static Tika tika = new Tika();
  public final static Function<Path, String> toType = (path) -> {
    synchronized (tika) { // FIXME Unnecessary
      try (InputStream ins = Files.newInputStream(path, StandardOpenOption.READ)) {
        return tika.detect(ins);
      } catch (IOException e) {
        throw new IBException("Failed during attempt to get tika type", e);
      }
    }

  };

  public final static IBChecksumPathType from(Path p, Checksum c, String type) {
    return new BasicIBChecksumPathType(p, c, type);
  }

  DefaultIBChecksumPathType(Path path, Checksum checksum) throws IOException {
    super(path, checksum, toType.apply(path));
  }
}
