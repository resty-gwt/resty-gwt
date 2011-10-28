package org.fusesource.restygwt.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONBoolean;
import com.google.gwt.json.client.JSONNull;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;

public class ObjectEncoderDecoder implements JsonEncoderDecoder<Object> {

    public static final ObjectEncoderDecoder INSTANCE = new ObjectEncoderDecoder();

    @Override
    public JSONValue encode(Object value) throws org.fusesource.restygwt.client.JsonEncoderDecoder.EncodingException {
	if (value instanceof Number)
	    return new JSONNumber(((Number) value).doubleValue());
	else if (value instanceof Boolean)
	    return JSONBoolean.getInstance(((Boolean) value).booleanValue());
	else if (value instanceof Iterable) {
	    JSONArray array = new JSONArray();
	    int ct = 0;
	    for (Object v : (Iterable<?>) value)
		array.set(ct++, encode(v));
	    return array;
	} else if (value instanceof Map) {
	    JSONObject object = new JSONObject();
	    for (Map.Entry<?, ?> entry : ((Map<?, ?>) value).entrySet())
		object.put(entry.getKey().toString(), encode(entry.getValue()));
	    return object;
	} else if(value == null)
	    return JSONNull.getInstance();
	else
	    return new JSONString(value.toString());
    }

    @Override
    public Object decode(JSONValue value) throws org.fusesource.restygwt.client.JsonEncoderDecoder.DecodingException {
	if (value instanceof JSONNumber)
	    return ((JSONNumber) value).doubleValue();
	else if (value instanceof JSONBoolean)
	    return ((JSONBoolean) value).booleanValue();
	else if (value instanceof JSONString)
	    return ((JSONString) value).stringValue();
	else if (value instanceof JSONArray) {
	    JSONArray array = value.isArray();
	    List<Object> list = new ArrayList<Object>(array.size());
	    for (int ct = 0; ct < array.size(); ct++)
		list.add(decode(array.get(ct)));
	    return list;
	} else if (value instanceof JSONObject) {
	    JSONObject object = value.isObject();
	    Map<String, Object> map = new HashMap<String, Object>();
	    for (String key : object.keySet())
		map.put(key, decode(object.get(key)));
	    return map;
	} else
	    return null;
    }

}
