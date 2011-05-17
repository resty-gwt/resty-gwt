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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.cache.QueuableRuntimeCacheStorage;
import org.fusesource.restygwt.client.cache.QueueableCacheStorage;
import org.fusesource.restygwt.client.callback.CachingCallbackFactory;
import org.fusesource.restygwt.client.callback.FilterawareRetryingCallback;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;
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
public class FilterawareRetryingDispatcher implements FilterawareDispatcher {

    public static FilterawareRetryingDispatcher INSTANCE;
    static {
        QueueableCacheStorage storage = new QueuableRuntimeCacheStorage();
        INSTANCE = new FilterawareRetryingDispatcher(new CachingDispatcherFilter(storage, 
            new CachingCallbackFactory(storage)));
    }
    
    /**
     * list of dispatcherfilters to be performed when an request is done
     */
    final protected List<DispatcherFilter> dispatcherFilters =
            new ArrayList<DispatcherFilter>();

    /**
     * get one instance of this class
     *
     * @param cacheStorage the one and only {@link QueueableCacheStorage} for this instance
     * @param cf CallbackFactory to be able to use {@link FilterawareRetryingCallback}
     * @return
     */
    public static FilterawareRetryingDispatcher singleton() {
        if (null != INSTANCE) return INSTANCE;

        INSTANCE = new FilterawareRetryingDispatcher();
        return INSTANCE;
    }
    
    public FilterawareRetryingDispatcher(){    
    }
    
    public FilterawareRetryingDispatcher(DispatcherFilter filter){
        addFilter(filter);
    }
    
    public Request send(Method method, RequestBuilder builder) throws RequestException {
        for (DispatcherFilter f : dispatcherFilters) {
            if (!f.filter(method, builder)) {
                // filter returned false, no continue
                if (LogConfiguration.loggingIsEnabled()) {
                    Logger.getLogger(FilterawareRetryingDispatcher.class.getName())
                            .fine(f.getClass() + " told me not to continue filtering for: "
                                    + builder.getHTTPMethod() + " " + builder.getUrl());
                }
                return null;
            }
        }

        return builder.send();
    }

    /**
     * well, add one more dispatcherfilter
     */
    @Override
    public void addFilter(DispatcherFilter filter) {
        this.dispatcherFilters.add(filter);
    }
}
