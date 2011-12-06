/**
 * Copyright (C) 2009-2011 the original author or authors.
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
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.fusesource.restygwt.client.AbstractAsyncCallback;
import org.fusesource.restygwt.client.AbstractRequestCallback;
import org.fusesource.restygwt.client.Attribute;
import org.fusesource.restygwt.client.Defaults;
import org.fusesource.restygwt.client.Dispatcher;
import org.fusesource.restygwt.client.JSONP;
import org.fusesource.restygwt.client.Json;
import org.fusesource.restygwt.client.Json.Style;
import org.fusesource.restygwt.client.JsonCallback;
import org.fusesource.restygwt.client.JsonpMethod;
import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.Options;
import org.fusesource.restygwt.client.OverlayCallback;
import org.fusesource.restygwt.client.Resource;
import org.fusesource.restygwt.client.ResponseFormatException;
import org.fusesource.restygwt.client.RestService;
import org.fusesource.restygwt.client.RestServiceProxy;
import org.fusesource.restygwt.client.TextCallback;
import org.fusesource.restygwt.client.XmlCallback;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayBoolean;
import com.google.gwt.core.client.JsArrayInteger;
import com.google.gwt.core.client.JsArrayNumber;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JGenericType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.JParameter;
import com.google.gwt.core.ext.typeinfo.JPrimitiveType;
import com.google.gwt.core.ext.typeinfo.JType;
import com.google.gwt.core.ext.typeinfo.JTypeParameter;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.xml.client.Document;

/**
 *
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 *
 *         Updates: added automatically create resource from Path annotation,
 *         enhanced generics support
 * @author <a href="http://www.acuedo.com">Dave Finch</a>
 */
public class RestServiceClassCreator extends BaseSourceCreator {

    private static final String REST_SERVICE_PROXY_SUFFIX = "_Generated_RestServiceProxy_";

    private static final String METHOD_CLASS = Method.class.getName();
    private static final String RESOURCE_CLASS = Resource.class.getName();
    private static final String DISPATCHER_CLASS = Dispatcher.class.getName();
    private static final String DEFAULTS_CLASS = Defaults.class.getName();
    private static final String ABSTRACT_REQUEST_CALLBACK_CLASS = AbstractRequestCallback.class.getName();
    private static final String ABSTRACT_ASYNC_CALLBACK_CLASS = AbstractAsyncCallback.class.getName();
    private static final String JSON_PARSER_CLASS = JSONParser.class.getName();
    private static final String JSON_ARRAY_CLASS = JSONArray.class.getName();
    private static final String JSON_OBJECT_CLASS = JSONObject.class.getName();
    private static final String REQUEST_EXCEPTION_CLASS = RequestException.class.getName();
    private static final String RESPONSE_FORMAT_EXCEPTION_CLASS = ResponseFormatException.class.getName();
    private static final String JSONP_METHOD_CLASS = JsonpMethod.class.getName();

    /*
     * static class in which are some compile-time relevant infos.
     *
     * TODO (andi): too much flexibility and overhead with reflection here?
     */
    private static final Class<BindingDefaults> BINDING_DEFAULTS = BindingDefaults.class;

    private static final String METHOD_JSONP = "jsonp";
    private static final String METHOD_PUT = "put";
    private static final String METHOD_POST = "post";
    private static final String METHOD_OPTIONS = "options";
    private static final String METHOD_HEAD = "head";
    private static final String METHOD_GET = "get";
    private static final String METHOD_DELETE = "delete";

    private static final HashSet<String> REST_METHODS = new HashSet<String>(8);
    static {
        REST_METHODS.add(METHOD_DELETE);
        REST_METHODS.add(METHOD_GET);
        REST_METHODS.add(METHOD_HEAD);
        REST_METHODS.add(METHOD_OPTIONS);
        REST_METHODS.add(METHOD_POST);
        REST_METHODS.add(METHOD_PUT);
        REST_METHODS.add(METHOD_JSONP);
    }

