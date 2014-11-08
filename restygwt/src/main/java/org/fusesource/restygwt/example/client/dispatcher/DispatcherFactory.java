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

package org.fusesource.restygwt.example.client.dispatcher;

import org.fusesource.restygwt.client.cache.DefaultQueueableCacheStorage;
import org.fusesource.restygwt.client.cache.QueueableCacheStorage;
import org.fusesource.restygwt.client.cache.VolatileQueueableCacheStorage;
import org.fusesource.restygwt.client.callback.CachingCallbackFilter;
import org.fusesource.restygwt.client.callback.CallbackFactory;
import org.fusesource.restygwt.client.callback.CallbackFilter;
import org.fusesource.restygwt.client.callback.DefaultCallbackFactory;
import org.fusesource.restygwt.client.callback.RestfulCachingCallbackFilter;
import org.fusesource.restygwt.client.callback.RetryingCallbackFactory;
import org.fusesource.restygwt.client.callback.XSRFToken;
import org.fusesource.restygwt.client.callback.XSRFTokenCallbackFilter;
import org.fusesource.restygwt.client.dispatcher.CachingDispatcherFilter;
import org.fusesource.restygwt.client.dispatcher.DefaultDispatcherFilter;
import org.fusesource.restygwt.client.dispatcher.DefaultFilterawareDispatcher;
import org.fusesource.restygwt.client.dispatcher.DispatcherFilter;
import org.fusesource.restygwt.client.dispatcher.FilterawareDispatcher;
import org.fusesource.restygwt.client.dispatcher.RestfulCachingDispatcherFilter;
import org.fusesource.restygwt.client.dispatcher.XSRFTokenDispatcherFilter;

/**
 * this factory can be used to create a Dispatcher which can be used as dispatcher Option on
 * the RestService interface.
 *
 * <pre><code>
 * public class RestfulRetryingDispatcherSingleton implements Dispatcher{
 *
 *   public static Dispatcher INSTANCE = new DispatcherFactory().restfulCachingDispatcher();
 *
 *   // do not allow concrete instances of this class
 *   private RestfulRetryingDispatcherSingleton(){
 *     throw new Error("never called");
 *   }
 *
 *   public Request send(Method method, RequestBuilder builder) throws RequestException {
 *     return null;   // dummy
 *   }
 * }
 * </code></pre>
 *
 * @author kristian
 *
 */
public class DispatcherFactory {

    public static final XSRFToken xsrf = new XSRFToken();

    public FilterawareDispatcher xsrfProtectionDispatcher(){
        DispatcherFilter xsrfDispatcherFilter = new XSRFTokenDispatcherFilter(xsrf);

        CallbackFilter xsrfCallbackFilter = new XSRFTokenCallbackFilter(xsrf);
        CallbackFactory callbackFactory = new DefaultCallbackFactory(xsrfCallbackFilter);
        DispatcherFilter defaultDispatcherFilter = new DefaultDispatcherFilter(callbackFactory);

        FilterawareDispatcher dispatcher = new DefaultFilterawareDispatcher(xsrfDispatcherFilter, defaultDispatcherFilter);

        return dispatcher;
    }

    public FilterawareDispatcher cachingDispatcher(){
        QueueableCacheStorage cache = new DefaultQueueableCacheStorage();

        CallbackFilter cachingCallbackFilter = new CachingCallbackFilter(cache);
        CallbackFactory callbackFactory = new DefaultCallbackFactory(cachingCallbackFilter);
        DispatcherFilter cachingDispatcherFilter = new CachingDispatcherFilter(cache,callbackFactory);

        FilterawareDispatcher dispatcher = new DefaultFilterawareDispatcher(cachingDispatcherFilter);

        return dispatcher;
    }

    public FilterawareDispatcher cachingXSRFProtectionDispatcher(){
        DispatcherFilter xsrfDispatcherFilter = new XSRFTokenDispatcherFilter(xsrf);

        CallbackFilter xsrfCallbackFilter = new XSRFTokenCallbackFilter(xsrf);

        QueueableCacheStorage cache = new DefaultQueueableCacheStorage();

        CallbackFilter cachingCallbackFilter = new CachingCallbackFilter(cache);
        CallbackFactory callbackFactory = new DefaultCallbackFactory(xsrfCallbackFilter, cachingCallbackFilter);
        DispatcherFilter cachingDispatcherFilter = new CachingDispatcherFilter(cache, callbackFactory);

        FilterawareDispatcher dispatcher = new DefaultFilterawareDispatcher(xsrfDispatcherFilter, cachingDispatcherFilter);

        return dispatcher;
    }

