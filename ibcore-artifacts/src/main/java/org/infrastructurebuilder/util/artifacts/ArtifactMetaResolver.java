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
package org.infrastructurebuilder.util.artifacts;

import java.util.Optional;
import java.util.function.Function;

import org.infrastructurebuilder.util.core.GAV;
import org.infrastructurebuilder.util.core.IdentifiedAndWeighted;

/**
 *
 * This is an artifact meta-resolver. It is meant to supply artifacts based on a signature, but does not specify where
 * those artifacts should come from. That is the pervue of the underlying "resolver".
 *
 * REQUIREMENT: The primary contract of this interface is that if a pre-resolved artifact path is available in the
 * source param, then the source parameter is returned unchanged as the result.
 *
 * REQUIREMENT: If the resolver cannot resolve the path, then Optional.empty() is the correct return result.
 *
 * A resolver has some internal (but undefined) understanding of where to look for the artifact bytestream. It then maps
 * the GAV from the supplied GAV param and returns that same GAV but with the file path set to that stream of bytes.
 *
 * This obviously means that any implementation requires access to a file that may not yet exist. The impl would need to
 * acquire that file in order to set the path in the resultant GAV.
 *
 * @author mykel.alvis
 *
 */
public interface ArtifactMetaResolver extends Function<GAV, Optional<GAV>>, IdentifiedAndWeighted {
}