    private JClassType XML_CALLBACK_TYPE;
    private JClassType METHOD_CALLBACK_TYPE;
    private JClassType TEXT_CALLBACK_TYPE;
    private JClassType JSON_CALLBACK_TYPE;
    private JClassType OVERLAY_CALLBACK_TYPE;
    private JClassType DOCUMENT_TYPE;
    private JClassType METHOD_TYPE;
    private JClassType STRING_TYPE;
    private JClassType JSON_VALUE_TYPE;
    private JClassType OVERLAY_VALUE_TYPE;
    private Set<JClassType> OVERLAY_ARRAY_TYPES;
    private Set<JClassType> QUERY_PARAM_LIST_TYPES;
    private JClassType REST_SERVICE_TYPE;
    private JsonEncoderDecoderInstanceLocator locator;

    public RestServiceClassCreator(TreeLogger logger, GeneratorContext context, JClassType source) throws UnableToCompleteException {
        super(logger, context, source, REST_SERVICE_PROXY_SUFFIX);
    }

    @Override
    protected ClassSourceFileComposerFactory createComposerFactory() {
    	String parameters = "";
    	if(source instanceof JGenericType)
    	{
    		JGenericType gtype = (JGenericType)source;
			StringBuilder builder = new StringBuilder();
			builder.append("<");
			boolean first = true;
   			for(JTypeParameter arg : gtype.getTypeParameters())
   			{
   				if(!first)
   					builder.append(",");
   				builder.append(arg.getName());
   				builder.append(" extends ");
	   			builder.append(arg.getFirstBound().getParameterizedQualifiedSourceName());
	   			first = false;
   			}
   			builder.append(">");
   			parameters = builder.toString();
     	}
    	
        ClassSourceFileComposerFactory composerFactory = new ClassSourceFileComposerFactory(packageName, shortName + parameters);
        composerFactory.addImplementedInterface(source.getParameterizedQualifiedSourceName());
        composerFactory.addImplementedInterface(RestServiceProxy.class.getName());
        return composerFactory;
    }

