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

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonSubTypes;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.annotate.JsonTypeInfo.As;
import org.codehaus.jackson.annotate.JsonTypeInfo.Id;
import org.codehaus.jackson.annotate.JsonTypeName;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonTypeIdResolver;
import org.codehaus.jackson.map.jsontype.TypeIdResolver;
import org.fusesource.restygwt.client.Json;
import org.fusesource.restygwt.client.Json.Style;

import com.google.gwt.core.ext.BadPropertyValueException;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JConstructor;
import com.google.gwt.core.ext.typeinfo.JField;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.JParameter;
import com.google.gwt.core.ext.typeinfo.JPrimitiveType;
import com.google.gwt.core.ext.typeinfo.JType;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.thirdparty.guava.common.collect.Lists;
import com.google.gwt.thirdparty.guava.common.collect.Maps;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;

/**
 * 
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 * 
 *         Updates: added getter & setter support, enhanced generics support
 * @author <a href="http://www.acuedo.com">Dave Finch</a>
 * 
 *         added polymorphic support
 * @author <a href="http://charliemason.info">Charlie Mason</a>
 * 
 *         Updates : add support for JsonTypeInfo default property value class for id.class
 *                   add support for JsonDeserialize for interface
 *                      -> will look for all possible concrete sub-classes
 * @author <a href="http://wwww.ronanquillevere.fr">Ronan Quillevere</a>
 * 
 */

public class JsonEncoderDecoderClassCreator extends BaseSourceCreator
{
    private static final String JSON_ENCODER_SUFFIX = "_Generated_JsonEncoderDecoder_";

    private String JSON_ENCODER_DECODER_CLASS = JsonEncoderDecoderInstanceLocator.JSON_ENCODER_DECODER_CLASS;
    private static final String JSON_VALUE_CLASS = JSONValue.class.getName();
    private static final String JSON_OBJECT_CLASS = JSONObject.class.getName();
    private static final String JSON_ARRAY_CLASS = JSONArray.class.getName();
 
    public JsonEncoderDecoderClassCreator(TreeLogger logger, GeneratorContext context, JClassType source)
            throws UnableToCompleteException
    {
        super(logger, context, source, JSON_ENCODER_SUFFIX);
    }

    @Override
    protected ClassSourceFileComposerFactory createComposerFactory()
    {
        ClassSourceFileComposerFactory composerFactory = new ClassSourceFileComposerFactory(packageName, shortName);
        composerFactory.setSuperclass(JSON_ENCODER_DECODER_CLASS + "<" + source.getParameterizedQualifiedSourceName() +
                ">");
        return composerFactory;
    }

    private static class Subtype
    {
        final String tag;
        final JClassType clazz;

        public Subtype(String tag, JClassType clazz)
        {
            this.tag = tag;
            this.clazz = clazz;
        }
    }

    private static <T extends Annotation> T findAnnotation(JClassType clazz, Class<T> annotation)
    {
        if (clazz == null)
            return null;
        else if (clazz.isAnnotationPresent(annotation))
            return clazz.getAnnotation(annotation);
        else
            return findAnnotation(clazz.getSuperclass(), annotation);
    }

    @Override
    public void generate() throws UnableToCompleteException
    {
        final JsonTypeInfo typeInfo = findAnnotation(source, JsonTypeInfo.class);
        final List<Subtype> possibleTypes = findPossibleTypes(context, source);

        if(source.isInterface() != null ){            
            if (!source.isAnnotationPresent(JsonDeserialize.class))
                    error("Interface must be annotated with @JsonDeserialize.");
        } else {
            if (source.isClass() == null) 
                error("Type is not a class or an interface");
            
            if (source.isAbstract())
            {
                if (typeInfo == null)
                    error("Abstract classes must be annotated with JsonTypeInfo");
            }
        }
        
        Json jsonAnnotation = source.getAnnotation(Json.class);
        final Style classStyle = jsonAnnotation != null ? jsonAnnotation.style() : Style.DEFAULT;
        final String railsWrapperName = jsonAnnotation != null && jsonAnnotation.name().length() > 0 ? jsonAnnotation
                .name() : source.getName().toLowerCase();
        final boolean isLeaf = isLeaf(source);
        final JsonEncoderDecoderInstanceLocator locator = new JsonEncoderDecoderInstanceLocator(context, logger);
        
        writeSingleton();
        writeEncode(source, typeInfo, possibleTypes, classStyle, railsWrapperName, isLeaf, locator);
        writeDecode(source, typeInfo, possibleTypes, classStyle, railsWrapperName, isLeaf, locator);
    }

