package org.fusesource.restygwt.client.codec;

import org.fusesource.restygwt.client.JsonEncoderDecoder;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.junit.client.GWTTestCase;

public class JsonIgnoreEncoderTestGwt extends GWTTestCase {

    @JsonTypeInfo(use = Id.NAME, include = As.PROPERTY, property = "@type")
    @JsonSubTypes({ @Type(Thing.class), @Type(SubThing.class) })
    @JsonTypeName("thing")
    public static class Thing {

        private String name;
        private String bar;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @JsonIgnore
        public String getBar() {
            return bar;
        }

        public void setBar(String bar) {
            this.bar = bar;
        }

        @JsonIgnore
        public String getFoo() {
            return "foo";
        }
    }

    @JsonTypeName("subthing")
    public static class SubThing extends Thing {

        private String foo;

        @Override
        @JsonIgnore(false)
        public String getBar() {
            return super.getBar();
        }

        @Override
        @JsonIgnore(false)
        public String getFoo() {
            return foo;
        }

        public void setFoo(String foo) {
            this.foo = foo;
        }
    }

    public interface ThingCodec extends JsonEncoderDecoder<Thing> {
    }

    @Override
    public String getModuleName() {
        return "org.fusesource.restygwt.JsonIgnoreEncoderTestGwt";
    }

    public void testEncodeJsonIgnoreTrueWithFieldDefinedInChild() {
        ThingCodec codec = GWT.create(ThingCodec.class);

        Thing thing = new Thing();

        JSONValue thingJson = codec.encode(thing);
        assertNull(thingJson.isObject().get("foo"));
    }

    public void testEncodeJsonIgnoreFalseWithFieldDefinedInChild() {
        ThingCodec codec = GWT.create(ThingCodec.class);

        SubThing thing = new SubThing();
        thing.setFoo("foo");

        JSONValue thingJson = codec.encode(thing);
        assertEquals(thing.getFoo(), thingJson.isObject().get("foo").isString().stringValue());
    }

    public void testEncodeJsonIgnoreFalseWithFieldDefinedInParent() {
        ThingCodec codec = GWT.create(ThingCodec.class);

        SubThing thing = new SubThing();
        thing.setBar("bar");

        JSONValue thingJson = codec.encode(thing);
        assertEquals(thing.getBar(), thingJson.isObject().get("bar").isString().stringValue());
    }
}