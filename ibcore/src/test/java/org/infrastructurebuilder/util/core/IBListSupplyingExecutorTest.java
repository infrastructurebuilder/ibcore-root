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
package org.infrastructurebuilder.util.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

import org.infrastructurebuilder.exceptions.IBException;
import org.infrastructurebuilder.pathref.TestingPathSupplier;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IBListSupplyingExecutorTest {

  private final static Logger log = LoggerFactory.getLogger(IBListSupplyingExecutorTest.class);
  private final static TestingPathSupplier wps = new TestingPathSupplier();

  @AfterAll
  public static void tearDownAfterClass() throws Exception {
    wps.finalize();
  }

  private IBListSupplyingExecutor<List<String>, Map<String, String>> e;
  private EnvSupplier config;
  private Map<String, String> map;

  @BeforeEach
  public void setUp() throws Exception {
    this.e = new ETest(() -> log);
    this.map = new HashMap<>();
    this.map.put("A", "Z");
    this.map.put("B", "Y");
    this.map.put("C", "X");
    this.config = new HandCraftedEnvSupplier(map);
  }

  @Test
  public void test() {
    IBListSupplyingExecutor<List<String>, Map<String, String>> q = this.e.configure(this.config);
    List<List<String>> l = q.get();
    assertEquals(1, l.size());
    List<String> m = l.get(0);
    assertEquals(3, m.size());
    List<String> theList = Arrays.asList("A", "B", "C");
    assertEquals(theList, m);
    IBListSupplyingExecutor<List<String>, Map<String, String>> q2 = this.e.configure(this.config);
    assertFalse(q == q2); // It is a different instance
    assertEquals(theList, q2.get().get(0));
    HandCraftedEnvSupplier newConfig = new HandCraftedEnvSupplier();
    assertEquals(0, this.e.configure(newConfig).get().get(0).size());
  }

  @Test
  public void testDefaultS() {
    assertEquals(0, this.e.getRequiredConfigItems().size());
    assertEquals(0, this.e.getOptionalConfigItems().size());
  }

  public class ETest implements IBListSupplyingExecutor<List<String>, Map<String, String>> {

    private final Logger log;
    private final Supplier<Map<String, String>> config;

    public ETest(LoggerSupplier l) {
      this(l, null);
    }

    private ETest(LoggerSupplier l, Supplier<Map<String, String>> config) {
      this.config = config;
      this.log = Objects.requireNonNull(l).get();
    }

    @Override
    public List<List<String>> get() {
      if (!config.get().keySet().containsAll(getRequiredConfigItems()))
        throw new IBException();
      getLog().info("The config value for 'A' is " + config.get().get("A"));
      return Arrays.asList(new ArrayList<String>(config.get().keySet().stream().sorted().toList()));
    }

    @Override
    public ETest configure(Supplier<Map<String, String>> config) {
      return new ETest(() -> log, config);
    }

    @Override
    public Logger getLog() {
      return log;
    }
  }
}
