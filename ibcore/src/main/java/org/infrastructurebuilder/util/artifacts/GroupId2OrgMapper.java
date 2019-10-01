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
package org.infrastructurebuilder.util.artifacts;

import java.util.Optional;
import java.util.function.Function;

/**
 * An instance of this maps a given "type" of groupId to the "correct" github org
 * The trivial reverse-map case is io.github.xxx as a groupId is the xxx org
 * The second trivial revers map is that xxx without any dots is the xxx org
 *
 * However, you might have abitrary additional mapping:
 *  "com.myorganization" -- to "myorganization"
 *  "[org,com].myorganization.*" -- to  "myorganization" (possible collision!)
 *
 *  Return empty if the org doesn't map
 * @author mykel.alvis
 *
 */
public interface GroupId2OrgMapper extends Function<String, Optional<String>>, IdentifiedAndWeighted {

}
