/**
 * Copyright (C) 2009  Hiram Chirino
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *    http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hiramchirino.restygwt.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONBoolean;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.XMLParser;

abstract public class JsonEncoderDecoder<T> {

    @SuppressWarnings("serial")
    public static class EncodingException extends RuntimeException {
        public EncodingException(String msg) {
            super(msg);
        }
    }

    @SuppressWarnings("serial")
    public static class DecodingException extends RuntimeException {
        public DecodingException(String msg) {
            super(msg);
        }
    }
    
    abstract public JSONValue encode(T value) throws EncodingException;
    abstract public T decode(JSONValue value) throws DecodingException;
    
    ///////////////////////////////////////////////////////////////////
    // Built in encoders for the native types.
    ///////////////////////////////////////////////////////////////////
    public static final JsonEncoderDecoder<Boolean> BOOLEAN = new JsonEncoderDecoder<Boolean>() {
        @Override
        public Boolean decode(JSONValue value) throws DecodingException {
            if( value == null ) {
                return null;
            }
            JSONBoolean bool = value.isBoolean();
            if( bool==null ) {
                throw new DecodingException("Expected a json boolean, but was given: "+value);
            }
            return bool.booleanValue();
        }
        @Override
        public JSONValue encode(Boolean value) throws EncodingException {
            if( value == null ) {
                return null;
            }
            return JSONBoolean.getInstance(value);
        }
    };
    
    public static final JsonEncoderDecoder<Character> CHAR = new JsonEncoderDecoder<Character>() {
        @Override
        public Character decode(JSONValue value) throws DecodingException {
            if( value == null ) {
                return null;
            }
            return (char)toDouble(value);
        }
        @Override
        public JSONValue encode(Character value) throws EncodingException {
            if( value==null ) {
                return null;
            }
            return new JSONNumber(value);
        }
    };
    
    public static final JsonEncoderDecoder<Short> SHORT = new JsonEncoderDecoder<Short>() {
        @Override
        public Short decode(JSONValue value) throws DecodingException {
            if( value == null ) {
                return null;
            }
            return (short)toDouble(value);
        }
        @Override
        public JSONValue encode(Short value) throws EncodingException {
            if( value==null ) {
                return null;
            }
            return new JSONNumber(value);
        }
    };

    public static final JsonEncoderDecoder<Integer> INT = new JsonEncoderDecoder<Integer>() {
        @Override
        public Integer decode(JSONValue value) throws DecodingException {
            if( value == null ) {
                return null;
            }
            return (int)toDouble(value);
        }
        @Override
        public JSONValue encode(Integer value) throws EncodingException {
            if( value==null ) {
                return null;
            }
            return new JSONNumber(value);
        }
    };

    public static final JsonEncoderDecoder<Long> LONG = new JsonEncoderDecoder<Long>() {
        @Override
        public Long decode(JSONValue value) throws DecodingException {
            if( value == null ) {
                return null;
            }
            return (long)toDouble(value);
        }
        @Override
        public JSONValue encode(Long value) throws EncodingException {
            if( value==null ) {
                return null;
            }
            return new JSONNumber(value);
        }
    };

    public static final JsonEncoderDecoder<Float> FLOAT = new JsonEncoderDecoder<Float>() {
        @Override
        public Float decode(JSONValue value) throws DecodingException {
            if( value == null ) {
                return null;
            }
            return (float)toDouble(value);
        }
        @Override
        public JSONValue encode(Float value) throws EncodingException {
            if( value==null ) {
                return null;
            }
            return new JSONNumber(value);
        }
    };
    
    public static final JsonEncoderDecoder<Double> DOUBLE = new JsonEncoderDecoder<Double>() {
        @Override
        public Double decode(JSONValue value) throws DecodingException {
            if( value == null ) {
                return null;
            }
            return toDouble(value);
        }
        @Override
        public JSONValue encode(Double value) throws EncodingException {
            if( value==null ) {
                return null;
            }
            return new JSONNumber(value);
        }
    };
    
    public static final JsonEncoderDecoder<String> STRING = new JsonEncoderDecoder<String>() {
        @Override
        public String decode(JSONValue value) throws DecodingException {
            if( value == null ) {
                return null;
            }
            JSONString str = value.isString();
            if( str==null ) {
                throw new DecodingException("Expected a json string, but was given: "+value);
            }
            return str.stringValue();
        }
        @Override
        public JSONValue encode(String value) throws EncodingException {
            if( value==null ) {
                return null;
            }
            return new JSONString(value);
        }
    };
    
    public static final JsonEncoderDecoder<Document> DOCUMENT = new JsonEncoderDecoder<Document>() {
        @Override
        public Document decode(JSONValue value) throws DecodingException {
            if( value == null ) {
                return null;
            }
            JSONString str = value.isString();
            if( str==null ) {
                throw new DecodingException("Expected a json string, but was given: "+value);
            }
            return XMLParser.parse(str.stringValue());
        }
        @Override
        public JSONValue encode(Document value) throws EncodingException {
            if( value==null ) {
                return null;
            }
            return new JSONString(value.toString());
        }
    };
    
    public static final JsonEncoderDecoder<JSONValue> JSON_VALUE = new JsonEncoderDecoder<JSONValue>() {
        @Override
        public JSONValue decode(JSONValue value) throws DecodingException {
            return value;
        }
        @Override
        public JSONValue encode(JSONValue value) throws EncodingException {
            return value;
        }
    }; 
    
    ///////////////////////////////////////////////////////////////////
    // Helper Methods.
    ///////////////////////////////////////////////////////////////////
    static private double toDouble(JSONValue value) {
        JSONNumber number = value.isNumber();
        if( number==null ) {
            throw new DecodingException("Expected a json number, but was given: "+value);
        }
        return number.doubleValue();
    }

    static protected JSONObject toObject(JSONValue value ) {
        JSONObject object = value.isObject();
        if( object == null ) {
            throw new DecodingException("Expected a json obejct, but was given: "+object);
        }
        return object;
    }
        
    static protected <Type> List<Type> toList(JSONValue value, JsonEncoderDecoder<Type> encoder) {
        if( value == null ) {
            return null;
        }
        JSONArray array = value.isArray();
        if( array==null ) {
            throw new DecodingException("Expected a json array, but was given: "+value);
        }
        
        ArrayList<Type> rc = new ArrayList<Type>(array.size());
        int size = array.size();
        for (int i = 0; i < size; i++) {
            rc.add( encoder.decode(array.get(i)) );
        }
        return rc;
    }
    
    static protected <Type> Set<Type> toSet(JSONValue value, JsonEncoderDecoder<Type> encoder) {
        if( value == null ) {
            return null;
        }
        JSONArray array = value.isArray();
        if( array==null ) {
            throw new DecodingException("Expected a json array, but was given: "+value);
        }
        
        HashSet<Type> rc = new HashSet<Type>(array.size()*2);
        int size = array.size();
        for (int i = 0; i < size; i++) {
            rc.add( encoder.decode(array.get(i)) );
        }
        return rc;
    }

    static protected <Type> Map<String,Type> toMap(JSONValue value, JsonEncoderDecoder<Type> encoder) {
        if( value == null ) {
            return null;
        }
        JSONObject object = value.isObject();
        if( object==null ) {
            throw new DecodingException("Expected a json array, but was given: "+value);
        }
        
        HashMap<String,Type> rc = new HashMap<String,Type>(object.size()*2);
        for (String key : object.keySet()) {
            rc.put(key, encoder.decode(object.get(key)));
        }
        return rc;
    }
    
    static protected <Type> JSONValue toJSON(Map<String, Type> value, JsonEncoderDecoder<Type> encoder) {
        if( value == null ) {
            return null;
        }
        JSONObject rc = new JSONObject();
        for (Entry<String, Type> t : value.entrySet()) {
            rc.put(t.getKey(), encoder.encode(t.getValue()));
        }
        return rc;
    }
    static protected <Type> JSONValue toJSON(Collection<Type> value, JsonEncoderDecoder<Type> encoder) {
        if( value == null ) {
            return null;
        }
        JSONArray rc = new JSONArray();
        int i=0;
        for (Type t : value) {
            rc.set(i++, encoder.encode(t));
        }
        return rc;
    }
    
}
