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
package org.infrastructurebuilder.data;

import java.io.InputStream;
import java.net.URL;
import java.util.Optional;

import org.infrastructurebuilder.util.BasicCredentials;
import org.infrastructurebuilder.util.artifacts.Checksum;

/**
 * An IBDataSource understands where a data stream originates and how to acquire it
 *
 * @author mykel.alvis
 *
 */
public interface IBDataSource {
  String getId();
  Optional<URL> getSourceURL();
  Optional<InputStream> getOverrideInputStream();
  Optional<BasicCredentials> getCredentials();
  Optional<Checksum> getChecksum();
}