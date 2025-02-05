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
package org.infrastructurebuilder.pathref.base;

import static java.util.Optional.ofNullable;

import java.net.URI;
import java.nio.file.FileSystems;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

import org.infrastructurebuilder.pathref.PathRefProducer;
import org.infrastructurebuilder.pathref.fs.PathRefFileSystem;
import org.infrastructurebuilder.pathref.fs.PathRefPath;
import org.json.JSONObject;
import org.slf4j.Logger;

abstract public class AbstractBasicPathPropertiesPathRefProducer implements PathRefProducer {

  private JSONObject config = new JSONObject();

  @Override
  public <T> T withConfig(JSONObject c) {
    this.config = c;
    return (T) this;
  }

  @Override
  public PathRefPath with(String data1) {
//    if (data == null )
//      return Optional.empty();
    Map<String, ?> config = this.config.toMap(); // FIXME?
    var pStr = getProperty().orElse(null);
    if (pStr == null)
      return null;
    if (!pStr.startsWith(PathRefPath.PATHREF))
      pStr = PathRefPath.PATHREF_TEMPLATE.formatted(pStr);
    PathRefFileSystem fs;
    URI data;
    try {
      data = URI.create(pStr);
    } catch (Throwable t) {

      return null;
    }
    try {
      fs = (PathRefFileSystem) FileSystems.getFileSystem(data);
    } catch (Throwable t) {
      try {
        fs = (PathRefFileSystem) FileSystems.newFileSystem(data, config);
      } catch (Throwable t2) {
        getLog().warn("Filesystem unavailable {}".formatted(data));
        return null;
      }
    }
    PathRefPath retVal = null;
    if (fs != null)
      retVal = ((PathRefPath) fs.getRootDirectories().iterator().next());
    return retVal;
  }

  /**
   * Override for different property name
   *
   * @return
   */
  public String getPropertyName() {
    return getName();
  }

  abstract protected Logger getLog();

  public Optional<String> getProperty() {
    return getProperties().map(p -> p.getProperty(getPropertyName()));
  }

  protected Optional<Properties> getProperties() {
    return Optional.of(System.getProperties());
  }

}
