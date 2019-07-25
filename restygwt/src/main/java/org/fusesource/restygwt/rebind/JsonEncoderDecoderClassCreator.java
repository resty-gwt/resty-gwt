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

import static org.fusesource.restygwt.rebind.util.AnnotationUtils.getAnnotation;
import static org.fusesource.restygwt.rebind.util.AnnotationUtils.getClassAnnotation;
import static org.fusesource.restygwt.rebind.util.ClassSourceFileComposerFactoryImportUtil.addFuseSourceStaticImports;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonValue;
import com.google.gwt.core.ext.BadPropertyValueException;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.HasAnnotations;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlTransient;

import org.fusesource.restygwt.client.Json;
import org.fusesource.restygwt.client.Json.Style;
import org.fusesource.restygwt.rebind.util.AnnotationUtils;

/**
 *
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 *
 *         Updates: added getter + setter support, enhanced generics support
 * @author <a href="http://www.acuedo.com">Dave Finch</a>
 *
 *         added polymorphic support
 * @author <a href="http://charliemason.info">Charlie Mason</a>
 *
 */

public class JsonEncoderDecoderClassCreator extends BaseSourceCreator {
    private static final String JSON_ENCODER_SUFFIX = "_Generated_JsonEncoderDecoder_";

    public static final String USE_JAVA_BEANS_SPEC_NAMING_CONVENTION_CONFIGURATION_PROPERTY_NAME =
        "restygwt.conventions.useJavaBeansSpecNaming";

    public String JSON_ENCODER_DECODER_CLASS = JsonEncoderDecoderInstanceLocator.JSON_ENCODER_DECODER_CLASS;
    protected static final String JSON_VALUE_CLASS = JSONValue.class.getName();
    private static final String JSON_OBJECT_CLASS = JSONObject.class.getName();
    private static final String JSON_ARRAY_CLASS = JSONArray.class.getName();
    private static final String JSON_NULL_CLASS = JSONNull.class.getName();
    protected static final String JSON_STRING_CLASS = JSONString.class.getName();

    protected EncoderDecoderLocator locator;

    protected boolean javaBeansNamingConventionEnabled;

    public JsonEncoderDecoderClassCreator(TreeLogger logger, GeneratorContext context, JClassType source) {
        super(logger, context, source, JSON_ENCODER_SUFFIX);

        // true, if the naming convention from JavaBeans API specification should be used
        javaBeansNamingConventionEnabled = getBooleanProperty(getLogger(), context.getPropertyOracle(),
            USE_JAVA_BEANS_SPEC_NAMING_CONVENTION_CONFIGURATION_PROPERTY_NAME, true);
    }

    @Override
    public void generate() throws UnableToCompleteException {
        JsonTypeInfo typeInfo = getClassAnnotation(source, JsonTypeInfo.class);
        boolean isLeaf = isLeaf(source);

        List<Subtype> possibleTypes = getPossibleTypes(typeInfo, isLeaf);
        Collections.sort(possibleTypes);

        JClassType sourceClazz = source.isClass() == null ? source.isInterface() : source.isClass();
        if (sourceClazz == null) {
            getLogger().log(ERROR, "Type is not a class");
            throw new UnableToCompleteException();
        }

        if (sourceClazz.isEnum() == null && sourceClazz.isAbstract()) {
            if (typeInfo == null) {
                getLogger().log(ERROR, "Abstract classes must be annotated with JsonTypeInfo");
                throw new UnableToCompleteException();
            }
        }
        Json jsonAnnotation = getAnnotation(source, Json.class);
        Style classStyle = jsonAnnotation != null ? jsonAnnotation.style() : Style.DEFAULT;
        String railsWrapperName =
                jsonAnnotation != null && !jsonAnnotation.name().isEmpty() ? jsonAnnotation.name() :
                sourceClazz.getName().toLowerCase();
        locator = EncoderDecoderLocatorFactory.getEncoderDecoderInstanceLocator(context, getLogger());

        generateSingleton(shortName);

        generateEncodeMethod(source, classStyle, typeInfo, railsWrapperName, possibleTypes, isLeaf, locator);

        generateDecodeMethod(source, classStyle, typeInfo, railsWrapperName, possibleTypes, isLeaf, locator);
    }

    @Override
    protected ClassSourceFileComposerFactory createComposerFactory() {
        ClassSourceFileComposerFactory composerFactory = new ClassSourceFileComposerFactory(packageName, shortName);
        addFuseSourceStaticImports(composerFactory);
        composerFactory
            .setSuperclass(JSON_ENCODER_DECODER_CLASS + "<" + source.getParameterizedQualifiedSourceName() + ">");
        return composerFactory;
    }

