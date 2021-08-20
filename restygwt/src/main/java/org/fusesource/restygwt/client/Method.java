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

package org.fusesource.restygwt.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONException;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.logging.client.LogConfiguration;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.XMLParser;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.fusesource.restygwt.rebind.AnnotationResolver;

/**
 *
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 */
public class Method {

    /**
     * GWT hides the full spectrum of methods because safari has a bug:
     * http://bugs.webkit.org/show_bug.cgi?id=3812
     *
     * We extend assume the server side will also check the
     * X-HTTP-Method-Override header.
     *
     * TODO: add an option to support using this approach to bypass restrictive
     * firewalls even if the browser does support the setting all the method
     * types.
     *
     * @author chirino
     */
    private static class MethodRequestBuilder extends RequestBuilder {
        public MethodRequestBuilder(String method, String url) {

            super(method, url);
            //without null value being explicitly set gwt would generate "undefined" as a default value,
            //so if request does not have a body, Internet Explorer would send string "undefined" in the body of
            // POST, PUT and DELETE requests,
            //which may cause the request to fall on server with "No operation matching request path"
            setRequestData(null);
            if (Defaults.isAddXHttpMethodOverrideHeader()) {
                setHeader("X-HTTP-Method-Override", method);
            }
        }

		@Override
		public void setHeader(String header, String value) {
			// prevent setting null or empty string
			if ((value == null) || (0 == value.trim().length()))
				return;
			super.setHeader(header, value);
		}
        
    }

    public RequestBuilder builder;

    final Set<Integer> expectedStatuses;

    {
        expectedStatuses = new HashSet<Integer>();
        expectedStatuses.add(200);
        expectedStatuses.add(201);
        expectedStatuses.add(204);
        // This is needed for MSIE mangling with status 204 to become 1223
        expectedStatuses.add(1223);
    }

    boolean anyStatus;

    Request request;
    Response response;
    Dispatcher dispatcher;

    /**
     * additional data which can be set per instance, e.g. from a {@link AnnotationResolver}
     */
    private final Map<String, String> data = new HashMap<String, String>();

    private Logger logger;

    protected Method() {
    }

    public Method(Resource resource, String method) {
        builder = new MethodRequestBuilder(method, resource.getUri());
    }

    public Method user(String user) {
        builder.setUser(user);
        return this;
    }

    public Method password(String password) {
        builder.setPassword(password);
        return this;
    }

    public Method header(String header, String value) {
        builder.setHeader(header, value);
        return this;
    }

    public Method headers(Map<String, String> headers) {
        if (headers != null) {
            for (Entry<String, String> entry : headers.entrySet()) {
                builder.setHeader(entry.getKey(), entry.getValue());
            }
        }
        return this;
    }

    private void doSetTimeout() {
        // Use default timeout only if it was not already set through the @Options(timeout =) annotation.
        // See https://github.com/resty-gwt/resty-gwt/issues/206
        if (builder.getTimeoutMillis() == 0 && Defaults.getRequestTimeout() > -1) {
            builder.setTimeoutMillis(Defaults.getRequestTimeout());
        }
    }

    public Method text(String data) {
        defaultContentType(Resource.CONTENT_TYPE_TEXT);
        builder.setRequestData(data);
        return this;
    }

    public Method json(JSONValue data) {
        defaultContentType(Resource.CONTENT_TYPE_JSON);
        builder.setRequestData(data.toString());


        return this;
    }

    public Method xml(Document data) {
        defaultContentType(Resource.CONTENT_TYPE_XML);
        builder.setRequestData(data.toString());
        return this;
    }

    public Method form(String encodedFormData) {
        defaultContentType(Resource.CONTENT_TYPE_FORM);
        builder.setRequestData(encodedFormData);
        return this;
    }

    public Method timeout(int timeout) {
        builder.setTimeoutMillis(timeout);
        return this;
    }

    /**
     * sets the expected response status code.  If the response status code does not match
     * any of the values specified then the request is considered to have failed.  Defaults to accepting
     * 200,201,204. If set to -1 then any status code is considered a success.
     */
    public Method expect(int... statuses) {
        if (statuses.length == 1 && statuses[0] < 0) {
            anyStatus = true;
        } else {
            anyStatus = false;
            expectedStatuses.clear();
            for (int status : statuses) {
                expectedStatuses.add(status);
            }
        }
        return this;
    }

    /**
     * Local file-system (file://) does not return any status codes.
     * Therefore - if we read from the file-system we accept all codes.
     *
     * This is for instance relevant when developing a PhoneGap application with
     * restyGwt.
     */
    public boolean isExpected(int status) {

        String baseUrl = GWT.getHostPageBaseURL();
        String requestUrl = builder.getUrl();

        if (FileSystemHelper.isRequestGoingToFileSystem(baseUrl, requestUrl)) {
            return true;
        } else if (anyStatus) {
            return true;
        } else {
            return expectedStatuses.contains(status);
        }
    }

