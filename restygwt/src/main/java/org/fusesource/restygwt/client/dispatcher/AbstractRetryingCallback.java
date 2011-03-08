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
package org.fusesource.restygwt.client.dispatcher;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.fusesource.restygwt.client.Method;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.FilterawareRequestCallback;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.callback.CallbackFilter;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;

public abstract class AbstractRetryingCallback implements FilterawareRequestCallback {

    /**
     * Used by RetryingCallback
     * default value is 5
     */
    protected static int numberOfRetries = 5;

    protected static Logger logger = Logger.getLogger(AbstractRetryingCallback.class.getName());

    /**
     * time to wait for reconnect upon failure
     */
    protected int gracePeriod = 1000;

    protected int currentRetryCounter = 0;

    protected final Method method;
    protected final RequestCallback requestCallback;

    protected List<CallbackFilter> callbackFilters;

    public AbstractRetryingCallback(Method method, RequestCallback requestCallback) {

        this.method = method;
        this.requestCallback = requestCallback;
    }

    @Override
    public final void onResponseReceived(Request request, Response response) {
        for (CallbackFilter f : callbackFilters) {
            f.filter(method, requestCallback);
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
    protected abstract void _onResponseReceived(Request request, Response response);

    @Override
    public void onError(Request request, Throwable exception) {
        handleErrorGracefully();
    }

    public void handleErrorGracefully() {

        // error handling...:
        if (currentRetryCounter < numberOfRetries) {
            GWT.log("error handling in progress...");

            currentRetryCounter++;

            Timer t = new Timer() {
                public void run() {
                    try {
                        method.builder.send();
                    } catch (RequestException ex) {
                        logger.severe(ex.getMessage());
                    }
                }
            };

            t.schedule(gracePeriod);
            gracePeriod = gracePeriod * 2;
        } else {
            // Super severe error.
            // reload app or redirect.
            // ===> this breaks the app but that's by intention.
            if (Window.confirm("error")) {
                Window.Location.reload();
            }
        }
    }

    /**
     * put a filter in the "chain of responsibility" of all callbackfilters that will be
     * performed on callback passing.
     */
    public void addFilter(CallbackFilter filter) {
        if (null == callbackFilters) {
            callbackFilters = new ArrayList<CallbackFilter>();
        }

        callbackFilters.add(filter);
    }

}
