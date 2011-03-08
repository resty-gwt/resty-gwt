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

import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.cache.QueueableCacheStorage;
import org.fusesource.restygwt.client.dispatcher.CacheKey;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.RequestCallback;

public class CachingCallbackFilter implements CallbackFilter {

    protected QueueableCacheStorage cache;

    public CachingCallbackFilter(QueueableCacheStorage cache) {
        this.cache = cache;
    }

    @Override
    public void filter(Method method, RequestCallback requestCallback) {
        CacheKey cacheKey = new CacheKey(method.builder);
        GWT.log("cache to " + cacheKey);
        cache.putResult(cacheKey, method.getResponse());
    }
}
