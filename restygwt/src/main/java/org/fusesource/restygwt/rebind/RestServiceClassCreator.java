/**
 * Copyright (C) 2009-2015 the original author or authors.
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

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.fusesource.restygwt.client.AbstractAsyncCallback;
import org.fusesource.restygwt.client.AbstractRequestCallback;
import org.fusesource.restygwt.client.Attribute;
import org.fusesource.restygwt.client.Defaults;
import org.fusesource.restygwt.client.Dispatcher;
import org.fusesource.restygwt.client.FormPostContent;
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
import org.fusesource.restygwt.client.ServiceRoots;
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
import com.google.gwt.core.ext.typeinfo.HasAnnotations;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JGenericType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.JParameter;
import com.google.gwt.core.ext.typeinfo.JPrimitiveType;
import com.google.gwt.core.ext.typeinfo.JType;
import com.google.gwt.core.ext.typeinfo.JTypeParameter;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.jsonp.client.JsonpRequest;
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

    private static final String PLAIN_TEXT_AUTODETECTION_CONFIGURATION_PROPERTY_NAME = "restygwt.autodetect.plainText";

    private static final String METHOD_CLASS = Method.class.getName();
    private static final String RESOURCE_CLASS = Resource.class.getName();
    private static final String DISPATCHER_CLASS = Dispatcher.class.getName();
    private static final String DEFAULTS_CLASS = Defaults.class.getName();
    private static final String ABSTRACT_REQUEST_CALLBACK_CLASS = AbstractRequestCallback.class.getName();
    private static final String ABSTRACT_ASYNC_CALLBACK_CLASS = AbstractAsyncCallback.class.getName();
    private static final String JSON_PARSER_CLASS = JSONParser.class.getName();
    private static final String JSON_ARRAY_CLASS = JSONArray.class.getName();
    private static final String JSON_OBJECT_CLASS = JSONObject.class.getName();
    private static final String JSON_VALUE_CLASS = JSONValue.class.getName();
    private static final String JSON_STRING_CLASS = JSONString.class.getName();
    private static final String REQUEST_EXCEPTION_CLASS = RequestException.class.getName();
    private static final String RESPONSE_FORMAT_EXCEPTION_CLASS = ResponseFormatException.class.getName();
    private static final String JSONP_METHOD_CLASS = JsonpMethod.class.getName();
    private static final String FORM_POST_CONTENT_CLASS = FormPostContent.class.getName();
    private static final String SERVICE_ROOTS_CLASS = ServiceRoots.class.getName();

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

    /**
     * Set of allowed request methods.
     */
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
    private EncoderDecoderLocator locator;

    private boolean autodetectTypeForStrings;

    public RestServiceClassCreator(TreeLogger logger, GeneratorContext context, JClassType source) {
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
            getLogger().log(ERROR, "Type is not an interface.");
            throw new UnableToCompleteException();
        }

        // true, if plain text autodetection for strings should be used
        autodetectTypeForStrings = getBooleanProperty(getLogger(), context.getPropertyOracle(), PLAIN_TEXT_AUTODETECTION_CONFIGURATION_PROPERTY_NAME, false);

        locator = EncoderDecoderLocatorFactory.getEncoderDecoderInstanceLocator(context, getLogger());

        this.XML_CALLBACK_TYPE = find(XmlCallback.class, getLogger(), context);
        this.METHOD_CALLBACK_TYPE = find(MethodCallback.class, getLogger(), context);
        this.TEXT_CALLBACK_TYPE = find(TextCallback.class, getLogger(), context);
        this.JSON_CALLBACK_TYPE = find(JsonCallback.class, getLogger(), context);
        this.OVERLAY_CALLBACK_TYPE = find(OverlayCallback.class, getLogger(), context);
        this.DOCUMENT_TYPE = find(Document.class, getLogger(), context);
        this.METHOD_TYPE = find(Method.class, getLogger(), context);
        this.STRING_TYPE = find(String.class, getLogger(), context);
        this.JSON_VALUE_TYPE = find(JSONValue.class, getLogger(), context);
        this.OVERLAY_VALUE_TYPE = find(JavaScriptObject.class, getLogger(), context);
        this.OVERLAY_ARRAY_TYPES = new HashSet<JClassType>();
        this.OVERLAY_ARRAY_TYPES.add(find(JsArray.class, getLogger(), context));
        this.OVERLAY_ARRAY_TYPES.add(find(JsArrayBoolean.class, getLogger(), context));
        this.OVERLAY_ARRAY_TYPES.add(find(JsArrayInteger.class, getLogger(), context));
        this.OVERLAY_ARRAY_TYPES.add(find(JsArrayNumber.class, getLogger(), context));
        this.OVERLAY_ARRAY_TYPES.add(find(JsArrayString.class, getLogger(), context));
        this.QUERY_PARAM_LIST_TYPES = new HashSet<JClassType>();
        this.QUERY_PARAM_LIST_TYPES.add(find(Collection.class, getLogger(), context));
        this.QUERY_PARAM_LIST_TYPES.add(find(List.class, getLogger(), context));
        this.QUERY_PARAM_LIST_TYPES.add(find(Set.class, getLogger(), context));
		this.REST_SERVICE_TYPE = find(RestService.class, getLogger(), context);

        String path = getPathFromSource(source);

        p("private " + RESOURCE_CLASS + " resource = null;");
        p();

        p("public void setResource(" + RESOURCE_CLASS + " resource) {").i(1);
        {
            p("this.resource = resource;");
        }
        i(-1).p("}");

        Options options = getAnnotation(source, Options.class);

        p("public " + RESOURCE_CLASS + " getResource() {").i(1);
        {
            p("if (this.resource == null) {").i(1);

            if (options != null && options.serviceRootKey() != null && !options.serviceRootKey().isEmpty()) {
            	p("String serviceRoot = " + SERVICE_ROOTS_CLASS + ".get(\"" + options.serviceRootKey() + "\");");
            } else {
            	p("String serviceRoot = " + DEFAULTS_CLASS + ".getServiceRoot();");
            }

            if (path == null) {
                p("this.resource = new " + RESOURCE_CLASS + "(serviceRoot);");
            } else {
                p("this.resource = new " + RESOURCE_CLASS + "(serviceRoot).resolve("+quote(path)+");");
            }

            i(-1).p("}");
            p("return this.resource;");
        }
        i(-1).p("}");


        if( options!=null && options.dispatcher()!=Dispatcher.class ) {
            p("private " + DISPATCHER_CLASS + " dispatcher = "+options.dispatcher().getName()+".INSTANCE;");
        } else {
            p("private " + DISPATCHER_CLASS + " dispatcher = null;");
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
                writeMethodImpl(method, options);
        }
    }

    private static String getPathFromSource(HasAnnotations annotatedType) {
        String path = null;

        Path pathAnnotation = getAnnotation(annotatedType, Path.class);
        if (pathAnnotation != null) {
            path = pathAnnotation.value();
        }

        RemoteServiceRelativePath relativePath = getAnnotation(annotatedType, RemoteServiceRelativePath.class);
        if (relativePath != null) {
            path = relativePath.value();
        }

        return path;
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
    		getLogger().log(ERROR, "Invalid subresource locator method. Method must have return type of an interface that extends RestService: " + method.getReadableDeclaration());
            throw new UnableToCompleteException();
    	}

        Path pathAnnotation = getAnnotation(method, Path.class);
        if (pathAnnotation == null) {
        	getLogger().log(ERROR, "Invalid subresource locator method. Method must have @Path annotation: " + method.getReadableDeclaration());
            throw new UnableToCompleteException();
        }
        String pathExpression = wrap(pathAnnotation.value());

        for (JParameter arg : method.getParameters()) {
            PathParam paramPath = getAnnotation(arg, PathParam.class);
            if (paramPath != null) {
                pathExpression = pathExpression(pathExpression, arg, paramPath);
            }
        }


        p(method.getReadableDeclaration(false, false, false, false, true) + " {").i(1);
        {
        	JType type = method.getReturnType();
        	String name;
        	if(type instanceof JClassType)
        	{
                JClassType restService = (JClassType)type;
                RestServiceClassCreator generator = new RestServiceClassCreator(getLogger(), context, restService);
                name = generator.create();
        	}
        	else
        	{
        		throw new UnsupportedOperationException("Subresource method may not return: " + type);
        	}
        	p(method.getReturnType().getQualifiedSourceName() + " __subresource = new " + name + "();");
                p("((" + RestServiceProxy.class.getName() + ")__subresource).setResource(getResource().resolve(" + pathExpression + "));");
                p("((" + RestServiceProxy.class.getName() + ")__subresource).setDispatcher(getDispatcher());");
        	p("return __subresource;");
        }
        i(-1).p("}");
    }

    private String pathExpression(String pathExpression, JParameter arg, PathParam paramPath) throws UnableToCompleteException {
        String expr = toStringExpression(arg);
        return pathExpression.replaceAll(Pattern.quote("{" + paramPath.value()) + "(\\s*:\\s*(.)+)?\\}",
                "\"+(" + expr + "== null? null : com.google.gwt.http.client.URL.encodePathSegment(" + expr + "))+\"");
    }

    void writeOptions(Options options, Options classOptions) {
        // configure the dispatcher
        if (options != null && options.dispatcher() != Dispatcher.class) {
            // use the dispatcher configured for the method.
            p("__method.setDispatcher(" + options.dispatcher().getName() + ".INSTANCE);");
        } else {
            // use the default dispatcher configured for the service..
            p("__method.setDispatcher(this.dispatcher);");
        }

        // configure the expected statuses..
        if (options != null && options.expect().length != 0) {
            // Using method level defined expected status
            p("__method.expect(" + join(options.expect(), ", ") + ");");
        } else if (classOptions != null && classOptions.expect().length != 0) {
            // Using class level defined expected status
            p("__method.expect(" + join(classOptions.expect(), ", ") + ");");
        }

        // configure the timeout
        if (options != null && options.timeout() >= 0) {
            // Using method level defined value
            p("__method.timeout(" + options.timeout() + ");");
        } else if (classOptions != null && classOptions.timeout() >= 0) {
            // Using class level defined value
            p("__method.timeout(" + classOptions.timeout() + ");");
        }
    }

    private void writeMethodImpl(JMethod method, Options classOptions) throws UnableToCompleteException {
        boolean returnRequest = false;
        if (method.getReturnType() != JPrimitiveType.VOID) {
            if (!method.getReturnType().getQualifiedSourceName().equals(Request.class.getName()) &&
                !method.getReturnType().getQualifiedSourceName().equals(JsonpRequest.class.getName())) {
                getLogger().log(ERROR, "Invalid rest method. Method must have void, Request or JsonpRequest return types: " + method.getReadableDeclaration());
                throw new UnableToCompleteException();
            }
            returnRequest = true;
        }

        Json jsonAnnotation = getAnnotation(source, Json.class);
        final Style classStyle = jsonAnnotation != null ? jsonAnnotation.style() : Style.DEFAULT;

        Options options = getAnnotation(method, Options.class);

        p(method.getReadableDeclaration(false, false, false, false, true) + " {").i(1);
        {
            String restMethod = getRestMethod(method);
            LinkedList<JParameter> args = new LinkedList<JParameter>(Arrays.asList(method.getParameters()));
            for (final JParameter arg : args.subList(0, args.size() - 1)) {
                p("final "
                        + arg.getType().getParameterizedQualifiedSourceName()
                        + " final_" + arg.getName() + " = " + arg.getName()
                        + ";");
            }

            // the last arg should be the callback.
            if (args.isEmpty()) {
                getLogger().log(ERROR, "Invalid rest method. Method must declare at least a callback argument: " + method.getReadableDeclaration());
                throw new UnableToCompleteException();
            }
            JParameter callbackArg = args.removeLast();
            JClassType callbackType = callbackArg.getType().isClassOrInterface();
            JClassType methodCallbackType = METHOD_CALLBACK_TYPE;
            if (callbackType == null || !callbackType.isAssignableTo(methodCallbackType)) {
                getLogger().log(ERROR, "Invalid rest method. Last argument must be a " + methodCallbackType.getName() + " type: " + method.getReadableDeclaration());
                throw new UnableToCompleteException();
            }
            JClassType resultType = getCallbackTypeGenericClass(callbackType);

            String pathExpression = null;
            Path pathAnnotation = getAnnotation(method, Path.class);
            if (pathAnnotation != null) {
                pathExpression = wrap(pathAnnotation.value());
            }

            JParameter contentArg = null;
            HashMap<String, JParameter> queryParams = new HashMap<String, JParameter>();
            HashMap<String, JParameter> formParams = new HashMap<String, JParameter>();
            HashMap<String, JParameter> headerParams = new HashMap<String, JParameter>();

            for (JParameter arg : args) {
                PathParam paramPath = getAnnotation(arg, PathParam.class);
                if (paramPath != null) {
                    if (pathExpression == null) {
                        getLogger().log(ERROR, "Invalid rest method.  Invalid @PathParam annotation. Method is missing the @Path annotation: " + method.getReadableDeclaration());
                        throw new UnableToCompleteException();
                    }
                    pathExpression = pathExpression(pathExpression, arg, paramPath);
                    //.replaceAll(Pattern.quote("{" + paramPath.value() + "}"), "\"+com.google.gwt.http.client.URL.encodePathSegment(" + toStringExpression(arg) + ")+\"");
                    if (getAnnotation(arg, Attribute.class) != null) {
                        // allow part of the arg-object participate in as PathParam and the object goes over the wire
                        contentArg = arg;
                    }
                    continue;
                }

                QueryParam queryParam = getAnnotation(arg, QueryParam.class);
                if (queryParam != null) {
                    queryParams.put(queryParam.value(), arg);
                    continue;
                }

                FormParam formParam = getAnnotation(arg, FormParam.class);
                if (formParam != null) {
                    formParams.put(formParam.value(), arg);
                    continue;
                }

                HeaderParam headerParam = getAnnotation(arg, HeaderParam.class);
                if (headerParam != null) {
                    headerParams.put(headerParam.value(), arg);
                    continue;
                }

                if (!formParams.isEmpty()) {
                    getLogger().log(ERROR, "You can not have both @FormParam parameters and a content parameter: " + method.getReadableDeclaration());
                    throw new UnableToCompleteException();
                }

                if (contentArg != null) {
                    getLogger().log(ERROR, "Invalid rest method. Only one content parameter is supported: " + method.getReadableDeclaration());
                    throw new UnableToCompleteException();
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

            // Handle JSONP specific configuration...
            JSONP jsonpAnnotation = getAnnotation(method, JSONP.class);

            final boolean isJsonp = restMethod.equals(METHOD_JSONP) && jsonpAnnotation != null;

            p("final " + (isJsonp ? JSONP_METHOD_CLASS : METHOD_CLASS) + " __method =");

            p("getResource()");
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

            if( isJsonp ) {
                if (returnRequest && !method.getReturnType().getQualifiedSourceName().equals(JsonpRequest.class.getName())) {
                    getLogger().log(ERROR, "Invalid rest method. JSONP method must have void or JsonpRequest return types: " + method.getReadableDeclaration());
                    throw new UnableToCompleteException();
                }
                if( jsonpAnnotation.callbackParam().length() > 0 ) {
                    p("__method.callbackParam("+wrap(jsonpAnnotation.callbackParam())+");");
                }
                if( jsonpAnnotation.failureCallbackParam().length() > 0 ) {
                    p("__method.failureCallbackParam("+wrap(jsonpAnnotation.failureCallbackParam())+");");
                }
            } else {
                if (returnRequest && !method.getReturnType().getQualifiedSourceName().equals(Request.class.getName())) {
                    getLogger().log(ERROR, "Invalid rest method. Non JSONP method must have void or Request return types: " + method.getReadableDeclaration());
                    throw new UnableToCompleteException();
                }
            }

            writeOptions(options, classOptions);

            String contentTypeHeaderValue = null;

            if(jsonpAnnotation == null) {
                final String acceptHeader; 
                Produces producesAnnotation = findAnnotationOnMethodOrEnclosingType(method, Produces.class);
                if (producesAnnotation != null) {
                    // Do not use autodetection, if accept type already set
                    if (acceptTypeBuiltIn == null && autodetectTypeForStrings && producesAnnotation.value()[0].startsWith("text/")) {
                        acceptTypeBuiltIn = "CONTENT_TYPE_TEXT";
                    }
                    acceptHeader = wrap(producesAnnotation.value()[0]);
                } else {
                    // set the default accept header value ...
                    if (acceptTypeBuiltIn != null) {
                        acceptHeader = RESOURCE_CLASS + "." + acceptTypeBuiltIn;
                    } else {
                        acceptHeader = RESOURCE_CLASS + ".CONTENT_TYPE_JSON";
                    }
                }
                p("__method.header(" + RESOURCE_CLASS + ".HEADER_ACCEPT, " + acceptHeader + ");");

                Consumes consumesAnnotation = findAnnotationOnMethodOrEnclosingType(method, Consumes.class);
                if (consumesAnnotation != null) {
                    contentTypeHeaderValue = consumesAnnotation.value()[0];
                    int split = contentTypeHeaderValue.indexOf(',');
                    if (split > 0) {
                        contentTypeHeaderValue = contentTypeHeaderValue.substring(0, split).trim();
                    }
                    p("__method.header(" + RESOURCE_CLASS + ".HEADER_CONTENT_TYPE, " + wrap(contentTypeHeaderValue) + ");");
               }

                // and set the explicit headers now (could override the accept header)
                for (Map.Entry<String, JParameter> entry : headerParams.entrySet()) {
                    String expr = entry.getValue().getName();
                    p("__method.header(" + wrap(entry.getKey()) + ", " + toStringExpression(entry.getValue().getType(), expr) + ");");
                }
            }

            if (! formParams.isEmpty()) {
                p(FORM_POST_CONTENT_CLASS + " __formPostContent = new " + FORM_POST_CONTENT_CLASS + "();");

                for (Map.Entry<String, JParameter> entry : formParams.entrySet()) {
                    JClassType type = entry.getValue().getType()
                            .isClassOrInterface();
                    if (type != null && isQueryParamListType(type)) {
                        p("__formPostContent.addParameters(" +
                                wrap(entry.getKey()) + ", " +
                                toIteratedFormStringExpression(entry.getValue(), classStyle) +
                                ");");
                    } else {
                        p("__formPostContent.addParameter(" +
                                wrap(entry.getKey()) + ", " +
                                toFormStringExpression(entry.getValue(), classStyle) +
                                ");");
                    }
                }

                p("__method.form(__formPostContent.getTextContent());");
            }

            if (contentArg != null) {
                if (contentArg.getType() == STRING_TYPE) {
                    if (autodetectTypeForStrings && MediaType.APPLICATION_JSON.equals(contentTypeHeaderValue)) {
                        p("__method.json(new " + JSON_STRING_CLASS + "(" + contentArg.getName() + "));");
                    } else {
                        p("__method.text(" + contentArg.getName() + ");");
                    }
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
                            getLogger().log(ERROR, "Content argument must be a class.");
                            throw new UnableToCompleteException();
                        }
                    }

                    jsonAnnotation = getAnnotation(contentArg, Json.class);
                    Style style = jsonAnnotation != null ? jsonAnnotation.style() : classStyle;

                    // example:
                    // .json(Listings$_Generated_JsonEncoder_$.INSTANCE.encode(arg0)
                    // )
                    p("__method.json(" + locator.encodeExpression(contentClass, contentArg.getName(), style) + ");");
                }
            }


            List<AnnotationResolver> annotationResolvers = getAnnotationResolvers(context, getLogger());
            getLogger().log(TreeLogger.DEBUG, "found " + annotationResolvers.size() + " additional AnnotationResolvers");

            for (AnnotationResolver a : annotationResolvers) {
                getLogger().log(TreeLogger.DEBUG, "(" + a.getClass().getName() + ") resolve `" + source.getName()
                        + "#" + method.getName() + "´ ...");
                final Map<String, String[]> addDataParams = a.resolveAnnotation(getLogger(), source, method, restMethod);

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

                        getLogger().log(TreeLogger.DEBUG, "add call with (\"" + s + "\", \"" +
                                sb.toString() + "\")");
                        p("__method.addData(\"" + s + "\", \"" + sb.toString() + "\");");
                    }
                }
            }


            if (acceptTypeBuiltIn != null) {
                // TODO: shouldn't we also have a cach in here?
                p(returnRequest(returnRequest,isJsonp) + "__method.send(" + callbackArg.getName() + ");");
            } else if ( isJsonp ){
                    p(returnRequest(returnRequest,isJsonp) + "__method.send(new " + ABSTRACT_ASYNC_CALLBACK_CLASS + "<" + resultType.getParameterizedQualifiedSourceName() + ">(__method, "
                                    + callbackArg.getName() + ") {").i(1);
                    {
                        p("protected " + resultType.getParameterizedQualifiedSourceName() + " parseResult(" + JSON_VALUE_CLASS + " result) throws Exception {").i(1);
                        {
                            if(resultType.getParameterizedQualifiedSourceName().equals("java.lang.Void")) {
                                p("return (java.lang.Void) null;");
                            }
                            else {
                                p("try {").i(1);
                                {
                                    if(resultType.isAssignableTo(locator.getListType())){
                                        p("result = new " + JSON_ARRAY_CLASS + "(((" + JSON_OBJECT_CLASS + ")result).getJavaScriptObject());");
                                    }
                                    jsonAnnotation = getAnnotation(method, Json.class);
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
                    p(returnRequest(returnRequest,isJsonp) + "__method.send(new " + ABSTRACT_REQUEST_CALLBACK_CLASS + "<" + resultType.getParameterizedQualifiedSourceName() + ">(__method, "
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
                                    jsonAnnotation = getAnnotation(method, Json.class);
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
                    if (returnRequest) {
                        p("return null;");
                    }
                }
                i(-1).p("}");
            }
        }
        i(-1).p("}");
    }

    private <T extends Annotation> T findAnnotationOnMethodOrEnclosingType(final JMethod method, final Class<T> annotationType) {
        T annotation = getAnnotation(method, annotationType);
        if (annotation == null) {
            annotation = getAnnotation(method.getEnclosingType(), annotationType);
        }
        return annotation;
    }

	protected String toStringExpression(JParameter arg) throws UnableToCompleteException {
		Attribute attribute = getAnnotation(arg, Attribute.class);
		if (attribute != null) {
			if (arg.getType().isClass().getField(attribute.value()) != null && arg.getType().isClass().getField(attribute.value()).isPublic()) {
				return "(" + arg.getName() + "." + attribute.value() + "+ \"\")";
			}
			String publicGetter = "get" + attribute.value().substring(0, 1).toUpperCase() + attribute.value().substring(1);
			for (JMethod jMethod : arg.getType().isClass().getMethods()) {
				if (jMethod.getName().equals(publicGetter)) {
					return "(" + arg.getName() + "." + publicGetter + "()" + "+ \"\")";
				}
			}
			getLogger().log(ERROR, "Neither public argument " + attribute.value() + " nor public getter " + publicGetter + " found!");
			throw new UnableToCompleteException();
		}
		return toStringExpression(arg.getType(), arg.getName());
	}

    protected String toFormStringExpression(JParameter argument, Style classStyle) throws UnableToCompleteException {
        JType type = argument.getType();
        String expr = argument.getName();

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
        if (type.getQualifiedBinaryName().startsWith("java.lang.") || type.isEnum() != null) {
            return String.format("(%s != null ? %s.toString() : null)", expr, expr);
        }

        Json jsonAnnotation = getAnnotation(argument, Json.class);
        final Style style = jsonAnnotation != null ? jsonAnnotation.style() : classStyle;

        return locator.encodeExpression(type, expr, style) + ".toString()";
    }

    protected String toIteratedFormStringExpression(JParameter argument, Style classStyle) throws UnableToCompleteException {
        assert isQueryParamListType(argument.getType().isClassOrInterface());
        final JClassType[] type_args = argument.getType().isParameterized().getTypeArgs();
        assert (type_args.length == 1);
        final JClassType class_type = type_args[0];
        final String argument_expr = "final_"
                + argument.getName();

        final StringBuilder result = new StringBuilder();
        result.append(argument_expr + " == null ? null : ");
        result.append("new java.lang.Iterable<String> () {\n");
        result.append(" @Override\n");
        result.append(" public java.util.Iterator<String> iterator() {\n");
        result.append("     final java.util.Iterator<"
                + class_type.getParameterizedQualifiedSourceName()
                + "> baseIterator =  " + argument_expr + ".iterator();\n");
        result.append("     return new java.util.Iterator<String>() {\n");
        result.append("         @Override\n");
        result.append("         public boolean hasNext() {\n");
        result.append("           return baseIterator.hasNext();\n");
        result.append("         }\n");
        result.append("         @Override\n");
        result.append("         public String next() {\n");
        final String expr = "baseIterator.next()";
        String returnExpr;
        if (class_type.isPrimitive() != null) {
            returnExpr = "\"\"+" + expr;
        } else if (STRING_TYPE == class_type) {
            returnExpr = expr;
        } else if (class_type.isClass() != null &&
            isOverlayArrayType(class_type.isClass())) {
            returnExpr = "(new " + JSON_ARRAY_CLASS + "(" + expr + ")).toString()";
        } else if (class_type.isClass() != null &&
            OVERLAY_VALUE_TYPE.isAssignableFrom(class_type.isClass())) {
            returnExpr = "(new " + JSON_OBJECT_CLASS + "(" + expr + ")).toString()";
        } else if (class_type.getQualifiedBinaryName().startsWith("java.lang.")) {
            result.append("             Object obj = " + expr + ";");
            returnExpr = "obj != null ? obj.toString() : null";
        } else {
            Json jsonAnnotation = getAnnotation(argument, Json.class);
            final Style style = jsonAnnotation != null ? jsonAnnotation.style() : classStyle;
            returnExpr = locator.encodeExpression(class_type, expr, style) + ".toString()";
        }
        result.append("             return " + returnExpr + ";\n");
        result.append("         }\n");
        result.append("         @Override\n");
        result.append("         public void remove() {\n");
        result.append("             throw new UnsupportedOperationException();\n");
        result.append("         }\n");
        result.append("     };\n");
        result.append(" }\n");
        result.append("}\n");

        return result.toString();
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

        return String.format("(%s != null ? %s.toString() : null)", expr, expr);
    }

    protected String toIteratedStringExpression(JParameter arg) {
        StringBuilder result = new StringBuilder();
        result.append("new org.fusesource.restygwt.client.StringIterable (")
            .append(arg.getName()).append(")");

        return result.toString();
    }

    private JClassType getCallbackTypeGenericClass(final JClassType callbackType) throws UnableToCompleteException {
        return branch("getCallbackTypeGenericClass()", new Branch<JClassType>() {
            @Override
            public JClassType execute() throws UnableToCompleteException {

                for (JMethod method : callbackType.getOverridableMethods()) {
                    getLogger().log(DEBUG, "checking method: " + method.getName());
                    if (method.getName().equals("onSuccess")) {
                        JParameter[] parameters = method.getParameters();
                        getLogger().log(DEBUG, "checking method params: " + parameters.length);
                        if (parameters.length == 2) {
                            getLogger().log(DEBUG, "checking first param: " + parameters[0].getType());
                            if (parameters[0].getType() == METHOD_TYPE) {
                                getLogger().log(DEBUG, "checking 2nd param: " + parameters[1].getType());
                                JType param2Type = parameters[1].getType();
                                JClassType type = param2Type.isClassOrInterface();
                                if (type == null) {
                                    getLogger().log(ERROR, "The type of the callback not supported: " + param2Type.getJNISignature());
                                    throw new UnableToCompleteException();
                                }
                                getLogger().log(DEBUG, "match: " + type);
                                return type;
                            }
                        }
                    }
                }
                getLogger().log(ERROR, "The type of the callback could not be determined: " + callbackType.getParameterizedQualifiedSourceName());
                throw new UnableToCompleteException();
            }
        });
    }

    /**
     * Returns the rest method.
     * <p />
     * Iterates over the annotations and look for an annotation that is annotated with HttpMethod.
     * 
     * @param method
     * @return
     * @throws UnableToCompleteException
     *             <ul>
     *             <li>If more than one annotation that is annotated with HttpMethod is present,</li>
     *             <li>an invalid jax-rs method annotation is found or</li>
     *             <li>no annotation was found and the method name does not match a valid jax-rs method</li>
     *             </ul>
     *             Valid means it is in the set {@link #REST_METHODS}
     */
    String getRestMethod(JMethod method) throws UnableToCompleteException {
        String restMethod = null;

        final Annotation[] annotations = method.getAnnotations();
        for (Annotation annotation : annotations) {
            HttpMethod httpMethod = annotation.annotationType().getAnnotation(HttpMethod.class);
            // Check is HttpMethod
            if (null != httpMethod) {
                if (null != restMethod) {
                    // Error, see description of HttpMethod
                    getLogger().log(ERROR, "Invalid method. It is an error for a method to be annotated with more than one annotation that is annotated with HttpMethod: " + method.getReadableDeclaration());
                    throw new UnableToCompleteException();
                }
                restMethod = httpMethod.value();
            }
        }

        if (null != restMethod) {
            // Allow custom methods later?
            restMethod = restMethod.toLowerCase();
            if (!REST_METHODS.contains(restMethod)) {
                getLogger().log(ERROR, "Invalid rest method. It must have a javax rs method annotation: " + method.getReadableDeclaration());
                throw new UnableToCompleteException();
            }
        } else if (null != getAnnotation(method, JSONP.class)) {
            restMethod = METHOD_JSONP;
        } else {
            restMethod = method.getName();
            if (!REST_METHODS.contains(restMethod)) {
                getLogger().log(ERROR, "Invalid rest method. It must either have a lower case rest method name or have a javax rs method annotation: " + method.getReadableDeclaration());
                throw new UnableToCompleteException();
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
    // TODO remove suppression
    @SuppressWarnings({"unchecked", "rawtypes"})
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

    private String returnRequest(boolean returnRequest, boolean isJsonp) {
        String type = isJsonp ? JsonpRequest.class.getName() : Request.class.getName();
        return returnRequest ? "return ("+type+")" : "";
    }
}