    private void writeDecode(final JClassType source, JsonTypeInfo typeInfo, final List<Subtype> possibleTypes,
            final Style classStyle, final String railsWrapperName, boolean isLeaf, final JsonEncoderDecoderInstanceLocator locator) throws UnableToCompleteException
    {
        //TODO @rqu sourceClazz instead of source
        if (null != source.isEnum())
        {
            p();
            p("public " + source.getName() + " decode(" + JSON_VALUE_CLASS + " value) {").i(1);
            {
                p("if( value == null || value.isNull()!=null ) {").i(1);
                {
                    p("return null;").i(-1);
                }
                p("}");
                p("com.google.gwt.json.client.JSONString str = value.isString();");
                p("if( null == str ) {").i(1);
                {
                    p("throw new DecodingException(\"Expected a json string (for enum), but was given: \"+value);").i(
                            -1);
                }
                p("}");
                p("return Enum.valueOf(" + source.getParameterizedQualifiedSourceName() + ".class, str.stringValue());")
                        .i(-1);
            }
            p("}");
            p();
            return;
        }
        
        p("public " + source.getName() + " decode(" + JSON_VALUE_CLASS + " value) {").i(1);
        {
            p("if( value == null || value.isNull()!=null ) {").i(1);
            {
                p("return null;").i(-1);
            }
            p("}");
            if (classStyle == Style.RAILS)
            {
                p(JSON_OBJECT_CLASS + " object = toObjectFromWrapper(value, \"" + railsWrapperName + "\");");
            }
            else if (typeInfo != null && typeInfo.include() == As.WRAPPER_ARRAY)
            {
                p(JSON_ARRAY_CLASS + " array = (" + JSON_ARRAY_CLASS + ")value;");
                if (!isLeaf)
                    p("String sourceName = org.fusesource.restygwt.client.AbstractJsonEncoderDecoder.STRING.decode(array.get(0));");
                p(JSON_OBJECT_CLASS + " object = toObject(array.get(1));");
            }
            else
            {
                p(JSON_OBJECT_CLASS + " object = toObject(value);");
            }

            typeInfo = writeSourceName(source, possibleTypes, typeInfo, isLeaf);

            for (Subtype possibleType : possibleTypes)
            {
                // Try to find a constuctor that is annotated as creator
                final JConstructor creator = findCreator(possibleType.clazz);
                if (typeInfo != null)
                {
                    if (typeInfo.include() == As.WRAPPER_OBJECT)
                    {
                        if (!isLeaf)
                        {
                            p("if(object.containsKey(\"" + possibleType.tag + "\"))");
                            p("{");
                        }
                        p("object = toObjectFromWrapper(value, \"" + possibleType.tag + "\");");
                    }
                    else if (!isLeaf)
                    {
                        p("if(sourceName.equals(\"" + possibleType.tag + "\"))");
                        p("{");
                    }
                }

                List<JField> orderedFields = null;
                if (creator != null)
                {
                    p("// We found a creator so we use the annotated constructor");
                    p("" + possibleType.clazz.getParameterizedQualifiedSourceName() + " rc = new " +
                            possibleType.clazz.getParameterizedQualifiedSourceName() + "(");
                    i(1).p("// The arguments are placed in the order they appear within the annotated constructor").i(
                            -1);
                    orderedFields = getOrderedFields(getFields(possibleType.clazz), creator);
                    final JField lastField = orderedFields.get(orderedFields.size() - 1);
                    for (final JField field : orderedFields)
                    {
                        branch("Processing field: " + field.getName(), new Branch<Void>()
                        {
                            public Void execute() throws UnableToCompleteException
                            {
                                Json jsonAnnotation = field.getAnnotation(Json.class);
                                Style style = jsonAnnotation != null ? jsonAnnotation.style() : classStyle;
                                String jsonName = field.getName();
                                if (jsonAnnotation != null && jsonAnnotation.name().length() > 0)
                                {
                                    jsonName = jsonAnnotation.name();
                                }
                                String objectGetter = "object.get(" + wrap(jsonName) + ")";
                                String expression = locator.decodeExpression(field.getType(), objectGetter, style);

                                String defaultValue = field.getType().isPrimitive() == null ? "null" : field.getType()
                                        .isPrimitive().getUninitializedFieldExpression() +
                                        "";
                                i(1).p("" +
                                        (objectGetter + " == null || " + objectGetter +
                                                " instanceof com.google.gwt.json.client.JSONNull ? " + defaultValue +
                                                " : " + expression + ((field != lastField) ? ", " : ""))).i(-1);

                                return null;
                            }
                        });
                    }
                    p(");");
                }

                if (orderedFields == null)
                {
                    p("" + possibleType.clazz.getParameterizedQualifiedSourceName() + " rc = new " +
                            possibleType.clazz.getParameterizedQualifiedSourceName() + "();");
                }

                for (final JField field : getFields(possibleType.clazz))
                {

                    boolean ignoreField = false;
                    if (possibleType.clazz.getAnnotation(JsonIgnoreProperties.class) != null)
                    {
                        for (String s : possibleType.clazz.getAnnotation(JsonIgnoreProperties.class).value())
                        {
                            if (s.equals(field.getName()))
                            {
                                ignoreField = true;
                                break;
                            }
                        }
                    }
                    if (ignoreField)
                    {
                        continue;
                    }

                    if (orderedFields != null && orderedFields.contains(field))
                    {
                        continue;
                    }

                    final String setterName = getSetterName(field);

                    // If can ignore some fields right off the back..
                    if (setterName == null && (field.isStatic() || field.isFinal() || field.isTransient()) ||
                            field.isAnnotationPresent(JsonIgnore.class))
                    {
                        continue;
                    }

                    branch("Processing field: " + field.getName(), new Branch<Void>()
                    {
                        public Void execute() throws UnableToCompleteException
                        {

                            // TODO: try to set the field with a setter
                            // or JSNI
                            if (setterName != null || field.isDefaultAccess() || field.isProtected() ||
                                    field.isPublic())
                            {

                                Json jsonAnnotation = field.getAnnotation(Json.class);
                                Style style = jsonAnnotation != null ? jsonAnnotation.style() : classStyle;
                                JsonProperty jsonPropertyAnnotation = field.getAnnotation(JsonProperty.class);

                                String name = field.getName();
                                String jsonName = name;

                                if (jsonAnnotation != null && jsonAnnotation.name().length() > 0)
                                {
                                    jsonName = jsonAnnotation.name();
                                }
                                if (jsonPropertyAnnotation != null && jsonPropertyAnnotation.value() != null &&
                                        jsonPropertyAnnotation.value().length() > 0)
                                {
                                    jsonName = jsonPropertyAnnotation.value();
                                }

                                String objectGetter = "object.get(" + wrap(jsonName) + ")";
                                String expression = locator.decodeExpression(field.getType(), objectGetter, style);

                                String cast = field.getType().isPrimitive() == JPrimitiveType.SHORT ? "(short) " : "";
                                p("if(" + objectGetter + " != null) {").i(1);

                                p("if(" + objectGetter + " instanceof com.google.gwt.json.client.JSONNull) {").i(1);
                                String defaultValue = field.getType().isPrimitive() == null ? "null" : field.getType()
                                        .isPrimitive().getUninitializedFieldExpression() +
                                        "";

                                if (setterName != null)
                                {
                                    p("rc." + setterName + "(" + cast + defaultValue + ");");
                                }
                                else
                                {
                                    p("rc." + name + "=" + cast + defaultValue + ";");
                                }

                                i(-1).p("} else {").i(1);

                                if (setterName != null)
                                {
                                    p("rc." + setterName + "(" + cast + expression + ");");
                                }
                                else
                                {
                                    p("rc." + name + "=" + cast + expression + ";");
                                }
                                i(-1).p("}");
                                i(-1).p("}");

                            }
                            else
                            {
                                logger.log(ERROR, "private field gets ignored: " +
                                        field.getEnclosingType().getQualifiedSourceName() + "." + field.getName());
                            }
                            return null;
                        }
                    });
                }

                p("return rc;");

                if (typeInfo != null && !isLeaf)
                {
                    p("}");
                }
            }

            if (typeInfo != null && !isLeaf)
            {
                p("return null;");
            }
            i(-1).p("}");
            p();
        }
    }

