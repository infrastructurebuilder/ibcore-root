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
package org.infrastructurebuilder.util;

import static java.util.Objects.requireNonNull;

public class RepositoryPolicyProxy {

  private final boolean enabled;
  private final ChecksumPolicy csum;
  private final UpdatePolicy upd;
  private final int interval;

  public RepositoryPolicyProxy(boolean enabled, ChecksumPolicy csum, UpdatePolicy upd, int interval) {
    this.enabled = enabled;
    this.csum = requireNonNull(csum);
    this.upd = requireNonNull(upd);
    this.interval = interval;
  }

  /**
   * Get what to do when verification of an artifact checksum
   * fails. Valid values are
   *             <code>ignore</code>
   *             ,
   *             <code>fail</code>
   *             or
   *             <code>warn</code>
   *             (the default).
   *
   * @return String
   */
  ChecksumPolicy getChecksumPolicy() {
    return this.csum;
  }

  /**
   * Get the frequency for downloading updates - can be
   *             <code>always,</code>
   *             <code>daily</code>
   *             (default),
   *             <code>interval:XXX</code>
   *             (in minutes) or
   *             <code>never</code>
   *             (only if it doesn't exist locally).
   *
   * @return String
   */
  UpdatePolicy getUpdatePolicy() {
    return this.upd;
  }

  int getIntervalMinutes() {
    return this.interval;
  }

  boolean isEnabled() {
    return this.enabled;
  }

}
