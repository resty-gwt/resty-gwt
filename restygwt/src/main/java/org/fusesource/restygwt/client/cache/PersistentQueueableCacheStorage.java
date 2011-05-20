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

package org.fusesource.restygwt.client.cache;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.logging.client.LogConfiguration;

public class PersistentQueueableCacheStorage implements QueueableCacheStorage {
    
    private static final String DEFAULT_SCOPE = "";

    /**
     * key <> value hashmap for holding cache values. nothing special here.
     *
     * invalidated values will be dropped by timer
     */
    protected final Map<String, HashMap<CacheKey, Response>> cache =
            new HashMap<String, HashMap<CacheKey, Response>>();

    private final Map<CacheKey, Set<RequestCallback>> pendingCallbacks =
            new HashMap<CacheKey, Set<RequestCallback>>();

    public Response getResultOrReturnNull(CacheKey key) {
        return getResultOrReturnNull(key, DEFAULT_SCOPE);
    }

    public Response getResultOrReturnNull(final CacheKey key, final String scope) {
        final HashMap<CacheKey, Response> scoped = cache.get(scope);
        if (null != scoped) {
            GWT.log("hmm--->" + scoped.toString());
            return scoped.get(key);
        }

        return null;
    }

    @Override
    public void putResult(final CacheKey key, final Response response) {
        putResult(key, response, DEFAULT_SCOPE);
    }

    protected void putResult(final CacheKey key, final Response response, final String scope) {
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
        //init value of key if not there...
        if (!pendingCallbacks.containsKey(k)) {
            pendingCallbacks.put(k, new java.util.LinkedHashSet<RequestCallback>());
        }

        pendingCallbacks.get(k).add(rc);
    }

    @Override
    public Set<RequestCallback> removeCallbacks(final CacheKey k) {
        return pendingCallbacks.remove(k);
    }

    @Override
    public void purge() {
        if (LogConfiguration.loggingIsEnabled()) {
            Logger.getLogger(PersistentQueueableCacheStorage.class.getName()).finer("remove "
                    + cache.size() + " elements from cache.");
        }
        cache.clear();
    }

    @Override
    public void purge(final String scope) {
        HashMap<CacheKey, Response> scoped = cache.get(scope);

        // TODO handle timers in scoping too
        if(null != scoped) scoped.clear();
    }

    @Override
    public void remove(CacheKey key) {
        remove(key, DEFAULT_SCOPE);
    }

    @Override
    public void remove(CacheKey key, String... scopes) {
        if(scopes != null){
            for(String scope: scopes){
                remove(key, scope);
            }
        }
    }
    
    private void remove(CacheKey key, String scope){
        if (LogConfiguration.loggingIsEnabled()) {
            Logger.getLogger(PersistentQueueableCacheStorage.class.getName())
                    .finer(
                            "removing cache-key " + key + " from scope \""
                                    + scope + "\"");
        }

        HashMap<CacheKey, Response> scoped = cache.get(scope);
        if(null != scoped) scoped.remove(key);
    }
}