    @Override
    protected void generate() throws UnableToCompleteException {

        if (source.isInterface() == null) {
            error("Type is not an interface.");
        }

        locator = new JsonEncoderDecoderInstanceLocator(context, logger);

        this.XML_CALLBACK_TYPE = find(XmlCallback.class);
        this.METHOD_CALLBACK_TYPE = find(MethodCallback.class);
        this.TEXT_CALLBACK_TYPE = find(TextCallback.class);
        this.JSON_CALLBACK_TYPE = find(JsonCallback.class);
        this.OVERLAY_CALLBACK_TYPE = find(OverlayCallback.class);
        this.DOCUMENT_TYPE = find(Document.class);
        this.METHOD_TYPE = find(Method.class);
        this.STRING_TYPE = find(String.class);
        this.JSON_VALUE_TYPE = find(JSONValue.class);
        this.OVERLAY_VALUE_TYPE = find(JavaScriptObject.class);
        this.OVERLAY_ARRAY_TYPES = new HashSet<JClassType>();
        this.OVERLAY_ARRAY_TYPES.add(find(JsArray.class));
        this.OVERLAY_ARRAY_TYPES.add(find(JsArrayBoolean.class));
        this.OVERLAY_ARRAY_TYPES.add(find(JsArrayInteger.class));
        this.OVERLAY_ARRAY_TYPES.add(find(JsArrayNumber.class));
        this.OVERLAY_ARRAY_TYPES.add(find(JsArrayString.class));
        this.QUERY_PARAM_LIST_TYPES = new HashSet<JClassType>();
        this.QUERY_PARAM_LIST_TYPES.add(find(List.class));
        this.QUERY_PARAM_LIST_TYPES.add(find(Set.class));
		this.REST_SERVICE_TYPE = find(RestService.class);
		
        String path = null;
        Path pathAnnotation = source.getAnnotation(Path.class);
        if (pathAnnotation != null) {
            path = pathAnnotation.value();
        }

        RemoteServiceRelativePath relativePath = source.getAnnotation(RemoteServiceRelativePath.class);
        if (relativePath != null) {
            path = relativePath.value();
        }

        if (path == null) {
            p("private " + RESOURCE_CLASS + " resource = new " + RESOURCE_CLASS + "(" + DEFAULTS_CLASS + ".getServiceRoot());");
        } else {
            p("private " + RESOURCE_CLASS + " resource = new " + RESOURCE_CLASS + "(" + DEFAULTS_CLASS + ".getServiceRoot()).resolve("+quote(path)+");");
        }
        p();

        p("public void setResource(" + RESOURCE_CLASS + " resource) {").i(1);
        {
            p("this.resource = resource;");
        }
        i(-1).p("}");

        p("public " + RESOURCE_CLASS + " getResource() {").i(1);
        {
            p("return this.resource;");
        }
        i(-1).p("}");


        Options options = source.getAnnotation(Options.class);
        if( options!=null && options.dispatcher()!=Dispatcher.class ) {
            p("private " + DISPATCHER_CLASS + " dispatcher = "+options.dispatcher().getName()+".INSTANCE;");
        } else {
            p("private " + DISPATCHER_CLASS + " dispatcher = "+DEFAULTS_CLASS+".getDispatcher();");
        }

        p();
        p("public void setDispatcher(" + DISPATCHER_CLASS + " dispatcher) {").i(1);
        {
            p("this.dispatcher = dispatcher;");
        }
        i(-1).p("}");

        p();
        p("public " + DISPATCHER_CLASS + " getDispatcher() {").i(1);
        {
            p("return this.dispatcher;");
        }
        i(-1).p("}");

        for (JMethod method : source.getInheritableMethods()) {
        	JClassType iface = method.getReturnType().isInterface();
        	if(iface != null && REST_SERVICE_TYPE.isAssignableFrom(iface))
        		writeSubresourceLocatorImpl(method);
        	else
            	writeMethodImpl(method);
        }
    }

    private String quote(String path) {
        // TODO: unlikely to occur. but we should escape chars like newlines..
        return "\"" + path + "\"";
    }

    private boolean isOverlayArrayType(JClassType type) {
        for (JClassType arrayType : OVERLAY_ARRAY_TYPES) {
            if (type.isAssignableTo(arrayType)) {
                return true;
            }
        }
        return false;
    }

    private boolean isQueryParamListType(JClassType type) {
        if (type.isParameterized() == null) {
            return false;
        }
        for (JClassType listType : QUERY_PARAM_LIST_TYPES) {
            if (type.isAssignableTo(listType)) {
                return true;
            }
        }
        return false;
    }
	
    private void writeSubresourceLocatorImpl(JMethod method) throws UnableToCompleteException
    {
    	JClassType iface = method.getReturnType().isInterface();
    	if(iface == null || !REST_SERVICE_TYPE.isAssignableFrom(iface)) {
    		error("Invalid subresource locator method. Method must have return type of an interface that extends RestService: " + method.getReadableDeclaration());
    	}
    	
        Path pathAnnotation = method.getAnnotation(Path.class);
        if (pathAnnotation == null) {
        	error("Invalid subresource locator method. Method must have @Path annotation: " + method.getReadableDeclaration());
        }
        String pathExpression = wrap(pathAnnotation.value());

        for (JParameter arg : method.getParameters()) {
            PathParam paramPath = arg.getAnnotation(PathParam.class);
            if (paramPath != null) {
                pathExpression = pathExpression.replaceAll(Pattern.quote("{" + paramPath.value() + "}"), "\"+" + toStringExpression(arg) + "+\"");
            }
        }


        p(method.getReadableDeclaration(false, false, false, false, true) + " {").i(1);
        {
        	JType type = method.getReturnType();
        	String name;
        	if(type instanceof JClassType)
        	{
                JClassType restService = (JClassType)type;
                RestServiceClassCreator generator = new RestServiceClassCreator(logger, context, restService);
                name = generator.create();
        	}
        	else
        	{
        		throw new UnsupportedOperationException("Subresource method may not return: " + type);
        	}
        	p(method.getReturnType().getQualifiedSourceName() + " __subresource = new " + name + "();");
        	p("((" + RestServiceProxy.class.getName() + ")__subresource).setResource(getResource().resolve(" + pathExpression + "));");
        	p("return __subresource;");
        }
        i(-1).p("}");
    }
    
