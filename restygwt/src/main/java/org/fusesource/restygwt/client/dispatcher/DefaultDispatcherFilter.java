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

package org.fusesource.restygwt.client.dispatcher;

import java.util.logging.Logger;

import org.fusesource.restygwt.client.Dispatcher;
import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.callback.CallbackFactory;
import org.fusesource.restygwt.client.callback.FilterawareRequestCallback;

import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.logging.client.LogConfiguration;

public class DefaultDispatcherFilter implements DispatcherFilter {

    /**
     * where to get a callback from. gives us the ability to use
     * customized {@link FilterawareRequestCallback}
     */
    private CallbackFactory callbackFactory;

    /**
     * the one and only constructor
     * @param cf
     */
    public DefaultDispatcherFilter(final CallbackFactory cf) {
        this.callbackFactory = cf;
    }

    /**
     * main filter method for a dispatcherfilter.
     *
     * @return continue filtering or not
     */
    @Override
    public boolean filter(final Method method, final RequestBuilder builder) {
        if (LogConfiguration.loggingIsEnabled()) {
            Logger.getLogger(Dispatcher.class.getName()).info(
                    "Sending http request: " + builder.getHTTPMethod() + " "
                            + builder.getUrl());
        }

        builder.setCallback(callbackFactory.createCallback(method));
        return true;
    }
}
