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
package org.infrastructurebuilder.util.readdetect.impl;

import org.infrastructurebuilder.util.core.RelativeRoot;
import org.infrastructurebuilder.util.readdetect.model.v1_0.IBResourceModel;

/**
 * A ReferencedIBResource has a path that points to some fixed location that is <b><i>expected to be immutable for the
 * life-span of the object.</i></b>
 *
 * As IBResource objects can be serialized as metadata, it is <b><i>possible</i></b> that the target object is durably
 * represented by the serialized ReferencedIBResource. However, the system takes no measures to ensure that this is the
 * case. It is entirely the responsibility of external infrastructure to manage the availability and immutability of the
 * referenced resource.
 *
 * If a {@link RelativeRoot} exists for this resource, then that root <b>MUST</b> be a parent for the resource. It is
 * invalid to specify a resource location that is not a child of the given (optional) root.
 *
 * If no root is provided for this resource, then the source must be an absolute location. No relative paths or
 * sub-paths to existing resources are valid as locations.
 *
 * You have been warned.
 *
 * @param <T>
 */
abstract public class ReferencedIBResource<T> extends AbstractIBResource<T> {

  public ReferencedIBResource(RelativeRoot root, IBResourceModel model) {
    super(root, model);
  }

}