    public Object send(RequestCallback callback) throws RequestException {
        doSetTimeout();
        builder.setCallback(callback);
        // lazily load dispatcher from defaults, if one is not set yet.
        Dispatcher localDispatcher = dispatcher == null ? Defaults.getDispatcher() : dispatcher;
        return localDispatcher.send(this, builder);
    }

    private Logger getLogger() {
        if (GWT.isClient() && LogConfiguration.loggingIsEnabled() && logger == null) {
            logger = Logger.getLogger(Method.class.getName());
        }
        return logger;
    }

    public Object send(TextCallback callback) {
        return send((MethodCallback<String>) callback);
    }

    public Object send(MethodCallback<String> callback) {
        defaultAcceptType(Resource.CONTENT_TYPE_TEXT);
        try {
            return send(new AbstractRequestCallback<String>(this, callback) {
                @Override
                protected String parseResult() throws Exception {
                    return response.getText();
                }
            });
        } catch (Throwable e) {
            if (getLogger() != null) {
                getLogger()
                    .log(Level.FINE, "Received http error for: " + builder.getHTTPMethod() + " " + builder.getUrl(), e);
            }
            callback.onFailure(this, e);
            return null;
        }
    }

    public Object send(JsonCallback callback) {
        defaultAcceptType(Resource.CONTENT_TYPE_JSON);

        try {
            return send(new AbstractRequestCallback<JSONValue>(this, callback) {
                @Override
                protected JSONValue parseResult() throws Exception {
                    try {
                        return JSONParser.parseStrict(response.getText());
                    } catch (Throwable e) {
                        throw new ResponseFormatException("Response was NOT a valid JSON document", e, request, response);
                    }
                }
            });
        } catch (Throwable e) {
            if (getLogger() != null) {
                getLogger()
                    .log(Level.FINE, "Received http error for: " + builder.getHTTPMethod() + " " + builder.getUrl(), e);
            }
            callback.onFailure(this, e);
            return null;
        }
    }

    public Object send(XmlCallback callback) {
        defaultAcceptType(Resource.CONTENT_TYPE_XML);
        try {
            return send(new AbstractRequestCallback<Document>(this, callback) {
                @Override
                protected Document parseResult() throws Exception {
                    try {
                        return XMLParser.parse(response.getText());
                    } catch (Throwable e) {
                        throw new ResponseFormatException("Response was NOT a valid XML document", e, request, response);
                    }
                }
            });
        } catch (Throwable e) {
            if (getLogger() != null) {
                getLogger()
                    .log(Level.FINE, "Received http error for: " + builder.getHTTPMethod() + " " + builder.getUrl(), e);
            }
            callback.onFailure(this, e);
            return null;
        }
    }

    public <T extends JavaScriptObject> Object send(OverlayCallback<T> callback) {


        defaultAcceptType(Resource.CONTENT_TYPE_JSON);
        try {
            return send(new AbstractRequestCallback<T>(this, callback) {
                @SuppressWarnings("unchecked")
                @Override
                protected T parseResult() throws Exception {
                    try {
                        JSONValue val = JSONParser.parseStrict(response.getText());
                        if (val.isObject() != null) {
                            return (T) val.isObject().getJavaScriptObject();
                        } else if (val.isArray() != null) {
                            return (T) val.isArray().getJavaScriptObject();
                        } else {
                            throw new ResponseFormatException("Response was NOT a JSON object", request, response);
                        }
                    } catch (JSONException e) {
                        throw new ResponseFormatException("Response was NOT a valid JSON document", e, request, response);
                    } catch (IllegalArgumentException e) {
                        throw new ResponseFormatException("Response was NOT a valid JSON document", e, request, response);
                    }
                }
            });
        } catch (Throwable e) {
            if (getLogger() != null) {
                getLogger()
                    .log(Level.FINE, "Received http error for: " + builder.getHTTPMethod() + " " + builder.getUrl(), e);
            }
            callback.onFailure(this, e);
            return null;
        }
    }

    public Request getRequest() {
        return request;
    }

    public Response getResponse() {
        return response;
    }

    protected void defaultContentType(String type) {
        if (builder.getHeader(Resource.HEADER_CONTENT_TYPE) == null) {
            header(Resource.HEADER_CONTENT_TYPE, type);
        }
    }

    protected void defaultAcceptType(String type) {
        if (builder.getHeader(Resource.HEADER_ACCEPT) == null) {
            header(Resource.HEADER_ACCEPT, type);
        }
    }

    public Dispatcher getDispatcher() {
        return dispatcher;
    }

    public void setDispatcher(Dispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    /**
     * add some information onto the method which could be interesting when this method
     * comes back to the dispatcher.
     *
     * @param key
     * @param value
     */
    public void addData(String key, String value) {
        data.put(key, value);
    }

    /**
     * get all data fields which was previously added
     *
     * @return
     */
    public Map<String, String> getData() {
        return data;
    }
}
