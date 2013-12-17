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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonSubTypes;
import org.codehaus.jackson.annotate.JsonSubTypes.Type;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.annotate.JsonTypeInfo.As;
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
import com.google.gwt.json.client.JSONNull;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
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
 */

public class JsonEncoderDecoderClassCreator extends BaseSourceCreator {
    private static final String JSON_ENCODER_SUFFIX = "_Generated_JsonEncoderDecoder_";

    private String JSON_ENCODER_DECODER_CLASS = JsonEncoderDecoderInstanceLocator.JSON_ENCODER_DECODER_CLASS;
    protected static final String JSON_VALUE_CLASS = JSONValue.class.getName();
    private static final String JSON_OBJECT_CLASS = JSONObject.class.getName();
    private static final String JSON_ARRAY_CLASS = JSONArray.class.getName();
    private static final String JSON_NULL_CLASS = JSONNull.class.getName();
    protected static final String JSON_STRING_CLASS = JSONString.class.getName();

    protected JsonEncoderDecoderInstanceLocator locator;

    public JsonEncoderDecoderClassCreator(TreeLogger logger, GeneratorContext context, JClassType source) throws UnableToCompleteException {
        super(logger, context, source, JSON_ENCODER_SUFFIX);
    }

    @Override
    public void generate() throws UnableToCompleteException {
        final JsonTypeInfo typeInfo = findAnnotation(source, JsonTypeInfo.class);
        final boolean isLeaf = isLeaf(source);

        final List<Subtype> possibleTypes = getPossibleTypes(typeInfo, isLeaf);

        final JClassType sourceClazz = source.isClass() == null ? source.isInterface() : source.isClass();
        if (sourceClazz == null) {
            getLogger().log(ERROR, "Type is not a class");
            throw new UnableToCompleteException();
        }

        if (sourceClazz.isAbstract()) {
            if (typeInfo == null) {
                getLogger().log(ERROR, "Abstract classes must be annotated with JsonTypeInfo");
                throw new UnableToCompleteException();
            }
        }
        Json jsonAnnotation = source.getAnnotation(Json.class);
        final Style classStyle = jsonAnnotation != null ? jsonAnnotation.style() : Style.DEFAULT;
        final String railsWrapperName = jsonAnnotation != null && jsonAnnotation.name().length() > 0 ? jsonAnnotation.name() : sourceClazz.getName().toLowerCase();
        locator = new JsonEncoderDecoderInstanceLocator(context, getLogger());

        generateSingleton(shortName);

        generateEncodeMethod(source, classStyle, typeInfo, railsWrapperName, possibleTypes, isLeaf, locator);

        generateDecodeMethod(source, classStyle, typeInfo, railsWrapperName, possibleTypes, isLeaf, locator);
    }

    @Override
    protected ClassSourceFileComposerFactory createComposerFactory() {
	ClassSourceFileComposerFactory composerFactory = new ClassSourceFileComposerFactory(packageName, shortName);
	composerFactory.setSuperclass(JSON_ENCODER_DECODER_CLASS + "<" + source.getParameterizedQualifiedSourceName() + ">");
	return composerFactory;
    }

    public static <T extends Annotation> T findAnnotation(JClassType clazz, Class<T> annotation) {
	if (clazz == null)
	    return null;
	else if (clazz.isAnnotationPresent(annotation))
	    return clazz.getAnnotation(annotation);
	else
	    return findAnnotation(clazz.getSuperclass(), annotation);
    }

    private List<Subtype> getPossibleTypes(final JsonTypeInfo typeInfo, final boolean isLeaf) throws UnableToCompleteException
    {
        if (typeInfo == null)
            return Lists.newArrayList(new Subtype(null, source));
        else {
            Collection<Type> subTypes = findJsonSubTypes(source);
            if(subTypes.isEmpty()) {
                JsonSubTypes foundAnnotation = findAnnotation(source, JsonSubTypes.class);
                if(foundAnnotation != null) {
                    Type[] value = foundAnnotation.value();
                    subTypes = Arrays.asList(value);
                }
            }
            PossibleTypesVisitor v = new PossibleTypesVisitor(context, source, isLeaf, getLogger(), subTypes);
            return v.visit(typeInfo.use());
        }
    }

