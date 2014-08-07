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

package org.fusesource.restygwt.client.callback;

import java.util.logging.Logger;

import org.fusesource.restygwt.client.FailedStatusCodeException;
import org.fusesource.restygwt.client.Method;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.logging.client.LogConfiguration;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;

public class RetryingFilterawareRequestCallback extends DefaultFilterawareRequestCallback {

    /**
     * Used by RetryingCallback
     * default value is 5
     */
    protected int numberOfRetries = 5;

    /**
     * time to wait for reconnect upon failure
     */
    protected int gracePeriod = 1000;

    protected int currentRetryCounter = 0;

    public RetryingFilterawareRequestCallback(Method method) {
        super(method);
    }

    public RetryingFilterawareRequestCallback(Method method,
            int gracePeriodMillis, int numberOfRetries) {
        super(method);
        this.gracePeriod = gracePeriodMillis;
        this.numberOfRetries = numberOfRetries;
    }

    @Override
    public final void doError(Request request, Response response) {
            int code = response.getStatusCode();
            /*
             * retry only on GET requests that are no redirects (301, 302, 303)
             */
            if (code != 301
                    && code != 302
                    && code != 303
                    && code != 404
                    && (method.builder == null // jsonp method do not have a builder !! 
                            || method.builder.getHTTPMethod().equalsIgnoreCase("get"))) {
                handleErrorGracefully(request, response, requestCallback);
            } else {
                if (LogConfiguration.loggingIsEnabled()) {
                    Logger.getLogger(RetryingFilterawareRequestCallback.class.getName()).severe(
                            "ERROR with non-GET method: " + method.builder.getHTTPMethod() + " "
                            + method.builder.getUrl() + ", " + response.getStatusText());
                }

                /*
                 *  RuntimeException token from
                 *  com.google.gwt.http.client.Request#fireOnResponseReceived()
                 */
                requestCallback.onError(request, new FailedStatusCodeException(response.getStatusText(), response.getStatusCode()));
            }
    }

    private void handleErrorGracefully(Request request, Response response,
            RequestCallback requestCallback) {
        // error handling...:
        if (currentRetryCounter < numberOfRetries) {
            if (GWT.isClient() && LogConfiguration.loggingIsEnabled()) {
                Logger.getLogger(RetryingFilterawareRequestCallback.class.getName()).severe(
                        "error handling in progress for: " + method.builder.getHTTPMethod()
                        + " " + method.builder.getUrl());
            }

            currentRetryCounter++;

            Timer t = new Timer() {
                @Override
                public void run() {
                    try {
                        method.builder.send();
                    } catch (RequestException ex) {
                        if (GWT.isClient() && LogConfiguration.loggingIsEnabled()) {
                            Logger.getLogger(RetryingFilterawareRequestCallback.class.getName())
                                    .severe(ex.getMessage());
                        }
                    }
                }
            };

            t.schedule(gracePeriod);
            gracePeriod = gracePeriod * 2;
        } else {
            if (GWT.isClient() && LogConfiguration.loggingIsEnabled()) {
                Logger.getLogger(RetryingFilterawareRequestCallback.class.getName()).severe("Request failed: "
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
                if (Window.confirm("something severly went wrong - error - reload page ?")) {
                    // Super severe error.
                    // reload app or redirect.
                    // ===> this breaks the app but that's by intention.
                    Window.Location.reload();
                }
            }
        }
    }
}
