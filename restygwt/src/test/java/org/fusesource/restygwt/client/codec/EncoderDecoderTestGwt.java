/**
 * Copyright (C) 2009-2011 the original author or authors.
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

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.fusesource.restygwt.client.AbstractJsonEncoderDecoder;
import org.fusesource.restygwt.client.JsonEncoderDecoder;
import org.fusesource.restygwt.client.ObjectEncoderDecoder;

import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.junit.client.GWTTestCase;

public class EncoderDecoderTestGwt extends GWTTestCase {

    public interface WrapperLibraryCodec extends JsonEncoderDecoder<LibraryWithWrapper> {
    }

    public interface ArrayWrapperLibraryCodec extends JsonEncoderDecoder<LibraryWithArrayWrapper> {
    }

    public interface PropertyLibraryCodec extends JsonEncoderDecoder<LibraryWithProperty> {
    }

    static class ANumber<T extends Number> {

        T n;

        T get() {
            return n;
        }
    }

    public interface IntegerCodec extends JsonEncoderDecoder<ANumber<Integer>> {
    }

    public interface FloatCodec extends JsonEncoderDecoder<ANumber<Float>> {
    }

    public static class Foo {
        public List<String> bars = new ArrayList<String>();
    }

    public interface FooCodec extends JsonEncoderDecoder<Foo> {
    }

    @Override
    public String getModuleName() {
        return "org.fusesource.restygwt.EncoderDecoderTestGwt";
    }

    public void testNullValueAsList() {
        FooCodec fooCoder = GWT.create(FooCodec.class);

        Foo foo = new Foo();
        foo.bars.add(null);
        JSONValue fooJ = fooCoder.encode(foo);
        assertEquals(foo.bars, fooCoder.decode(fooJ).bars);
    }

    public void testSubtypeWrappeObjectWithSingleSubtype() {
        WrapperLibraryCodec lc = GWT.create(WrapperLibraryCodec.class);
        LibraryWithWrapper l = new LibraryWithWrapper();
        ArrayList<LibraryItemWithWrapper> libraryItems = new ArrayList<LibraryItemWithWrapper>();
        SpriteBasedItemWithWrapper li = new SpriteBasedItemWithWrapper();
        li.id = "1";
        li.imageRef = "src.png";
        libraryItems.add(li);
        l.items = libraryItems;

        JSONValue encode = lc.encode(l);
        LibraryWithWrapper decode = lc.decode(encode);
        assertEquals(l, decode);
    }

    public void testSubtypeArrayWrappeObjectWithSingleSubtype() {
        ArrayWrapperLibraryCodec lc = GWT.create(ArrayWrapperLibraryCodec.class);
        LibraryWithArrayWrapper l = new LibraryWithArrayWrapper();
        ArrayList<LibraryItemWithArrayWrapper> libraryItems = new ArrayList<LibraryItemWithArrayWrapper>();
        SpriteBasedItemWithArrayWrapper li = new SpriteBasedItemWithArrayWrapper();
        li.id = "1";
        li.imageRef = "src.png";
        libraryItems.add(li);
        l.items = libraryItems;

        JSONValue encode = lc.encode(l);
        LibraryWithArrayWrapper decode = lc.decode(encode);
        assertEquals(l, decode);
    }

    public void testSubtypePropertytWithSingleSubtype() {
        PropertyLibraryCodec lc = GWT.create(PropertyLibraryCodec.class);
        LibraryWithProperty l = new LibraryWithProperty();
        ArrayList<LibraryItemWithProperty> libraryItems = new ArrayList<LibraryItemWithProperty>();
        SpriteBasedItemWithProperty li = new SpriteBasedItemWithProperty();
        li.id = "1";
        li.imageRef = "src.png";
        libraryItems.add(li);
        l.items = libraryItems;

        JSONValue encode = lc.encode(l);
        LibraryWithProperty decode = lc.decode(encode);
        assertEquals(l, decode);
    }

    public void testGenericTypes() {
        IntegerCodec integerCoder = GWT.create(IntegerCodec.class);
        FloatCodec floatCoder = GWT.create(FloatCodec.class);

        ANumber<Integer> intA = new ANumber<Integer>();
        intA.n = 123;
        JSONValue intJ = integerCoder.encode(intA);

        assertEquals(intA.n, integerCoder.decode(intJ).n);

        ANumber<Float> floatA = new ANumber<Float>();
        floatA.n = 123.456f;
        JSONValue floatJ = floatCoder.encode(floatA);

        assertEquals(floatA.n, floatCoder.decode(floatJ).n);
    }

    public interface CreatorCodec extends JsonEncoderDecoder<CredentialsWithCreator> {
    }

    public void testCreators() {
        CreatorCodec codec = GWT.create(CreatorCodec.class);
        CredentialsWithCreator c = new CredentialsWithCreator("email", "password");
        c.age = 12;
        JSONValue cJson = codec.encode(c);
        CredentialsWithCreator cRoundTrip = codec.decode(cJson);
        assertEquals("email", cRoundTrip.email);
        assertEquals("password", cRoundTrip.password);
        assertEquals(12, cRoundTrip.age);
    }

    public void testCreatorsWithNullValue() {
        CreatorCodec codec = GWT.create(CreatorCodec.class);
        CredentialsWithCreator c = new CredentialsWithCreator(null, "password");
        c.age = 12;
        JSONValue cJson = codec.encode(c);
        CredentialsWithCreator cRoundTrip = codec.decode(cJson);
        assertNull(cRoundTrip.email);
        assertEquals("password", cRoundTrip.password);
        assertEquals(12, cRoundTrip.age);
    }

    public interface WrapperCodec extends JsonEncoderDecoder<CredentialsWithWrapperObject> {
    }

    public interface SubWrapperCodec extends JsonEncoderDecoder<SubCredentialsWithWrapperObject> {
    }

    public void testSubtypeWrapperObject() {
        WrapperCodec codec = GWT.create(WrapperCodec.class);
        CredentialsWithWrapperObject base = new CredentialsWithWrapperObject();
        base.setEmail("email-super");
        base.setPassword("password-super");
        JSONValue baseJson = codec.encode(base);
        CredentialsWithWrapperObject baseRoundTrip = codec.decode(baseJson);
        assertEquals("email-super", baseRoundTrip.email);
        assertEquals("password-super", baseRoundTrip.password);
        assertFalse(baseRoundTrip.getClass().equals(SubCredentialsWithWrapperObject.class));

        SubCredentialsWithWrapperObject sub = new SubCredentialsWithWrapperObject();
        sub.setEmail("email-sub");
        sub.setPassword("password-sub");
        sub.login = "login-sub";
        JSONValue subJson = codec.encode(sub);
        SubCredentialsWithWrapperObject subRoundTrip = (SubCredentialsWithWrapperObject) codec.decode(subJson);
        assertEquals("email-sub", subRoundTrip.email);
        assertEquals("password-sub", subRoundTrip.password);
        assertEquals("login-sub", subRoundTrip.login);

        SubWrapperCodec subCodec = GWT.create(SubWrapperCodec.class);
        sub.setEmail("email-direct");
        sub.setPassword("password-direct");
        sub.login = "login-direct";
        subJson = subCodec.encode(sub);
        subRoundTrip = subCodec.decode(subJson);
        assertEquals("email-direct", subRoundTrip.email);
        assertEquals("password-direct", subRoundTrip.password);
        assertEquals("login-direct", subRoundTrip.login);
    }

    public interface PropertyCodec extends JsonEncoderDecoder<CredentialsWithProperty> {
    }

    public interface SubPropertyCodec extends JsonEncoderDecoder<SubCredentialsWithProperty> {
    }

    public void testSubtypeProperty() {
        PropertyCodec codec = GWT.create(PropertyCodec.class);
        CredentialsWithProperty base = new CredentialsWithProperty();
        base.setEmail("email-super");
        base.setPassword("password-super");
        JSONValue baseJson = codec.encode(base);
        CredentialsWithProperty baseRoundTrip = codec.decode(baseJson);
        assertEquals("email-super", baseRoundTrip.email);
        assertEquals("password-super", baseRoundTrip.password);
        assertFalse(baseRoundTrip.getClass().equals(SubCredentialsWithProperty.class));

        SubCredentialsWithProperty sub = new SubCredentialsWithProperty();
        sub.setEmail("email-sub");
        sub.setPassword("password-sub");
        sub.login = "login-sub";
        JSONValue subJson = codec.encode(sub);
        SubCredentialsWithProperty subRoundTrip = (SubCredentialsWithProperty) codec.decode(subJson);
        assertEquals("email-sub", subRoundTrip.email);
        assertEquals("password-sub", subRoundTrip.password);
        assertEquals("login-sub", subRoundTrip.login);

        SubPropertyCodec subCodec = GWT.create(SubPropertyCodec.class);
        sub.setEmail("email-direct");
        sub.setPassword("password-direct");
        sub.login = "login-direct";
        subJson = subCodec.encode(sub);
        subRoundTrip = subCodec.decode(subJson);
        assertEquals("email-direct", subRoundTrip.email);
        assertEquals("password-direct", subRoundTrip.password);
        assertEquals("login-direct", subRoundTrip.login);
    }

    static class B {
        BigInteger age;
    }

    static interface BigCodec extends JsonEncoderDecoder<B> {
    }

    public void testBigIntegers() {
        BigCodec big = GWT.create(BigCodec.class);
        B b = new B();
        b.age = new BigInteger("1234567890123456789012345678901234567890");
        JSONValue bJson = big.encode(b);
        B bRoundTrip = big.decode(bJson);
        assertEquals(b.age, bRoundTrip.age);
    }

    public void testObjectEncoderDecoder() {
        {
            double value = Math.random() * 10000;
            JSONValue json = ObjectEncoderDecoder.INSTANCE.encode(value);
            assertEquals(value, ObjectEncoderDecoder.INSTANCE.decode(json));
        }
        {
            String value = "Fred Flintstone";
            JSONValue json = ObjectEncoderDecoder.INSTANCE.encode(value);
            assertEquals(value, ObjectEncoderDecoder.INSTANCE.decode(json));
        }
        {
            boolean value = Boolean.TRUE;
            JSONValue json = ObjectEncoderDecoder.INSTANCE.encode(value);
            assertEquals(value, ObjectEncoderDecoder.INSTANCE.decode(json));
        }
        {
            Map<String, Object> value = new HashMap<String, Object>();
            value.put("fred", "flintstone");
            value.put("shoeSize", 12.0);
            value.put("geek", true);
            JSONValue json = ObjectEncoderDecoder.INSTANCE.encode(value);
            assertEquals(value, ObjectEncoderDecoder.INSTANCE.decode(json));
        }
        {
            List<Object> value = new ArrayList<Object>();
            value.add("Fred Flintstone");
            value.add(12.0);
            value.add(false);
            JSONValue json = ObjectEncoderDecoder.INSTANCE.encode(value);
            assertEquals(value, ObjectEncoderDecoder.INSTANCE.decode(json));
        }

    }

    public void testIntegerToStringDecode() {
        Integer i = 123;
        assertEquals(i.toString(), AbstractJsonEncoderDecoder.STRING.decode(AbstractJsonEncoderDecoder.INT.encode(i)));
    }

    public void testBooleanToStringDecode() {
        Boolean b = true;
        assertEquals(b.toString(),
                AbstractJsonEncoderDecoder.STRING.decode(AbstractJsonEncoderDecoder.BOOLEAN.encode(b)));
    }
    
    public void testBooleanArrayDecode() {
        boolean[] array = {true, false};
        AbstractJsonEncoderDecoder<Boolean> encoder = AbstractJsonEncoderDecoder.BOOLEAN;
        assertEquals(Arrays.toString(array),
                Arrays.toString(AbstractJsonEncoderDecoder.toArray(AbstractJsonEncoderDecoder.toJSON(array, encoder), 
                            encoder, new boolean[2])));
    }

    public void testByteArrayDecode() {
        byte[] array = {2, 8};
        AbstractJsonEncoderDecoder<Byte> encoder = AbstractJsonEncoderDecoder.BYTE;
        assertEquals(Arrays.toString(array),
                Arrays.toString(AbstractJsonEncoderDecoder.toArray(AbstractJsonEncoderDecoder.toJSON(array, encoder), 
                            encoder, new byte[2])));
    }

    public void testCharacterArrayDecode() {
        char[] array = {'a','z'};
        AbstractJsonEncoderDecoder<Character> encoder = AbstractJsonEncoderDecoder.CHAR;
        assertEquals(Arrays.toString(array),
                Arrays.toString(AbstractJsonEncoderDecoder.toArray(AbstractJsonEncoderDecoder.toJSON(array, encoder), 
                            encoder, new char[2])));
    }

    public void testFloatArrayDecode() {
        float[] array = {1.4e19f, -13.53e-18f};
        AbstractJsonEncoderDecoder<Float> encoder = AbstractJsonEncoderDecoder.FLOAT;
        assertEquals(Arrays.toString(array),
                Arrays.toString(AbstractJsonEncoderDecoder.toArray(AbstractJsonEncoderDecoder.toJSON(array, encoder), 
                            encoder, new float[2])));
    }

    public void testDoubleArrayDecode() {
        double[] array = {1.4e193, -13.53e-188};
        AbstractJsonEncoderDecoder<Double> encoder = AbstractJsonEncoderDecoder.DOUBLE;
        assertEquals(Arrays.toString(array),
                Arrays.toString(AbstractJsonEncoderDecoder.toArray(AbstractJsonEncoderDecoder.toJSON(array, encoder), 
                            encoder, new double[2])));
    }

    public void testShortArrayDecode() {
        short[] array = {1, -13};
        AbstractJsonEncoderDecoder<Short> encoder = AbstractJsonEncoderDecoder.SHORT;
        assertEquals(Arrays.toString(array),
                Arrays.toString(AbstractJsonEncoderDecoder.toArray(AbstractJsonEncoderDecoder.toJSON(array, encoder), 
                            encoder, new short[2])));
    }

    public void testIntArrayDecode() {
        int[] array = {1010, -13100};
        AbstractJsonEncoderDecoder<Integer> encoder = AbstractJsonEncoderDecoder.INT;
        assertEquals(Arrays.toString(array),
                Arrays.toString(AbstractJsonEncoderDecoder.toArray(AbstractJsonEncoderDecoder.toJSON(array, encoder), 
                            encoder, new int[2])));
    }

    public void testLongArrayDecode() {
        long[] array = {1010, -13100};
        AbstractJsonEncoderDecoder<Long> encoder = AbstractJsonEncoderDecoder.LONG;
        assertEquals(Arrays.toString(array),
                Arrays.toString(AbstractJsonEncoderDecoder.toArray(AbstractJsonEncoderDecoder.toJSON(array, encoder), 
                            encoder, new long[2])));
    }

    public void testTypeArrayDecode() {
        String[] array = {"may", "all", "be", "happy"};
        AbstractJsonEncoderDecoder<String> encoder = AbstractJsonEncoderDecoder.STRING;
        assertEquals(Arrays.toString(array),
                Arrays.toString(AbstractJsonEncoderDecoder.toArray(AbstractJsonEncoderDecoder.toJSON(array, encoder), 
                            encoder, new String[4])));
    }

    static class CCC {
        
        @JsonIgnore
        private String lastName;
        
        String name;
        
        @JsonIgnore
        String firstName;
        
        String getLastName(){
            return lastName;
        }
        
        void setLastName(String name){
            lastName = name;
        }
    }

    static interface CCCCodec extends JsonEncoderDecoder<CCC> {
    }

    public void testIgnores() {
        CCCCodec cccc = GWT.create(CCCCodec.class);
        CCC ccc = new CCC();
        ccc.name = "me and the corner";
        ccc.firstName = "chaos";
        
        JSONValue json = cccc.encode(ccc);
        System.err.println("\n\n\n\n\n\n" + json + "\n\n\n\n\n\n");
        assertEquals("{\"name\":\"me and the corner\"}", json.toString());
        CCC roundTrip = cccc.decode(json);
        assertEquals(ccc.name, roundTrip.name);
        assertNull(roundTrip.firstName);
        assertNull(roundTrip.getLastName());
    }

}
