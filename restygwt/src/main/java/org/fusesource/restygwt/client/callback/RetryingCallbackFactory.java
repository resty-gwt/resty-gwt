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

import org.fusesource.restygwt.client.Method;

public class RetryingCallbackFactory implements CallbackFactory {

    private final CallbackFilter[] callbackFilters;

    private final int gracePeriodMillis;
    private final int numberOfRetries;

    public RetryingCallbackFactory(int gracePeriodMillis, int numberOfRetries, CallbackFilter... callbackFilters) {
        this.callbackFilters = callbackFilters;
        this.gracePeriodMillis = gracePeriodMillis;
        this.numberOfRetries = numberOfRetries;
    }

    public RetryingCallbackFactory(CallbackFilter... callbackFilters) {
        this.callbackFilters = callbackFilters;
        this.gracePeriodMillis = 1000;
        this.numberOfRetries = 5;
    }

    /**
     * helper method to create the callback with all configurations wanted
     *
     * @param method
     * @return
     */
    @Override
    public FilterawareRequestCallback createCallback(Method method) {
        final FilterawareRequestCallback retryingCallback = new RetryingFilterawareRequestCallback(
                method, gracePeriodMillis, numberOfRetries);

        for(CallbackFilter filter: callbackFilters){
            retryingCallback.addFilter(filter);
        }
        return retryingCallback;
    }
}
