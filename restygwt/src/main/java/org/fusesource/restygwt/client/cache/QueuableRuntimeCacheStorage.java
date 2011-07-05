/**
 * Copyright (C) 2009-2010 the original author or authors. See the notice.md file distributed with
 * this work for additional information regarding copyright ownership.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package org.fusesource.restygwt.client.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.fusesource.restygwt.client.Defaults;

import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.Timer;

public class QueuableRuntimeCacheStorage implements QueueableCacheStorage {

    /**
     * how long will a cachekey be allowed to exist
     */
    public long DEFAULT_LIFETIME_MS = 90 * 1000;

    /**
     * key <> value hashmap for holding cache values. nothing special here.
     *
     * invalidated values will be dropped by timer
     */
    protected final Map<CacheKey, Response> cache =
            new HashMap<CacheKey, Response>();

    /**
     * keep all callbacks to a cachekey that need to be performed when a response comes from the
     * server.
     */
    private final Map<CacheKey, List<RequestCallback>> pendingCallbacks =
            new HashMap<CacheKey, List<RequestCallback>>();

    /**
     * store timers to invalidate data after predefined timeout. each cachekey has one invalidation
     * timer.
     */
    private final List<Timer> timers = new ArrayList<Timer>();

    /**
     * reference from scope to cachekey
     */
    protected final Map<String, List<CacheKey>> scopeRef =
        new HashMap<String, List<CacheKey>>();

    public Response getResultOrReturnNull(CacheKey key) {
        return cache.get(key);
    }

    @Override
    public void putResult(final CacheKey key, final Response response) {
        putResult(key, response, "");
    }

    public void putResult(final CacheKey key, final Response response, final String scope) {
        final Timer t = new Timer() {
            public void run() {
                removeResult(key, scope);
            }
        };

        cache.put(key, response);

        getScope(scope).add(key);

        t.schedule((int) DEFAULT_LIFETIME_MS);
        timers.add(t);
    }

    /**
     * put a result to one or many scopes
     *
     * e.g. <code>
     *      putResult("foo_key", "bar_result", {"UserDto", "ProfileDto"}) {
     * </code>
     */
    @Override
    public void putResult(final CacheKey key, final Response response, final String[] scopes) {
        if (null == scopes) {
            putResult(key, response);
            return;
        }

        int count = 0;

        for (String s: scopes) {
            if (0 == count) {
                putResult(key, response, s);
            } else {
                getScope(s).add(key);
            }
            ++count;
        }
    }

    public void removeResult(CacheKey key) {
        removeResult(key, null);
    }

    public void removeResult(CacheKey key, final String scope) {
        try {
            if (Defaults.canLog()) {
                String s = scope;

                if (null == scope) {
                    s = "";
                }
                Logger.getLogger(QueuableRuntimeCacheStorage.class.getName()).finer(
                        "removing cache-key " + key + " from scope \"" + s + "\"");
            }

            try {
                timers.remove(this);
            } catch (Exception ignored) {
            }

            if (null != scope) {
                List<CacheKey> currentScope = scopeRef.get(scope);

                currentScope.remove(key);
            }

            cache.remove(key);
        } catch (Exception ex) {
            if (Defaults.canLog()) {
                Logger.getLogger(QueuableRuntimeCacheStorage.class.getName()).severe(
                        ex.getMessage());
            }
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
            pendingCallbacks.put(k, new ArrayList<RequestCallback>());
        }

        pendingCallbacks.get(k).add(rc);
    }

    @Override
    public List<RequestCallback> removeCallbacks(final CacheKey k) {
        return pendingCallbacks.remove(k);
    }

    /**
     * purge all
     */
    @Override
    public void purge() {
        if (Defaults.canLog()) {
            Logger.getLogger(QueuableRuntimeCacheStorage.class.getName()).finer(
                    "remove " + cache.size() + " elements from cache.");
        }
        cache.clear();
        if (Defaults.canLog()) {
            Logger.getLogger(QueuableRuntimeCacheStorage.class.getName()).finer(
                    "remove " + timers.size() + " timers from list.");
        }
        for (Timer t : timers) {
            t.cancel();
        }
        timers.clear();
    }

    /**
     * purge only a scope
     */
    @Override
    public void purge(final String scope) {
        List<CacheKey> currentScope = scopeRef.get(scope);
        List<CacheKey> tmpScope = new ArrayList<CacheKey>();

        if (null != currentScope) {
            for (CacheKey k : currentScope) {
                tmpScope.add(k);
            }
            for (CacheKey k : tmpScope) {
                removeResult(k, scope);
            }
        }
    }

    /**
     * helper method to access a scope
     *
     * @param scope
     * @return
     */
    protected List<CacheKey> getScope(final String scope) {
        List<CacheKey> currentScope = scopeRef.get(scope);

        if (null == currentScope) {
            scopeRef.put(scope, new ArrayList<CacheKey>());
            currentScope = scopeRef.get(scope);
        }

        return currentScope;
    }
}
