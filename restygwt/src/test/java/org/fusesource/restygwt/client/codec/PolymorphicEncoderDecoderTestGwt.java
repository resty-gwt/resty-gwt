/**
 * Copyright (C) 2009-2015 the original author or authors.
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

package org.fusesource.restygwt.client.codec;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.junit.client.GWTTestCase;

import org.fusesource.restygwt.client.JsonEncoderDecoder;

public class PolymorphicEncoderDecoderTestGwt extends GWTTestCase {

    @Override
    public String getModuleName() {
        return "org.fusesource.restygwt.PolymorphicEncoderDecoderTestGwt";
    }

    @JsonTypeInfo(use = Id.CLASS, include = As.PROPERTY, property = "@class")
    @JsonSubTypes(
        { @Type(DefaultImplementationOfSubTypeInterface.class), @Type(SecondImplementationOfSubTypeInterface.class) })
    interface JsonSubTypesWithAnInterface {
        String getValue();
    }

    abstract static class AbstractSubType implements JsonSubTypesWithAnInterface {
    }

    static class DefaultImplementationOfSubTypeInterface extends AbstractSubType {

        private String value;

        @JsonCreator
        public DefaultImplementationOfSubTypeInterface(@JsonProperty("value") String value) {
            this.value = value;
        }

        @Override
        public String getValue() {
            return value;
        }
    }

    static class SecondImplementationOfSubTypeInterface extends AbstractSubType {

        public String value;

        @Override
        public String getValue() {
            return value;
        }
    }

    interface JsonSubTypesWithAnInterfaceCodec extends JsonEncoderDecoder<JsonSubTypesWithAnInterface> {
    }

    interface JsonSubTypesWithAnInterfaceImplementationCodec
        extends JsonEncoderDecoder<DefaultImplementationOfSubTypeInterface> {
    }

    public void testJsonSubTypesWithAnInterface() {
        JsonSubTypesWithAnInterfaceCodec codec = GWT.create(JsonSubTypesWithAnInterfaceCodec.class);
        String value = "Hello, world!";
        JsonSubTypesWithAnInterface o1 = new DefaultImplementationOfSubTypeInterface(value);

        JSONValue json = codec.encode(o1);
        assertEquals(json.isObject().get("@class").isString().stringValue(),
            DefaultImplementationOfSubTypeInterface.class.getName().replace("$", "."));
        JsonSubTypesWithAnInterface o2 = codec.decode(json);
        assertEquals(json.toString(), codec.encode(o2).toString());
        assertEquals(value, o1.getValue());
        assertEquals(o1.getValue(), o2.getValue());
        assertEquals(o2.getClass(), DefaultImplementationOfSubTypeInterface.class);
    }

    public void testJsonSubTypesWithInterfaceUsingConcreteImplementationCodec() {
        JsonSubTypesWithAnInterfaceImplementationCodec codec =
            GWT.create(JsonSubTypesWithAnInterfaceImplementationCodec.class);
        String value = "Hello, world!";
        DefaultImplementationOfSubTypeInterface o1 = new DefaultImplementationOfSubTypeInterface(value);

        JSONValue json = codec.encode(o1);
        JSONValue objectClass = json.isObject().get("@class");
        assertNotNull(objectClass);
        assertEquals(DefaultImplementationOfSubTypeInterface.class.getName().replace("$", "."),
            objectClass.isString().stringValue());
        DefaultImplementationOfSubTypeInterface o2 = codec.decode(json);
        assertEquals(json.toString(), codec.encode(o2).toString());
        assertEquals(value, o1.getValue());
    }

    // ######################################################################################

    @JsonTypeInfo(use = Id.CLASS, include = As.PROPERTY, property = "@class")
    @JsonSubTypes({ @Type(EnumOfSubTypeInterface.class) })
    interface JsonSubTypesWithAnInterfaceForUseWithAnEnum {
        @JsonProperty("name")
        String name();
    }

    enum EnumOfSubTypeInterface implements JsonSubTypesWithAnInterfaceForUseWithAnEnum {
        HELLO, WORLD
    }

    interface JsonSubTypesWithAnInterfaceForUseWithAnEnumCodec
        extends JsonEncoderDecoder<JsonSubTypesWithAnInterfaceForUseWithAnEnum> {
    }

    public void testJsonSubTypesWithAnInterfaceImplementedByAnEnum() {
        JsonSubTypesWithAnInterfaceForUseWithAnEnumCodec codec =
            GWT.create(JsonSubTypesWithAnInterfaceForUseWithAnEnumCodec.class);
        JSONValue json = codec.encode(EnumOfSubTypeInterface.HELLO);
        JsonSubTypesWithAnInterfaceForUseWithAnEnum useWithAnEnum = codec.decode(json);
        assertEquals(useWithAnEnum.name(), EnumOfSubTypeInterface.HELLO.name());
    }

    // ######################################################################################

    @JsonTypeInfo(use = Id.CLASS, include = As.PROPERTY, property = "@class")
    @JsonSubTypes({ @Type(SubForJsonProperty.class) })
    public static class BaseForJsonProperty {
        private String myField;

        @JsonProperty
        public String getMyField() {
            return myField;
        }

        public void setMyField(String myField) {
            this.myField = myField;
        }
    }

    public static class SubForJsonProperty extends BaseForJsonProperty {
        private String otherField;

        public String getOtherField() {
            return otherField;
        }

        public void setOtherField(String otherField) {
            this.otherField = otherField;
        }
    }

    interface JsonPropertyOnSuperClassCodec extends JsonEncoderDecoder<BaseForJsonProperty> {
    }

    public void testJsonPropertyOnSuperClass() {
        JsonPropertyOnSuperClassCodec codec = GWT.create(JsonPropertyOnSuperClassCodec.class);

        SubForJsonProperty o1 = new SubForJsonProperty();
        o1.setMyField("my-field-value");
        o1.setOtherField("other-field-value");

        JSONValue json = codec.encode(o1);

        assertEquals("{\"@class\":\"org.fusesource.restygwt.client.codec.PolymorphicEncoderDecoderTestGwt" +
                ".SubForJsonProperty\", \"otherField\":\"other-field-value\", \"myField\":\"my-field-value\"}",
            json.toString());
    }

    // ######################################################################################

    @JsonTypeInfo(use = Id.NAME, include = As.PROPERTY, property = "@class")
    @JsonTypeName("A")
    @JsonSubTypes({@Type(A.class), @Type(B.class)})
    public static class A {
        private String code;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }
    }

    @JsonTypeName("B")
    public static class B extends A {
        private String desc;

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }
    }

    public interface IdNamePolymorphicCodec extends JsonEncoderDecoder<A> {
    }

    public void testIdNamePolymorphic() {
        IdNamePolymorphicCodec codec = GWT.create(IdNamePolymorphicCodec.class);

        String json = "{\"code\":\"test code\"}";

        A obj = codec.decode(json);
    }

}