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
///**
// * Copyright © 2019 admin (admin@infrastructurebuilder.org)
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *     http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//package org.infrastructurebuilder.util.artifacts;
//
//import java.util.Optional;
//
//import org.infrastructurebuilder.util.artifacts.impl.DefaultGAV;
//
//public class DummyGAV extends DefaultGAV {
//
//  private final String artifactId;
//  private final Optional<String> classifier;
//  private final String extension;
//  private final String groupId;
//  private final String versionString;
//
//  public DummyGAV(final String groupId, final String a, final String v, final String c, final String e) {
//    super(groupId, a, v, c, e);
//    this.groupId = groupId;
//    artifactId = a;
//    versionString = v;
//    classifier = Optional.ofNullable(c);
//    extension = e == null ? "jar" : e;
//  }
//
//  @Override
//  public String getArtifactId() {
//    return artifactId;
//  }
//
//  @Override
//  public Optional<String> getClassifier() {
//    return classifier;
//  }
//
//  @Override
//  public String getExtension() {
//    return extension;
//  }
//
//  @Override
//  public String getGroupId() {
//    return groupId;
//  }
//
//  @Override
//  public Optional<String> getVersion() {
//    return Optional.ofNullable(versionString);
//  }
//
//  @Override
//  public String toString() {
//    return getDefaultSignaturePath();
//  }
//
//}