    private void writeEncode(final JClassType source, final JsonTypeInfo typeInfo, final List<Subtype> possibleTypes,
            final Style classStyle, final String railsWrapperName, boolean isLeaf, final JsonEncoderDecoderInstanceLocator locator) throws UnableToCompleteException
    {
        if (null != source.isEnum())
        {
            p();
            p("public " + JSON_VALUE_CLASS + " encode(" + source.getParameterizedQualifiedSourceName() + " value) {")
                    .i(1);
            {
                p("if( value==null ) {").i(1);
                {
                    p("return com.google.gwt.json.client.JSONNull.getInstance();").i(-1);
                }
                p("}");
                p("return new com.google.gwt.json.client.JSONString(value.name());");
            }
            i(-1).p("}");
            p();  
            return;
        }
        
        p("public " + JSON_VALUE_CLASS + " encode(" + source.getParameterizedQualifiedSourceName() + " value) {").i(1);
        {
            p("if( value==null ) {").i(1);
            {
                p("return null;");
            }
            i(-1).p("}");

            boolean returnWrapper = false; // if set, return rrc

            p(JSON_OBJECT_CLASS + " rc = new " + JSON_OBJECT_CLASS + "();");
            if (classStyle == Style.RAILS)
            {
                returnWrapper = true;
                p(JSON_OBJECT_CLASS + " rrc = new " + JSON_OBJECT_CLASS + "();");
                p("rrc.put(\"" + railsWrapperName + "\" , rc);");
            }

            for (Subtype possibleType : possibleTypes)
            {
                // Try to find a constuctor that is annotated as creator
                final JConstructor creator = findCreator(possibleType.clazz);

                List<JField> orderedFields = creator == null ? null : getOrderedFields(getFields(possibleType.clazz),
                        creator);

                if (!isLeaf)
                {
                    // Generate a decoder for each possible type
                    p("if(value.getClass().getName().equals(\"" + possibleType.clazz.getQualifiedBinaryName() + "\"))");
                    p("{");
                }

                if (typeInfo != null)
                {
                    switch (typeInfo.include())
                    {
                        case PROPERTY:
                            p("com.google.gwt.json.client.JSONValue className=org.fusesource.restygwt.client.AbstractJsonEncoderDecoder.STRING.encode(\"" +
                                    possibleType.tag + "\");");
                            p("if( className!=null ) { ").i(1);
                            p("rc.put(" + getTypeInfoPropertyValue(typeInfo) + ", className);");
                            i(-1).p("}");
                            break;
                        case WRAPPER_OBJECT:
                            returnWrapper = true;
                            p(JSON_OBJECT_CLASS + " rrc = new " + JSON_OBJECT_CLASS + "();");
                            p("rrc.put(\"" + possibleType.tag + "\", rc);");
                            break;
                        case WRAPPER_ARRAY:
                            returnWrapper = true;
                            p(JSON_ARRAY_CLASS + " rrc = new " + JSON_ARRAY_CLASS + "();");
                            p("rrc.set(0, org.fusesource.restygwt.client.AbstractJsonEncoderDecoder.STRING.encode(\"" +
                                    possibleType.tag + "\"));");
                            p("rrc.set(1, rc);");
                    }
                }

                p(possibleType.clazz.getParameterizedQualifiedSourceName() + " parseValue = (" +
                        possibleType.clazz.getParameterizedQualifiedSourceName() + ")value;");

                for (final JField field : getFields(possibleType.clazz))
                {

                    final String getterName = getGetterName(field);

                    boolean ignoreField = false;
                    if (possibleType.clazz.getAnnotation(JsonIgnoreProperties.class) != null)
                    {
                        for (String s : possibleType.clazz.getAnnotation(JsonIgnoreProperties.class).value())
                        {
                            if (s.equals(field.getName()))
                            {
                                ignoreField = true;
                                break;
                            }
                        }
                    }

                    // If can ignore some fields right off the back..
                    // if there is a creator encode only final fields with JsonProperty annotation
                    if (ignoreField ||
                            getterName == null &&
                            (field.isStatic() ||
                                    (field.isFinal() && !(creator != null && orderedFields.contains(field))) ||
                                    field.isTransient() || field.isAnnotationPresent(JsonIgnore.class)))
                    {
                        continue;
                    }

                    branch("Processing field: " + field.getName(), new Branch<Void>()
                    {
                        public Void execute() throws UnableToCompleteException
                        {
                            // TODO: try to get the field with a setter or
                            // JSNI
                            if (getterName != null || field.isDefaultAccess() || field.isProtected() ||
                                    field.isPublic())
                            {

                                Json jsonAnnotation = field.getAnnotation(Json.class);
                                JsonProperty jsonPropertyAnnotation = field.getAnnotation(JsonProperty.class);

                                String name = field.getName();
                                String jsonName = name;

                                if (jsonAnnotation != null && jsonAnnotation.name().length() > 0)
                                {
                                    jsonName = jsonAnnotation.name();
                                }
                                if (jsonPropertyAnnotation != null && jsonPropertyAnnotation.value() != null &&
                                        jsonPropertyAnnotation.value().length() > 0)
                                {
                                    jsonName = jsonPropertyAnnotation.value();
                                }

                                String fieldExpr = "parseValue." + name;
                                if (getterName != null)
                                {
                                    fieldExpr = "parseValue." + getterName + "()";
                                }

                                Style style = jsonAnnotation != null ? jsonAnnotation.style() : classStyle;
                                String expression = locator.encodeExpression(field.getType(), fieldExpr, style);

                                p("{").i(1);
                                {
                                    if (null != field.getType().isEnum())
                                    {
                                        p("if(" + fieldExpr + " == null) {").i(1);
                                        p("rc.put(" + wrap(jsonName) + ", null);");
                                        i(-1).p("} else {").i(1);
                                    }

                                    p(JSON_VALUE_CLASS + " v=" + expression + ";");
                                    p("if( v!=null ) {").i(1);
                                    {
                                        p("rc.put(" + wrap(jsonName) + ", v);");
                                    }
                                    i(-1).p("}");

                                    if (null != field.getType().isEnum())
                                    {
                                        i(-1).p("}");
                                    }

                                }
                                i(-1).p("}");

                            }
                            else
                            {
                                logger.log(ERROR, "private field gets ignored: " +
                                        field.getEnclosingType().getQualifiedSourceName() + "." + field.getName());
                            }
                            return null;
                        }
                    });

                }

                if (returnWrapper)
                {
                    p("return rrc;");
                }
                else
                {
                    p("return rc;");
                }

                if (!isLeaf)
                {
                    p("}");
                }
            }

            if (!isLeaf)
            {
                // Shouldn't get called
                p("return null;");
            }
        }
        i(-1).p("}");
        p();
    }