    private Collection<Type> findJsonSubTypes(JClassType clazz) {
        if (clazz == null)
            return Collections.emptyList();
        else if (clazz.isAnnotationPresent(JsonSubTypes.class)) {
            JsonSubTypes annotation = (JsonSubTypes) clazz.getAnnotation(JsonSubTypes.class);
            Set<Type> result = new HashSet<JsonSubTypes.Type>();
            Type[] value = annotation.value();
            for (Type type : value) {
                result.add(type);
                Class<?> subclazz = type.value();
                String newSubClassName = subclazz.getName().replaceAll("\\$", ".");
                JClassType subJClazz = context.getTypeOracle().findType(newSubClassName);
                if(!isSame(clazz, subclazz)) {
                    result.addAll(findJsonSubTypes(subJClazz));
                }
            }
            return result;
        } else
            return Collections.emptyList();
        }

    private boolean isSame(JClassType clazz, Class<?> subclazz) {
        return (clazz.getPackage().getName()+"."+clazz.getName()).equals(subclazz.getName());
    }

    protected void generateSingleton(String shortName)
    {
        p();
        p("public static final " + shortName + " INSTANCE = new " + shortName + "();");
        p();
    }

    private void generateEncodeMethod(JClassType classType,
            final Style classStyle,
            JsonTypeInfo typeInfo,
            String railsWrapperName,
            List<Subtype> possibleTypes,
            boolean isLeaf,
            final JsonEncoderDecoderInstanceLocator locator) throws UnableToCompleteException
    {
        if (null != classType.isEnum()) {
            generateEnumEncodeMethod(classType, JSON_VALUE_CLASS);
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
            if (classStyle == Style.RAILS) {
                returnWrapper = true;
                p(JSON_OBJECT_CLASS + " rrc = new " + JSON_OBJECT_CLASS + "();");
                p("rrc.put(\"" + railsWrapperName + "\" , rc);");
            }

            for (Subtype possibleType : possibleTypes) {

                if (!possibleType.clazz.isAssignableTo(classType)) {
                    getLogger().log(DEBUG, "Only assignable classes are allowed: " + possibleType.clazz.getParameterizedQualifiedSourceName() + " is not assignable to: " + classType.getParameterizedQualifiedSourceName());
                    continue;
                }

                if (!isLeaf) {
                    // Generate a decoder for each possible type
                    p("if(value.getClass().getName().equals(\"" + possibleType.clazz.getQualifiedBinaryName() + "\"))");
                    p("{");
                }

                if (possibleType.clazz.isEnum() != null) {
                    generateEnumEncodeMethodBody(possibleType, typeInfo);
                } else {

                    // Try to find a constuctor that is annotated as creator
                    final JConstructor creator = findCreator(possibleType.clazz);

                    List<JField> orderedFields = creator == null ? null : getOrderedFields(getFields(possibleType.clazz), creator);

                    if (typeInfo != null) {
                        switch (typeInfo.include()) {
                            case PROPERTY:
                                p("com.google.gwt.json.client.JSONValue className=org.fusesource.restygwt.client.AbstractJsonEncoderDecoder.STRING.encode(\"" + possibleType.tag + "\");");
                                p("if( className!=null ) { ").i(1);
                                p("rc.put(" + wrap(getTypeInfoPropertyValue(typeInfo)) + ", className);");
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
                                p("rrc.set(0, org.fusesource.restygwt.client.AbstractJsonEncoderDecoder.STRING.encode(\"" + possibleType.tag + "\"));");
                                p("rrc.set(1, rc);");
                        }
                    }

                    p(possibleType.clazz.getParameterizedQualifiedSourceName() + " parseValue = (" + possibleType.clazz.getParameterizedQualifiedSourceName() + ")value;");

                    for (final JField field : getFields(possibleType.clazz)) {

                        final String getterName = getGetterName(field);

                        boolean ignoreField = false;
                        if(possibleType.clazz.getAnnotation(JsonIgnoreProperties.class) != null) {
                            for(String s : possibleType.clazz.getAnnotation(JsonIgnoreProperties.class).value()) {
                                if(s.equals(field.getName())) {
                                    ignoreField = true;
                                    break;
                                }
                            }
                        }

                        // If can ignore some fields right off the back..
                        // if there is a creator encode only final fields with JsonProperty annotation
                        if (ignoreField || getterName == null && (field.isStatic() || (field.isFinal() && !(creator != null && orderedFields.contains(field))) || field.isTransient()
                                || field.isAnnotationPresent(JsonIgnore.class))) {
                            continue;
                        }

                        branch("Processing field: " + field.getName(), new Branch<Void>() {
                            public Void execute() throws UnableToCompleteException {
                                // TODO: try to get the field with a setter or
                                // JSNI
                                if (getterName != null || field.isDefaultAccess() || field.isProtected() || field.isPublic()) {

                                    Json jsonAnnotation = field.getAnnotation(Json.class);
                                    JsonProperty jsonPropertyAnnotation = field.getAnnotation(JsonProperty.class);

                                    String name = field.getName();
                                    String jsonName = name;

                                    if (jsonAnnotation != null && jsonAnnotation.name().length() > 0) {
                                        jsonName = jsonAnnotation.name();
                                    }
                                    if (jsonPropertyAnnotation != null && jsonPropertyAnnotation.value() != null && jsonPropertyAnnotation.value().length() > 0) {
                                        jsonName = jsonPropertyAnnotation.value();
                                    }

                                    String fieldExpr = "parseValue." + name;
                                    if (getterName != null) {
                                        fieldExpr = "parseValue." + getterName + "()";
                                    }

                                    Style style = jsonAnnotation != null ? jsonAnnotation.style() : classStyle;
                                    String expression = locator.encodeExpression(field.getType(), fieldExpr, style);

                                    p("{").i(1);
                                    {
                                        if (null != field.getType().isEnum()) {
                                            p("if(" + fieldExpr + " == null) {").i(1);
                                            p("rc.put(" + wrap(jsonName) + ", " + JSON_NULL_CLASS + ".getInstance());");
                                            i(-1).p("} else {").i(1);
                                        }

                                        p(JSON_VALUE_CLASS + " v=" + expression + ";");
                                        p("if( v!=null ) {").i(1);
                                        {
                                            p("rc.put(" + wrap(jsonName) + ", v);");
                                        }
                                        i(-1).p("}");

                                        if (null != field.getType().isEnum()) {
                                            i(-1).p("}");
                                        }

                                    }
                                    i(-1).p("}");

                                } else {
                                    getLogger().log(DEBUG, "private field gets ignored: " + field.getEnclosingType().getQualifiedSourceName() + "." + field.getName());
                                }
                                return null;
                            }
                        });

                    }

                    if (returnWrapper) {
                        p("return rrc;");
                    } else {
                        p("return rc;");
                    }
                }
                if (!isLeaf) {
                    p("}");
                }
            }

            if (!isLeaf) {
                // Shouldn't get called
                p("return null;");
            }
        }
        i(-1).p("}");
        p();
    }

