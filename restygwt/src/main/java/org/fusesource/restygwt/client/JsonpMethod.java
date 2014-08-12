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

package org.fusesource.restygwt.client;

import java.util.Map;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.jsonp.client.JsonpRequestBuilder;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.xml.client.Document;

/**
 * A specialized method which accesses a resource as a JSONP request.
 * 
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 */
public class JsonpMethod extends Method {

    private final Resource resource;
    private final JsonpRequestBuilder jsonpBuilder = new JsonpRequestBuilder();

    public JsonpMethod(Resource resource) {
        this.resource = resource;
    }
    
    public JsonpMethod callbackParam(String callbackParam) {
        jsonpBuilder.setCallbackParam(callbackParam);
        return this;
    }

    public JsonpMethod failureCallbackParam(String failureCallbackParam) {
        jsonpBuilder.setFailureCallbackParam(failureCallbackParam);
        return this;
    }

    @Override
    public Method timeout(int timeout) {
        jsonpBuilder.setTimeout(timeout);
        return this;
    }

    @Override
    public Object send(final JsonCallback callback) {
        return jsonpBuilder.requestObject(resource.getUri(), new AsyncCallback<JavaScriptObject>() {
            @Override
            public void onSuccess(JavaScriptObject result) {
                callback.onSuccess(JsonpMethod.this, new JSONObject(result));
            }

            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(JsonpMethod.this, caught);
            }
        });
    }

    @Override
    public Object send(final TextCallback callback) {
        return jsonpBuilder.requestString(resource.getUri(), new AsyncCallback<String>() {
            @Override
            public void onSuccess(String result) {
                callback.onSuccess(JsonpMethod.this, result);
            }

            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(JsonpMethod.this, caught);
            }
        });
    }

    /**
     * helper method to make RestServiceClassCreator easier to maintain
     * @param callback
     */
    public Object send(final AsyncCallback<JavaScriptObject> callback) {
        return jsonpBuilder.requestObject(resource.getUri(), callback);
    }

    @Override
    public Object send(RequestCallback callback) throws RequestException {
        throw unsupported();
    }

    @Override
    public Method expect(int ...status) {
        throw unsupported();
    }

    @Override
    public Request getRequest() {
        throw unsupported();
    }

    @Override
    public Response getResponse() {
        throw unsupported();
    }

    @Override
    public Method header(String header, String value) {
        throw unsupported();
    }

    @Override
    public Method headers(Map<String, String> headers) {
        throw unsupported();
    }

    @Override
    public Method json(JSONValue data) {
        throw unsupported();
    }

    @Override
    public Method password(String password) {
        throw unsupported();
    }

    @Override
    public Object send(XmlCallback callback) {
        throw unsupported();
    }

    @Override
    public Method text(String data) {
        throw unsupported();
    }

    @Override
    public Method user(String user) {
        throw unsupported();
    }

    @Override
    public Method xml(Document data) {
        throw unsupported();
    }

    private UnsupportedOperationException unsupported() {
        return new UnsupportedOperationException("The jsonp method is restricted in what it can be configured with.");
    }

}
