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

import java.io.StringReader;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import javax.xml.parsers.DocumentBuilderFactory;

import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

public interface IBMetadata {
  public final static Function<Xpp3Dom, Document> fromXpp3Dom = (document) -> IBDataException.cet
      .withReturningTranslation(() -> DocumentBuilderFactory.newInstance().newDocumentBuilder()
          .parse(new InputSource(new StringReader(document.toString()))));

  String getName();
  Optional<String> getValue();
  Optional<List<IBMetadata>> getChildren();
  Xpp3Dom asDOM();
}