    private void generateEnumEncodeMethodBody(final Subtype possibleType, final JsonTypeInfo typeInfo) {
        p("if( value==null ) {").i(1);
        {
            p("return " + JSON_NULL_CLASS + ".getInstance();").i(-1);
        }
        p("}");
        p(JSON_OBJECT_CLASS + " rrc = new " + JSON_OBJECT_CLASS + "();");
        p(JSON_VALUE_CLASS + " className=org.fusesource.restygwt.client.AbstractJsonEncoderDecoder.STRING.encode(\""
                + possibleType.tag + "\");");
        p("rrc.put(" + wrap(getTypeInfoPropertyValue(typeInfo)) + ", className);");
        p("rrc.put(\"name\", new " + JSON_STRING_CLASS + "(value.name()));");

        p("return rrc;");
    }

    private void generateEnumEncodeMethod(JClassType classType, String jsonValueClass)
    {
        p();
        p("public " + jsonValueClass + " encode(" + classType.getParameterizedQualifiedSourceName() + " value) {").i(1);
        {
        p("if( value==null ) {").i(1);
        {
            p("return " + JSON_NULL_CLASS + ".getInstance();").i(-1);
        }
        p("}");
        p("return new " + JSON_STRING_CLASS + "(value.name());");
        }
        i(-1).p("}");
        p();
    }

