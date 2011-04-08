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

import java.util.logging.Logger;

import org.fusesource.restygwt.client.Dispatcher;
import org.fusesource.restygwt.client.FilterawareRequestCallback;
import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.cache.CacheKey;
import org.fusesource.restygwt.client.cache.QueueableCacheStorage;
import org.fusesource.restygwt.client.cache.UrlCacheKey;
import org.fusesource.restygwt.client.callback.CallbackFactory;
import org.fusesource.restygwt.client.callback.FilterawareRetryingCallback;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.logging.client.LogConfiguration;

/**
 * Some valuable ideas came from:
 * http://turbomanage.wordpress.com/2010/07/12/caching-batching-dispatcher-for-gwt-dispatch/
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

    private static CachingRetryingDispatcher INSTANCE = null;

    /**
     * where to get a callback from. gives us the ability to use
     * customized {@link FilterawareRequestCallback}
     */
    private CallbackFactory callbackFactory;

    /**
     * one instance of {@link QueueableCacheStorage}
     */
    private QueueableCacheStorage cacheStorage;

    /**
     * get one instance of this class
     *
     * @param cacheStorage the one and only {@link QueueableCacheStorage} for this instance
     * @param cf CallbackFactory to be able to use {@link FilterawareRetryingCallback}
     * @return
     */
    public static CachingRetryingDispatcher singleton(QueueableCacheStorage cacheStorage,
            CallbackFactory cf) {
        if (null != INSTANCE) return INSTANCE;

        INSTANCE = new CachingRetryingDispatcher();
        INSTANCE.cacheStorage = cacheStorage;
        INSTANCE.setCallbackFactory(cf);
        return INSTANCE;
    }

    public Request send(Method method, RequestBuilder builder) throws RequestException {
        final RequestCallback outerRequestCallback = builder.getCallback();
        final CacheKey cacheKey = new UrlCacheKey(builder);
        final Response cachedResponse = cacheStorage.getResultOrReturnNull(cacheKey);
        final boolean cachable = builder.getHTTPMethod().equals(RequestBuilder.GET.toString());

        if (cachable == true) {
            if (cachedResponse != null) {
                //case 1: we got a result in cache => return it...
                if (LogConfiguration.loggingIsEnabled()) {
                    Logger.getLogger(Dispatcher.class.getName())
                            .info("already got a cached response for: " + builder.getHTTPMethod() + " "
                            + builder.getUrl());
                }
                outerRequestCallback.onResponseReceived(null, cachedResponse);
                return null;
            }  else {
                final RequestCallback retryingCallback = callbackFactory.createCallback(method);

                //case 2: => no cache in result => queue it....
                if (!cacheStorage.hasCallback(cacheKey)) {
                    //case 2.1 => first callback => make a new one and execute...
                    cacheStorage.addCallback(cacheKey, outerRequestCallback);

                    if (LogConfiguration.loggingIsEnabled()) {
                        Logger.getLogger(Dispatcher.class.getName())
                                .info("Sending *caching* http request: " + builder.getHTTPMethod() + " "
                                + builder.getUrl());
                    }

                    // important part:
                    builder.setCallback(retryingCallback);
                    return builder.send();
                } else {
                    //case 2.2 => a callback already in progress => queue to get response when back
                    if (LogConfiguration.loggingIsEnabled()) {
                        Logger.getLogger(Dispatcher.class.getName())
                                .info("request in progress, queue callback: " + builder.getHTTPMethod() + " "
                                + builder.getUrl());
                    }
                    cacheStorage.addCallback(cacheKey, retryingCallback);
                    return null;
                }
            }
        } else {
            // non cachable case
            if (LogConfiguration.loggingIsEnabled()) {
                String content = builder.getRequestData();
                Logger.getLogger(Dispatcher.class.getName())
                        .info("Sending *non-caching* http request: " + builder.getHTTPMethod() + " "
                        + builder.getUrl() + " (Content: `" + content + "´)");
            }

            builder.setCallback(callbackFactory.createCallback(method));
            return builder.send();
        }
    }

    /**
     * set the callbackFactory once on creation. non-public with purpose as this is
     * an immutable field.
     *
     * @param callbackFactory
     */
    private void setCallbackFactory(CallbackFactory callbackFactory) {
        this.callbackFactory = callbackFactory;
    }
}
