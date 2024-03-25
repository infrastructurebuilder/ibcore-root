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
package org.infrastructurebuilder.util.readdetect.impls.base;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.util.NoSuchElementException;

import org.codehaus.plexus.components.io.resources.AbstractPlexusIoResource;
import org.infrastructurebuilder.util.readdetect.IBResourceIS;

public class IBURLPlexusIOResource extends AbstractPlexusIoResource {

  private IBResourceIS r;

  public IBURLPlexusIOResource(IBResourceIS r) {
    super(r.getName(), // Name
        r.getLastUpdateDate().map(Instant::toEpochMilli)
            .orElseThrow(() -> new IllegalArgumentException("No last update")),
        r.getBasicFileAttributes().map(BasicFileAttributes::size)
            .orElseThrow(() -> new IllegalArgumentException("No file attributes")),
        true, false, true);
    this.r = r;
  }

  @Override
  public InputStream getContents() throws IOException {
    return r.get().orElseThrow(
        // This will cause us to return the "correct" exception
        () -> new IOException(new NoSuchElementException("IBResource " + r.getName() + " provided no InputStream")));
  }

  @Override
  public URL getURL() throws IOException {
    return r.getPath().get().toUri().toURL();
  }

}
