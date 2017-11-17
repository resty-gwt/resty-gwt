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

import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.Response;

import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.cache.CacheKey;
import org.fusesource.restygwt.client.cache.QueueableCacheStorage;
import org.fusesource.restygwt.client.cache.UrlCacheKey;
import org.fusesource.restygwt.client.dispatcher.RestfulCachingDispatcherFilter;

/**
 * see {@link RestfulCachingDispatcherFilter}
 */
public class RestfulCachingCallbackFilter extends CachingCallbackFilter {

    public RestfulCachingCallbackFilter(QueueableCacheStorage cache) {
        super(cache);
    }

    @Override
    protected CacheKey cacheKey(RequestBuilder builder) {
        return new UrlCacheKey(builder);
    }

    @Override
    protected boolean isCachingStatusCode(int code) {
        return code == Response.SC_CONFLICT || super.isCachingStatusCode(code);
    }

    @Override
    protected void cacheResult(Method method, Response response) {
        CacheKey cacheKey;
        if (response.getStatusCode() == Response.SC_CREATED && response.getHeader("Location") != null) {
            String uri;
            if (response.getHeader("Location").startsWith("http")) {
                uri = response.getHeader("Location");
            } else {
                // TODO very fragile way of getting the URL
                uri = method.builder.getUrl().replaceFirst("/[^/]*$", "") + response.getHeader("Location");
            }
            cacheKey = new UrlCacheKey(uri);
        } else {
            cacheKey = cacheKey(method.builder);
        }
        if (RequestBuilder.DELETE.toString().equalsIgnoreCase(method.builder.getHTTPMethod()) ||
            // in case of a conflict the next GET request needs to
            // go remote !!
            response.getStatusCode() == Response.SC_CONFLICT) {
            cache.remove(cacheKey);
        } else if (method.builder.getUrl().matches(".*/[0-9]+$")) {
            // if url has an ID at the end then treat it as single entity
            // otherwise assume a collection which are not cached.
            cache.putResult(cacheKey, response);
        }
    }
}
