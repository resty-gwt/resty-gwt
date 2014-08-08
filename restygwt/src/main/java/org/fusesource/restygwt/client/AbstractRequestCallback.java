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
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.logging.client.LogConfiguration;

/**
 *
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 */
public abstract class AbstractRequestCallback<T> implements RequestCallback {

    protected final Method method;

    protected MethodCallback<T> callback;

    private Logger logger;

    public AbstractRequestCallback(Method method, MethodCallback<T> callback) {
        this.method = method;
        this.callback = callback;
    }

    @Override
    final public void onError(Request request, Throwable exception) {
        this.method.request = request;
        callback.onFailure(this.method, exception);
    }

    private Logger getLogger() {
        if (GWT.isClient() && LogConfiguration.loggingIsEnabled() && this.logger == null) {
            this.logger = Logger.getLogger( AbstractRequestCallback.class.getName() );
        }
        return this.logger;
    }
    
    @Override
    final public void onResponseReceived(Request request, Response response) {
        this.method.request = request;
        this.method.response = response;
        if (response == null) {
            callback.onFailure(this.method, new FailedStatusCodeException("TIMEOUT", 999));
        } else if (isFailedStatus(response)) {
            callback.onFailure(this.method, new FailedStatusCodeException(response.getStatusText(),
                    response.getStatusCode()));
        } else {
            T value;
            try { 
                if ( getLogger() != null ) {
                    getLogger().fine("Received http response for request: " + this.method.builder.getHTTPMethod()
                        + " " + this.method.builder.getUrl());
                }
                String content = response.getText();
                if (content != null && content.length() > 0) {
                    if ( getLogger() != null ) {
                        getLogger().fine(content);
                    }
                    value = parseResult();
                } else {
                    value = null;
                }
            } catch (Throwable e) {
                if ( getLogger() != null ) {
                    getLogger().log(Level.FINE, "Could not parse response: " + e, e);
                }
                callback.onFailure(this.method, e);
                return;
            }

            callback.onSuccess(this.method, value);
        }
    }

    protected boolean isFailedStatus(Response response) {
        return !this.method.isExpected(response.getStatusCode());
    }

    abstract protected T parseResult() throws Exception;
}
