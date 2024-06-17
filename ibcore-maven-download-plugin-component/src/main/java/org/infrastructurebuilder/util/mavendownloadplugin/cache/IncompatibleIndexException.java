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
package org.infrastructurebuilder.util.mavendownloadplugin.cache;

/**
 * <p>Thrown when {@link FileBackedIndex} fails to read an existing index.</p>
 * <p>This occurs when upgrading to a new version of the plugin with breaking changes in the index storage strategy
 * (including Java serialization changes, or even moving to a different serialization mechanism (JSON, XML, etc.).</p>
 */
class IncompatibleIndexException extends Exception {
    private static final long serialVersionUID = -7428088362523122344L;

    IncompatibleIndexException(Exception cause) {
        super(cause);
    }
}
