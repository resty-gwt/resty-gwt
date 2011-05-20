/**
 * Copyright (C) 2010 the original author or authors.
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

import org.fusesource.restygwt.client.Method;

public class DefaultCallbackFactory implements CallbackFactory {

    private final CallbackFilter[] callbackFilters;

    public DefaultCallbackFactory(CallbackFilter... callbackFilter) {
        this.callbackFilters = callbackFilter;
    }

    /**
     * helper method to create the callback with all configurations wanted
     *
     * @param method
     * @return
     */
    public FilterawareRequestCallback createCallback(Method method) {
        final FilterawareRequestCallback retryingCallback = new DefaultFilterawareRequestCallback(
                method);

        for(CallbackFilter filter: callbackFilters){
            retryingCallback.addFilter(filter);
        }
        return retryingCallback;
    }
}
