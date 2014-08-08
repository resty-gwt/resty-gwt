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

/**
 * default callback factory with a given set of callback filters which
 * gets added to a new callback after creating it.
 * 
 * @author <a href="blog.mkristian.tk">Kristian</a>
 *
 */
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
    @Override
    public FilterawareRequestCallback createCallback(Method method) {
        final FilterawareRequestCallback callback = new DefaultFilterawareRequestCallback(
                method);

        for(CallbackFilter filter: callbackFilters){
            callback.addFilter(filter);
        }
        return callback;
    }
}
