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

package org.fusesource.restygwt.rebind;

import com.fasterxml.jackson.annotation.JsonValue;
import com.github.nmorel.gwtjackson.client.ObjectMapper;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;

/**
 *
 * @author lucasam
 *
 *         Based on @see
 *         corg.fusesource.restygwt.rebind.JsonEncoderDecoderClassCreator
 *
 */

public class GwtJacksonEncoderDecoderClassCreator extends BaseSourceCreator {
    private static final String GWT_JACKSON_ENCODER_SUFFIX = "_Gen_GwtJackEncDec_";
    private static final String GWT_JACKSON_MAPPER_IF = "GwtJackMapper";

    private static final String OBJECT_MAPPER_CLASS = ObjectMapper.class.getName();

    protected static final String JSON_VALUE_CLASS = JSONValue.class.getName();
    protected static final String JSON_STRING_CLASS = JSONString.class.getName();


    protected boolean javaBeansNamingConventionEnabled;

    public GwtJacksonEncoderDecoderClassCreator(TreeLogger logger, GeneratorContext context, JClassType source) {
        super(logger, context, source, GWT_JACKSON_ENCODER_SUFFIX);
    }

    @Override
    public void generate() throws UnableToCompleteException {
        //Forcing class to be loaded
        try {
            Class.forName(ObjectMapper.class.getName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        final JClassType sourceClazz = source.isClass() == null ? source.isInterface() : source.isClass();
        if (sourceClazz == null) {
            getLogger().log(ERROR, "Type is not a class");
            throw new UnableToCompleteException();
        }
        generateMapper();
        generateSingleton(shortName);
        generateEncodeMethod(source);
        generateDecodeMethod(source);
    }

    private void generateMapper() {
        p();
        p("public static interface " + GWT_JACKSON_MAPPER_IF + " extends " + OBJECT_MAPPER_CLASS + "<" +
            source.getParameterizedQualifiedSourceName() + "> {};");
        p();
    }

    protected void generateSingleton(String shortName) {
        p();
        p("public static final " + shortName + " INSTANCE = new " + shortName + "();");
        p();
    }

    @Override
    protected ClassSourceFileComposerFactory createComposerFactory() {
        ClassSourceFileComposerFactory composerFactory = new ClassSourceFileComposerFactory(packageName, shortName);
        composerFactory.setSuperclass(JsonEncoderDecoderInstanceLocator.JSON_ENCODER_DECODER_CLASS + "<" +
            source.getParameterizedQualifiedSourceName() + ">");
        return composerFactory;
    }

    private void generateEncodeMethod(JClassType classType) {

        p("public " + JSON_VALUE_CLASS + " encode(" + source.getParameterizedQualifiedSourceName() + " value) {").i(1);

        {
            p("if( value==null ) {").i(1);
            {
                p("return getNullType();");
            }
            i(-1).p("}");

            p(GWT_JACKSON_MAPPER_IF + " mapper__ = " + GWT.class.getName() + ".create(" + GWT_JACKSON_MAPPER_IF +
                ".class);");
            p(" String returnStr = mapper__.write(value);");
            p("return " + JSONParser.class.getName() + ".parseLenient(returnStr);");
        }
        i(-1).p("}");
        p();
    }

    protected String getValueMethod(JClassType classType) {
        String method = "name";
        for (JMethod jm : classType.isEnum().getMethods()) {
            if (jm.isAnnotationPresent(JsonValue.class)) {
                method = jm.getName();
                break;
            }
        }
        return method;
    }

    private void generateDecodeMethod(JClassType classType) {
        p("public " + source.getParameterizedQualifiedSourceName() + " decode(" + JSON_VALUE_CLASS + " value) {").i(1);
        {
            p("if( value == null || value.isNull()!=null ) {").i(1);
            {
                p("return null;").i(-1);
            }
            p("}");


            p(GWT_JACKSON_MAPPER_IF + " mapper__ = " + GWT.class.getName() + ".create(" + GWT_JACKSON_MAPPER_IF +
                ".class);");
            p(" return (" + source.getParameterizedQualifiedSourceName() + ") mapper__.read(value.toString());");

            i(-1).p("}");
            p();
        }
    }

}
