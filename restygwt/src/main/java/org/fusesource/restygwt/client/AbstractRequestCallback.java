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

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.logging.client.LogConfiguration;

import java.util.logging.Level;
import java.util.logging.Logger;

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
    public final void onError(Request request, Throwable exception) {
        method.request = request;
        callback.onFailure(method, exception);
    }

    private Logger getLogger() {
        if (GWT.isClient() && LogConfiguration.loggingIsEnabled() && logger == null) {
            logger = Logger.getLogger(AbstractRequestCallback.class.getName());
        }
        return logger;
    }

    @Override
    public final void onResponseReceived(Request request, Response response) {
        method.request = request;
        method.response = response;
        if (response == null) {
            callback.onFailure(method, Defaults.getExceptionMapper().createNoResponseException());
        } else if (isFailedStatus(response)) {
            callback
                    .onFailure(method, Defaults.getExceptionMapper().createFailedStatusException(method, response));
        } else {
            if (getLogger() != null) {
                getLogger().fine(
                        "Received http response for request: " + method.builder.getHTTPMethod() + " " +
                                method.builder.getUrl());
            }
            if (Response.SC_NO_CONTENT == response.getStatusCode()) {
                callback.onSuccess(method, null);
            }
            T value;
            try {
                String content = response.getText();
                if (content != null) {
                    if (getLogger() != null) {
                        getLogger().finest(content);
                    }
                    value = parseResult();
                } else {
                    value = null;
                }
            } catch (Throwable e) {
                if (getLogger() != null) {
                    getLogger().log(Level.FINE, "Could not parse response: " + e, e);
                }
                callback.onFailure(method, e);
                return;
            }

            callback.onSuccess(method, value);
        }
    }

    protected boolean isFailedStatus(Response response) {
        return !method.isExpected(response.getStatusCode());
    }

    protected abstract T parseResult() throws Exception;
}
