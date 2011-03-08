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

import org.fusesource.restygwt.client.dispatcher.CacheKey;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
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
    private final HashMap<CacheKey, Response> cache = new HashMap<CacheKey, Response>();

    private final Map<CacheKey, List<RequestCallback>> pendingCallbacks
            = new HashMap<CacheKey, List<RequestCallback>>();

    public Response getResultOrReturnNull(CacheKey key) {
        Response val = cache.get(key);

        return val;
    }

    public void putResult(final CacheKey key, Response response) {
        Timer t = new Timer() {
            public void run() {
                try {
                    GWT.log("removing cache-key " + key.getEverythingAsConcatenatedString()
                            + " from internal storage");
                    cache.remove(key);
                } catch (Exception ex) {
                    Logger.getLogger(QueuableRuntimeCacheStorage.class.getName())
                            .severe(ex.getMessage());
                }
            }
        };
        t.schedule((int) DEFAULT_LIFETIME_MS);
        cache.put(key, response);
    }

    @Override
    public boolean hasCallback(CacheKey k) {
        return pendingCallbacks.containsKey(k);
    }

    @Override
    public void addCallback(CacheKey k, RequestCallback rc) {
        //init value of key if not there...
        if (!pendingCallbacks.containsKey(k)) {
            pendingCallbacks.put(k, new ArrayList<RequestCallback>());
        }

        pendingCallbacks.get(k).add(rc);
    }

    @Override
    public List<RequestCallback> removeCallbacks(CacheKey k) {
        return pendingCallbacks.remove(k);
    }
}