    private List<Subtype> getPossibleTypes(JsonTypeInfo typeInfo, boolean isLeaf)
        throws UnableToCompleteException {
        if (typeInfo == null) {
            return Lists.newArrayList(new Subtype(null, source));
        }
        Collection<Type> subTypes = findJsonSubTypes(source);
        if (subTypes.isEmpty()) {
            JsonSubTypes foundAnnotation = getAnnotation(source, JsonSubTypes.class);
            if (foundAnnotation != null) {
                Type[] value = foundAnnotation.value();
                subTypes = Arrays.asList(value);
            }
        }
        PossibleTypesVisitor v = new PossibleTypesVisitor(context, source, isLeaf, getLogger(), subTypes);
        return v.visit(typeInfo.use());
    }

    /**
     * This method does NOT return the subtypes of the given class, but all the subtypes associated with the
     * {@link JsonSubTypes} annotation, even if this annotation is assigned to
     * a parent class or an interface.
     */
    private Collection<Type> findJsonSubTypes(JClassType clazz) {
        return findJsonSubTypes(clazz, new HashSet<JsonSubTypes.Type>());
    }

    private Collection<Type> findJsonSubTypes(JClassType clazz, Set<Type> types) {
        if (clazz == null) {
            return Collections.emptyList();
        }
        JsonSubTypes annotation = getClassAnnotation(clazz, JsonSubTypes.class);

        if (annotation == null) {
            return Collections.emptyList();
        }

        for (Type type : annotation.value()) {
            if (types.add(type)) {
                Class<?> subclazz = type.value();
                String newSubClassName = subclazz.getName().replaceAll("\\$", ".");
                JClassType subJClazz = context.getTypeOracle().findType(newSubClassName);
                findJsonSubTypes(subJClazz, types);
            }
        }

        return types;
    }

    protected void generateSingleton(String shortName) {
        p();
        p("public static final " + shortName + " INSTANCE = new " + shortName + "();");
        p();
    }

    private void generateEncodeMethod(JClassType classType, final Style classStyle, JsonTypeInfo typeInfo,
                                      String railsWrapperName, List<Subtype> possibleTypes, boolean isLeaf,
                                      final EncoderDecoderLocator locator) throws UnableToCompleteException {
        if (null != classType.isEnum()) {
            generateEnumEncodeMethod(classType, JSON_VALUE_CLASS);
            return;
        }
        List<Subtype> assignableSubTypes = new ArrayList<Subtype>();
        p("public " + JSON_VALUE_CLASS + " encode(" + source.getParameterizedQualifiedSourceName() + " value) {").i(1);
        {
            p("if( value==null ) {").i(1);
            {
                p("return getNullType();");
            }
            i(-1).p("}");

            
            for (Subtype possibleType : possibleTypes) {

                if (!possibleType.clazz.isAssignableTo(classType)) {
                    getLogger().log(DEBUG, "Only assignable classes are allowed: " +
                        possibleType.clazz.getParameterizedQualifiedSourceName() + " is not assignable to: " +
                        classType.getParameterizedQualifiedSourceName());
                    continue;
                }
                assignableSubTypes.add(possibleType);

                if (!isLeaf && possibleTypes.size() > 1) {
                    // Generate a decoder for each possible type
                    p("if(value.getClass().getName().equals(\"" + possibleType.clazz.getQualifiedBinaryName() + "\"))");
                    p("{");
                }
                i(1).p("return " + getEncodeMethodName(possibleType) + "(value);");
                if (!isLeaf && possibleTypes.size() > 1) {
                    i(-1).p("}");
                }
            }

            if (!isLeaf && possibleTypes.size() > 1) {
                // Shouldn't get called
                p("return null;");
            }
        }
        i(-1).p("}");
        p();
        for (Subtype type : assignableSubTypes) {
	        p("private "+ JSON_VALUE_CLASS + " " + getEncodeMethodName(type) + "( " + source.getParameterizedQualifiedSourceName() + " value) {").i(1);
	        {
	            buildEncodePossibleTypeLogicBody(type, typeInfo, classStyle, railsWrapperName, classType);
	            i(-1).p("}");
                p();
	        }
        }
    }

