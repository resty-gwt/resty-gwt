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
package org.fusesource.restygwt.rebind;

import java.util.ArrayList;
import java.util.List;

import org.fusesource.restygwt.client.Json;
import org.fusesource.restygwt.client.Json.Style;

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JField;
import com.google.gwt.core.ext.typeinfo.JPrimitiveType;
import com.google.gwt.core.ext.typeinfo.JType;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;

/**
 * 
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 * 
 * Updates:
 * added getter & setter support, enhanced generics support
 * @author <a href="http://www.acuedo.com">Dave Finch</a>
 */
public class JsonEncoderDecoderClassCreator extends BaseSourceCreator {
    private static final String JSON_ENCODER_SUFFIX = "_Generated_JsonEncoderDecoder_";

	private String JSON_ENCODER_DECODER_CLASS = JsonEncoderDecoderInstanceLocator.JSON_ENCODER_DECODER_CLASS;
	private static final String JSON_VALUE_CLASS = JSONValue.class.getName();
	private static final String JSON_OBJECT_CLASS = JSONObject.class.getName();

	JsonEncoderDecoderInstanceLocator locator;
	
    public JsonEncoderDecoderClassCreator(TreeLogger logger, GeneratorContext context, JClassType source) throws UnableToCompleteException {
        super(logger, context, source, JSON_ENCODER_SUFFIX);
    }

    protected ClassSourceFileComposerFactory createComposerFactory() {
        ClassSourceFileComposerFactory composerFactory = new ClassSourceFileComposerFactory(packageName, shortName);
        composerFactory.setSuperclass(JSON_ENCODER_DECODER_CLASS + "<" + source.getParameterizedQualifiedSourceName() + ">");
        return composerFactory;
    }

    public void generate() throws UnableToCompleteException {
        
    	locator = new JsonEncoderDecoderInstanceLocator(context, logger);
    	
        JClassType soruceClazz = source.isClass();
        if (soruceClazz == null) {
            error("Type is not a class");
        }
        if (!soruceClazz.isDefaultInstantiable()) {
            error("No default constuctor");
        }

        Json jsonAnnotation = source.getAnnotation(Json.class);
        final Style classStyle = jsonAnnotation!=null ? jsonAnnotation.style() : Style.DEFAULT;

        p();
        p("public static final " + shortName + " INSTANCE = new " + shortName + "();");
        p();

        p("public " + JSON_VALUE_CLASS + " encode(" + source.getParameterizedQualifiedSourceName() + " value) {").i(1);
        {
        	p(JSON_OBJECT_CLASS + " rc = new " + JSON_OBJECT_CLASS + "();");

            for (final JField field : getFields(source)) {

                final String getterName = getGetterName(field);
                
                // If can ignore some fields right off the back..
                if (getterName == null && (field.isStatic() || field.isFinal() || field.isTransient())) {
                    continue;
                }
                
                branch("Processing field: " + field.getName(), new Branch<Void>(){
                    public Void execute() throws UnableToCompleteException {
                        // TODO: try to get the field with a setter or JSNI
                        if (getterName != null || field.isDefaultAccess() || field.isProtected() || field.isPublic()) {

                            String name = field.getName();
                            
                            String fieldExpr = "value." + name;
                            if(getterName != null){
                                fieldExpr = "value." + getterName+"()";
                            }
                            
                            Json jsonAnnotation = field.getAnnotation(Json.class);
                            Style style = jsonAnnotation!=null ? jsonAnnotation.style() : classStyle;
                            
                            String expression = locator.encodeExpression(field.getType(), fieldExpr, style);

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
            p("" + source.getParameterizedQualifiedSourceName() + " rc = new " + source.getParameterizedQualifiedSourceName() + "();");
            for (final JField field : getFields(source)) {

                final String setterName = getSetterName(field);
                
                // If can ignore some fields right off the back..
                if (setterName == null && (field.isStatic() || field.isFinal() || field.isTransient())) {
                    continue;
                }
                
                branch("Processing field: " + field.getName(), new Branch<Void>(){
                    public Void execute() throws UnableToCompleteException {

                        // TODO: try to set the field with a setter or JSNI
                       if (setterName != null || field.isDefaultAccess() || field.isProtected() || field.isPublic()) {

                            Json jsonAnnotation = field.getAnnotation(Json.class);
                            Style style = jsonAnnotation!=null ? jsonAnnotation.style() : classStyle;

                            String name = field.getName();
                            String expression = locator.decodeExpression(field.getType(), "object.get(" + wrap(name) + ")", style);
                        	
                        	if(setterName != null){
                                p("rc." + setterName + "(" + expression + ");");
                        	}else{
                                p("rc." + name + "=" + expression + ";");
                        	}
                        	
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

    /**
     * 
     * @param field
     * @return the name for the setter for the specified field or null if a setter can't be found.
     */
    private String getSetterName( JField field){
        String fieldName = field.getName();
        fieldName = "set"+upperCaseFirstChar(fieldName);
        JClassType type = field.getEnclosingType();
        if(exists(type, field, fieldName, true)){
            return fieldName;
        }else{
            return null;
        }
    }
    
    /**
     * 
     * @param field
     * @return the name for the getter for the specified field or null if a getter can't be found.
     */
    private String getGetterName( JField field){
        String fieldName = field.getName();
        JType booleanType = null;
        try{
            booleanType = find(Boolean.class);
        }catch(UnableToCompleteException e){
            //do nothing
        }
        if(field.getType().equals(JPrimitiveType.BOOLEAN) || field.getType().equals(booleanType)){
            fieldName = "is"+upperCaseFirstChar(fieldName);
        }else{
            fieldName = "get"+upperCaseFirstChar(fieldName);
        }
        JClassType type = field.getEnclosingType();
        if(exists(type, field, fieldName, false)){
            return fieldName;
        }else{
            return null;
        }
    }
    
    private String upperCaseFirstChar(String in){
        if(in.length()==1){
            return in.toUpperCase();
        }else{
            return in.substring(0, 1).toUpperCase()+in.substring(1);
        }
    }
    
    /**
     * checks whether a getter or setter exists on the specified 
     * type or any of its super classes excluding Object.
     * 
     * @param type
     * @param field
     * @param fieldName
     * @param isSetter
     * @return
     */
    private boolean exists(JClassType type, JField field, String fieldName, boolean isSetter){
        JType[] args = null;
        if(isSetter){
            args = new JType[]{field.getType()};
        }else{
            args = new JType[]{};
        }
        
        if(null != type.findMethod(fieldName, args)){
            return true;
        }else{
            try{
                JType objectType = find(Object.class);
                JClassType superType = type.getSuperclass();
                if(!objectType.equals(superType)){
                    return exists(superType, field, fieldName, isSetter);
                }
            }catch(UnableToCompleteException e){
                //do nothing
            }
        }
        return false;
    }
    
    /**
     * Inspects the supplied type and all super classes
     *  up to but excluding Object and returns a list of 
     *  all fields found in these classes.
     * 
     * @param type
     * @return
     */
    private List < JField > getFields( JClassType type){
        return getFields(new ArrayList < JField >(), type);
    }

    private List < JField > getFields(List < JField > allFields, JClassType type){
        JField[] fields = type.getFields();
        for (JField field : fields) {
            allFields.add(field);
        }
        try{
            JType objectType = find(Object.class);
            JClassType superType = type.getSuperclass();
            if(!objectType.equals(superType)){
                return getFields(allFields, superType);
            }
        }catch(UnableToCompleteException e){
            //do nothing
        }
        return allFields;
    }
}