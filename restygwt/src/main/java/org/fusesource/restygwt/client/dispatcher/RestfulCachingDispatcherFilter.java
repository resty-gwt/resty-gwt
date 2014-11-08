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

package org.fusesource.restygwt.client.dispatcher;

import org.fusesource.restygwt.client.cache.CacheKey;
import org.fusesource.restygwt.client.cache.QueueableCacheStorage;
import org.fusesource.restygwt.client.cache.UrlCacheKey;
import org.fusesource.restygwt.client.callback.CallbackFactory;

import com.google.gwt.http.client.RequestBuilder;

/**
 * using a different caching 'algorithm' obeying the restful paradigm, i.e.
 * the cache respects the lifecycle of a restful resource:
 * <ul>
 * <li>POST /model : will get the user responds into the cache using the location header for the key</li>
 * <li>GET /model/{id} : will use the cached responds from cache if present</li>
 * <li>PUT /model/{id} : will put the responds from the server into the cache. a conflict will delete the cache entry to allow
 * a get to retrieve the up to date data</li>
 * <li>DELETE /model/{id} : will also delete the resource in the cache</li>
 * </ul>
 *
 * @author <a href="blog.mkristian.tk">Kristian</a>
 */
public class RestfulCachingDispatcherFilter extends CachingDispatcherFilter {

    public RestfulCachingDispatcherFilter(QueueableCacheStorage cacheStorage,
            CallbackFactory cf) {
        super(cacheStorage, cf);
    }
    
    @Override
    protected CacheKey cacheKey(RequestBuilder builder){
        if (RequestBuilder.GET.toString().equalsIgnoreCase(builder.getHTTPMethod())){
            return new UrlCacheKey(builder);
        }
        return null;
    }
}