    private JsonTypeInfo writeSourceName(final JClassType source, final List<Subtype> possibleTypes, JsonTypeInfo typeInfo, boolean isLeaf) throws UnableToCompleteException
    {
        if (source.isInterface() != null){
                
            boolean classFound = false;
            JsonTypeInfo ti = null;
            for (Subtype subtype : possibleTypes)
            {
                ti = subtype.clazz.findAnnotationInTypeHierarchy(JsonTypeInfo.class);
                if (ti != null && ti.use() == Id.CLASS){
                    classFound = true;
                    break;
                }
            }
            
            if (!classFound)
                error("One of the class implementing the interface should have the annotation JsonTypeInfo = class");
            
            p("String sourceName = org.fusesource.restygwt.client.AbstractJsonEncoderDecoder.STRING.decode(object.get(" +
                    getTypeInfoPropertyValue(ti) + "));");
            
            return ti;
    
        } else {
            if (!isLeaf && typeInfo != null && typeInfo.include() == As.PROPERTY)
            {
                p("String sourceName = org.fusesource.restygwt.client.AbstractJsonEncoderDecoder.STRING.decode(object.get(" +
                        getTypeInfoPropertyValue(typeInfo) + "));");
            }
            
            return typeInfo;
        }
    }

    private void writeSingleton()
    {
        p();
        p("public static final " + shortName + " INSTANCE = new " + shortName + "();");
        p();
    }

