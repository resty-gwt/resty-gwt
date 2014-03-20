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

package org.fusesource.restygwt.client;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONBoolean;
import com.google.gwt.json.client.JSONException;
import com.google.gwt.json.client.JSONNull;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.XMLParser;

import org.fusesource.restygwt.client.Json.Style;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 *
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 * @author <a href="http://www.acuedo.com">Dave Finch</a>
 */
abstract public class AbstractJsonEncoderDecoder<T> implements JsonEncoderDecoder<T> {

    @Override
    public T decode(String value) throws DecodingException {
        try {
            return decode(JSONParser.parseStrict(value));
        } catch (JSONException e) {
            // that can happen for generic key types like Object and then a String key gets passed in
            return decode(JSONParser.parseStrict("\"" + value + "\""));
        }
    }

    // /////////////////////////////////////////////////////////////////
    // Built in encoders for the native types.
    // /////////////////////////////////////////////////////////////////
    public static final AbstractJsonEncoderDecoder<Boolean> BOOLEAN = new AbstractJsonEncoderDecoder<Boolean>() {

        public Boolean decode(JSONValue value) throws DecodingException {
            if (value == null || value.isNull() != null) {
                return null;
            }
            JSONBoolean bool = value.isBoolean();
            if (bool == null) {
                throw new DecodingException("Expected a json boolean, but was given: " + value);
            }
            return bool.booleanValue();
        }

        public JSONValue encode(Boolean value) throws EncodingException {
            return (value == null) ? getNullType() : JSONBoolean.getInstance(value);
        }
    };

    public static final AbstractJsonEncoderDecoder<Character> CHAR = new AbstractJsonEncoderDecoder<Character>() {

        public Character decode(JSONValue value) throws DecodingException {
            if (value == null || value.isNull() != null) {
                return null;
            }
            return (char) toDouble(value);
        }

        public JSONValue encode(Character value) throws EncodingException {
            return (value == null) ? getNullType() : new JSONNumber(value);
        }
    };

    public static final AbstractJsonEncoderDecoder<Byte> BYTE = new AbstractJsonEncoderDecoder<Byte>() {

        public Byte decode(JSONValue value) throws DecodingException {
            if (value == null || value.isNull() != null) {
                return null;
            }
            return (byte) toDouble(value);

        }

        public JSONValue encode(Byte value) throws EncodingException {
            if (value == null) {
                return null;
            }
            return new JSONNumber(value);
        }
    };

    public static final AbstractJsonEncoderDecoder<Short> SHORT = new AbstractJsonEncoderDecoder<Short>() {

        public Short decode(JSONValue value) throws DecodingException {
            if (value == null || value.isNull() != null) {
                return null;
            }
            return (short) toDouble(value);
        }

        public JSONValue encode(Short value) throws EncodingException {
            return (value == null) ? getNullType() : new JSONNumber(value);
        }
    };

    public static final AbstractJsonEncoderDecoder<Integer> INT = new AbstractJsonEncoderDecoder<Integer>() {

        public Integer decode(JSONValue value) throws DecodingException {
            if (value == null || value.isNull() != null) {
                return null;
            }
            return (int) toDouble(value);
        }

        public JSONValue encode(Integer value) throws EncodingException {
            return (value == null) ? getNullType() : new JSONNumber(value);
        }
    };

    public static final AbstractJsonEncoderDecoder<Long> LONG = new AbstractJsonEncoderDecoder<Long>() {

        public Long decode(JSONValue value) throws DecodingException {
            if (value == null || value.isNull() != null) {
                return null;
            }
            final JSONString valueString = value.isString();
            if (valueString != null) {
                 return Long.parseLong(valueString.stringValue());
            } else {
                return (long) toDouble(value);
            }
        }

        public JSONValue encode(Long value) throws EncodingException {
            return (value == null) ? getNullType() : new JSONNumber(value);
        }
    };

    public static final AbstractJsonEncoderDecoder<Float> FLOAT = new AbstractJsonEncoderDecoder<Float>() {

        public Float decode(JSONValue value) throws DecodingException {
            if (value == null || value.isNull() != null) {
                return null;
            }
            return (float) toDouble(value);
        }

        public JSONValue encode(Float value) throws EncodingException {
            return (value == null) ? getNullType() : new JSONNumber(value);
        }
    };

