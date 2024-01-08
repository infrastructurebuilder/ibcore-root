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

import static java.util.Optional.ofNullable;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.infrastructurebuilder.exceptions.IBException;
import org.infrastructurebuilder.util.artifacts.impl.DefaultGAVMatcher;
import org.infrastructurebuilder.util.core.GAVMatcher;
import org.infrastructurebuilder.util.core.IBVersion.IBVersionRange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class GAVMatcherBuilder {
  private final static Logger log = LoggerFactory.getLogger(GAVMatcherBuilder.class);

  public final static GAVMatcherBuilder builder() {
    return new GAVMatcherBuilder();
  }

  private String groupId;
  private String artifactId;
  private IBVersionRange versionRange;
  private String versionAsString;
  private String classifier;
  private String type;

  private String compileIt(String x) {
    try {
      Pattern.compile(x);
      return x;
    } catch (PatternSyntaxException e) {
      log.error("Pattern " + x + " is not a valid regex", e);
      throw new IBException(e);
    }
  }

  public GAVMatcherBuilder withGroupId(String id) {
    this.groupId = compileIt(id);
    return this;
  }

  public GAVMatcherBuilder withArtifactId(String id) {
    this.artifactId = compileIt(id);
    return this;
  }

  public GAVMatcherBuilder withVersionRange(IBVersionRange range) {
    this.versionRange = range;
    return this;
  }

  public GAVMatcherBuilder withVersionAsStringPattern(String version) {
    this.versionAsString = compileIt(version);
    return this;
  }

  public GAVMatcherBuilder withClassifier(String id) {
    this.classifier = compileIt(id);
    return this;
  }

  public GAVMatcherBuilder withType(String id) {
    this.type = compileIt(id);
    return this;
  }

  public GAVMatcher build() {
    return DefaultGAVMatcher.from(ofNullable(this.groupId), ofNullable(this.artifactId), ofNullable(this.versionRange),
        ofNullable(this.versionAsString), ofNullable(this.classifier), ofNullable(this.type));
  }

}