    private static boolean isLeaf(JClassType source)
    {
        return !(source.getSubtypes() != null && source.getSubtypes().length > 0);
    }

    private List<Subtype> findPossibleTypes(GeneratorContext context, JClassType classType) throws UnableToCompleteException
    {
        if (classType.isClass() != null)
            return findPossibleTypesForClass(context, classType);
        
        if (classType.isInterface() != null)
            return findPossibleTypesForInterface(context, classType);

        error("Cannot find required subtype resolution for : " + classType);
        return null;
    }

    private List<Subtype> findPossibleTypesForInterface(GeneratorContext context, JClassType classType) throws UnableToCompleteException
    {
        if (!classType.isAnnotationPresent(JsonDeserialize.class))
            error("Cannot find required subtype resolution for interface (missing @JsonDeserialize annotation): " + classType);
        
        final List<Subtype> possibleTypes = Lists.newArrayList();
        
        final JClassType type = find(classType.getAnnotation(JsonDeserialize.class).as());
        
        List<JClassType> resolvedSubtypes = findConcreteSubtypes(context, type);
        
        for (JClassType typeClass : resolvedSubtypes) {
            possibleTypes.add(new Subtype(typeClass.getQualifiedSourceName(), typeClass));
        }

        return possibleTypes;
    }

