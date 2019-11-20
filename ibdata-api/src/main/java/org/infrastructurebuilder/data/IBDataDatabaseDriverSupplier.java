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

import java.sql.Connection;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import org.infrastructurebuilder.util.BasicCredentials;
import org.infrastructurebuilder.util.artifacts.GAV;

/**
 * Supplies the class name for the driver to make a Connection using a given JDBC URL
 *
 * @author mykel.alvis
 *
 */
public interface IBDataDatabaseDriverSupplier {

  /**
   * Return a list of coordinates that must be in the classpath in order to load the driver
   * @return List of GAV items in order of required insertion into the (new) classpath that the driver will be created from
   */
  List<GAV> getRequiredArtifacts();

  Optional<String> getDatabaseDriverClassName(String jdbcUrl);

  /**
   * The mapped hint for type of component
   * @return
   */
  String getHint();

  /**
   * The name of the Jooq SQLDialect enum.  Defaults to be the same as the getHint() call
   * @return
   */
  default String getJooqName() {
    return getHint();
  }

  Optional<IBDatabaseDialect> getDialect(String jdbcUrl);

  boolean respondsTo(String jdbcURL);

  Optional<Supplier<Connection>> getDataSourceSupplier(String jdbcURL, Optional<BasicCredentials> creds);
}
