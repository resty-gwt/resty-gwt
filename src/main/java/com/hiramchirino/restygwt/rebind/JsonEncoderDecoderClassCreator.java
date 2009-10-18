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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JField;
import com.google.gwt.core.ext.typeinfo.JParameterizedType;
import com.google.gwt.core.ext.typeinfo.JPrimitiveType;
import com.google.gwt.core.ext.typeinfo.JType;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.xml.client.Document;
import com.hiramchirino.restygwt.client.AbstractJsonEncoderDecoder;

/**
 * 
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 */
public class JsonEncoderDecoderClassCreator extends BaseSourceCreator {
    private static final String JSON_ENCODER_SUFFIX = "_Generated_JsonEncoderDecoder_";

    private static final String JSON_ENCODER_DECODER_CLASS = AbstractJsonEncoderDecoder.class.getName();
    private static final String JSON_VALUE_CLASS = JSONValue.class.getName();
    private static final String JSON_OBJECT_CLASS = JSONObject.class.getName();

    private JClassType STRING_TYPE = null;
    private JClassType JSON_VALUE_TYPE = null;
    private JClassType DOCUMENT_TYPE = null;
    private JClassType MAP_TYPE = null;
    private JClassType SET_TYPE = null;
    private JClassType COLLECTION_CLASS;
    private JClassType LIST_TYPE;

    private HashMap<JType, String> builtInEncoderDecoders;


    public JsonEncoderDecoderClassCreator(TreeLogger logger, GeneratorContext context, JClassType source) throws UnableToCompleteException {
        super(logger, context, source, JSON_ENCODER_SUFFIX);
    }

    protected ClassSourceFileComposerFactory createComposerFactory() {
        ClassSourceFileComposerFactory composerFactory = new ClassSourceFileComposerFactory(packageName, shortName);
        composerFactory.setSuperclass(JSON_ENCODER_DECODER_CLASS + "<" + source.getName() + ">");
        return composerFactory;
    }

    public void generate() throws UnableToCompleteException {
        
        this.STRING_TYPE = find(String.class);
        this.JSON_VALUE_TYPE = find(JSONValue.class);
        this.DOCUMENT_TYPE = find(Document.class);
        this.LIST_TYPE = find(List.class);
        this.MAP_TYPE = find(Map.class);
        this.SET_TYPE = find(Set.class);
        this.COLLECTION_CLASS = find(Collection.class); 
        
        builtInEncoderDecoders = new HashMap<JType, String>();
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

        JClassType soruceClazz = source.isClass();
        if (soruceClazz == null) {
            error("Type is not a class");
        }
        if (!soruceClazz.isDefaultInstantiable()) {
            error("No default constuctor");
        }

        p();
        p("public static final " + shortName + " INSTANCE = new " + shortName + "();");
        p();
        p("public " + JSON_VALUE_CLASS + " encode(" + source.getName() + " value) {").i(1);
        {
            p(JSON_OBJECT_CLASS + " rc = new " + JSON_OBJECT_CLASS + "();");

            for (final JField field : source.getFields()) {

                // If can ignore some fields right off the back..
                if (field.isStatic() || field.isFinal() || field.isTransient()) {
                    continue;
                }
                
                branch("Processing field: " + field.getName(), new Branch<Void>(){
                    public Void execute() throws UnableToCompleteException {
                        // TODO: try to get the field with a setter or JSNI
                        if (field.isDefaultAccess() || field.isProtected() || field.isPublic()) {

                            String name = field.getName();
                            String expression = encodeExpression(field.getType(), "value." + name);

                            p("{").i(1);
                            {
                                p(JSON_VALUE_CLASS + " v=" + expression + ";");
                                p("if( v!=null ) {").i(1);
                                {
                                    p("rc.put(" + wrap(name) + ", v);");
                                }
                                i(-1).p("}");
                            }
                            i(-1).p("}");
                            
                        } else {
                            error("field must not be private: " + field.getEnclosingType().getQualifiedSourceName() + "." + field.getName());
                        }
                        return null;
                    }
                });

            }

            p("return rc;");
        }
        i(-1).p("}");
        p();
        p("public " + source.getName() + " decode(" + JSON_VALUE_CLASS + " value) {").i(1);
        {
            p(JSON_OBJECT_CLASS + " object = toObject(value);");
            p("" + source.getName() + " rc = new " + source.getName() + "();");
            for (final JField field : source.getFields()) {

                // If can ignore some fields right off the back..
                if (field.isStatic() || field.isFinal() || field.isTransient()) {
                    continue;
                }
                
                branch("Processing field: " + field.getName(), new Branch<Void>(){
                    public Void execute() throws UnableToCompleteException {

                        // TODO: try to set the field with a setter or JSNI
                        if (field.isDefaultAccess() || field.isProtected() || field.isPublic()) {

                            String name = field.getName();
                            String expression = decodeExpression(field.getType(), "object.get(" + wrap(name) + ")");

                            p("rc." + name + "=" + expression + ";");
                        } else {
                            error("field must not be private.");
                        }
                        return null;
                    }
                });
            }

            p("return rc;");
        }
        i(-1).p("}");
        p();
    }

    private String encodeExpression(JType type, String expression) throws UnableToCompleteException {
        return encodeDecodeExpression(type, expression, "encode", "toJSON", "toJSON", "toJSON");
    }

    private String decodeExpression(JType type, String expression) throws UnableToCompleteException {
        return encodeDecodeExpression(type, expression, "decode", "toMap", "toSet", "toList");
    }
    
    private String encodeDecodeExpression(JType type, String expression, String encoderMethod, String mapMethod, String setMethod, String listMethod) throws UnableToCompleteException {
        String encoderDecoder = getEncoderDecoder(type);
        if (encoderDecoder != null) {
            return encoderDecoder + "." + encoderMethod + "(" + expression + ")";
        }

        JClassType clazz = type.isClassOrInterface();
        if (clazz != null && clazz.isAssignableTo(COLLECTION_CLASS)) {
            JParameterizedType parameterizedType = type.isParameterized();
            if (parameterizedType == null || parameterizedType.getTypeArgs() == null) {
                error("Collection types must be parameterized.");
            }
            JClassType[] types = parameterizedType.getTypeArgs();
            
            if (parameterizedType.isAssignableTo(MAP_TYPE)) {
                if (types.length != 2) {
                    error("Map must define two and only two type parameters");
                }
                if( types[0]!= STRING_TYPE ) {
                    error("Map's frst type parameter must be of type String");
                }
                encoderDecoder = getEncoderDecoder(types[1]);
                if (encoderDecoder != null) {
                    return mapMethod + "(" + expression + ", " + encoderDecoder + ")";
                }
            } else if (parameterizedType.isAssignableTo(SET_TYPE)) {
                if (types.length != 1) {
                    error("Set must define one and only one type parameter");
                }
                encoderDecoder = getEncoderDecoder(types[0]);
                if (encoderDecoder != null) {
                    return setMethod + "(" + expression + ", " + encoderDecoder + ")";
                }
            } else if ( parameterizedType.isAssignableFrom(LIST_TYPE) ) {
                if (types.length != 1) {
                    error("List must define one and only one type parameter");
                }
                encoderDecoder = getEncoderDecoder(types[0]);
                debug("type encoder for: "+types[0]+" is "+encoderDecoder);
                if (encoderDecoder != null) {
                    return listMethod + "(" + expression + ", " + encoderDecoder + ")";
                }
            }
        }

        error("Do not know how to encode/decode " + type + " to JSON");
        return null;
    }

    private String getEncoderDecoder(JType type) throws UnableToCompleteException {
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