    private void writeMethodImpl(JMethod method) throws UnableToCompleteException {
        if (method.getReturnType().isPrimitive() != JPrimitiveType.VOID) {
            error("Invalid rest method. Method must have void return type: " + method.getReadableDeclaration());
        }

        Json jsonAnnotation = source.getAnnotation(Json.class);
        final Style classStyle = jsonAnnotation != null ? jsonAnnotation.style() : Style.DEFAULT;

        Options classOptions = source.getAnnotation(Options.class);
        Options options = method.getAnnotation(Options.class);

        p(method.getReadableDeclaration(false, false, false, false, true) + " {").i(1);
        {
            String restMethod = getRestMethod(method);
            LinkedList<JParameter> args = new LinkedList<JParameter>(Arrays.asList(method.getParameters()));

            // the last arg should be the callback.
            if (args.isEmpty()) {
                error("Invalid rest method. Method must declare at least a callback argument: " + method.getReadableDeclaration());
            }
            JParameter callbackArg = args.removeLast();
            JClassType callbackType = callbackArg.getType().isClassOrInterface();
            JClassType methodCallbackType = METHOD_CALLBACK_TYPE;
            if (callbackType == null || !callbackType.isAssignableTo(methodCallbackType)) {
                error("Invalid rest method. Last argument must be a " + methodCallbackType.getName() + " type: " + method.getReadableDeclaration());
            }
            JClassType resultType = getCallbackTypeGenericClass(callbackType);

            String pathExpression = null;
            Path pathAnnotation = method.getAnnotation(Path.class);
            if (pathAnnotation != null) {
                pathExpression = wrap(pathAnnotation.value());
            }

            JParameter contentArg = null;
            HashMap<String, JParameter> queryParams = new HashMap<String, JParameter>();
            HashMap<String, JParameter> headerParams = new HashMap<String, JParameter>();

            for (JParameter arg : args) {
                PathParam paramPath = arg.getAnnotation(PathParam.class);
                if (paramPath != null) {
                    if (pathExpression == null) {
                        error("Invalid rest method.  Invalid @PathParam annotation. Method is missing the @Path annotation: " + method.getReadableDeclaration());
                    }
                    pathExpression = pathExpression.replaceAll(Pattern.quote("{" + paramPath.value() + "}"), "\"+" + toStringExpression(arg) + "+\"");
                    if (arg.getAnnotation(Attribute.class) != null) {
                        // allow part of the arg-object participate in as PathParam and the object goes over the wire
                        contentArg = arg;
                    }
                    continue;
                }

                QueryParam queryParam = arg.getAnnotation(QueryParam.class);
                if (queryParam != null) {
                    queryParams.put(queryParam.value(), arg);
                    continue;
                }

                HeaderParam headerParam = arg.getAnnotation(HeaderParam.class);
                if (headerParam != null) {
                    headerParams.put(headerParam.value(), arg);
                    continue;
                }

                if (contentArg != null) {
                    error("Invalid rest method. Only one content parameter is supported: " + method.getReadableDeclaration());
                }
                contentArg = arg;
            }

            String acceptTypeBuiltIn = null;
            if (callbackType.equals(TEXT_CALLBACK_TYPE)) {
                acceptTypeBuiltIn = "CONTENT_TYPE_TEXT";
            } else if (callbackType.equals(JSON_CALLBACK_TYPE)) {
                acceptTypeBuiltIn = "CONTENT_TYPE_JSON";
            } else if (callbackType.isAssignableTo(OVERLAY_CALLBACK_TYPE)) {
                acceptTypeBuiltIn = "CONTENT_TYPE_JSON";
            } else if (callbackType.equals(XML_CALLBACK_TYPE)) {
                acceptTypeBuiltIn = "CONTENT_TYPE_XML";
            }

            p("final " + METHOD_CLASS + " __method =");

            p("this.resource");
            if (pathExpression != null) {
                p(".resolve(" + pathExpression + ")");
            }
            for (Map.Entry<String, JParameter> entry : queryParams.entrySet()) {
                String expr = entry.getValue().getName();
                JClassType type = entry.getValue().getType().isClassOrInterface();
                if (type != null && isQueryParamListType(type)) {
                    p(".addQueryParams(" + wrap(entry.getKey()) + ", " +
                      toIteratedStringExpression(entry.getValue()) + ")");
                } else {
                    p(".addQueryParam(" + wrap(entry.getKey()) + ", " +
                      toStringExpression(entry.getValue().getType(), expr) + ")");
                }
            }
            // example: .get()
            p("." + restMethod + "();");

            // Handle JSONP specific configuration...
            JSONP jsonpAnnotation = method.getAnnotation(JSONP.class);

            final boolean isJsonp = restMethod.equals(METHOD_JSONP) && jsonpAnnotation!=null;
            if( isJsonp ) {
                if( jsonpAnnotation.callbackParam().length() > 0 ) {
                    p("(("+JSONP_METHOD_CLASS+")__method).callbackParam("+wrap(jsonpAnnotation.callbackParam())+");");
                }
                if( jsonpAnnotation.failureCallbackParam().length() > 0 ) {
                    p("(("+JSONP_METHOD_CLASS+")__method).failureCallbackParam("+wrap(jsonpAnnotation.failureCallbackParam())+");");
                }
            }

            // configure the dispatcher
            if( options!=null && options.dispatcher()!=Dispatcher.class ) {
                // use the dispatcher configured for the method.
                p("__method.setDispatcher("+options.dispatcher().getName()+".INSTANCE);");
            } else {
                // use the default dispatcher configured for the service..
                p("__method.setDispatcher(this.dispatcher);");
            }

            // configure the expected statuses..
            if( options!=null && options.expect().length!=0 ) {
                // Using method level defined expected status
                p("__method.expect("+join(options.expect(), ", ")+");");
            } else if( classOptions!=null && classOptions.expect().length!=0 ) {
                // Using class level defined expected status
                p("__method.expect("+join(classOptions.expect(), ", ")+");");
            }

            // configure the timeout
            if( options!=null && options.timeout() >= 0 ) {
                // Using method level defined value
                p("__method.timeout("+options.timeout()+");");
            } else if( classOptions!=null && classOptions.timeout() >= 0 ) {
                // Using class level defined value
                p("__method.timeout("+classOptions.timeout()+");");
            }

            if(jsonpAnnotation == null) {
                Produces producesAnnotation = findAnnotationOnMethodOrEnclosingType(method, Produces.class);
                if (producesAnnotation != null) {
                    p("__method.header(" + RESOURCE_CLASS + ".HEADER_ACCEPT, "+wrap(producesAnnotation.value()[0])+");");
                } else {
                    // set the default accept header....
                    if (acceptTypeBuiltIn != null) {
                        p("__method.header(" + RESOURCE_CLASS + ".HEADER_ACCEPT, " + RESOURCE_CLASS + "." + acceptTypeBuiltIn + ");");
                    } else {
                        p("__method.header(" + RESOURCE_CLASS + ".HEADER_ACCEPT, " + RESOURCE_CLASS + ".CONTENT_TYPE_JSON);");
                    }
                }

                Consumes consumesAnnotation = findAnnotationOnMethodOrEnclosingType(method, Consumes.class);
                if (consumesAnnotation != null) {
                    p("__method.header(" + RESOURCE_CLASS + ".HEADER_CONTENT_TYPE, "+wrap(consumesAnnotation.value()[0])+");");
                }

                // and set the explicit headers now (could override the accept header)
                for (Map.Entry<String, JParameter> entry : headerParams.entrySet()) {
                    String expr = entry.getValue().getName();
                    p("__method.header(" + wrap(entry.getKey()) + ", " + toStringExpression(entry.getValue().getType(), expr) + ");");
                }
            }

            if (contentArg != null) {
                if (contentArg.getType() == STRING_TYPE) {
                    p("__method.text(" + contentArg.getName() + ");");
                } else if (contentArg.getType() == JSON_VALUE_TYPE) {
                    p("__method.json(" + contentArg.getName() + ");");
                } else if (contentArg.getType().isClass() != null &&
                           isOverlayArrayType(contentArg.getType().isClass())) {
                    p("__method.json(new " + JSON_ARRAY_CLASS + "(" + contentArg.getName() + "));");
                } else if (contentArg.getType().isClass() != null &&
                           contentArg.getType().isClass().isAssignableTo(OVERLAY_VALUE_TYPE)) {
                    p("__method.json(new " + JSON_OBJECT_CLASS + "(" + contentArg.getName() + "));");
                } else if (contentArg.getType() == DOCUMENT_TYPE) {
                    p("__method.xml(" + contentArg.getName() + ");");
                } else {
                    JClassType contentClass = contentArg.getType().isClass();
                    if (contentClass == null) {
                        contentClass = contentArg.getType().isClassOrInterface();
                        if (!locator.isCollectionType(contentClass)) {
                            error("Content argument must be a class.");
                        }
                    }

                    jsonAnnotation = contentArg.getAnnotation(Json.class);
                    Style style = jsonAnnotation != null ? jsonAnnotation.style() : classStyle;

                    // example:
                    // .json(Listings$_Generated_JsonEncoder_$.INSTANCE.encode(arg0)
                    // )
                    p("__method.json(" + locator.encodeExpression(contentClass, contentArg.getName(), style) + ");");
                }
            }


            List<AnnotationResolver> annotationResolvers = getAnnotationResolvers(context, logger);
            logger.log(TreeLogger.DEBUG, "found " + annotationResolvers.size() + " additional AnnotationResolvers");

            for (AnnotationResolver a : annotationResolvers) {
                logger.log(TreeLogger.DEBUG, "(" + a.getClass().getName() + ") resolve `" + source.getName()
                        + "#" + method.getName() + "´ ...");
                final Map<String, String[]> addDataParams = a.resolveAnnotation(logger, source, method, restMethod);

                if (addDataParams != null) {
                    for (String s : addDataParams.keySet()) {
                        final StringBuilder sb = new StringBuilder();
                        final List<String> classList = Arrays.asList(addDataParams.get(s));

                        sb.append("[");
                        for (int i = 0; i < classList.size(); ++i) {
                            sb.append("\\\"").append(classList.get(i)).append("\\\"");

                            if ((i+1) <  classList.size()) {
                                sb.append(",");
                            }
                        }
                        sb.append("]");

                        logger.log(TreeLogger.DEBUG, "add call with (\"" + s + "\", \"" +
                                sb.toString() + "\")");
                        p("__method.addData(\"" + s + "\", \"" + sb.toString() + "\");");
                    }
                }
            }


            if (acceptTypeBuiltIn != null) {
                p("__method.send(" + callbackArg.getName() + ");");
            } else if ( isJsonp ){
                    p("((" + JSONP_METHOD_CLASS + ")__method).send(new " + ABSTRACT_ASYNC_CALLBACK_CLASS + "<" + resultType.getParameterizedQualifiedSourceName() + ">((" + JSONP_METHOD_CLASS + ")__method, "
                                    + callbackArg.getName() + ") {").i(1);
                    {
                        p("protected " + resultType.getParameterizedQualifiedSourceName() + " parseResult(" + JSON_OBJECT_CLASS + " result) throws Exception {").i(1);
                        {
                            if(resultType.getParameterizedQualifiedSourceName().equals("java.lang.Void")) {
                                p("return (java.lang.Void) null;");
                            }
                            else {
                                p("try {").i(1);
                                {
                                    jsonAnnotation = method.getAnnotation(Json.class);
                                    Style style = jsonAnnotation != null ? jsonAnnotation.style() : classStyle;
                                    p("return " + locator.decodeExpression(resultType, "result", style) + ";");
                                }
                                i(-1).p("} catch (Throwable __e) {").i(1);
                                {
                                    p("throw new " + RESPONSE_FORMAT_EXCEPTION_CLASS + "(\"Response was NOT a valid JSON document\", __e);");
                                }
                                i(-1).p("}");
                            }
                        }
                        i(-1).p("}");
                    }
                    i(-1).p("});");
            } else {
                p("try {").i(1);
                {
                    p("__method.send(new " + ABSTRACT_REQUEST_CALLBACK_CLASS + "<" + resultType.getParameterizedQualifiedSourceName() + ">(__method, "
                                    + callbackArg.getName() + ") {").i(1);
                    {
                        p("protected " + resultType.getParameterizedQualifiedSourceName() + " parseResult() throws Exception {").i(1);
                        {
                            if(resultType.getParameterizedQualifiedSourceName().equals("java.lang.Void")) {
                                p("return (java.lang.Void) null;");
                            }
                            else {
                                p("try {").i(1);
                                {
                                    jsonAnnotation = method.getAnnotation(Json.class);
                                    Style style = jsonAnnotation != null ? jsonAnnotation.style() : classStyle;
                                    p("return " + locator.decodeExpression(resultType, JSON_PARSER_CLASS + ".parse(__method.getResponse().getText())", style) + ";");
                                }
                                i(-1).p("} catch (Throwable __e) {").i(1);
                                {
                                    p("throw new " + RESPONSE_FORMAT_EXCEPTION_CLASS + "(\"Response was NOT a valid JSON document\", __e);");
                                }
                                i(-1).p("}");
                            }
                        }
                        i(-1).p("}");
                    }
                    i(-1).p("});");
                }
                i(-1).p("} catch (" + REQUEST_EXCEPTION_CLASS + " __e) {").i(1);
                {
                    p(callbackArg.getName() + ".onFailure(__method,__e);");
                }
                i(-1).p("}");
            }
        }
        i(-1).p("}");
    }

