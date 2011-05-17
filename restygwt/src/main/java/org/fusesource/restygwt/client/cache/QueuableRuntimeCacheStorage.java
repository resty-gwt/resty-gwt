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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.logging.client.LogConfiguration;
import com.google.gwt.user.client.Timer;

public class QueuableRuntimeCacheStorage implements QueueableCacheStorage {
    /**
     * how long will a cachekey be allowed to exist
     */
    private static final long DEFAULT_LIFETIME_MS = 30 * 1000;

    /**
     * key <> value hashmap for holding cache values. nothing special here.
     *
     * invalidated values will be dropped by timer
     */
    private final Map<String, HashMap<CacheKey, Response>> cache =
            new HashMap<String, HashMap<CacheKey, Response>>();

    private final Map<CacheKey, List<RequestCallback>> pendingCallbacks =
            new HashMap<CacheKey, List<RequestCallback>>();

    private final List<Timer> timers = new ArrayList<Timer>();

    public Response getResultOrReturnNull(CacheKey key) {
        return getResultOrReturnNull(key, "");
    }

    public Response getResultOrReturnNull(final CacheKey key, final String scope) {
        final HashMap<CacheKey, Response> scoped = cache.get(scope);
        if (null != scoped) {
            GWT.log(scoped.toString());
            return scoped.get(key);
        }

        return null;
    }

    @Override
    public void putResult(final CacheKey key, final Response response) {
        putResult(key, response, "");
    }

    public void putResult(final CacheKey key, final Response response, final String scope) {
        final Timer t = new Timer() {
            public void run() {
                try {
                    if (LogConfiguration.loggingIsEnabled()) {
                        Logger.getLogger(QueuableRuntimeCacheStorage.class.getName())
                                .finer("removing cache-key " + key + " from scope \"" + scope + "\"");
                    }
                    cache.get(scope).remove(key);
                    timers.remove(this);
                } catch (Exception ex) {
                    Logger.getLogger(QueuableRuntimeCacheStorage.class.getName())
                            .severe(ex.getMessage());
                }
            }
        };
        t.schedule((int) DEFAULT_LIFETIME_MS);
        timers.add(t);

        HashMap<CacheKey, Response> scoped = cache.get(scope);

        if (null == scoped) {
            cache.put(scope, new HashMap<CacheKey, Response>());
            scoped = cache.get(scope);
        }

        scoped.put(key, response);
    }

    @Override
    public void putResult(CacheKey key, Response response, List<String> scopes) {
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
            pendingCallbacks.put(k, new ArrayList<RequestCallback>());
        }

        pendingCallbacks.get(k).add(rc);
    }

    @Override
    public List<RequestCallback> removeCallbacks(final CacheKey k) {
        return pendingCallbacks.remove(k);
    }

    @Override
    public void purge() {
        if (LogConfiguration.loggingIsEnabled()) {
            Logger.getLogger(QueuableRuntimeCacheStorage.class.getName()).finer("remove "
                    + cache.size() + " elements from cache.");
        }
        cache.clear();
        if (LogConfiguration.loggingIsEnabled()) {
            Logger.getLogger(QueuableRuntimeCacheStorage.class.getName()).finer("remove "
                    + timers.size() + " timers from list.");
        }
        for (Timer t: timers) {
            t.cancel();
        }
        timers.clear();
    }

    @Override
    public void purge(final String scope) {
        HashMap<CacheKey, Response> scoped = cache.get(scope);

        // TODO handle timers in scoping too
        if(null != scoped) scoped.clear();
    }
}