    private void generateDecodeMethod(JClassType classType,
            final Style classStyle,
            JsonTypeInfo typeInfo,
            String railsWrapperName,
            List<Subtype> possibleTypes,
            boolean isLeaf,
            final JsonEncoderDecoderInstanceLocator locator) throws UnableToCompleteException
    {
        if (null != classType.isEnum()) {
            generateEnumDecodeMethod(classType, JSON_VALUE_CLASS);
            return;
        }

        p("public " + source.getName() + " decode(" + JSON_VALUE_CLASS + " value) {").i(1);
        {
            p("if( value == null || value.isNull()!=null ) {").i(1);
            {
                p("return null;").i(-1);
            }
            p("}");
            if (classStyle == Style.RAILS) {
                p(JSON_OBJECT_CLASS + " object = toObjectFromWrapper(value, \"" + railsWrapperName + "\");");
            } else if (typeInfo != null && typeInfo.include() == As.WRAPPER_ARRAY) {
                p(JSON_ARRAY_CLASS + " array = (" + JSON_ARRAY_CLASS + ")value;");
                if (!isLeaf)
                    p("String sourceName = org.fusesource.restygwt.client.AbstractJsonEncoderDecoder.STRING.decode(array.get(0));");
                p(JSON_OBJECT_CLASS + " object = toObject(array.get(1));");
            } else {
                p(JSON_OBJECT_CLASS + " object = toObject(value);");
            }

            if (!isLeaf && typeInfo != null && typeInfo.include() == As.PROPERTY) {
                p("String sourceName = org.fusesource.restygwt.client.AbstractJsonEncoderDecoder.STRING.decode(object.get(" + wrap(getTypeInfoPropertyValue(typeInfo)) + "));");
            }

            for (Subtype possibleType : possibleTypes) {

                if (!possibleType.clazz.isAssignableTo(classType)) {
                    getLogger().log(DEBUG, "Only assignable classes are allowed: " + possibleType.clazz.getParameterizedQualifiedSourceName() + " is not assignable to: " + classType.getParameterizedQualifiedSourceName());
                    continue;
                }

                if (typeInfo != null) {
                    if (typeInfo.include() == As.WRAPPER_OBJECT) {
                        if (!isLeaf) {
                            p("if(object.containsKey(\"" + possibleType.tag + "\"))");
                            p("{");
                        }
                        p("object = toObjectFromWrapper(value, \"" + possibleType.tag + "\");");
                    } else if (!isLeaf) {
                        p("if(sourceName.equals(\"" + possibleType.tag + "\"))");
                        p("{");
                    }
                }

                if (possibleType.clazz.isEnum() != null) {
                    generateEnumDecodeMethodBody(possibleType.clazz);
                } else {
                    // Try to find a constuctor that is annotated as creator
                    final JConstructor creator = findCreator(possibleType.clazz);

                    List<JField> orderedFields = null;
                    if (creator != null) {
                        p("// We found a creator so we use the annotated constructor");
                        p("" + possibleType.clazz.getParameterizedQualifiedSourceName() + " rc = new " + possibleType.clazz.getParameterizedQualifiedSourceName() + "(");
                        i(1).p("// The arguments are placed in the order they appear within the annotated constructor").i(-1);
                        orderedFields = getOrderedFields(getFields(possibleType.clazz), creator);
                        final JField lastField = orderedFields.get(orderedFields.size() - 1);
                        for (final JField field : orderedFields) {
                            branch("Processing field: " + field.getName(), new Branch<Void>() {
                                public Void execute() throws UnableToCompleteException {
                                    Json jsonAnnotation = field.getAnnotation(Json.class);
                                    Style style = jsonAnnotation != null ? jsonAnnotation.style() : classStyle;
                                    String jsonName = field.getName();
                                    if (jsonAnnotation != null && jsonAnnotation.name().length() > 0) {
                                        jsonName = jsonAnnotation.name();
                                    }
                                    String objectGetter = "object.get(" + wrap(jsonName) + ")";
                                    String expression = locator.decodeExpression(field.getType(), objectGetter, style);

                                    String defaultValue = getDefaultValue(field);
                                    i(1).p("" + (objectGetter + " == null || " + objectGetter + " instanceof " + JSON_NULL_CLASS + " ? " + defaultValue + " : " + expression + ((field != lastField) ? ", " : ""))).i(-1);

                                    return null;
                                }
                            });
                        }
                        p(");");
                    }

                    if (orderedFields == null){
                        p("" + possibleType.clazz.getParameterizedQualifiedSourceName() + " rc = new " + possibleType.clazz.getParameterizedQualifiedSourceName() + "();");
                    }

                    for (final JField field : getFields(possibleType.clazz)) {

                        boolean ignoreField = false;
                        if(possibleType.clazz.getAnnotation(JsonIgnoreProperties.class) != null) {
                            for(String s : possibleType.clazz.getAnnotation(JsonIgnoreProperties.class).value()) {
                                if(s.equals(field.getName())) {
                                    ignoreField = true;
                                    break;
                                }
                            }
                        }
                        if(ignoreField) {
                            continue;
                        }

                        if (orderedFields != null && orderedFields.contains(field)){
                            continue;
                        }

                        final String setterName = getSetterName(field);

                        // If can ignore some fields right off the back..
                        if (setterName == null && (field.isStatic() || field.isFinal() || field.isTransient()) ||
                                field.isAnnotationPresent(JsonIgnore.class)) {
                            continue;
                        }

                        branch("Processing field: " + field.getName(), new Branch<Void>() {
                            public Void execute() throws UnableToCompleteException {

                                // TODO: try to set the field with a setter
                                // or JSNI
                                if (setterName != null || field.isDefaultAccess() || field.isProtected() || field.isPublic()) {

                                    Json jsonAnnotation = field.getAnnotation(Json.class);
                                    Style style = jsonAnnotation != null ? jsonAnnotation.style() : classStyle;
                                    JsonProperty jsonPropertyAnnotation = field.getAnnotation(JsonProperty.class);

                                    String name = field.getName();
                                    String jsonName = name;

                                    if (jsonAnnotation != null && jsonAnnotation.name().length() > 0) {
                                        jsonName = jsonAnnotation.name();
                                    }
                                    if (jsonPropertyAnnotation != null && jsonPropertyAnnotation.value() != null && jsonPropertyAnnotation.value().length() > 0) {
                                        jsonName = jsonPropertyAnnotation.value();
                                    }

                                    String objectGetter = "object.get(" + wrap(jsonName) + ")";
                                    String expression = locator.decodeExpression(field.getType(), objectGetter, style);

                                    boolean needNullHandling = !locator.hasCustomEncoderDecoder(field.getType());

                                    String cast = field.getType().isPrimitive() == JPrimitiveType.SHORT ? "(short) " : "";

                                    if (needNullHandling) {
                                        p("if(" + objectGetter + " != null) {").i(1);
                                        p("if(" + objectGetter + " instanceof " + JSON_NULL_CLASS + ") {").i(1);
                                        String defaultValue = getDefaultValue(field);

                                        assignFieldValue(name, defaultValue, cast, setterName);
                                        i(-1);
                                        p("} else {").i(1);
                                    }

                                    assignFieldValue(name, expression, cast, setterName);

                                    if (needNullHandling) {
                                        i(-1);
                                        p("}").i(-1);
                                        p("}");
                                    }

                                } else {
                                    getLogger().log(DEBUG, "private field gets ignored: " + field.getEnclosingType().getQualifiedSourceName() + "." + field.getName());
                                }
                                return null;
                            }
                        });
                    }

                    p("return rc;");
                }
                if (typeInfo != null && !isLeaf) {
                    p("}");
                }
            }

            if (typeInfo != null && !isLeaf) {
                p("return null;");
            }
            i(-1).p("}");
            p();
        }
    }