    private <T extends Annotation> T findAnnotationOnMethodOrEnclosingType(final JMethod method, final Class<T> annotationType) {
        T annotation = method.getAnnotation(annotationType);
        if (annotation == null) {
            annotation = method.getEnclosingType().getAnnotation(annotationType);
        }
        return annotation;
    }

    protected String toStringExpression(JParameter arg) {
        Attribute attribute = arg.getAnnotation(Attribute.class);
        if(attribute != null){
            return arg.getName() + "." + attribute.value();
        }
        return toStringExpression(arg.getType(), arg.getName());
    }

    protected String toStringExpression(JType type, String expr) {
        if (type.isPrimitive() != null) {
            return "\"\"+" + expr;
        }
        if (STRING_TYPE == type) {
            return expr;
        }
        if (type.isClass() != null &&
            isOverlayArrayType(type.isClass())) {
          return "(new " + JSON_ARRAY_CLASS + "(" + expr + ")).toString()";
        }
        if (type.isClass() != null &&
            OVERLAY_VALUE_TYPE.isAssignableFrom(type.isClass())) {
          return "(new " + JSON_OBJECT_CLASS + "(" + expr + ")).toString()";
        }

        return String.format("%s != null ? %s.toString() : null", expr, expr);
    }

    protected String toIteratedStringExpression(JParameter arg) {
        StringBuilder result = new StringBuilder();
        result.append("new org.fusesource.restygwt.client.StringIterable (")
            .append(arg.getName()).append(")");

        return result.toString();
    }

