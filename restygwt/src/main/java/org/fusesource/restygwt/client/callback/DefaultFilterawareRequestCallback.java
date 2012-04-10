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

package org.fusesource.restygwt.client.callback;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.fusesource.restygwt.client.Method;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.logging.client.LogConfiguration;

public class DefaultFilterawareRequestCallback implements FilterawareRequestCallback {

    protected final Method method;

    protected RequestCallback requestCallback;

    final protected List<CallbackFilter> callbackFilters = new ArrayList<CallbackFilter>();

    public DefaultFilterawareRequestCallback(Method method) {
        this.method = method;
        // need to keep requestcallback here, as ``method.builder.getCallback()`` does not
        // give the same callback later on
        this.requestCallback = method.builder.getCallback();
    }

    @Override
    public void onResponseReceived(Request request, Response response) {
        if (!(response.getStatusCode() < 300 && response.getStatusCode() >= 200)) {
            if (GWT.isClient() && LogConfiguration.loggingIsEnabled()) {
                Logger.getLogger(
                        DefaultFilterawareRequestCallback.class.getName())
                        .severe(
                                "ERROR with method: "
                                        + method.builder.getHTTPMethod() + " "
                                        + method.builder.getUrl() + ", "
                                        + response.getStatusText());
            }

            /*
             * RuntimeException token from
             * com.google.gwt.http.client.Request#fireOnResponseReceived()
             */
            requestCallback.onError(request, new RuntimeException("Response "
                    + response.getStatusCode() + " for "
                    + method.builder.getHTTPMethod() + " "
                    + method.builder.getUrl()));
        } else {
            // filter only in success case for now
            runFilters(request, response);
        }
    }

    protected void runFilters(Request request, Response response) {
        for (CallbackFilter f : callbackFilters) {
            requestCallback = f.filter(method, response, requestCallback);
        }
        requestCallback.onResponseReceived(request, response);
    }

    @Override
    public void onError(Request request, Throwable exception) {
        requestCallback.onError(request, exception);
    }

    /**
     * put a filter in the "chain of responsibility" of all callbackfilters that will be
     * performed on callback passing.
     */
    public void addFilter(CallbackFilter filter) {
        callbackFilters.add(filter);
    }
}