    private void generateEnumDecodeMethodBody(JClassType classType) {
        p(JSON_VALUE_CLASS + " str = object.get(\"name\");");
        p("if( null == str || str.isString() == null ) {").i(1);
        {
            p("throw new DecodingException(\"Expected a string field called 'name' for enum; not found\");").i(-1);
        }
        p("}");
        final String className = classType.getParameterizedQualifiedSourceName();
        p("return Enum.valueOf(" + className + ".class, str.isString().stringValue());").i(-1);
    }

    private String getDefaultValue(JField field) {
        return field.getType().isPrimitive() == null ? "null" : field.getType().isPrimitive().getUninitializedFieldExpression() + "";
    }

    private void assignFieldValue(String name, String expression, String cast, String setterName) {
        if (setterName != null) {
            p("rc." + setterName + "(" + cast + expression + ");");
        } else {
            p("rc." + name + "=" + cast + expression + ";");
        }
    }

    protected void generateEnumDecodeMethod(JClassType classType, String jsonValueClass)
    {
        p();
        p("public " + classType.getName() + " decode(" + jsonValueClass + " value) {").i(1);
        {
        p("if( value == null || value.isNull()!=null ) {").i(1);
        {
            p("return null;").i(-1);
        }
        p("}");
        p(JSON_STRING_CLASS + " str = value.isString();");
        p("if( null == str ) {").i(1);
        {
            p("throw new DecodingException(\"Expected a json string (for enum), but was given: \"+value);").i(-1);
        }
        p("}");
        p("return Enum.valueOf(" + classType.getParameterizedQualifiedSourceName() + ".class, str.stringValue());").i(-1);
        }
        p("}");
        p();
    }

