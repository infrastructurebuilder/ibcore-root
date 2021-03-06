/*
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

import static java.nio.file.Files.newInputStream;
import static org.infrastructurebuilder.exceptions.IBException.cet;

import java.io.InputStream;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;

import javax.inject.Inject;
import javax.inject.Named;
/**
 * The PropertiesInjectedConfigMapSupplier takes a list of ExtendedListSuppliers.  These can be files or a resource.
 * The target is expected to be loadable as a Properties objects, which is then added or overridden
 * based on the type of the ExtendedListSupplier.
 *
 * Any number of ExtendedListSupplier items can be supplied, and they are applied in-order
 *
 * @author mykel.alvis
 *
 */
@Named("properties")
public class PropertiesInjectedConfigMapSupplier extends DefaultConfigMapSupplier {

  @Inject
  public PropertiesInjectedConfigMapSupplier(final List<ExtendedListSupplier> suppliers) {
    for (final ExtendedListSupplier s : suppliers) {
      for (final String r : s.get()) {
        final Properties p = s.isFile() ? loadFile(r) : loadResource(r);
        if (s.isOverride()) {
          overrideConfiguration(p);
        } else {
          addConfiguration(p);
        }
      }
    }
  }

  private Properties loadFile(final String fileName) {
    return loadit(cet.withReturningTranslation(() -> newInputStream(Paths.get(fileName))), fileName);
  }

  private Properties loadit(final InputStream ins, final String fileName) {
    final Properties p = new Properties();
    if (fileName.toLowerCase().endsWith(".xml")) {
      cet.withTranslation(() -> p.loadFromXML(ins));
    } else {
      cet.withTranslation(() -> p.load(ins));
    }

    cet.withTranslation(() -> ins.close());
    return p;
  }

  private Properties loadResource(final String resourceId) {
    return loadit(cet.withReturningTranslation(() -> getClass().getResourceAsStream(resourceId)), resourceId);

  }

}
