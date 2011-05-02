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
import org.fusesource.restygwt.client.cache.QueueableCacheStorage;

public class CachingCallbackFactory implements CallbackFactory {

    private final QueueableCacheStorage cacheStorage;

    public CachingCallbackFactory(QueueableCacheStorage cacheStorage) {
        this.cacheStorage = cacheStorage;
    }

    /**
     * helper method to create the callback with all configurations wanted
     *
     * @param method
     * @return
     */
    public FilterawareRequestCallback createCallback(Method method) {
        final FilterawareRequestCallback retryingCallback = new FilterawareRetryingCallback(
                method);

        retryingCallback.addFilter(new CachingCallbackFilter(cacheStorage));
        return retryingCallback;
    }
}