    public static Map<Class<?>, RestyJsonTypeIdResolver> getRestyResolverClassMap(GeneratorContext context, TreeLogger logger) throws UnableToCompleteException {
	if (sTypeIdResolverMap == null) {
	    try {
		Map<Class<?>, RestyJsonTypeIdResolver> map = Maps.newHashMap();
		List<String> values = context.getPropertyOracle().getConfigurationProperty("org.fusesource.restygwt.jsontypeidresolver").getValues();
		for (String value : values)
		    try {
			Class<?> clazz = Class.forName(value);
			RestyJsonTypeIdResolver resolver = (RestyJsonTypeIdResolver) clazz.newInstance();
			map.put(resolver.getTypeIdResolverClass(), resolver);
		    } catch (Exception e) {
			logger.log(WARN, "Could not access class: " + values.get(0), e);
		    }
		    sTypeIdResolverMap = map;
	    } catch (BadPropertyValueException e) {
		logger.log(ERROR, "Could not acccess property: RestyJsonTypeIdResolver", e);
		throw new UnableToCompleteException();
	    }
	}
	return sTypeIdResolverMap;
    }

    private List<JField> getOrderedFields(List<JField> fields, JConstructor creator) throws UnableToCompleteException {
	List<JField> orderedFields = new ArrayList<JField>();
	for (JParameter param : creator.getParameters()) {
	    JsonProperty prop = param.getAnnotation(JsonProperty.class);
	    if (prop != null) {
		for (JField field : fields) {
		    if (field.getName().equals(prop.value())) {
			orderedFields.add(field);
		    }
		}
	    } else {
		getLogger().log(ERROR, "a constructor annotated with @JsonCreator requires that all paramaters are annotated with @JsonProperty.");
        throw new UnableToCompleteException();
	    }
	}

	return orderedFields;
    }

    private JConstructor findCreator(JClassType sourceClazz) {
	for (JConstructor constructor : sourceClazz.getConstructors()) {
	    if (constructor.getAnnotation(JsonCreator.class) != null) {
		return constructor;
	    }
	}

	return null;
    }

    /**
     *
     * @param field
     * @return the name for the setter for the specified field or null if a
     *         setter can't be found.
     */
    private String getSetterName(JField field) {
	String fieldName = field.getName();
	fieldName = "set" + upperCaseFirstChar(fieldName);
	JClassType type = field.getEnclosingType();
	if (exists(type, field, fieldName, true)) {
	    return fieldName;
	} else {
	    return null;
	}
    }

    /**
     *
     * @param field
     * @return the name for the getter for the specified field or null if a
     *         getter can't be found.
     */
    private String getGetterName(JField field) {
	String fieldName = field.getName();
	JType booleanType = null;
	try {
	    booleanType = find(Boolean.class, getLogger(), context);
	} catch (UnableToCompleteException e) {
	    // do nothing
	}
	JClassType type = field.getEnclosingType();
	if (field.getType().equals(JPrimitiveType.BOOLEAN) || field.getType().equals(booleanType)) {
	    fieldName = "is" + upperCaseFirstChar(field.getName());
	    if (exists(type, field, fieldName, false)) {
		return fieldName;
	    }
	    fieldName = "has" + upperCaseFirstChar(field.getName());
	    if (exists(type, field, fieldName, false)) {
		return fieldName;
	    }
	}
	fieldName = "get" + upperCaseFirstChar(field.getName());
	if (exists(type, field, fieldName, false)) {
	    return fieldName;
	} else {
	    return null;
	}
    }

    private String upperCaseFirstChar(String in) {
	if (in.length() == 1) {
	    return in.toUpperCase();
	} else {
	    return in.substring(0, 1).toUpperCase() + in.substring(1);
	}
    }

