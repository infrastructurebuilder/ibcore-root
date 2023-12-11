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
package org.infrastructurebuilder.util.executor.execution.model;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Optional;

import org.infrastructurebuilder.util.core.DefaultIBVersion;
import org.infrastructurebuilder.util.core.DefaultIBVersion.DefaultIBVersionBoundedRange;
import org.infrastructurebuilder.util.core.IBVersion;
import org.infrastructurebuilder.util.core.IBVersion.IBVersionBoundedRange;
import org.infrastructurebuilder.util.core.Modeled;
import org.infrastructurebuilder.util.executor.ModeledProcessExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProcessPersistenceProvider implements ProcessExecutionVersionedPersistenceProvider{

  public static final String VERSION = "1.0";
  private final static Logger log = LoggerFactory.getLogger(ProcessPersistenceProvider.class);

  @Override
  public IBVersion getVersion() {
    return new DefaultIBVersion(VERSION);
  }

  @Override
  public IBVersionBoundedRange getVersionRange() {
    // This particular one can only handle version 1.0
    return DefaultIBVersionBoundedRange.versionBoundedRangeFrom(getVersion(), getVersion());
  }

  @Override
  public void write(Writer w, ModeledProcessExecution s) throws IOException {
    // TODO Auto-generated method stub
    
  }

  @Override
  public ModeledProcessExecution read(Reader r) throws IOException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Optional<ModeledProcessExecution> fromVersionedObject(Modeled o) {
    // TODO Auto-generated method stub
    return Optional.empty();
  }

}
