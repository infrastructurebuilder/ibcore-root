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

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;

import org.infrastructurebuilder.util.core.Checksum;
import org.infrastructurebuilder.util.readdetect.model.IBResourceModel;

/**
 * Just a testing implementation that allows us to throw an excetion when get is called
 *
 * @author mykel.alvis
 *
 */
public class ThrowingIBChecksumType extends IBResourceModel {

  /**
   *
   */
  private static final long serialVersionUID = 5376806798385645619L;

  public ThrowingIBChecksumType() throws IOException {
    super(Paths.get("."), new Checksum(), "doesnt/matter");
  }

  @Override
  public InputStream get() {
    return new ThrowingInputStream(IOException.class);
  }

}