    public FilterawareDispatcher retryingDispatcher(){
        QueueableCacheStorage cache = new VolatileQueueableCacheStorage();

        CallbackFilter cachingCallbackFilter = new CachingCallbackFilter(cache);
        CallbackFactory callbackFactory = new RetryingCallbackFactory(cachingCallbackFilter);
        DispatcherFilter cachingDispatcherFilter = new CachingDispatcherFilter(cache, callbackFactory);

        FilterawareDispatcher dispatcher = new DefaultFilterawareDispatcher(cachingDispatcherFilter);

        return dispatcher;
    }

    public FilterawareDispatcher retryingCachingDispatcher(){
        QueueableCacheStorage cache = new DefaultQueueableCacheStorage();

        CallbackFilter cachingCallbackFilter = new CachingCallbackFilter(cache);
        CallbackFactory callbackFactory = new RetryingCallbackFactory(cachingCallbackFilter);
        DispatcherFilter cachingDispatcherFilter = new CachingDispatcherFilter(cache, callbackFactory);

        FilterawareDispatcher dispatcher = new DefaultFilterawareDispatcher(cachingDispatcherFilter);

        return dispatcher;
    }

    public FilterawareDispatcher retryingCachingXSRFProtectionDispatcher(){
        DispatcherFilter xsrfDispatcherFilter = new XSRFTokenDispatcherFilter(xsrf);

        CallbackFilter xsrfCallbackFilter = new XSRFTokenCallbackFilter(xsrf);

        QueueableCacheStorage cache = new DefaultQueueableCacheStorage();

        CallbackFilter cachingCallbackFilter = new CachingCallbackFilter(cache);
        CallbackFactory callbackFactory = new RetryingCallbackFactory(xsrfCallbackFilter, cachingCallbackFilter);
        DispatcherFilter cachingDispatcherFilter = new CachingDispatcherFilter(cache, callbackFactory);

        FilterawareDispatcher dispatcher = new DefaultFilterawareDispatcher(xsrfDispatcherFilter, cachingDispatcherFilter);

        return dispatcher;
    }

    public FilterawareDispatcher restfulCachingDispatcher(){
        QueueableCacheStorage cache = new DefaultQueueableCacheStorage();

        CallbackFilter cachingCallbackFilter = new RestfulCachingCallbackFilter(cache);
        CallbackFactory callbackFactory = new DefaultCallbackFactory(cachingCallbackFilter);
        DispatcherFilter cachingDispatcherFilter = new RestfulCachingDispatcherFilter(cache, callbackFactory);

        FilterawareDispatcher dispatcher = new DefaultFilterawareDispatcher(cachingDispatcherFilter);

        return dispatcher;
    }

    public FilterawareDispatcher restfulCachingXSRFProtectionDispatcher(){
        DispatcherFilter xsrfDispatcherFilter = new XSRFTokenDispatcherFilter(xsrf);

        CallbackFilter xsrfCallbackFilter = new XSRFTokenCallbackFilter(xsrf);

        QueueableCacheStorage cache = new DefaultQueueableCacheStorage();

        CallbackFilter cachingCallbackFilter = new RestfulCachingCallbackFilter(cache);
        CallbackFactory callbackFactory = new DefaultCallbackFactory(xsrfCallbackFilter, cachingCallbackFilter);
        DispatcherFilter cachingDispatcherFilter = new RestfulCachingDispatcherFilter(cache, callbackFactory);

        FilterawareDispatcher dispatcher = new DefaultFilterawareDispatcher(xsrfDispatcherFilter, cachingDispatcherFilter);

        return dispatcher;
    }

    public FilterawareDispatcher restfulRetryingCachingDispatcher(){
        QueueableCacheStorage cache = new DefaultQueueableCacheStorage();

        CallbackFilter cachingCallbackFilter = new RestfulCachingCallbackFilter(cache);
        CallbackFactory callbackFactory = new RetryingCallbackFactory(cachingCallbackFilter);
        DispatcherFilter cachingDispatcherFilter = new RestfulCachingDispatcherFilter(cache, callbackFactory);

        FilterawareDispatcher dispatcher = new DefaultFilterawareDispatcher(cachingDispatcherFilter);

        return dispatcher;
    }

    public FilterawareDispatcher restfulRetryingCachingXSRFProtectionDispatcher(){
        DispatcherFilter xsrfDispatcherFilter = new XSRFTokenDispatcherFilter(xsrf);

        CallbackFilter xsrfCallbackFilter = new XSRFTokenCallbackFilter(xsrf);

        QueueableCacheStorage cache = new DefaultQueueableCacheStorage();

        CallbackFilter cachingCallbackFilter = new RestfulCachingCallbackFilter(cache);
        CallbackFactory callbackFactory = new RetryingCallbackFactory(xsrfCallbackFilter, cachingCallbackFilter);
        DispatcherFilter cachingDispatcherFilter = new RestfulCachingDispatcherFilter(cache, callbackFactory);

        FilterawareDispatcher dispatcher = new DefaultFilterawareDispatcher(xsrfDispatcherFilter, cachingDispatcherFilter);

        return dispatcher;
    }

}