    private List<Subtype> findPossibleTypesForClass(GeneratorContext context, JClassType source) throws UnableToCompleteException
    {
        final List<Subtype> possibleTypes = Lists.newArrayList();
        final JsonTypeInfo typeInfo = findAnnotation(source, JsonTypeInfo.class);
        final boolean isLeaf = isLeaf(source);
        if (typeInfo != null)
        {
            final JsonSubTypes jacksonSubTypes = findAnnotation(source, JsonSubTypes.class);
            if (typeInfo.use() == Id.CLASS || typeInfo.use() == Id.MINIMAL_CLASS)
            {
                List<JClassType> resolvedSubtypes = Lists.newArrayList();
                if (jacksonSubTypes != null)
                {
                    for (JsonSubTypes.Type type : jacksonSubTypes.value())
                    {
                        JClassType typeClass = find(type.value());
                        if (!isLeaf || source.equals(typeClass))
                            resolvedSubtypes.add(typeClass);
                    }
                }
                else
                {
                   resolvedSubtypes.addAll(findConcreteSubtypes(context, source));                  
                }
                for (JClassType typeClass : resolvedSubtypes)
                    possibleTypes.add(new Subtype(typeInfo.use() == Id.CLASS ? typeClass.getQualifiedSourceName()
                            : "." + typeClass.getSimpleSourceName(), typeClass));
            }
            else if (typeInfo.use() != Id.NONE)
            {
                final JsonTypeIdResolver typeResolver = findAnnotation(source, JsonTypeIdResolver.class);
                if (jacksonSubTypes != null)
                {
                    for (JsonSubTypes.Type type : jacksonSubTypes.value())
                    {
                        if (type.name() != null && !type.name().isEmpty())
                        {
                            JClassType typeClass = find(type.value());
                            if (!isLeaf || source.equals(typeClass))
                                possibleTypes.add(new Subtype(type.name(), typeClass));
                        }
                        else
                        {
                            JsonTypeName nameAnnotation = type.value().getAnnotation(JsonTypeName.class);
                            if (nameAnnotation == null || nameAnnotation.value() == null ||
                                    nameAnnotation.value().isEmpty())
                                error("Cannot find @JsonTypeName annotation for type: " + type.value());
                            JClassType typeClass = find(type.value());
                            if (!isLeaf || source.equals(typeClass))
                                possibleTypes.add(new Subtype(nameAnnotation.value(), typeClass));
                        }
                    }
                    if (isLeaf && possibleTypes.size() == 0)
                        error("Could not find @JsonSubTypes entry for type: " + source);
                }
                else if (typeResolver != null)
                {
                    Class<? extends TypeIdResolver> resolverClass = typeResolver.value();
                    RestyJsonTypeIdResolver restyResolver;
                    if (RestyJsonTypeIdResolver.class.isAssignableFrom(resolverClass))
                    {
                        try
                        {
                            restyResolver = (RestyJsonTypeIdResolver) resolverClass.newInstance();
                        }
                        catch (Exception e)
                        {
                            logger.log(ERROR, "Could not acccess: " + resolverClass, e);
                            throw new UnableToCompleteException();
                        }
                    }
                    else
                    {
                        restyResolver = getRestyResolverClassMap(context, logger).get(resolverClass);
                        if (restyResolver == null)
                            error("Could not find RestyJsonTypeIdResolver for " +
                                    resolverClass +
                                    " did you forget to put <extend-configuration-property name=\"org.fusesource.restygwt.jsontypeidresolver\" value=\"<fully-qualified-class-implementing-RestyJsonTypeIdResolver>\"/> in your *.gwt.xml?");

                    }

                    for (Map.Entry<String, Class<?>> entry : restyResolver.getIdClassMap().entrySet())
                    {
                        JClassType entryType = find(entry.getValue());
                        if (!isLeaf || source.equals(entryType))
                            possibleTypes.add(new Subtype(entry.getKey(), entryType));
                    }
                    if (isLeaf && possibleTypes.size() == 0)
                        error("Could not find entry in " + restyResolver.getClass().getName() + " for type: " + source);
                }
                else
                {
                    error("Cannot find required subtype resolution for type: " + source);
                }
            }
            else
            {
                error("Id.NONE not supported");
            }
        }
        else
        {           
            possibleTypes.add(new Subtype(null, source));
        }
        return possibleTypes;
    }

