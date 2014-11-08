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

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.logging.client.LogConfiguration;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 *
 * @author <a href="http://blog.mkristian.tk">Kristian</a>
 */
public abstract class AbstractAsyncCallback<T> implements AsyncCallback<JavaScriptObject> {

    protected final JsonpMethod method;

    protected MethodCallback<T> callback;

    private Logger logger;

    public AbstractAsyncCallback(JsonpMethod method, MethodCallback<T> callback) {
        this.method = method;
        this.callback = callback;
    }

    @Override
    final public void onFailure(Throwable exception) {
        callback.onFailure(this.method, exception);
    }

    private Logger getLogger() {
        if ( GWT.isClient() && LogConfiguration.loggingIsEnabled() && this.logger == null) {
            this.logger = Logger.getLogger( AbstractAsyncCallback.class.getName() );
        }
        return this.logger;
    }
    
    @Override
    final public void onSuccess(JavaScriptObject result) {
        try {
            if ( getLogger() != null ) {
                getLogger().fine("Received http response for jsonp request");
            }
            if (result == null){
                callback.onSuccess(this.method, null);
            }
            else{
                JSONObject json = new JSONObject(result);
                if ( getLogger() != null ) {
                    getLogger().fine(json.toString());
                }
                callback.onSuccess(this.method, parseResult(json));
            }
        } catch (Throwable e) {
            if ( getLogger() != null ) {
                getLogger().log(Level.FINE, "Could not parse response: " + e, e);
            }
            callback.onFailure(this.method, e);
            return;
        }
    }

    abstract protected T parseResult(JSONValue result) throws Exception;
}