    public static final AbstractJsonEncoderDecoder<Double> DOUBLE = new AbstractJsonEncoderDecoder<Double>() {

        public Double decode(JSONValue value) throws DecodingException {
            if (value == null || value.isNull() != null) {
                return null;
            }
            return toDouble(value);
        }

        public JSONValue encode(Double value) throws EncodingException {
            return (value == null) ? getNullType() : new JSONNumber(value);
        }
    };

    public static final AbstractJsonEncoderDecoder<String> STRING = new AbstractJsonEncoderDecoder<String>() {

        public String decode(JSONValue value) throws DecodingException {
            if (value == null || value.isNull() != null) {
                return null;
            }
            JSONString str = value.isString();
            if (str == null) {
                if (value.isBoolean() != null || value.isNumber() != null) {
                    return value.toString();
                } else {
                    throw new DecodingException("Expected a json string, but was given: " + value);
                }
            }
            return str.stringValue();
        }

        @Override
        public String decode(String value) throws DecodingException {
            return value;
        }

        public JSONValue encode(String value) throws EncodingException {
            return (value == null) ? getNullType() : new JSONString(value);
        }
    };

    public static final AbstractJsonEncoderDecoder<BigDecimal> BIG_DECIMAL = new AbstractJsonEncoderDecoder<BigDecimal>() {

        public BigDecimal decode(JSONValue value) throws DecodingException {
            if (value == null || value.isNull() != null) {
                return null;
            }
            return toBigDecimal(value);
        }

        public JSONValue encode(BigDecimal value) throws EncodingException {
            return (value == null) ? getNullType() : new JSONString(value.toString());
        }
    };

    public static final AbstractJsonEncoderDecoder<BigInteger> BIG_INTEGER = new AbstractJsonEncoderDecoder<BigInteger>() {

        public BigInteger decode(JSONValue value) throws DecodingException {
            if (value == null || value.isNull() != null) {
                return null;
            }
            JSONNumber number = value.isNumber();
            if (number == null) {
                JSONString str = value.isString();
                if (str == null) {
                    throw new DecodingException("Expected a json number r string, but was given: " + value);
                }

                // Doing a straight conversion from string to BigInteger will
                // not work for large values
                // So we convert to BigDecimal first and then convert it to
                // BigInteger.
                return new BigDecimal(str.stringValue()).toBigInteger();
            }

            // Doing a straight conversion from string to BigInteger will not
            // work for large values
            // So we convert to BigDecimal first and then convert it to
            // BigInteger.
            return new BigDecimal(value.toString()).toBigInteger();
        }

        public JSONValue encode(BigInteger value) throws EncodingException {
            return (value == null) ? getNullType() : new JSONString(value.toString());
        }
    };

    public static final AbstractJsonEncoderDecoder<Document> DOCUMENT = new AbstractJsonEncoderDecoder<Document>() {

        public Document decode(JSONValue value) throws DecodingException {
            if (value == null || value.isNull() != null) {
                return null;
            }
            JSONString str = value.isString();
            if (str == null) {
                throw new DecodingException("Expected a json string, but was given: " + value);
            }
            return XMLParser.parse(str.stringValue());
        }

        public JSONValue encode(Document value) throws EncodingException {
            return (value == null) ? getNullType() : new JSONString(value.toString());
        }
    };

    public static final AbstractJsonEncoderDecoder<JSONValue> JSON_VALUE = new AbstractJsonEncoderDecoder<JSONValue>() {

        public JSONValue decode(JSONValue value) throws DecodingException {
            return value;
        }

        public JSONValue encode(JSONValue value) throws EncodingException {
            return value;
        }
    };

