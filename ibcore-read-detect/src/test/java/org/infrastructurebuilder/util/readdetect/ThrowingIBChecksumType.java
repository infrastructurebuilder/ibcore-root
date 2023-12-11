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

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Optional;

import org.infrastructurebuilder.util.core.Checksum;
import org.infrastructurebuilder.util.readdetect.impl.AbsolutePathIBResource;

/**
 * Just a testing implementation that allows us to throw an excetion when get is called
 *
 * @author mykel.alvis
 *
 */
public class ThrowingIBChecksumType extends AbsolutePathIBResource {

  public ThrowingIBChecksumType() throws IOException {
    super(Optional.empty(), Paths.get("."), new Checksum(), Optional.of("doesnt/matter"));
  }

  @Override
  public Optional<InputStream> get() {
    return Optional.of(new ThrowingInputStream(IOException.class));
  }

}
