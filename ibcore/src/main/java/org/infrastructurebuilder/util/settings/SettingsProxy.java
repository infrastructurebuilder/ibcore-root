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

import static java.util.Collections.unmodifiableList;
import static java.util.Objects.requireNonNull;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collector;

import org.infrastructurebuilder.util.credentials.basic.BasicCredentials;
import org.infrastructurebuilder.util.credentials.basic.CredentialsSupplier;
import org.json.JSONObject;

public class SettingsProxy implements CredentialsSupplier {

  private final Path localRepository;
  private final List<ServerProxy> servers;
  private final List<ProfileProxy> profiles;
  private final List<MirrorProxy> mirrors;
  private final Charset modelEncoding;
  private final List<String> pluginGroups;
  private final boolean offline;
  private final List<ProxyProxy> proxies;

  public SettingsProxy(boolean offline, Path localRepo, Charset modelEncoding, List<ServerProxy> servers,
      List<ProfileProxy> profiles, List<MirrorProxy> mirrors, List<String> pluginGroups, List<ProxyProxy> proxies)
  {
    this.offline = offline;
    this.localRepository = requireNonNull(localRepo);
    this.modelEncoding = requireNonNull(modelEncoding);
    this.servers = unmodifiableList(servers);
    this.profiles = unmodifiableList(profiles);
    this.mirrors = unmodifiableList(mirrors);
    this.pluginGroups = unmodifiableList(pluginGroups);
    this.proxies = unmodifiableList(proxies);
  }

  /**
   * Method getActiveProfiles.
   *
   * @return List
   */
  public List<String> getActiveProfiles() {
    return profiles.stream().filter(ProfileProxy::isActive).map(ProfileProxy::getId).collect(toList());
  }

  /**
   * Get the local repository.<br>
   * <b>Default value is:</b> <code>${user.home}/.m2/repository</code>
   *
   * @return String
   */
  public Path getLocalRepository() {
    return this.localRepository;
  }

  /**
   * Method getMirrorProxys.
   *
   * @return List
   */
  public List<MirrorProxy> getMirrors() {
    return this.mirrors;
  }

  /**
   * Get the modelEncoding field.
   *
   * @return String supplied
   */
  public Charset getModelEncoding() {
    return this.modelEncoding;
  }

  /**
   * Method getPluginGroups.
   *
   * @return List
   */
  public List<String> getPluginGroups() {
    return this.pluginGroups;
  }

  /**
   * Method getProfiles.
   *
   * @return List
   */
  public List<ProfileProxy> getProfiles() {
    return this.profiles;
  }

  /**
   * Method getProxies.
   *
   * @return List
   */
  public List<ProxyProxy> getProxies() {
    return this.proxies;
  }

  /**
   * Method getServers.
   *
   * @return List
   */
  public List<ServerProxy> getServers() {
    return this.servers;
  }

  public final static Collector<JSONObject, JSONObject, JSONObject> joc = Collector.of(() -> new JSONObject(),
      (a, b) -> a.put(b.getString(ServerProxy.ID_STRING), b), (x, y) -> x, Collector.Characteristics.IDENTITY_FINISH,
      Collector.Characteristics.UNORDERED);

  /**
   * Get a mapped list of servers from the settings object
   *
   * @return Servers mapped by getId
   */
  public JSONObject getServersAsJSON() {
    return getServers().stream().map(ServerProxy::asJSON).collect(joc);
  }

  /**
   * Get whether Maven should attempt to interact with the user for input.
   *
   * @return false
   */
  public boolean isInteractiveMode() {
    return false;
  }

  /**
   * Get indicate whether maven should operate in offline mode full-time.
   *
   * @return boolean
   */
  public boolean isOffline() {
    return this.offline;
  }

  /**
   * Get whether Maven should use the plugin-registry.xml file to manage plugin versions.
   *
   * @return false
   */
  public boolean isUsePluginRegistry() {
    return false;
  }

  /**
   * @return the first active proxy
   */
  public Optional<ProxyProxy> getActiveProxy() {
    return getProxies().stream().filter(ProxyProxy::isActive).findFirst();
  }

  public Optional<ServerProxy> getServer(String serverId) {
    return getServers().stream().filter(s -> s.getId().equals(serverId)).findFirst();
  }

  public Optional<MirrorProxy> getMirrorOf(String repositoryId) {
    return getMirrors().stream().filter(p -> p.isProxyOf(repositoryId)).findAny();
  }

  /**
   * @return a Map of profiles field with <code>Profile#getId()</code> as key
   */
  public Map<String, ProfileProxy> getProfilesAsMap() {
    return getProfiles().stream().collect(toMap(ProfileProxy::getId, identity()));
  }

  public Optional<MirrorProxy> getMirror(String mirrorId) {
    return getMirrors().stream().filter(p -> p.getId().equals(Objects.requireNonNull(mirrorId))).findFirst();
  }

  @Override
  public Optional<BasicCredentials> getCredentialsFor(String query) {
    return getServer(query).map(ServerProxy::get);
  }
}
