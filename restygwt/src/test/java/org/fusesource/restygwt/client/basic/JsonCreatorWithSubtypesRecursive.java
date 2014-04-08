/**
 * Copyright (C) 2009-2012 the original author or authors.
 * See the notice.md file distributed with this work for additional
 * information regarding copyright ownership.
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

package org.fusesource.restygwt.client.basic;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.annotation.JsonTypeName;
import org.fusesource.restygwt.client.JsonEncoderDecoder;
import org.junit.Test;

import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.junit.client.GWTTestCase;

public class JsonCreatorWithSubtypesRecursive extends GWTTestCase {

    @Override
    public String getModuleName() {
	return "org.fusesource.restygwt.BasicTestGwt";
    }

    @JsonTypeInfo(use = Id.NAME, include = As.PROPERTY, property = "@type")
    @JsonSubTypes({ @Type(Thing1.class), @Type(Thing2.class) })
    public static abstract class AbstractThing {
	private final String name;

	protected AbstractThing(String name) {
	    this.name = name;
	}

	public String getName() {
	    return name;
	}
    }

    @JsonTypeName("thing1")
    public static class Thing1 extends AbstractThing {
	private final double value;

	@JsonCreator
	public Thing1(@JsonProperty("name") String name, @JsonProperty("value") double value) {
	    super(name);
	    this.value = value;
	}

	public double getValue() {
	    return value;
	}
    }

    @JsonTypeName("thing2")
    @JsonSubTypes({ @Type(Thing3.class)})
    public static class Thing2 extends AbstractThing {
        private final String value;

        @JsonCreator
        public Thing2(@JsonProperty("name")
        String name, @JsonProperty("value")
        String value) {
            super(name);
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    @JsonTypeName("thing3")
    public static class Thing3 extends Thing2 {
        private final String value2;

        @JsonCreator
        public Thing3(@JsonProperty("name")
        String name, @JsonProperty("value")
        String value, @JsonProperty("value2")
        String value2) {
            super(name, value);
            this.value2 = value2;
        }

        public String getValue2() {
            return value2;
        }
    }

    public interface ThingCodec extends JsonEncoderDecoder<AbstractThing> {
    }

    @Test
    public void test() {
	ThingCodec codec = GWT.create(ThingCodec.class);

	Thing1 t1 = new Thing1("Fred", 12.0);
	JSONValue t1v = codec.encode(t1);
	Thing1 t1a = (Thing1)codec.decode(t1v);

	assertEquals("name", t1.getName(), t1a.getName());
	assertEquals("value", t1.getValue(), t1a.getValue(), 0.0);

	Thing2 t2 = new Thing2("Fred", "Bob");
    JSONValue t2v = codec.encode(t2);
    Thing2 t2a = (Thing2)codec.decode(t2v);

    assertEquals("name", t2.getName(), t2a.getName());
    assertEquals("value", t2.getValue(), t2a.getValue());

    Thing3 t3 = new Thing3("Fred", "Bob", "Wilma"); //$NON-NLS-1$
    JSONValue t3v = codec.encode(t3);
    Thing3 t3a = (Thing3)codec.decode(t3v);
    assertNotNull(t3a);

    assertEquals("name", t3.getName(), t3a.getName());
    assertEquals("value", t3.getValue(), t3a.getValue());
    assertEquals("value2", t3.getValue2(), t3a.getValue2());
    }
}
