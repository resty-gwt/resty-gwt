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

package org.fusesource.restygwt.client.cache;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Header;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.logging.client.LogConfiguration;

/**
 * this implementation stores Response objects until they are removed or purged. when retrieved from
 * the cache the Response will have an extra header field "X-Resty-Cache". this allows CallbackFilter to
 * determine the action on whether the Response came from the cache or just came over the wire.
 *
 * @author kristian
 *
 */
public class DefaultQueueableCacheStorage implements QueueableCacheStorage {

    public static class ResponseWrapper extends Response {

        // keep it public for testing
        public final Response response;

        @Override
        public boolean equals(Object obj) {
            return response.equals(obj);
        }

        @Override
        public String getHeader(String header) {
            if (RESTY_CACHE_HEADER.equals(header)) {
                return "true";
            }
            return response.getHeader(header);
        }

        @Override
        public Header[] getHeaders() {
            List<Header> headers = Arrays.asList(response.getHeaders());
            headers.add(new Header() {

                @Override
                public String getValue() {
                    return "true";
                }

                @Override
                public String getName() {
                    return RESTY_CACHE_HEADER;
                }
            });
            return (Header[]) headers.toArray();
        }

        @Override
        public String getHeadersAsString() {
            return response.getHeadersAsString() + RESTY_CACHE_HEADER
                    + "=true\r\n";
        }

        @Override
        public int getStatusCode() {
            return response.getStatusCode();
        }

        @Override
        public String getStatusText() {
            return response.getStatusText();
        }

        @Override
        public String getText() {
            return response.getText();
        }

        @Override
        public int hashCode() {
            return response.hashCode();
        }

        @Override
        public String toString() {
            return response.toString();
        }

        ResponseWrapper(Response resp) {
            this.response = resp;
        }
    }

    private static final String DEFAULT_SCOPE = "";

    /**
     * key-value hashmap for holding cache values. nothing special here.
     * 
     * invalidated values will be dropped by timer
     */
    protected final Map<String, HashMap<CacheKey, Response>> cache = new HashMap<String, HashMap<CacheKey, Response>>();

    private final Map<CacheKey, List<RequestCallback>> pendingCallbacks = new HashMap<CacheKey, List<RequestCallback>>();

    @Override
    public Response getResultOrReturnNull(CacheKey key) {
        return getResultOrReturnNull(key, DEFAULT_SCOPE);
    }

    @Override
    public Response getResultOrReturnNull(final CacheKey key, final String scope) {
        final HashMap<CacheKey, Response> scoped = cache.get(scope);
        if (null != scoped) {
            Response result = scoped.get(key);
            if (result != null) {
                return new ResponseWrapper(result);
            }
        }

        return null;
    }

    @Override
    public void putResult(final CacheKey key, final Response response) {
        putResult(key, response, DEFAULT_SCOPE);
    }

    protected void putResult(final CacheKey key, final Response response,
            final String scope) {
        HashMap<CacheKey, Response> scoped = cache.get(scope);

        if (null == scoped) {
            cache.put(scope, new HashMap<CacheKey, Response>());
            scoped = cache.get(scope);
        }

        scoped.put(key, response);
    }

    @Override
    public void putResult(CacheKey key, Response response, String... scopes) {
        if (null == scopes) {
            putResult(key, response);
            return;
        }

        // TODO mark multi-scoped values as one invalidation group
        // TODO remove redundant storage
        for (String scope : scopes) {
            putResult(key, response, scope);
        }
    }

    @Override
    public boolean hasCallback(final CacheKey k) {
        return pendingCallbacks.containsKey(k);
    }

    @Override
    public void addCallback(final CacheKey k, final RequestCallback rc) {
        // init value of key if not there...
        if (!pendingCallbacks.containsKey(k)) {
            pendingCallbacks
                    .put(k, new java.util.LinkedList<RequestCallback>());
        }

        // just add callbacks which are not already there
        if (!pendingCallbacks.get(k).contains(rc)) {
            pendingCallbacks.get(k).add(rc);
        }
    }

    @Override
    public List<RequestCallback> removeCallbacks(final CacheKey k) {
        return pendingCallbacks.remove(k);
    }

    @Override
    public void purge() {
        if (GWT.isClient() && LogConfiguration.loggingIsEnabled()) {
            Logger.getLogger(DefaultQueueableCacheStorage.class.getName())
                    .finer("remove " + cache.size() + " elements from cache.");
        }
        cache.clear();
    }

    @Override
    public void purge(final String scope) {
        HashMap<CacheKey, Response> scoped = cache.get(scope);

        // TODO handle timers in scoping too
        if (null != scoped)
            scoped.clear();
    }

    @Override
    public void remove(CacheKey key) {
        doRemove(key, DEFAULT_SCOPE);
    }

    @Override
    public void remove(CacheKey key, String... scopes) {
        if (scopes != null) {
            for (String scope : scopes) {
                doRemove(key, scope);
            }
        }
    }

    private void doRemove(CacheKey key, String scope) {
        if (GWT.isClient() && LogConfiguration.loggingIsEnabled()) {
            Logger.getLogger(DefaultQueueableCacheStorage.class.getName())
                    .finer("removing cache-key " + key + " from scope \""
                            + scope + "\"");
        }

        HashMap<CacheKey, Response> scoped = cache.get(scope);
        if (null != scoped)
            scoped.remove(key);
    }
}
