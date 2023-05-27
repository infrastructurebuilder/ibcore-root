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

import java.net.URL;
import java.util.List;
import java.util.Optional;

import org.infrastructurebuilder.exceptions.IBException;

public class MirrorProxy {
  private String id;
  private Layout layout;
  private List<String> mirrorOf;
  private List<Layout> mirrorOfLayouts;
  private Optional<String> name;
  private URL url;

  /**
   * @param id
   * @param layout
   * @param mirrorOf
   * @param mirrorOfLayouts
   * @param name
   * @param url
   */
  public MirrorProxy(String id, Layout layout, List<String> mirrorOf, List<Layout> mirrorOfLayouts,
      Optional<String> name, URL url)
  {
    this.id = id;
    this.layout = layout;
    this.mirrorOf = mirrorOf;
    this.mirrorOfLayouts = mirrorOfLayouts;
    this.name = name;
    this.url = url;
  }

  public String getId() {
    return this.id;
  }

  /**
   * Get the layout of the mirror repository. Since Maven 3.
   *
   * @return String
   */
  public Layout getLayout() {
    return this.layout;
  }

  /**
   * Get the server IDs of the repositories being mirrored, e.g., "central". This MUST NOT match the mirror id.
   *
   * @return String
   */
  public List<String> getMirrorOf() {
    return this.mirrorOf;
  }

  /**
   * Get the layouts of repositories being mirrored. This value can be used to restrict the usage of the mirror to
   * repositories with a matching layout (apart from a matching id). Since Maven 3.
   *
   * @return String
   */
  public List<Layout> getMirrorOfLayouts() {
    return this.mirrorOfLayouts;
  }; // -- String getMirrorOfLayouts()

  /**
   * Get the optional name that describes the mirror.
   *
   * @return String
   */
  public Optional<String> getName() {
    return this.name;
  }

  /**
   * Get the URL of the mirror repository.
   *
   * @return String
   */
  public URL getUrl() {
    return this.url;
  }

  public boolean isProxyOf(String repositoryId) {
    throw new IBException("isProxyOf is not implemented");

  }

}
