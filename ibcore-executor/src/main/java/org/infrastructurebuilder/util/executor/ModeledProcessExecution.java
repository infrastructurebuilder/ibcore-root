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
package org.infrastructurebuilder.util.executor;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toMap;

import java.util.List;
import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.Function;

import org.infrastructurebuilder.exceptions.IBException;
import org.infrastructurebuilder.pathref.AbsoluteURLRelativeRoot;
import org.infrastructurebuilder.pathref.ChecksumBuilder;
import org.infrastructurebuilder.pathref.ChecksumBuilderFactory;
import org.infrastructurebuilder.pathref.JSONAndChecksumEnabled;
import org.infrastructurebuilder.pathref.RelativeRoot;
import org.infrastructurebuilder.util.core.Modeled;
import org.infrastructurebuilder.util.executor.model.executor.model.utils.IBCoreExecutorModelUtils;
import org.infrastructurebuilder.util.executor.model.executor.model.v1_0.Environment;
import org.infrastructurebuilder.util.executor.model.executor.model.v1_0.GeneratedProcessExecution;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;

public class ModeledProcessExecution extends GeneratedProcessExecution implements Modeled, JSONAndChecksumEnabled {
  private static final long serialVersionUID = 92979582578090306L;
  private final static Logger log = LoggerFactory.getLogger(ModeledProcessExecution.class);
  public final static Function<Environment, Optional<SortedMap<String, String>>> envToMapSS = (e) -> {
    return requireNonNull(e).getEnvEntry().map(es -> es.stream().collect(toMap(k -> k.getKey(), v -> v.getValue(), //
        (v1, v2) -> {
          throw new IBException(format("Duplicate %s / %s", v1, v2));
        }, TreeMap::new)));
  };

  public ModeledProcessExecution() {
    super();
  }

  public ModeledProcessExecution(GeneratedProcessExecution s) {
    super(s.getModelVersion() //
        , s.getId() //
        , s.getExecutable() //
        , s.getArguments().orElse(null) //
        , s.getTimeout().orElse(null) //
        , s.getOptional().orElse(null) //
        , s.getBackground().orElse(null) //
        , s.getWorkDirectory().orElse(null) //
        , s.getExitValues().orElse(null) //
        , s.getStdOutPath().orElse(null) //
        , s.getStdErrPath().orElse(null) //
        , s.getStdInPath().orElse(null) //
        , s.getRelativeRootURL().orElse(null)//
        , s.getEnvironment().orElse(null));
  }

  public ModeledProcessExecution(String modelVersion, String id, String executable, List<String> arguments,
      String timeout, Boolean optional, Boolean background, String workDirectory, List<String> exitValues,
      String stdOutPath, String stdErrPath, String stdInPath, String relativeRootURL, Environment environment)
  {
    super(modelVersion, id, executable, arguments, timeout, optional, background, workDirectory, exitValues, stdOutPath,
        stdErrPath, stdInPath, relativeRootURL, environment);
  }

  @Override
  public JSONObject asJSON() {
    String json = null;
    try {
      json = IBCoreExecutorModelUtils.getObjectMapper().writeValueAsString(this);
      return new JSONObject(json);
    } catch (JsonProcessingException e1) {
      log.error("Error processinging asJSON()", e1);
      throw new ProcessException("Cannot convert modeled process to JSON", e1);
    }
  }

  @Override
  public Optional<RelativeRoot> getRelativeRoot() {
    return getRelativeRootURL().map(AbsoluteURLRelativeRoot::new);
  }

  @Override
  public ChecksumBuilder getChecksumBuilder() {
    return ChecksumBuilderFactory.newAlternateInstanceWithRelativeRoot(this.getRelativeRoot()).addString(getModelVersion()) //
        .addString(getId()) //
        .addString(getExecutable()) //
        .addListString(getArguments()) //
        .addString(getTimeout()) //
        .addBoolean(getOptional()) //
        .addBoolean(getBackground()) //
        .addPathAsString(getWorkDirectory()) //
        .addListString(getExitValues()) //
        .addPathAsString(getStdOutPath()) //
        .addPathAsString(getStdErrPath()) //
        .addPathAsString(getStdInPath()) //
//    .addPathAsString(getRelativeRootURL()) // Never add RR to Checksum
        .addMapStringString(getEnvironment().flatMap(ModeledProcessExecution.envToMapSS));
  }

}
