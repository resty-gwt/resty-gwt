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

import org.fusesource.restygwt.client.Dispatcher;
import org.fusesource.restygwt.client.FilterawareRequestCallback;
import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.cache.QueuableRuntimeCacheStorage;
import org.fusesource.restygwt.client.cache.QueueableCacheStorage;
import org.fusesource.restygwt.client.callback.CachingCallbackFilter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;

/**
 * Some valuable ideas came from:
 * http://turbomanage.wordpress.com/2010/07/12/caching
 * -batching-dispatcher-for-gwt-dispatch/
 * <p/>
 * Thanks David!
 * <p/>
 * Especially: - Waiting if a particular request is already on the way
 * (otherwise you end up having many requests on the same source.
 *
 * @author <a href="mailto:mail@raphaelbauer.com">rEyez</<a>
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 */
public class CachingRetryingDispatcher implements Dispatcher {

    public static final CachingRetryingDispatcher INSTANCE = new CachingRetryingDispatcher();

    /**
     * one instance of {@link QueueableCacheStorage}
     *
     * could be static as well, but since we are a singleton, non-static is more nice
     */
    private QueueableCacheStorage cacheStorage = new QueuableRuntimeCacheStorage();

    public Request send(Method method, RequestBuilder builder) throws RequestException {

        final RequestCallback outerRequestCallback = builder.getCallback();
        final CacheKey cacheKey = new CacheKey(builder);

        final Response cachedResponse = cacheStorage.getResultOrReturnNull(cacheKey);

        if (cachedResponse != null) {
            Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                @Override
                public void execute() {
                    outerRequestCallback.onResponseReceived(null,
                            cachedResponse);
                }
            });
            return null;

        } else {

            FilterawareRequestCallback retryingCallback = new AbstractRetryingCallback(method, outerRequestCallback) {
                @Override
                protected void _onResponseReceived(Request request, Response response) {
                }
            };
            retryingCallback.addFilter(new CachingCallbackFilter(cacheStorage));
            builder.setCallback(retryingCallback);

            GWT.log("Sending http request: " + builder.getHTTPMethod() + " "
                    + builder.getUrl() + " ,timeout:"
                    + builder.getTimeoutMillis(), null);

            String content = builder.getRequestData();

            if (content != null && content.length() > 0) {
                GWT.log(content, null);
            }

            return builder.send();
        }
    }
}