    public static final AbstractJsonEncoderDecoder<Date> DATE = new AbstractJsonEncoderDecoder<Date>() {

        public Date decode(JSONValue value) throws DecodingException {
            if (value == null || value.isNull() != null) {
                return null;
            }
            String format = Defaults.getDateFormat();
            if (format == null) {
                JSONNumber num = value.isNumber();
                if (num == null) {
                    throw new DecodingException("Expected a json number, but was given: " + value);
                }
                return new Date((long) num.doubleValue());
            } else {
                JSONString str = value.isString();
                if (str == null) {
                    throw new DecodingException("Expected a json string, but was given: " + value);
                }
                return DateTimeFormat.getFormat(format).parse(str.stringValue());
            }
        }

        public JSONValue encode(Date value) throws EncodingException {
            if (value == null) {
                return getNullType();
            }
            String format = Defaults.getDateFormat();
            if (format == null) {
                return new JSONNumber(value.getTime());
            } else {
                return new JSONString(DateTimeFormat.getFormat(format).format(value));
            }
        }
    };

    // /////////////////////////////////////////////////////////////////
    // Helper Methods.
    // /////////////////////////////////////////////////////////////////

    static public BigDecimal toBigDecimal(JSONValue value) {
        JSONNumber number = value.isNumber();
        if (number == null) {
            throw new DecodingException("Expected a json number, but was given: " + value);
        }
        return new BigDecimal(value.toString());
    }

    static public double toDouble(JSONValue value) {
        JSONNumber number = value.isNumber();
        if (number == null) {
            JSONString val = value.isString();
            if (val != null){
                try {
                    return Double.parseDouble(val.stringValue());
                }
                catch(NumberFormatException e){
                    // just through exception below
                }
            }
            throw new DecodingException("Expected a json number, but was given: " + value);
        }
        return number.doubleValue();
    }

    static public JSONObject toObject(JSONValue value) {
        JSONObject object = value.isObject();
        if (object == null) {
            throw new DecodingException("Expected a json object, but was given: " + object);
        }
        return object;
    }

    static public JSONObject toObjectFromWrapper(JSONValue value, String name) {
        JSONObject object = value.isObject();
        if (object == null) {
            throw new DecodingException("Expected a json object, but was given: " + object);
        }
        JSONValue result = object.get(name);
        if (result == null) {
            // no wrapper found but that is possible within the hierarchy
            return toObject(value);
        }
        return toObject(result);
    }
    
    static JSONArray asArray(JSONValue value) {
        JSONArray array = value.isArray();
        if (array == null) {
            throw new DecodingException("Expected a json array, but was given: " + value);
        }
        return array;
    }

    static public <Type> List<Type> toList(JSONValue value, AbstractJsonEncoderDecoder<Type> encoder) {
        if (value == null || value.isNull() != null) {
            return null;
        }
        JSONArray array = asArray(value);

        ArrayList<Type> rc = new ArrayList<Type>(array.size());
        int size = array.size();
        for (int i = 0; i < size; i++) {
            rc.add(encoder.decode(array.get(i)));
        }
        return rc;
    }

    static public <Type> Type[] toArray(JSONValue value, AbstractJsonEncoderDecoder<Type> encoder, Type[] template) {
        if (value == null || value.isNull() != null) {
            return null;
        }
        JSONArray array = asArray(value);

        int size = array.size();
        for (int i = 0; i < size; i++) {
            template[i] = encoder.decode(array.get(i));
        }
        return template;
    }

    static public short[] toArray(JSONValue value, AbstractJsonEncoderDecoder<Short> encoder, short[] template) {
        if (value == null || value.isNull() != null) {
            return null;
        }
        JSONArray array = asArray(value);

        int size = array.size();
        for (int i = 0; i < size; i++) {
            template[i] = encoder.decode(array.get(i));
        }
        return template;
    }

    static public long[] toArray(JSONValue value, AbstractJsonEncoderDecoder<Long> encoder, long[] template) {
        if (value == null || value.isNull() != null) {
            return null;
        }
        JSONArray array = asArray(value);

        int size = array.size();
        for (int i = 0; i < size; i++) {
            template[i] = encoder.decode(array.get(i));
        }
        return template;
    }

    static public int[] toArray(JSONValue value, AbstractJsonEncoderDecoder<Integer> encoder, int[] template) {
        if (value == null || value.isNull() != null) {
            return null;
        }
        JSONArray array = asArray(value);

        int size = array.size();
        for (int i = 0; i < size; i++) {
            template[i] = encoder.decode(array.get(i));
        }
        return template;
    }

