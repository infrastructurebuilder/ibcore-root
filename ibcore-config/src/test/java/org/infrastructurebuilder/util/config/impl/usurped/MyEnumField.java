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
package org.infrastructurebuilder.util.config.impl.usurped;

/**
 * An enum that contains getters and some internal fields
 */
@SuppressWarnings("boxing")
public enum MyEnumField {
    VAL1(1, "val 1"),
    VAL2(2, "val 2"),
    VAL3(3, "val 3");

    private String value;
    private Integer intVal;
    private MyEnumField(Integer intVal, String value) {
        this.value = value;
        this.intVal = intVal;
    }
    public String getValue() {
        return this.value;
    }
    public Integer getIntVal() {
        return this.intVal;
    }
    @Override
    public String toString(){
        return this.value;
    }
}
