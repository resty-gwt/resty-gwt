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

package org.fusesource.restygwt.client;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;

public abstract class AbstractNestedRequestCallback implements RequestCallback {

    protected final Method method;

    protected RequestCallback requestCallback;

    public AbstractNestedRequestCallback(Method method, RequestCallback callback) {
        this.method = method;
        this.requestCallback = callback;
    }

    @Override
    public void onResponseReceived(Request request, Response response) {
        this.method.request = request;
        this.method.response = response;
        if (response == null) {
            requestCallback.onError(request, 
                    new FailedStatusCodeException("TIMEOUT", 999));
        } else if (isFailedStatus(response)) {
            doError(request, response);
        } else {
            doReceive(request, response);
        }
    }

    protected void doError(Request request, Response response){
        requestCallback.onError(request, 
                    new FailedStatusCodeException(response.getStatusText(), 
                            response.getStatusCode()));
    }
    
    protected abstract void doReceive(Request request, Response response);

    @Override
    public void onError(Request request, Throwable exception) {
        this.method.request = request;
        requestCallback.onError(request, exception);
    }

    protected boolean isFailedStatus(Response response) {
        return !this.method.isExpected(response.getStatusCode());
    }
}
