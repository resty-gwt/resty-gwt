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

import org.fusesource.restygwt.client.JsonEncoderDecoder;

import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.junit.client.GWTTestCase;


public class EncoderDecoderTestGwt extends GWTTestCase{
    
    public interface WrapperLibraryCodec extends JsonEncoderDecoder<LibraryWithWrapper>{
    }
    public interface ArrayWrapperLibraryCodec extends JsonEncoderDecoder<LibraryWithArrayWrapper>{
    }
    public interface PropertyLibraryCodec extends JsonEncoderDecoder<LibraryWithProperty>{
    }
    
    static class ANumber<T extends Number> {
        
        T n;
        
        T get(){
            return n;
        }
    }
    public interface IntegerCodec extends JsonEncoderDecoder<ANumber<Integer>>{
    }
    public interface FloatCodec extends JsonEncoderDecoder<ANumber<Float>>{
    }

    @Override
    public String getModuleName() {
        return "org.fusesource.restygwt.EncoderDecoderTestGwt";
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

    public void testGenericTypes(){
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

    public interface CreatorCodec extends JsonEncoderDecoder<CredentialsWithCreator>{
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

    public interface WrapperCodec extends JsonEncoderDecoder<CredentialsWithWrapperObject>{
    }
    
    public interface SubWrapperCodec extends JsonEncoderDecoder<SubCredentialsWithWrapperObject>{
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

    public interface PropertyCodec extends JsonEncoderDecoder<CredentialsWithProperty>{
    }
    
    public interface SubPropertyCodec extends JsonEncoderDecoder<SubCredentialsWithProperty>{
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
    
    static interface BigCodec extends JsonEncoderDecoder<B>{
    } 
    
    public void testBigIntegers(){
        BigCodec big = GWT.create(BigCodec.class);
        B b = new B();
        b.age = new BigInteger("1234567890123456789012345678901234567890");
        JSONValue bJson = big.encode(b);
        B bRoundTrip = big.decode(bJson);
        assertEquals(b.age, bRoundTrip.age);
    }
}
