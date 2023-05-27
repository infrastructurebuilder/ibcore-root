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

import java.util.List;
import java.util.Optional;

public class ProxyProxy {
  private final String id;
  private final String host;
  private final List<String> nonProxyHosts;
  private final Optional<String> password;
  private final int port;
  private final String protocol;
  private final Optional<String> username;
  private final boolean active;

  /**
   * @param id
   * @param host
   * @param nonProxyHosts
   * @param password
   * @param port
   * @param protocol
   * @param username
   * @param active
   */
  public ProxyProxy(String id, String host, List<String> nonProxyHosts, Optional<String> password, int port,
      String protocol, Optional<String> username, boolean active)
  {
    this.id = id;
    this.host = host;
    this.nonProxyHosts = nonProxyHosts;
    this.password = password;
    this.port = port;
    this.protocol = protocol; // "protocol" is the same as "type" for WagonManager
    this.username = username;
    this.active = active;
  }

  public String getId() {
    return this.id;
  }

  /**
   * Get the proxy host.
   *
   * @return String
   */
  public String getHost() {
    return this.host;
  }

  /**
   * Get the list of non-proxied hosts (was delimited by |).
   *
   * @return String
   */
  public List<String> getNonProxyHosts() {
    return this.nonProxyHosts;
  }

  /**
   * Get the proxy password.
   *
   * @return String
   */
  public Optional<String> getPassword() {
    return this.password;
  }

  /**
   * Get the proxy port.
   *
   * @return int
   */
  public int getPort() {
    return this.port;
  }; // -- int getPort()

  /**
   * Get the proxy protocol.
   *
   * @return String
   */
  public String getProtocol() {
    return this.protocol;
  }

  /**
   * Get the proxy user.
   *
   * @return String
   */
  public Optional<String> getUsername() {
    return this.username;
  }

  /**
   * Get whether this proxy configuration is the active one.
   *
   * @return boolean
   */
  public boolean isActive() {
    return this.active;
  }
}
