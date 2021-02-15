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
package org.infrastructurebuilder.util.config;

import static java.util.Collections.emptyList;

import java.nio.file.Path;
import java.util.List;

import org.infrastructurebuilder.util.CredentialsFactory;
import org.infrastructurebuilder.util.LoggerEnabled;
import org.infrastructurebuilder.util.artifacts.GAV;
import org.infrastructurebuilder.util.artifacts.IBArtifactVersionMapper;
import org.infrastructurebuilder.util.files.TypeToExtensionMapper;

/**
 * An {@code IBRuntimeUtils} instance is an aggregation of every
 * early-configurable, (somewhat) statically-usable, and easily-transported
 * element of functionality. It's purpose is to supply InfrastructureBuilder
 * projects with usable configuration to allow broad re-use and easy testing.
 *
 * It accomplishes about 75% of these goals.
 *
 * @author mykel.alvis
 *
 */
public interface IBRuntimeUtils extends
    // Wrap internal CredentialsFactory
    CredentialsFactory
    // Load all internal artifact versions
    , IBArtifactVersionMapper
    // provide single logger configuraiton (sorry if you don't like this)
    , LoggerEnabled
    // And whatever MIME<->Extension mapping is configured
    , TypeToExtensionMapper
    // Get GAV and other project data from current working project
    , GAVSupplier
    {

  /**
   * Get an instance of a {@link Path} that the current instance considers a
   * usable Path.
   *
   * By contract, the returned value must exist and be a writeable directory. No
   * guarantees that the returned value will remain the same for subsequent calls.
   *
   * @return Existing {@link Path} that already exists and is a directory.
   */
  Path getWorkingPath();

  /**
   * Get the list of {@link GAV} elements that define the entities that must be
   * resolved and available for local use.
   *
   * The contract here is that the list of artifacts returned must be fully
   * resolved (or implicitly resolvable with some internal resolver), so that one
   * might do the following:
   *
   * <code>
   * List<Path> l = getDependencies().stream()
   *    .map(GAV::getFile) // Get all the file objects
   *    .filter(g -> g.isPresent()) // Ensure they're present (Java 11 version probably different)
   *    .map(g -> g.get()) // Fetch that path
   *    .collect(Collectors.toList()); // Collect to a list
   * </code>
   *
   * @return
   */
  default List<GAV> getDependencies() {
    return emptyList();
  }
}
