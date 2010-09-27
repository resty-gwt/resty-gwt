/**
 * Copyright (C) 2010, Progress Software Corporation and/or its 
 * subsidiaries or affiliates.  All rights reserved.
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

/**
 * 
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 */
public abstract class AbstractRequestCallback<T> implements RequestCallback {

    protected final Method method;
    protected MethodCallback<T> callback;

    public AbstractRequestCallback(Method method, MethodCallback<T> callback) {
        this.method = method;
        this.callback = callback;
    }

    final public void onError(Request request, Throwable exception) {
        this.method.request = request;
        callback.onFailure(this.method, exception);
    }

    final public void onResponseReceived(Request request, Response response) {
        this.method.request = request;
        this.method.response = response;
        if (response == null) {
            callback.onFailure(this.method, new FailedStatusCodeException("TIMEOUT", 999));
        } else if (isFailedStatus(response)) {
            callback.onFailure(this.method, new FailedStatusCodeException(response.getStatusText(), response.getStatusCode()));
        } else {
            T value;
            try {
                GWT.log("Received http response for request: " + method.builder.getHTTPMethod() + " " + method.builder.getUrl(), null);
                String content = response.getText();
                if (content != null && content.length() > 0) {
                    GWT.log(content, null);
                    value = parseResult();
                } else {
                    value = null;
                }
            } catch (Throwable e) {
                GWT.log("Could not parse response: " + e, e);
                callback.onFailure(this.method, e);
                return;
            }
            callback.onSuccess(this.method, value);
        }
    }

    protected boolean isFailedStatus(Response response) {
        if( this.method.expectedStatus < 0 ) {
            return false;
        }
        return response.getStatusCode() != this.method.expectedStatus;
    }

    abstract protected T parseResult() throws Exception;
}