    private JClassType getCallbackTypeGenericClass(final JClassType callbackType) throws UnableToCompleteException {
        return branch("getCallbackTypeGenericClass()", new Branch<JClassType>() {
            public JClassType execute() throws UnableToCompleteException {

                for (JMethod method : callbackType.getOverridableMethods()) {
                    debug("checking method: " + method.getName());
                    if (method.getName().equals("onSuccess")) {
                        JParameter[] parameters = method.getParameters();
                        debug("checking method params: " + parameters.length);
                        if (parameters.length == 2) {
                            debug("checking first param: " + parameters[0].getType());
                            if (parameters[0].getType() == METHOD_TYPE) {
                                debug("checking 2nd param: " + parameters[1].getType());
                                JType param2Type = parameters[1].getType();
                                JClassType type = param2Type.isClassOrInterface();
                                if (type == null) {
                                    error("The type of the callback not supported: " + param2Type.getJNISignature());
                                }
                                debug("match: " + type);
                                return type;
                            }
                        }
                    }
                }
                error("The type of the callback could not be determined: " + callbackType.getParameterizedQualifiedSourceName());
                return null;

            }
        });
    }

    private String getRestMethod(JMethod method) throws UnableToCompleteException {
        String restMethod = null;
        if (method.getAnnotation(DELETE.class) != null) {
            restMethod = METHOD_DELETE;
        } else if (method.getAnnotation(GET.class) != null) {
            restMethod = METHOD_GET;
        } else if (method.getAnnotation(HEAD.class) != null) {
            restMethod = METHOD_HEAD;
        } else if (method.getAnnotation(OPTIONS.class) != null) {
            restMethod = METHOD_OPTIONS;
        } else if (method.getAnnotation(POST.class) != null) {
            restMethod = METHOD_POST;
        } else if (method.getAnnotation(PUT.class) != null) {
            restMethod = METHOD_PUT;
        } else if (method.getAnnotation(JSONP.class) != null) {
            restMethod = METHOD_JSONP;
        } else {
            restMethod = method.getName();
            if (!REST_METHODS.contains(restMethod)) {
                error("Invalid rest method. It must either have a lower case rest method name or have a javax rs method annotation: " + method.getReadableDeclaration());
            }
        }
        return restMethod;
    }

