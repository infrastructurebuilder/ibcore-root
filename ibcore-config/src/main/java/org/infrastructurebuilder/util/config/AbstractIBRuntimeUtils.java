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

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.SortedSet;

import org.infrastructurebuilder.util.BasicCredentials;
import org.infrastructurebuilder.util.CredentialsFactory;
import org.infrastructurebuilder.util.GAV;
import org.infrastructurebuilder.util.IBArtifactVersionMapper;
import org.infrastructurebuilder.util.LoggerSupplier;
import org.infrastructurebuilder.util.PathSupplier;
//import org.infrastructurebuilder.util.artifacts.IBArtifactVersionMapper;
import org.infrastructurebuilder.util.files.TypeToExtensionMapper;
import org.infrastructurebuilder.util.versions.IBVersionsSupplier;
import org.slf4j.Logger;

abstract public class AbstractIBRuntimeUtils implements IBRuntimeUtils {

  protected final PathSupplier          wps;
  private final GAVSupplier             gs;
  private final LoggerSupplier          ls;
  private final CredentialsFactory      cf;
  private final IBArtifactVersionMapper avm;
  private final TypeToExtensionMapper   t2em;

  protected AbstractIBRuntimeUtils(PathSupplier wps, LoggerSupplier ls, GAVSupplier gs, CredentialsFactory cf,
      IBArtifactVersionMapper avm, TypeToExtensionMapper t2em) {
    this.wps = wps;
    this.ls = ls;
    this.gs = gs;
    this.cf = cf;
    this.avm = avm;
    this.t2em = t2em;
  }

  protected AbstractIBRuntimeUtils(AbstractIBRuntimeUtils ibr) {
    this.wps = ibr.wps;
    this.ls = ibr.ls;
    this.gs = ibr.gs;
    this.cf = ibr.cf;
    this.avm = ibr.avm;
    this.t2em = ibr.t2em;
  }

  @Override
  public Path getWorkingPath() {
    return wps.get();
  }

  @Override
  public GAV getGAV() {
    return gs.getGAV();
  }

  @Override
  public Optional<String> getDescription() {
    return gs.getDescription();
  }

  @Override
  public Logger getLog() {
    return ls.get();
  }

  @Override
  public Optional<BasicCredentials> getCredentialsFor(String query) {
    return cf.getCredentialsFor(query);
  }

  @Override
  public List<IBVersionsSupplier> getMatchingArtifacts(String groupId, String artifactId) {
    return this.avm.getMatchingArtifacts(groupId, artifactId);
  }

  @Override
  public String getExtensionForType(String type) {
    return t2em.getExtensionForType(type);
  }

  @Override
  public SortedSet<String> reverseMapFromExtension(String extension) {
    return t2em.reverseMapFromExtension(extension);
  }

  @Override
  public Optional<String> getStructuredSupplyTypeClassName(String type) {
    return t2em.getStructuredSupplyTypeClassName(type);
  }


}