    private void buildEncodePossibleTypeLogicBody(Subtype possibleType, JsonTypeInfo typeInfo, final Style classStyle, 
            String railsWrapperName, JClassType classType) throws UnableToCompleteException {
        boolean returnWrapper = false; // if set, return rrc

        p(JSON_OBJECT_CLASS + " rc = new " + JSON_OBJECT_CLASS + "();");
        if (classStyle == Style.RAILS) {
            returnWrapper = true;
            p(JSON_OBJECT_CLASS + " rrc = new " + JSON_OBJECT_CLASS + "();");
            p("rrc.put(\"" + railsWrapperName + "\" , rc);");
        }
        if (possibleType.clazz.isEnum() != null) {
             generateEnumEncodeMethodBody(possibleType, typeInfo);
        } else {
            
            // Try to find a constructor that is annotated as creator
            JConstructor creator = findCreator(possibleType.clazz);
            List<JField> fields = getFields(possibleType.clazz);
            List<JField> orderedFields = creator == null ? null : getOrderedFields(fields, creator);
            if (typeInfo != null) {
                 switch (typeInfo.include()) {
                     case PROPERTY:
                         p("com.google.gwt.json.client.JSONValue className=STRING.encode(\"" + possibleType.tag + "\");");
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
                         p("rrc.set(0, STRING.encode" +
                             "(\"" + possibleType.tag + "\"));");
                         p("rrc.set(1, rc);");
                         break;
                     case EXISTING_PROPERTY:
                         getLogger().log(WARN, classType + " comes with not implemented type info 'as' " +
                             JsonTypeInfo.As.EXISTING_PROPERTY);
                         // not implemented
                         break;
                     case EXTERNAL_PROPERTY:
                         getLogger().log(WARN, classType + " comes with not implemented type info 'as' " +
                             JsonTypeInfo.As.EXTERNAL_PROPERTY);
                         // not implemented
                         break;
                     default:
                 }
             }

             p(possibleType.clazz.getParameterizedQualifiedSourceName() + " parseValue = (" +
                 possibleType.clazz.getParameterizedQualifiedSourceName() + ")value;");

             for (final JField field : fields) {

                 final String getterName = getGetterName(possibleType.clazz, field);

                 boolean ignoreField = false;
                 if (getAnnotation(possibleType.clazz, JsonIgnoreProperties.class) != null) {
                     for (String s : getAnnotation(possibleType.clazz, JsonIgnoreProperties.class).value()) {
                         if (s.equals(field.getName())) {
                             ignoreField = true;
                             break;
                         }
                     }
                 }

                 // If can ignore some fields right off the back..
                 // if there is a creator encode only final fields with JsonProperty annotation
                 if (ignoreField || getterName == null && (field.isStatic() ||
                     (field.isFinal() && !(creator != null && orderedFields.contains(field))) ||
                     field.isTransient() || isIgnored(field))) {
                     continue;
                 }

                 branch("Processing field: " + field.getName(), new Branch<Void>() {
                     @Override
                     public Void execute() throws UnableToCompleteException {
                         // TODO: try to get the field with a setter or
                         // JSNI
                         if (getterName != null || field.isDefaultAccess() || field.isProtected() ||
                             field.isPublic()) {

                             Json jsonAnnotation = getAnnotation(field, Json.class);
                             JsonProperty jsonPropertyAnnotation = getAnnotation(field, JsonProperty.class);

                             String name = field.getName();
                             String jsonName = name;

                             if (jsonAnnotation != null && !jsonAnnotation.name().isEmpty()) {
                                 jsonName = jsonAnnotation.name();
                             }
                             if (jsonPropertyAnnotation != null && jsonPropertyAnnotation.value() != null &&
                                     !jsonPropertyAnnotation.value().isEmpty()) {
                                 jsonName = jsonPropertyAnnotation.value();
                             }

                             String fieldExpr = "parseValue." + name;
                             if (getterName != null) {
                                 fieldExpr = "parseValue." + getterName + "()";
                             }

                             Style style = jsonAnnotation != null ? jsonAnnotation.style() : classStyle;
                             String expression = locator.encodeExpression(field.getType(), fieldExpr, style);


                             if (null != field.getType().isEnum()) {
                                 p("if(isNotNullAndCheckDefaults(" + fieldExpr + ", rc, " + wrap(jsonName) +
                                     ")) {").i(1);
                             }

                             p("isNotNullValuePut(" + expression + ", rc, " + wrap(jsonName) + ");");

                             if (null != field.getType().isEnum()) {
                                 i(-1).p("}");
                             }


                         } else {
                             getLogger().log(DEBUG, "private field gets ignored: " +
                                 field.getEnclosingType().getQualifiedSourceName() + "." + field.getName());
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
    }
    
    private void generateEnumEncodeMethodBody(Subtype possibleType, JsonTypeInfo typeInfo) {
        p("if( value==null ) {").i(1);
        {
            p("return " + JSON_NULL_CLASS + ".getInstance();").i(-1);
        }
        p("}");
        p(JSON_OBJECT_CLASS + " rrc = new " + JSON_OBJECT_CLASS + "();");
        p(JSON_VALUE_CLASS + " className=STRING.encode(\"" +
            possibleType.tag + "\");");
        p("rrc.put(" + wrap(getTypeInfoPropertyValue(typeInfo)) + ", className);");
        p("rrc.put(\"name\", new " + JSON_STRING_CLASS + "(value." + getValueMethod(possibleType.clazz) + "()));");
        p("return rrc;");
    }

    private void generateEnumEncodeMethod(JClassType classType, String jsonValueClass) {
        p();
        p("public " + jsonValueClass + " encode(" + classType.getParameterizedQualifiedSourceName() + " value) {").i(1);
        {
            p("if( value==null ) {").i(1);
            {
                p("return " + JSON_NULL_CLASS + ".getInstance();").i(-1);
            }
            p("}");
            p("return new " + JSON_STRING_CLASS + "(value." + getValueMethod(classType) + "());");
            i(-1).p("}");
        }
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

    private void generateDecodeMethod(JClassType classType, final Style classStyle, JsonTypeInfo typeInfo,
                                      String railsWrapperName, List<Subtype> possibleTypes, boolean isLeaf,
                                      final EncoderDecoderLocator locator) throws UnableToCompleteException {
        if (null != classType.isEnum()) {
            generateEnumDecodeMethod(classType, JSON_VALUE_CLASS);
            return;
        }
        List<Subtype> assignableSubTypes = new ArrayList<Subtype>();
        p("public " + source.getParameterizedQualifiedSourceName() + " decode(" + JSON_VALUE_CLASS + " value) {").i(1);
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
                if (!isLeaf) {
                    p("String sourceName = STRING.decode" +
                        "(array.get(0));");
                }
                p(JSON_OBJECT_CLASS + " object = toObject(array.get(1));");
            } else {
                p(JSON_OBJECT_CLASS + " object = toObject(value);");
            }

            if (!isLeaf && typeInfo != null && typeInfo.include() == As.PROPERTY) {
                p("String sourceName = STRING.decode(object" +
                    ".get(" + wrap(getTypeInfoPropertyValue(typeInfo)) + "));");
            }
            
            for (Subtype possibleType : possibleTypes) {

                if (!possibleType.clazz.isAssignableTo(classType)) {
                    getLogger().log(DEBUG, "Only assignable classes are allowed: " +
                        possibleType.clazz.getParameterizedQualifiedSourceName() + " is not assignable to: " +
                        classType.getParameterizedQualifiedSourceName());
                    continue;
                }
                assignableSubTypes.add(possibleType);
                if (typeInfo != null) {
                    if (typeInfo.include() == As.WRAPPER_OBJECT) {
                        if (!isLeaf) {
                            p("if(object.containsKey(\"" + possibleType.tag + "\"))");
                            p("{");
                        }
                        p("object = toObjectFromWrapper(value, \"" + possibleType.tag + "\");");
                    } else if (!isLeaf) {
                        if (classType.equals(possibleType.clazz)) {
                            p("if(sourceName == null || sourceName.equals(\"" + possibleType.tag + "\"))");
                            p("{");
                        } else {
                            p("if(\"" + possibleType.tag + "\".equals(sourceName))");
                            p("{");
                        }
                    }
                }
                //
                i(1).p("return " + getDecodeMethodName(possibleType) + "(object);");
                if (typeInfo != null && !isLeaf) {
                    i(-1).p("}");
                }
            }

            if (typeInfo != null && !isLeaf) {
                p("return null;");
            }
            i(-1).p("}");
            p();
            
        }
        for (Subtype type : assignableSubTypes) {
            p("private " + source.getParameterizedQualifiedSourceName() + " " + getDecodeMethodName(type) + "(" + JSON_OBJECT_CLASS + " object) {").i(1);
            {
                buildDecodePossibleSubtypeLogic(type, classStyle);
                i(-1).p("}");
                p();
            }
            
        }
        //TODO: Create separate methods for sub classes
    }

    private String getEncodeMethodName(Subtype type) {
    	return getCodingMethodName("encode", type);
    }
    private String getCodingMethodName(String codingType, Subtype type) {
        return codingType + type.clazz.getSimpleSourceName() + type.hashCode();
    }
    private String getDecodeMethodName(Subtype type) {
        return getCodingMethodName("decode", type);
    }
    
    private void buildDecodePossibleSubtypeLogic(Subtype possibleType, final Style classStyle)
          throws UnableToCompleteException {
        if (possibleType.clazz.isEnum() != null) {
            generateEnumDecodeMethodBody(possibleType.clazz);
        } else {
            // Try to find a constuctor that is annotated as creator
            JConstructor creator = findCreator(possibleType.clazz);

            List<JField> orderedFields = null;
            if (creator != null) {
                p("// We found a creator so we use the annotated constructor");
                p("" + possibleType.clazz.getParameterizedQualifiedSourceName() + " rc = new " +
                    possibleType.clazz.getParameterizedQualifiedSourceName() + "(");
                i(1).p("// The arguments are placed in the order they appear within the annotated constructor")
                    .i(-1);
                orderedFields = getOrderedFields(getFields(possibleType.clazz), creator);
                final JField lastField = orderedFields.get(orderedFields.size() - 1);
                for (final JField field : orderedFields) {
                    branch("Processing field: " + field.getName(), new Branch<Void>() {
                        @Override
                        public Void execute() throws UnableToCompleteException {
                            Json jsonAnnotation = getAnnotation(field, Json.class);
                            Style style = jsonAnnotation != null ? jsonAnnotation.style() : classStyle;
                            String jsonName = field.getName();
                            if (jsonAnnotation != null && !jsonAnnotation.name().isEmpty()) {
                                jsonName = jsonAnnotation.name();
                            }
                            String objectGetter = "object.get(" + wrap(jsonName) + ")";
                            String expression = locator.decodeExpression(field.getType(), objectGetter, style);

                            String defaultValue = getDefaultValue(field);
                            i(1).p("" + (objectGetter + " == null || " + objectGetter + " instanceof " +
                                JSON_NULL_CLASS + " ? " + defaultValue + " : " + expression +
                                ((field != lastField) ? ", " : ""))).i(-1);

                            return null;
                        }
                    });
                }
                p(");");
            }

            if (orderedFields == null) {
                p("" + possibleType.clazz.getParameterizedQualifiedSourceName() + " rc = new " +
                    possibleType.clazz.getParameterizedQualifiedSourceName() + "();");
            }

            for (final JField field : getFields(possibleType.clazz)) {

                boolean ignoreField = false;
                if (getAnnotation(possibleType.clazz, JsonIgnoreProperties.class) != null) {
                    for (String s : getAnnotation(possibleType.clazz, JsonIgnoreProperties.class).value()) {
                        if (s.equals(field.getName())) {
                            ignoreField = true;
                            break;
                        }
                    }
                }
                if (ignoreField) {
                    continue;
                }

                if (orderedFields != null && orderedFields.contains(field)) {
                    continue;
                }

                final String setterName = getSetterName(field);

                // If can ignore some fields right off the back..
                if (setterName == null && (field.isStatic() || field.isFinal() || field.isTransient()) ||
                    isIgnored(field)) {
                    continue;
                }

                branch("Processing field: " + field.getName(), new Branch<Void>() {
                    @Override
                    public Void execute() throws UnableToCompleteException {

                        // TODO: try to set the field with a setter
                        // or JSNI
                        if (setterName != null || field.isDefaultAccess() || field.isProtected() ||
                            field.isPublic()) {

                            Json jsonAnnotation = getAnnotation(field, Json.class);
                            Style style = jsonAnnotation != null ? jsonAnnotation.style() : classStyle;
                            JsonProperty jsonPropertyAnnotation = getAnnotation(field, JsonProperty.class);

                            String name = field.getName();
                            String jsonName = name;

                            if (jsonAnnotation != null && !jsonAnnotation.name().isEmpty()) {
                                jsonName = jsonAnnotation.name();
                            }
                            if (jsonPropertyAnnotation != null && jsonPropertyAnnotation.value() != null &&
                                    !jsonPropertyAnnotation.value().isEmpty()) {
                                jsonName = jsonPropertyAnnotation.value();
                            }

                            String objectGetter = "object.get(" + wrap(jsonName) + ")";
                            String expression = locator.decodeExpression(field.getType(), objectGetter, style);

                            boolean isShort = field.getType().isPrimitive() == JPrimitiveType.SHORT;
                            String defaultValue = getDefaultValue(field);

                            String methodName = isShort ? "getValueToSetForShort" : "getValueToSet";

                            if (setterName != null) {
                                p("rc." + setterName + "(" + methodName + "(" + expression + ", " +
                                    defaultValue + "));");
                            } else {
                                p("rc." + name + "= " + methodName + "(" + expression + "," + defaultValue +
                                    ");");
                            }

                        } else {
                            getLogger().log(DEBUG, "private field gets ignored: " +
                                field.getEnclosingType().getQualifiedSourceName() + "." + field.getName());
                        }
                        return null;
                    }
                });
            }

            p("return rc;");
        }
    }
    
    private void generateEnumDecodeMethodBody(JClassType classType) {
        p(JSON_VALUE_CLASS + " str = object.get(\"name\");");
        p("if( null == str || str.isString() == null ) {").i(1);
        {
            p("throw new DecodingException(\"Expected a string field called 'name' for enum; not found\");").i(-1);
        }
        p("}");
        decodeEnum(classType, "str.isString().stringValue()");
    }

    private String getDefaultValue(JField field) {
        return field.getType().isPrimitive() == null ? "null" :
            field.getType().isPrimitive().getUninitializedFieldExpression() + "";
    }

    protected void generateEnumDecodeMethod(JClassType classType, String jsonValueClass) {
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

            String value = "str.stringValue()";
            decodeEnum(classType, value);
        }
        p("}");
        p();
    }

    protected void decodeEnum(JClassType classType, String value) {
        String className = classType.getParameterizedQualifiedSourceName();
        String method = getValueMethod(classType);
        if (method == null) {
            p("return Enum.valueOf(" + className + ".class, " + value + ");").i(-1);
        } else {
            p("for(" + className + " v: " + className + ".values()) {").i(1);
            {
                p("if(v." + method + "().equals(" + value + ")) {").i(1);
                {
                    p("return v;").i(-1);
                }
                p("}").i(-1);
            }
            p("}");
            p("throw new DecodingException(\"can not find enum for given value: \"+" + value + ");").i(-1);
        }
    }

    public static void clearRestyResolverClassMap() {
        sTypeIdResolverMap = null;
    }

    public static Map<Class<?>, RestyJsonTypeIdResolver> getRestyResolverClassMap(GeneratorContext context,
                                                                                  TreeLogger logger)
        throws UnableToCompleteException {
        if (sTypeIdResolverMap == null) {
            try {
                Map<Class<?>, RestyJsonTypeIdResolver> map = Maps.newHashMap();
                List<String> values =
                    context.getPropertyOracle().getConfigurationProperty("org.fusesource.restygwt.jsontypeidresolver")
                        .getValues();
                for (String value : values) {
                    try {
                        Class<?> clazz = Class.forName(value);
                        RestyJsonTypeIdResolver resolver = (RestyJsonTypeIdResolver) clazz.newInstance();
                        map.put(resolver.getTypeIdResolverClass(), resolver);
                    } catch (Exception e) {
                        logger.log(WARN, "Could not access class: " + values.get(0), e);
                    }
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
            JsonProperty prop = getAnnotation(param, JsonProperty.class);
            if (prop != null) {
                for (JField field : fields) {
                    if (field.getName().equals(prop.value())) {
                        orderedFields.add(field);
                    }
                }
            } else {
                getLogger().log(ERROR,
                    "a constructor annotated with @JsonCreator requires that all paramaters are annotated with " +
                        "@JsonProperty.");
                throw new UnableToCompleteException();
            }
        }

        return orderedFields;
    }

    private JConstructor findCreator(JClassType sourceClazz) {
        for (JConstructor constructor : sourceClazz.getConstructors()) {
            if (getAnnotation(constructor, JsonCreator.class) != null) {
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
        String fieldName = "set" + getMiddleNameForPrefixingAsAccessorMutator(field.getName());
        JClassType type = field.getEnclosingType();
        if (exists(type, field, fieldName, true)) {
            return fieldName;
        }
        return null;
    }

    /**
     *
     * @param field
     * @return the name for the getter for the specified field or null if a
     *         getter can't be found.
     */
    private String getGetterName(JClassType type, JField field) {
        String methodBaseName = getMiddleNameForPrefixingAsAccessorMutator(field.getName());
        String fieldName;
        JType booleanType = null;
        try {
            booleanType = find(Boolean.class, getLogger(), context);
        } catch (UnableToCompleteException e) {
            // do nothing
        }
        if (field.getType().equals(JPrimitiveType.BOOLEAN) || field.getType().equals(booleanType)) {
            if (field instanceof DummyJField) {
                return ((DummyJField) field).getGetterMethod().getName();
            }
            fieldName = "is" + methodBaseName;
            if (exists(type, field, fieldName, false)) {
                return fieldName;
            }
            fieldName = "has" + methodBaseName;
            if (exists(type, field, fieldName, false)) {
                return fieldName;
            }
        }
        fieldName = "get" + methodBaseName;
        if (exists(type, field, fieldName, false)) {
            return fieldName;
        }
        return null;
    }

    private String getMiddleNameForPrefixingAsAccessorMutator(String fieldName) {
        if (javaBeansNamingConventionEnabled) {
            return getMiddleNameForPrefixingAsAccessorMutatorJavaBeansSpecCompliance(fieldName);
        }
        return upperCaseFirstChar(fieldName);
    }

    /**
     * Get the middle part of the method name in compliance with the naming convention in the JavaBeans API
     * specification.
     *
     * @param fieldName
     * @return
     */
    static String getMiddleNameForPrefixingAsAccessorMutatorJavaBeansSpecCompliance(String fieldName) {
        if (fieldName.length() > 1 && Character.isUpperCase(fieldName.charAt(1))) {
            return fieldName;
        }
        return fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
    }

    private String upperCaseFirstChar(String in) {
        if (in.length() == 1) {
            return in.toUpperCase();
        }
        return in.substring(0, 1).toUpperCase() + in.substring(1);
    }

    /**
     * Checks if hasAnnotations should be ignored based on JsonIgnore and XmlTransient
     *
     * @param hasAnnotations
     * @return true if hasAnnotations should be ignored
     * @see #isJsonIgnored(HasAnnotations)
     * @see #isXmlTransient(HasAnnotations)
     */
    private boolean isIgnored(HasAnnotations hasAnnotations) {
        return isJsonIgnored(hasAnnotations) || isXmlTransient(hasAnnotations);
    }

    /**
     * @param hasAnnotations
     * @return true if hasAnnotations is annotated with @JsonIgnore and its value is true
     * @see AnnotationUtils#getAnnotation(HasAnnotations, Class)
     */
    private boolean isJsonIgnored(HasAnnotations hasAnnotations) {
        return isJsonIgnored(getAnnotation(hasAnnotations, JsonIgnore.class));
    }

    /**
     * @param jsonIgnore
     * @return true if jsonIgnore.value() is true
     */
    private boolean isJsonIgnored(JsonIgnore jsonIgnore) {
        return jsonIgnore != null && jsonIgnore.value();
    }

    /**
     * @param hasAnnotations
     * @return true of hasAnnotations is annotated with XmlTransient
     * @see AnnotationUtils#getAnnotation(HasAnnotations, Class)
     */
    private boolean isXmlTransient(HasAnnotations hasAnnotations) {
        return getAnnotation(hasAnnotations, XmlTransient.class) != null;
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
        if (field instanceof DummyJField) {
            return true;
        }

        JType[] args;
        if (isSetter) {
            args = new JType[] { field.getType() };
        } else {
            args = new JType[] {};
        }
        JMethod m = type.findMethod(fieldName, args);
        if (null != m) {
            if (isIgnored(m)) {
                return false;
            }
            if (isSetter) {
                return true;
            }
            JClassType returnType = m.getReturnType().isClassOrInterface();
            JClassType fieldType = field.getType().isClassOrInterface();
            if (returnType == null || fieldType == null) {
                // at least one is a primitive type
                return m.getReturnType().equals(field.getType());
            }
            // both are non-primitives
            return returnType.isAssignableFrom(fieldType);
        }
        try {
            JType objectType = find(Object.class, getLogger(), context);
            JClassType superType = type.getSuperclass();
            if (!objectType.equals(superType)) {
                return exists(superType, field, fieldName, isSetter);
            }
        } catch (UnableToCompleteException e) {
            // do nothing
        }
        return false;
    }

    /**
     * Get {@link JsonProperty} from getter or setter. Annotation from setter is preferred to getter.
     *
     * @param getter
     * @param setter
     * @return
     */
    private JsonProperty getJsonPropertyFromGetterSetter(JMethod getter, JMethod setter) {
        JsonProperty setterProp = getAnnotation(setter, JsonProperty.class);
        return (null != setterProp) ? setterProp : getAnnotation(getter, JsonProperty.class);
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
        Map<String, JMethod> setters = new HashMap<String, JMethod>();

        JType booleanType = null;
        try {
            booleanType = find(Boolean.class, getLogger(), context);
        } catch (UnableToCompleteException e) {
            // do nothing
        }
        for (JMethod m : type.getInheritableMethods()) {
            if (m.getName().startsWith("set") && m.getParameterTypes().length == 1 &&
                m.getReturnType() == JPrimitiveType.VOID && !isIgnored(m)) {
                setters.put(m.getName().substring("set".length()), m);
            } else if (m.getName().startsWith("get") && m.getParameterTypes().length == 0 &&
                m.getReturnType() != JPrimitiveType.VOID && !isIgnored(m)) {
                getters.put(m.getName().substring("get".length()), m);
            } else if (m.getName().startsWith("is") && m.getParameterTypes().length == 0 &&
                (m.getReturnType() == JPrimitiveType.BOOLEAN || m.getReturnType().equals(booleanType)) &&
                !isIgnored(m)) {
                getters.put(m.getName().substring("is".length()), m);
            } else if (m.getName().startsWith("has") && m.getParameterTypes().length == 0 &&
                (m.getReturnType() == JPrimitiveType.BOOLEAN || m.getReturnType().equals(booleanType)) &&
                !isIgnored(m)) {
                getters.put(m.getName().substring("has".length()), m);
            }
        }
        for (Map.Entry<String, JMethod> entry : getters.entrySet()) {
            JMethod getter = entry.getValue();
            JMethod setter = setters.get(entry.getKey());

            if (null != setter && setter.getParameterTypes()[0].equals(getter.getReturnType())) {
                String name = entry.getKey().substring(0, 1).toLowerCase() + entry.getKey().substring(1);
                JField f = null;
                for (JField field : allFields) {
                    if (field.getName().equals(name)) {
                        f = field;
                        break;
                    }
                }

                if (f != null && isJsonIgnored(f)) {
                    continue;
                }

                JsonProperty propName = getJsonPropertyFromGetterSetter(getter, setter);

                // if have a field and an annotation from the getter/setter then use that annotation
                if (f != null) {
                    if (propName != null && !f.getName().equals(propName.value())) {
                        allFields.remove(f);
                        DummyJField dummy = new DummyJField(name, getter.getReturnType(), getter);
                        dummy.setAnnotation(propName);
                        allFields.add(dummy);
                    }
                } else {
                    DummyJField dummy = new DummyJField(name, getter.getReturnType(), getter);
                    if (getter.isAnnotationPresent(JsonProperty.class)) {
                        dummy.setAnnotation(getAnnotation(getter, JsonProperty.class));
                    }
                    allFields.add(dummy);
                }
            }
        }

        // remove fields annotated with JsonIgnore
        for (Iterator<JField> iter = allFields.iterator(); iter.hasNext(); ) {
            JField field = iter.next();
            if (isJsonIgnored(field)) {
                iter.remove();
            }
        }

        return allFields;
    }

    /**
     * Returns a list of all fields (non {@code transient} and not annotated with {@link XmlTransient}) in the
     * supplied type and all super classes.
     *
     * @param allFields
     * @param type
     * @return
     */
    private List<JField> getFields(List<JField> allFields, JClassType type) {
        JField[] fields = type.getFields();
        for (JField field : fields) {
            if (!field.isTransient() && !isXmlTransient(field)) {
                allFields.add(field);
            }
        }
        try {
            JType objectType = find(Object.class, getLogger(), context);
            if (!objectType.equals(type)) {
                JClassType superType = type.getSuperclass();
                return getFields(allFields, superType);
            }
        } catch (UnableToCompleteException e) {
            // do nothing
        }

        return allFields;
    }

    public static String getTypeInfoPropertyValue(JsonTypeInfo typeInfo) {
        if (typeInfo.include() == JsonTypeInfo.As.PROPERTY) {
            if (typeInfo.property() == null || "".equals(typeInfo.property())) {
                return typeInfo.use().getDefaultPropertyName();
            }
        }

        return typeInfo.property();
    }

    public static boolean isLeaf(JClassType source) {
        return !(source.getSubtypes() != null && source.getSubtypes().length > 0);
    }

    public static class Subtype implements Comparable<Subtype> {
        final String tag;
        final JClassType clazz;

        public Subtype(String tag, JClassType clazz) {
            this.tag = tag;
            this.clazz = clazz;
        }

        @Override
        public int compareTo(Subtype o) {
            return tag.compareTo(o.tag);
        }
    }

    private static Map<Class<?>, RestyJsonTypeIdResolver> sTypeIdResolverMap = null;
}