    /**
     * access additional AnnotationResolvers possibly added by
     *
     * {@link BindingDefaults#addAnnotationResolver(AnnotationResolver)}
     * @return
     */
    @SuppressWarnings("unchecked")
    private List<AnnotationResolver> getAnnotationResolvers(final GeneratorContext context, final TreeLogger logger) {
        java.lang.reflect.Method m = null;
        ArrayList args = new ArrayList();
        ArrayList types = new ArrayList();

        types.add(GeneratorContext.class);
        args.add(context);
        types.add(TreeLogger.class);
        args.add(logger);

        Object[] argValues = args.toArray();
        Class[] argtypes = (Class[]) types.toArray(new Class[argValues.length]);

        try {
             m = BINDING_DEFAULTS.getMethod("getAnnotationResolvers", argtypes);
        } catch (SecurityException e) {
            throw new RuntimeException("could not call method `getAnnotationResolvers´ on "
                    + BINDING_DEFAULTS, e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("could not resolve method `getAnnotationResolvers´ on "
                    + BINDING_DEFAULTS, e);
        }

        List<AnnotationResolver> l = new ArrayList<AnnotationResolver>();
        try {
             Object[] params = new Object[]{context};

            l = (List<AnnotationResolver>) m.invoke(null, context, logger);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("could not call method `getAnnotationResolvers´ on "
                    + BINDING_DEFAULTS, e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("could not call method `getAnnotationResolvers´ on "
                    + BINDING_DEFAULTS, e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException("could not call method `getAnnotationResolvers´ on "
                    + BINDING_DEFAULTS, e);
        }

        return l;
    }
}