    static public float[] toArray(JSONValue value, AbstractJsonEncoderDecoder<Float> encoder, float[] template) {
        if (value == null || value.isNull() != null) {
            return null;
        }
        JSONArray array = asArray(value);

        int size = array.size();
        for (int i = 0; i < size; i++) {
            template[i] = encoder.decode(array.get(i));
        }
        return template;
    }

    static public double[] toArray(JSONValue value, AbstractJsonEncoderDecoder<Double> encoder, double[] template) {
        if (value == null || value.isNull() != null) {
            return null;
        }
        JSONArray array = asArray(value);

        int size = array.size();
        for (int i = 0; i < size; i++) {
            template[i] = encoder.decode(array.get(i));
        }
        return template;
    }

    static public byte[] toArray(JSONValue value, AbstractJsonEncoderDecoder<Byte> encoder, byte[] template) {
        if (value == null || value.isNull() != null) {
            return null;
        }
        JSONArray array = asArray(value);

        int size = array.size();
        for (int i = 0; i < size; i++) {
            template[i] = encoder.decode(array.get(i));
        }
        return template;
    }

    static public char[] toArray(JSONValue value, AbstractJsonEncoderDecoder<Character> encoder, char[] template) {
        if (value == null || value.isNull() != null) {
            return null;
        }
        JSONArray array = asArray(value);

        int size = array.size();
        for (int i = 0; i < size; i++) {
            template[i] = encoder.decode(array.get(i));
        }
        return template;
    }

    static public boolean[] toArray(JSONValue value, AbstractJsonEncoderDecoder<Boolean> encoder, boolean[] template) {
        if (value == null || value.isNull() != null) {
            return null;
        }
        JSONArray array = asArray(value);

        int size = array.size();
        for (int i = 0; i < size; i++) {
            template[i] = encoder.decode(array.get(i));
        }
        return template;
    }

    static public int getSize(JSONValue value) {
        if (value == null || value.isNull() != null) {
            return 0;
        }
        JSONArray array = asArray(value);
        return array.size();
    }

    static public <Type> Set<Type> toSet(JSONValue value, AbstractJsonEncoderDecoder<Type> encoder) {
        if (value == null || value.isNull() != null) {
            return null;
        }
        JSONArray array = asArray(value);

        HashSet<Type> rc = new HashSet<Type>(array.size() * 2);
        int size = array.size();
        for (int i = 0; i < size; i++) {
            rc.add(encoder.decode(array.get(i)));
        }
        return rc;
    }

    static public <Type> Map<String, Type> toMap(JSONValue value, AbstractJsonEncoderDecoder<Type> encoder, Style style) {
        if (value == null || value.isNull() != null) {
            return null;
        }

        switch (style) {
        case DEFAULT:
        case SIMPLE: {
            JSONObject object = value.isObject();
            if (object == null) {
                throw new DecodingException("Expected a json object, but was given: " + value);
            }

            HashMap<String, Type> rc = new HashMap<String, Type>(object.size() * 2);
            for (String key : object.keySet()) {
                rc.put(key, encoder.decode(object.get(key)));
            }
            return rc;
        }
        case JETTISON_NATURAL: {
            JSONObject object = value.isObject();
            if (object == null) {
                throw new DecodingException("Expected a json object, but was given: " + value);
            }
            value = object.get("entry");
            if (value == null) {
                throw new DecodingException("Expected an entry array not found");
            }
            JSONArray entries = value.isArray();
            if (entries == null) {
                throw new DecodingException("Expected an entry array, but was given: " + value);
            }

            HashMap<String, Type> rc = new HashMap<String, Type>(object.size() * 2);
            for (int i = 0; i < entries.size(); i++) {
                JSONObject entry = entries.get(i).isObject();
                if (entry == null)
                    throw new DecodingException("Expected an entry object, but was given: " + value);
                JSONValue key = entry.get("key");
                if (key == null)
                    throw new DecodingException("Expected an entry key field not found");
                JSONString k = key.isString();
                if (k == null)
                    throw new DecodingException("Expected an entry key to be a string, but was given: " + value);

                rc.put(k.stringValue(), encoder.decode(entry.get("value")));
            }
            return rc;
        }
        default:
            throw new UnsupportedOperationException("The encoding style is not yet suppored: " + style.name());
        }
    }