    private static List<JClassType> findConcreteSubtypes(GeneratorContext context, final JClassType classType)
    {
        List<JClassType> l = Lists.newArrayList();
        for (JClassType t : context.getTypeOracle().getTypes()) {
            if (!t.isAbstract() && t.isAssignableTo(classType))
                l.add(t);
        }
        return l;
    }

    private static Map<Class<?>, RestyJsonTypeIdResolver> sTypeIdResolverMap = null;

    private static Map<Class<?>, RestyJsonTypeIdResolver> getRestyResolverClassMap(GeneratorContext context,
            TreeLogger logger) throws UnableToCompleteException
    {
        if (sTypeIdResolverMap == null)
        {
            try
            {
                Map<Class<?>, RestyJsonTypeIdResolver> map = Maps.newHashMap();
                List<String> values = context.getPropertyOracle()
                        .getConfigurationProperty("org.fusesource.restygwt.jsontypeidresolver").getValues();
                for (String value : values)
                    try
                    {
                        Class<?> clazz = Class.forName(value);
                        RestyJsonTypeIdResolver resolver = (RestyJsonTypeIdResolver) clazz.newInstance();
                        map.put(resolver.getTypeIdResolverClass(), resolver);
                    }
                    catch (Exception e)
                    {
                        logger.log(WARN, "Could not access class: " + values.get(0), e);
                    }
                sTypeIdResolverMap = map;
            }
            catch (BadPropertyValueException e)
            {
                logger.log(ERROR, "Could not acccess property: RestyJsonTypeIdResolver", e);
                throw new UnableToCompleteException();
            }
        }
        return sTypeIdResolverMap;
    }

    private List<JField> getOrderedFields(List<JField> fields, JConstructor creator) throws UnableToCompleteException
    {
        List<JField> orderedFields = new ArrayList<JField>();
        for (JParameter param : creator.getParameters())
        {
            JsonProperty prop = param.getAnnotation(JsonProperty.class);
            if (prop != null)
            {
                for (JField field : fields)
                {
                    if (field.getName().equals(prop.value()))
                    {
                        orderedFields.add(field);
                    }
                }
            }
            else
            {
                error("a constructor annotated with @JsonCreator requires that all paramaters are annotated with @JsonProperty.");
            }
        }

        return orderedFields;
    }

    private JConstructor findCreator(JClassType sourceClazz)
    {
        for (JConstructor constructor : sourceClazz.getConstructors())
        {
            if (constructor.getAnnotation(JsonCreator.class) != null)
            {
                return constructor;
            }
        }

        return null;
    }

    /**
     * 
     * @param field
     * @return the name for the setter for the specified field or null if a setter can't be found.
     */
    private String getSetterName(JField field)
    {
        String fieldName = field.getName();
        fieldName = "set" + upperCaseFirstChar(fieldName);
        JClassType type = field.getEnclosingType();
        if (exists(type, field, fieldName, true))
        {
            return fieldName;
        }
        else
        {
            return null;
        }
    }

    /**
     * 
     * @param field
     * @return the name for the getter for the specified field or null if a getter can't be found.
     */
    private String getGetterName(JField field)
    {
        String fieldName = field.getName();
        JType booleanType = null;
        try
        {
            booleanType = find(Boolean.class);
        }
        catch (UnableToCompleteException e)
        {
            // do nothing
        }
        JClassType type = field.getEnclosingType();
        if (field.getType().equals(JPrimitiveType.BOOLEAN) || field.getType().equals(booleanType))
        {
            fieldName = "is" + upperCaseFirstChar(field.getName());
            if (exists(type, field, fieldName, false))
            {
                return fieldName;
            }
            fieldName = "has" + upperCaseFirstChar(field.getName());
            if (exists(type, field, fieldName, false))
            {
                return fieldName;
            }
        }
        fieldName = "get" + upperCaseFirstChar(field.getName());
        if (exists(type, field, fieldName, false))
        {
            return fieldName;
        }
        else
        {
            return null;
        }
    }

