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

import java.util.ArrayList;
import java.util.List;

import org.fusesource.restygwt.client.AbstractNestedRequestCallback;
import org.fusesource.restygwt.client.Method;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.Response;

public class DefaultFilterawareRequestCallback extends AbstractNestedRequestCallback implements FilterawareRequestCallback {


    final protected List<CallbackFilter> callbackFilters = new ArrayList<CallbackFilter>();

    public DefaultFilterawareRequestCallback(Method method) {
        // need to keep requestcallback here, as ``method.builder.getCallback()`` does not
        // give the same callback later on
        super(method, method.builder.getCallback());
    }

    @Override
    protected void doReceive(Request request, Response response) {
        for (CallbackFilter f : callbackFilters) {
            requestCallback = f.filter(method, response, requestCallback);
        }
        requestCallback.onResponseReceived(request, response);
    }

    @Override
    protected void doError(Request request, Response response){
        for (CallbackFilter f : callbackFilters) {
            requestCallback = f.filter(method, response, requestCallback);
        }
        super.doError(request, response);
    }

    /**
     * put a filter in the "chain of responsibility" of all callbackfilters that will be
     * performed on callback passing.
     */
    @Override
    public void addFilter(CallbackFilter filter) {
        callbackFilters.add(filter);
    }
}