    static public <KeyType, ValueType> Map<KeyType, ValueType> toMap(JSONValue value,
            AbstractJsonEncoderDecoder<KeyType> keyEncoder, AbstractJsonEncoderDecoder<ValueType> valueEncoder,
            Style style) {
        if (value == null || value.isNull() != null) {
            return null;
        }

        switch (style) {
        case DEFAULT:
        case SIMPLE: {
            JSONObject object = value.isObject();
            if (object == null) {
                throw new DecodingException("Expected a json object, but was given: " + value);
            }

            HashMap<KeyType, ValueType> rc = new HashMap<KeyType, ValueType>(object.size() * 2);
            for (String key : object.keySet()) {
                rc.put(keyEncoder.decode(key), valueEncoder.decode(object.get(key)));
            }
            return rc;
        }
        case JETTISON_NATURAL: {
            JSONObject object = value.isObject();
            if (object == null) {
                throw new DecodingException("Expected a json object, but was given: " + value);
            }
            value = object.get("entry");
            if (value == null) {
                throw new DecodingException("Expected an entry array not found");
            }
            JSONArray entries = value.isArray();
            if (entries == null) {
                throw new DecodingException("Expected an entry array, but was given: " + value);
            }

            HashMap<KeyType, ValueType> rc = new HashMap<KeyType, ValueType>(object.size() * 2);
            for (int i = 0; i < entries.size(); i++) {
                JSONObject entry = entries.get(i).isObject();
                if (entry == null)
                    throw new DecodingException("Expected an entry object, but was given: " + value);
                JSONValue key = entry.get("key");
                if (key == null)
                    throw new DecodingException("Expected an entry key field not found");
                JSONString k = key.isString();
                if (k == null)
                    throw new DecodingException("Expected an entry key to be a string, but was given: " + value);
                rc.put(keyEncoder.decode(k.stringValue()), valueEncoder.decode(entry.get("value")));
            }
            return rc;
        }
        default:
            throw new UnsupportedOperationException("The encoding style is not yet supported: " + style.name());
        }
    }

    // TODO(sbeutel): new map method to handle other key values than String
    static public <KeyType, ValueType> JSONValue toJSON(Map<KeyType, ValueType> value,
            AbstractJsonEncoderDecoder<KeyType> keyEncoder, AbstractJsonEncoderDecoder<ValueType> valueEncoder,
            Style style) {
        if (value == null) {
            return JSONNull.getInstance();
        }

        switch (style) {
        case DEFAULT:
        case SIMPLE: {
            JSONObject rc = new JSONObject();
            
            for (Entry<KeyType, ValueType> t : value.entrySet()) {
                //TODO find a way to check only once
                JSONValue k = keyEncoder.encode(t.getKey());
                if (k.isString() != null) {
                    rc.put(k.isString().stringValue(), valueEncoder.encode(t.getValue()));
                }
                else {
                    rc.put(k.toString(), valueEncoder.encode(t.getValue()));
                }
            }
            return rc;
        }
        case JETTISON_NATURAL: {
            JSONObject rc = new JSONObject();
            JSONArray entries = new JSONArray();
            int i = 0;
            for (Entry<KeyType, ValueType> t : value.entrySet()) {
                JSONObject entry = new JSONObject();
                //TODO find a way to check only once
                JSONValue k = keyEncoder.encode(t.getKey());
                if (k.isString() != null) {
                    entry.put("key", k);
                }
                else {
                    entry.put("key", new JSONString(k.toString()));
                }
                entry.put("value", valueEncoder.encode(t.getValue()));
                entries.set(i++, entry);
            }
            rc.put("entry", entries);
            return rc;
        }
        default:
            throw new UnsupportedOperationException("The encoding style is not yet supported: " + style.name());
        }
    }

    static public <Type> JSONValue toJSON(Map<String, Type> value, AbstractJsonEncoderDecoder<Type> encoder, Style style) {
        if (value == null) {
            return JSONNull.getInstance();
        }

        switch (style) {
        case DEFAULT:
        case SIMPLE: {
            JSONObject rc = new JSONObject();
            for (Entry<String, Type> t : value.entrySet()) {
                rc.put(t.getKey(), encoder.encode(t.getValue()));
            }
            return rc;
        }
        case JETTISON_NATURAL: {
            JSONObject rc = new JSONObject();
            JSONArray entries = new JSONArray();
            int i = 0;
            for (Entry<String, Type> t : value.entrySet()) {
                JSONObject entry = new JSONObject();
                entry.put("key", new JSONString(t.getKey()));
                entry.put("value", encoder.encode(t.getValue()));
                entries.set(i++, entry);
            }
            rc.put("entry", entries);
            return rc;
        }
        default:
            throw new UnsupportedOperationException("The encoding style is not yet suppored: " + style.name());
        }
    }

