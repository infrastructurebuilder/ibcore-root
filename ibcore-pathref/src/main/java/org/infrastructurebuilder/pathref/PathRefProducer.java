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
package org.infrastructurebuilder.pathref;

import org.infrastructurebuilder.api.Weighted;
import org.infrastructurebuilder.api.base.NameDescribed;
import org.infrastructurebuilder.api.base.ResponsiveWith;

/**
 * Provides an (Optional) PathRef instance. The Optional part is for the case where the PathRef cannot be
 * configured/created, such as if pointed to a remote blobstore that was inaccessible for whatever reason.
 */
public interface PathRefProducer extends NameDescribed, ResponsiveWith<PathRef>, Weighted {

}