    private String upperCaseFirstChar(String in)
    {
        if (in.length() == 1)
        {
            return in.toUpperCase();
        }
        else
        {
            return in.substring(0, 1).toUpperCase() + in.substring(1);
        }
    }

    /**
     * checks whether a getter or setter exists on the specified type or any of its super classes
     * excluding Object.
     * 
     * @param type
     * @param field
     * @param fieldName
     * @param isSetter
     * @return
     */
    private boolean exists(JClassType type, JField field, String fieldName, boolean isSetter)
    {
        if (field instanceof DummyJField)
        {
            return true;
        }

        JType[] args = null;
        if (isSetter)
        {
            args = new JType[]
            {
                field.getType()
            };
        }
        else
        {
            args = new JType[] {};
        }

        if (null != type.findMethod(fieldName, args))
        {
            return true;
        }
        else
        {
            try
            {
                JType objectType = find(Object.class);
                JClassType superType = type.getSuperclass();
                if (!objectType.equals(superType))
                {
                    return exists(superType, field, fieldName, isSetter);
                }
            }
            catch (UnableToCompleteException e)
            {
                // do nothing
            }
        }
        return false;
    }

    /**
     * Inspects the supplied type and all super classes up to but excluding Object and returns a
     * list of all fields found in these classes.
     * 
     * @param type
     * @return
     */
    private List<JField> getFields(JClassType type)
    {
        List<JField> allFields = getFields(new ArrayList<JField>(), type);
        Map<String, JMethod> getters = new HashMap<String, JMethod>();
        Map<String, JType> setters = new HashMap<String, JType>();
        for (JMethod m : type.getInheritableMethods())
        {
            if (m.getName().startsWith("set") && m.getParameterTypes().length == 1 &&
                    m.getReturnType() == JPrimitiveType.VOID)
            {
                setters.put(m.getName().replaceFirst("^set", ""), m.getParameterTypes()[0]);
            }
            else if (m.getName().startsWith("get") && m.getParameterTypes().length == 0 &&
                    m.getReturnType() != JPrimitiveType.VOID)
            {
                getters.put(m.getName().replaceFirst("^get", ""), m);
            }
        }
        for (Map.Entry<String, JMethod> entry : getters.entrySet())
        {
            if (setters.containsKey(entry.getKey()) &&
                    setters.get(entry.getKey()).equals(entry.getValue().getReturnType()))
            {
                String name = entry.getKey().substring(0, 1).toLowerCase() + entry.getKey().substring(1);

                boolean found = false;
                for (JField f : allFields)
                {
                    if (f.getName().equals(name))
                    {
                        found = true;
                        break;
                    }
                }
                JField f = type.getField(name);
                if (!found && !(f != null && f.isAnnotationPresent(JsonIgnore.class)))
                {
                    DummyJField dummy = new DummyJField(name, entry.getValue().getReturnType());
                    if (entry.getValue().isAnnotationPresent(JsonProperty.class))
                    {
                        dummy.setAnnotation(entry.getValue().getAnnotation(JsonProperty.class));
                    }
                    allFields.add(dummy);
                }
            }
        }
        return allFields;
    }

    private List<JField> getFields(List<JField> allFields, JClassType type)
    {
        JField[] fields = type.getFields();
        for (JField field : fields)
        {
            if (!field.isTransient() && !field.isAnnotationPresent(JsonIgnore.class))
            {
                allFields.add(field);
            }
        }
        try
        {
            JType objectType = find(Object.class);
            if (!objectType.equals(type))
            {
                JClassType superType = type.getSuperclass();
                return getFields(allFields, superType);
            }
        }
        catch (UnableToCompleteException e)
        {
            // do nothing
        }

        return allFields;
    }

    private String getTypeInfoPropertyValue(final JsonTypeInfo typeInfo)
    {
        String propValue;
        if (typeInfo.use() == Id.CLASS && "".equals(typeInfo.property()))
            propValue = Id.CLASS.getDefaultPropertyName();
        else
            propValue = typeInfo.property();

        return wrap(propValue);
    }
}
