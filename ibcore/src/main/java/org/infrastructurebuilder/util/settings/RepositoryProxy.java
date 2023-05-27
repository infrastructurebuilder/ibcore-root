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
package org.infrastructurebuilder.util.settings;

import static java.util.Objects.requireNonNull;

import java.net.URL;
import java.util.Optional;

public class RepositoryProxy {

  private final String id;
  private final Layout layout;
  private final Optional<String> name;
  private final URL url;
  private final Optional<RepositoryPolicyProxy> releases, snapshots;

  public RepositoryProxy(String id, Layout layout, Optional<String> name, URL url,
      Optional<RepositoryPolicyProxy> releases, Optional<RepositoryPolicyProxy> snapshots) {
    this.id = requireNonNull(id);
    this.layout = requireNonNull(layout);
    this.name = requireNonNull(name);
    this.url = requireNonNull(url);
    this.releases = requireNonNull(releases);
    this.snapshots = requireNonNull(snapshots);
  }

  /**
   * Get a unique identifier for a repository.
   *
   * @return String
   */
  public String getId() {
    return this.id;
  }

  /**
   * Get the type of layout this repository uses for locating and
   *             storing artifacts - can be "legacy" or
   * "default".
   *
   * @return String
   */
  public Layout getLayout() {
    return this.layout;
  }

  /**
   * Get human readable name of the repository.
   *
   * @return String
   */
  public Optional<String> getName() {
    return this.name;
  }

  /**
   * Get the url of the repository.
   *
   * @return String
   */
  public URL getUrl() {
    return this.url;
  }

  /**
   * Get how to handle downloading of releases from this
   * repository.
   *
   * @return RepositoryPolicy
   */
  public Optional<RepositoryPolicyProxy> getReleases() {
    return this.releases;
  }

  /**
   * Get how to handle downloading of snapshots from this
   * repository.
   *
   * @return RepositoryPolicy
   */
  public Optional<RepositoryPolicyProxy> getSnapshots() {
    return this.snapshots;
  }
}
