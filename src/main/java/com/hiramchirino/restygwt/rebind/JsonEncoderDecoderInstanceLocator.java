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
package com.hiramchirino.restygwt.rebind;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JPrimitiveType;
import com.google.gwt.core.ext.typeinfo.JType;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.xml.client.Document;
import com.hiramchirino.restygwt.client.AbstractJsonEncoderDecoder;

/**
 * 
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 */
public class JsonEncoderDecoderInstanceLocator {
	
	public static final String JSON_ENCODER_DECODER_CLASS = AbstractJsonEncoderDecoder.class.getName();

	public final JClassType STRING_TYPE;
	public final JClassType JSON_VALUE_TYPE;
	public final JClassType DOCUMENT_TYPE;
	public final JClassType COLLECTION_CLASS;

    public final HashMap<JType, String> builtInEncoderDecoders = new HashMap<JType, String>();

    public final GeneratorContext context;
    public final TreeLogger logger;


    public JsonEncoderDecoderInstanceLocator(GeneratorContext context, TreeLogger logger) throws UnableToCompleteException {       	
        this.context = context;
        this.logger = logger;

        this.STRING_TYPE = find(String.class);
        this.JSON_VALUE_TYPE = find(JSONValue.class);
        this.DOCUMENT_TYPE = find(Document.class);
        this.COLLECTION_CLASS = find(Collection.class); 

        builtInEncoderDecoders.put(JPrimitiveType.BOOLEAN, JSON_ENCODER_DECODER_CLASS + ".BOOLEAN");
        builtInEncoderDecoders.put(JPrimitiveType.BYTE, JSON_ENCODER_DECODER_CLASS + ".BYTE");
        builtInEncoderDecoders.put(JPrimitiveType.CHAR, JSON_ENCODER_DECODER_CLASS + ".CHAR");
        builtInEncoderDecoders.put(JPrimitiveType.SHORT, JSON_ENCODER_DECODER_CLASS + ".SHORT");
        builtInEncoderDecoders.put(JPrimitiveType.INT, JSON_ENCODER_DECODER_CLASS + ".INT");
        builtInEncoderDecoders.put(JPrimitiveType.LONG, JSON_ENCODER_DECODER_CLASS + ".LONG");
        builtInEncoderDecoders.put(JPrimitiveType.FLOAT, JSON_ENCODER_DECODER_CLASS + ".FLOAT");
        builtInEncoderDecoders.put(JPrimitiveType.DOUBLE, JSON_ENCODER_DECODER_CLASS + ".DOUBLE");
        builtInEncoderDecoders.put(find(Boolean.class), JSON_ENCODER_DECODER_CLASS + ".BOOLEAN");
        builtInEncoderDecoders.put(find(Byte.class), JSON_ENCODER_DECODER_CLASS + ".BYTE");
        builtInEncoderDecoders.put(find(Character.class), JSON_ENCODER_DECODER_CLASS + ".CHAR");
        builtInEncoderDecoders.put(find(Short.class), JSON_ENCODER_DECODER_CLASS + ".SHORT");
        builtInEncoderDecoders.put(find(Integer.class), JSON_ENCODER_DECODER_CLASS + ".INT");
        builtInEncoderDecoders.put(find(Long.class), JSON_ENCODER_DECODER_CLASS + ".LONG");
        builtInEncoderDecoders.put(find(Float.class), JSON_ENCODER_DECODER_CLASS + ".FLOAT");
        builtInEncoderDecoders.put(find(Double.class), JSON_ENCODER_DECODER_CLASS + ".DOUBLE");
        
        builtInEncoderDecoders.put(STRING_TYPE, JSON_ENCODER_DECODER_CLASS + ".STRING");
        builtInEncoderDecoders.put(DOCUMENT_TYPE, JSON_ENCODER_DECODER_CLASS + ".DOCUMENT");
        builtInEncoderDecoders.put(JSON_VALUE_TYPE, JSON_ENCODER_DECODER_CLASS + ".JSON_VALUE");

        builtInEncoderDecoders.put(find(Date.class), JSON_ENCODER_DECODER_CLASS + ".DATE");
		
	}
    
    private JClassType find(Class<?> type) throws UnableToCompleteException {
        return find(type.getName());
    }
    private JClassType find(String type) throws UnableToCompleteException {
        return RestServiceGenerator.find(logger, context, type);
    }
    
    public String getEncoderDecoder(JType type, TreeLogger logger) throws UnableToCompleteException {
        String rc = builtInEncoderDecoders.get(type);
        if (rc == null) {
            JClassType ct = type.isClass();
            if (ct != null && !ct.isAssignableTo(COLLECTION_CLASS)) {
                JsonEncoderDecoderClassCreator generator = new JsonEncoderDecoderClassCreator(logger, context, ct);
                return generator.create()+".INSTANCE";
            }
        }
        return rc;
    }

}