    static public <Type> JSONValue toJSON(Collection<Type> value, AbstractJsonEncoderDecoder<Type> encoder) {
        if (value == null) {
            return JSONNull.getInstance();
        }
        JSONArray rc = new JSONArray();
        int i = 0;
        for (Type t : value) {
            rc.set(i++, encoder.encode(t));
        }
        return rc;
    }

    static public <Type> JSONValue toJSON(Type[] value, AbstractJsonEncoderDecoder<Type> encoder) {
        if (value == null) {
            return JSONNull.getInstance();
        }
        JSONArray rc = new JSONArray();
        int i = 0;
        for (Type t : value) {
            rc.set(i++, encoder.encode(t));
        }
        return rc;
    }

    static public <Type> JSONValue toJSON(short[] value, AbstractJsonEncoderDecoder<Type> encoder) {
        if (value == null) {
            return JSONNull.getInstance();
        }
        JSONArray rc = new JSONArray();
        int i = 0;
        for (short t : value) {
            rc.set(i++, new JSONNumber(t));
        }
        return rc;
    }

    static public <Type> JSONValue toJSON(int[] value, AbstractJsonEncoderDecoder<Type> encoder) {
        if (value == null) {
            return JSONNull.getInstance();
        }
        JSONArray rc = new JSONArray();
        int i = 0;
        for (int t : value) {
            rc.set(i++, new JSONNumber(t));
        }
        return rc;
    }

    static public <Type> JSONValue toJSON(long[] value, AbstractJsonEncoderDecoder<Type> encoder) {
        if (value == null) {
            return JSONNull.getInstance();
        }
        JSONArray rc = new JSONArray();
        int i = 0;
        for (long t : value) {
            rc.set(i++, new JSONNumber(t));
        }
        return rc;
    }

    static public <Type> JSONValue toJSON(float[] value, AbstractJsonEncoderDecoder<Type> encoder) {
        if (value == null) {
            return JSONNull.getInstance();
        }
        JSONArray rc = new JSONArray();
        int i = 0;
        for (float t : value) {
            rc.set(i++, new JSONNumber(t));
        }
        return rc;
    }
    
    static public <Type> JSONValue toJSON(double[] value, AbstractJsonEncoderDecoder<Type> encoder) {
        if (value == null) {
            return JSONNull.getInstance();
        }
        JSONArray rc = new JSONArray();
        int i = 0;
        for (double t : value) {
            rc.set(i++, new JSONNumber(t));
        }
        return rc;
    }

    static public <Type> JSONValue toJSON(boolean[] value, AbstractJsonEncoderDecoder<Type> encoder) {
        if (value == null) {
            return JSONNull.getInstance();
        }
        JSONArray rc = new JSONArray();
        int i = 0;
        for (boolean t : value) {
            rc.set(i++, JSONBoolean.getInstance(t));
        }
        return rc;
    }

    static public <Type> JSONValue toJSON(char[] value, AbstractJsonEncoderDecoder<Type> encoder) {
        if (value == null) {
            return JSONNull.getInstance();
        }
        JSONArray rc = new JSONArray();
        int i = 0;
        for (char t : value) {
            rc.set(i++, new JSONNumber(t));
        }
        return rc;
    }

    static public <Type> JSONValue toJSON(byte[] value, AbstractJsonEncoderDecoder<Type> encoder) {
        if (value == null) {
            return JSONNull.getInstance();
        }
        JSONArray rc = new JSONArray();
        int i = 0;
        for (byte t : value) {
            rc.set(i++, new JSONNumber(t));
        }
        return rc;
    }

    static private JSONNull getNullType() {
        return (Defaults.doesIgnoreJsonNulls()) ? null : JSONNull.getInstance();
    }
}