    /**
     * checks whether a getter or setter exists on the specified type or any of
     * its super classes excluding Object. respects JsonIgnore accordingly.
     *
     * @param type
     * @param field
     * @param fieldName
     * @param isSetter
     * @return
     */
    private boolean exists(JClassType type, JField field, String fieldName, boolean isSetter) {
        if ( field instanceof DummyJField ){
            return true;
        }

	JType[] args = null;
	if (isSetter) {
	    args = new JType[] { field.getType() };
	} else {
	    args = new JType[] {};
	}
	JMethod m = type.findMethod(fieldName, args);
	if (null != m) {
        if(m.getAnnotation(JsonIgnore.class) != null)
            return false;
        if(isSetter)
            return true;
        JClassType returnType = m.getReturnType().isClassOrInterface();
        JClassType fieldType = field.getType().isClassOrInterface();
        if(returnType == null || fieldType == null) {
            // at least one is a primitive type
            return m.getReturnType().equals(field.getType());
        } else {
            // both are non-primitives
            return returnType.isAssignableFrom(fieldType);
        }
	} else {
	    try {
		JType objectType = find(Object.class, getLogger(), context);
		JClassType superType = type.getSuperclass();
		if (!objectType.equals(superType)) {
		    return exists(superType, field, fieldName, isSetter);
		}
	    } catch (UnableToCompleteException e) {
		// do nothing
	    }
	}
	return false;
    }

    /**
     * Inspects the supplied type and all super classes up to but excluding
     * Object and returns a list of all fields found in these classes.
     *
     * @param type
     * @return
     */
    private List<JField> getFields(JClassType type) {
        List<JField> allFields = getFields(new ArrayList<JField>(), type);
        Map<String, JMethod> getters = new HashMap<String, JMethod>();
        Map<String, JType> setters = new HashMap<String, JType>();
        for( JMethod m: type.getInheritableMethods() ){
            if( m.getName().startsWith("set") &&
                    m.getParameterTypes().length == 1 &&
                    m.getReturnType() == JPrimitiveType.VOID &&
                    m.getAnnotation(JsonIgnore.class) == null){
                setters.put( m.getName().replaceFirst("^set", ""), m.getParameterTypes()[0] );
            }
            else if( m.getName().startsWith("get") &&
                    m.getParameterTypes().length == 0 &&
                    m.getReturnType() != JPrimitiveType.VOID &&
                    m.getAnnotation(JsonIgnore.class) == null){
                getters.put( m.getName().replaceFirst("^get", ""), m );
            }
        }
        for( Map.Entry<String, JMethod> entry: getters.entrySet() ){
            if ( setters.containsKey( entry.getKey() ) && setters.get( entry.getKey() ).equals( entry.getValue().getReturnType() ) ) {
                String name = entry.getKey().substring(0, 1).toLowerCase() + entry.getKey().substring(1);

                boolean found = false;
                for( JField f : allFields ){
                    if( f.getName().equals( name ) ){
                        found = true;
                        break;
                    }
                }
                JField f = type.findField( name );
                if ( ! found && !( f != null && f.isAnnotationPresent( JsonIgnore.class ) ) ){
                    DummyJField dummy = new DummyJField( name, entry.getValue().getReturnType() );
                    if ( entry.getValue().isAnnotationPresent(JsonProperty.class) ) {
                        dummy.setAnnotation( entry.getValue().getAnnotation(JsonProperty.class) );
                    }
                    allFields.add( dummy );
                }
            }
        }
        return allFields;
    }

    private List<JField> getFields(List<JField> allFields, JClassType type) {
        JField[] fields = type.getFields();
        for (JField field : fields) {
            if (!field.isTransient() && !field.isAnnotationPresent(JsonIgnore.class)) {
                allFields.add(field);
            }
        }
        try {
            JType objectType = find(Object.class, getLogger(), context);
            if (!objectType.equals(type)) {
                JClassType superType = type.getSuperclass();
                return getFields(allFields, superType);
            }
        }
        catch (UnableToCompleteException e) {
	    // do nothing
        }

    	return allFields;
    }

    public static String getTypeInfoPropertyValue(final JsonTypeInfo typeInfo)
    {
        if (typeInfo.include() == JsonTypeInfo.As.PROPERTY)
            if(typeInfo.property() == null || "".equals(typeInfo.property()))
                return typeInfo.use().getDefaultPropertyName();

        return typeInfo.property();
    }

    public static boolean isLeaf(JClassType source)
    {
        return !(source.getSubtypes() != null && source.getSubtypes().length > 0);
    }

    public static class Subtype {
    final String tag;
    final JClassType clazz;

    public Subtype(String tag, JClassType clazz) {
        this.tag = tag;
        this.clazz = clazz;
    }
    }

    private static Map<Class<?>, RestyJsonTypeIdResolver> sTypeIdResolverMap = null;
}
