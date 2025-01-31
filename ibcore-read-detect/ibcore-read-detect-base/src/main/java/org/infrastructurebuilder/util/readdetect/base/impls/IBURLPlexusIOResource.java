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
package org.infrastructurebuilder.util.readdetect.base.impls;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;

import org.codehaus.plexus.components.io.resources.AbstractPlexusIoResource;
import org.infrastructurebuilder.util.readdetect.api.IBResource;

// TODO Split this out to a separate dependency to extract direct need for plexus
public class IBURLPlexusIOResource extends AbstractPlexusIoResource {

  private final IBResource r;

  public IBURLPlexusIOResource(IBResource r) {
    super(r.getName(), // Name
        r.getLastUpdateDate().map(Instant::toEpochMilli)
            .orElseThrow(() -> new IllegalArgumentException("No last update")),
        r.getFileAttributes().map(BasicFileAttributes::size)
            .orElseThrow(() -> new IllegalArgumentException("No file attributes")),
        true, false, true);
    this.r = r;
  }

  @Override
  public InputStream getContents() throws IOException {
    return Files.newInputStream(r.get());
  }

  @Override
  public URL getURL() throws IOException {
    return null; // forces a call to getContents()
  }

}
