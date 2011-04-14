/**
 * Copyright (C) 2009-2010 the original author or authors.
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

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.logging.client.LogConfiguration;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;

public class FilterawareRetryingCallback implements FilterawareRequestCallback {

    /**
     * Used by RetryingCallback
     * default value is 5
     */
    protected static int numberOfRetries = 5;

    /**
     * time to wait for reconnect upon failure
     */
    protected int gracePeriod = 1000;

    protected int currentRetryCounter = 0;

    protected final Method method;

    protected RequestCallback requestCallback;

    final protected List<CallbackFilter> callbackFilters = new ArrayList<CallbackFilter>();

    public FilterawareRetryingCallback(Method method) {
        this.method = method;
        // need to keep requestcallback here, as ``method.builder.getCallback()`` does not
        // give the same callback later on
        this.requestCallback = method.builder.getCallback();
    }

    @Override
    public final void onResponseReceived(Request request, Response response) {
        if (response.getStatusCode() == Response.SC_UNAUTHORIZED) {
            if (LogConfiguration.loggingIsEnabled()) {
                Logger.getLogger(FilterawareRetryingCallback.class.getName()).severe("Unauthorized: "
                        + method.builder.getUrl());
                // HACK TODO handle this via a callbackfilter
                Window.Location.assign("login.html" + Window.Location.getQueryString());
            }
        } else if (!(response.getStatusCode() < 300 && response.getStatusCode() >= 200)) {
            /*
             * retry only on GET requests that are no redirects (301, 302)
             */
            if (response.getStatusCode() != 301
                    && response.getStatusCode() != 302
                    && response.getStatusCode() != 404
                    && method.builder.getHTTPMethod().equals(RequestBuilder.GET.toString())) {
                handleErrorGracefully(request, response, requestCallback);
            } else {
                if (LogConfiguration.loggingIsEnabled()) {
                    Logger.getLogger(FilterawareRetryingCallback.class.getName()).severe(
                            "ERROR with non-GET method: " + method.builder.getHTTPMethod() + " "
                            + method.builder.getUrl() + ", " + response.getStatusText());
                }

                /*
                 *  RuntimeException token from
                 *  com.google.gwt.http.client.Request#fireOnResponseReceived()
                 */
                requestCallback.onError(request, new RuntimeException("Response "
                        + response.getStatusCode() + " for " + method.builder.getHTTPMethod() + " "
                        + method.builder.getUrl()));
            }
            return;
        } else {
            // filter only in success case for now
            for (CallbackFilter f : callbackFilters) {
                requestCallback = f.filter(method, response, requestCallback);
            }
            requestCallback.onResponseReceived(request, response);
        }
    }

    /**
     * replacement for the override of {@link #onResponseReceived(Request, Response)}
     *
     * but this is set to final because of the filter handling, this method has to be
     * implemented instead.
     *
     * @param request
     * @param response
     */
    protected void _onResponseReceived(Request request, Response response) {

    }

    /**
     * TODO when is this used ?
     */
    @Override
    public void onError(Request request, Throwable exception) {
        if (LogConfiguration.loggingIsEnabled()) {
            Logger.getLogger(FilterawareRetryingCallback.class.getName())
                    .severe("call onError in " + this.getClass() + ". this should not happen...");
        }
        handleErrorGracefully(null, null, null);
    }

    public void handleErrorGracefully(Request request, Response response,
            RequestCallback requestCallback) {
        // error handling...:
        if (currentRetryCounter < numberOfRetries) {
            if (LogConfiguration.loggingIsEnabled()) {
                Logger.getLogger(FilterawareRetryingCallback.class.getName()).severe(
                        "error handling in progress for: " + method.builder.getHTTPMethod()
                        + " " + method.builder.getUrl());
            }

            currentRetryCounter++;

            Timer t = new Timer() {
                public void run() {
                    try {
                        method.builder.send();
                    } catch (RequestException ex) {
                        if (LogConfiguration.loggingIsEnabled()) {
                            Logger.getLogger(FilterawareRetryingCallback.class.getName())
                                    .severe(ex.getMessage());
                        }
                    }
                }
            };

            t.schedule(gracePeriod);
            gracePeriod = gracePeriod * 2;
        } else {
            if (LogConfiguration.loggingIsEnabled()) {
                Logger.getLogger(FilterawareRetryingCallback.class.getName()).severe("Request failed: "
                        + method.builder.getHTTPMethod() + " " + method.builder.getUrl()
                        + " after " + currentRetryCounter + " tries.");
            }

            if (null != request
                    && null != response
                    && null != requestCallback) {
                // got the original callback, call error here
                requestCallback.onError(request, new RuntimeException("Response "
                        + response.getStatusCode() + " for " + method.builder.getHTTPMethod() + " "
                        + method.builder.getUrl() + " after " + numberOfRetries + " retries."));
            } else {
                // got no callback - well, goodbye
                if (Window.confirm("error")) {
                    // Super severe error.
                    // reload app or redirect.
                    // ===> this breaks the app but that's by intention.
                    Window.Location.reload();
                }
            }
        }
    }

    /**
     * put a filter in the "chain of responsibility" of all callbackfilters that will be
     * performed on callback passing.
     */
    public void addFilter(CallbackFilter filter) {
        callbackFilters.add(filter);
    }
}
