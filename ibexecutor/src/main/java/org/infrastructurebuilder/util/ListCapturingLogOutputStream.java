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
package org.infrastructurebuilder.util;

import java.io.PrintStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ListCapturingLogOutputStream extends CapturingLogOutputStream {
  private final List<String> list = new ArrayList<>();
  private final Optional<PrintStream> pw;

  public ListCapturingLogOutputStream(final Optional<Path> p, final Optional<PrintStream> pw) {
    super(p);
    this.pw = Objects.requireNonNull(pw);
  }

  public List<String> getList() {
    return list;
  }

  @Override
  public void secondaryProcessLine(final String line) {
    pw.ifPresent(w -> w.println(line));
    list.add(line);
  }

}
