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
package org.infrastructurebuilder.util.artifacts;

import java.util.List;
import java.util.function.Function;

import org.infrastructurebuilder.util.core.GAV;
import org.infrastructurebuilder.util.core.IdentifiedAndWeighted;

/**
 * Some resolver systems might need a bit of help mapping the resolved versions correctly.
 * This component type should return some list of values for a given string that are "valid" matches
 * based on the asset type needed.  For instance, if you're seeking a 1.0.0 version of a x86_64  of a zip file from
 * a github release of a Golang application called xyz , there might be multiple "release" identifiers that would work
 * such as "v1.0.0", "1.0.0", and "xyz-1.0.0"
 *
 * @author mykel.alvis
 *
 */
public interface VersionMapper extends Function<GAV,List<String>>, IdentifiedAndWeighted {

}
