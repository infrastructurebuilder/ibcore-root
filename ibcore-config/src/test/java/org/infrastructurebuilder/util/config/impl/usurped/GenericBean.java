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

import java.io.StringReader;

/**
 * 
 * @author John Aylward
 *
 * @param <T>
 *            generic number value
 */
public class GenericBean<T extends Number> implements MyBean {
    /**
     * @param genericValue
     *            value to initiate with
     */
    public GenericBean(T genericValue) {
        super();
        this.genericValue = genericValue;
    }

    /** */
    protected T genericValue;
    /** to be used by the calling test to see how often the getter is called */
    public int genericGetCounter;
    /** to be used by the calling test to see how often the setter is called */
    public int genericSetCounter;

    /** @return the genericValue */
    public T getGenericValue() {
        this.genericGetCounter++;
        return this.genericValue;
    }

    /**
     * @param genericValue
     *            generic value to set
     */
    public void setGenericValue(T genericValue) {
        this.genericSetCounter++;
        this.genericValue = genericValue;
    }

    @Override
    public Integer getIntKey() {
        return Integer.valueOf(42);
    }

    @Override
    public Double getDoubleKey() {
        return Double.valueOf(4.2);
    }

    @Override
    public String getStringKey() {
        return "MyString Key";
    }

    @Override
    public String getEscapeStringKey() {
        return "\"My String with \"s";
    }

    @Override
    public Boolean isTrueKey() {
        return Boolean.TRUE;
    }

    @Override
    public Boolean isFalseKey() {
        return Boolean.FALSE;
    }

    @Override
    public StringReader getStringReaderKey() {
        return new StringReader("Some String Value in a reader");
    }

}
