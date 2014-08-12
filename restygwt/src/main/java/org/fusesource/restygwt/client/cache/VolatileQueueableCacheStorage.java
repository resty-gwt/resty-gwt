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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.google.gwt.http.client.Response;
import com.google.gwt.logging.client.LogConfiguration;
import com.google.gwt.user.client.Timer;

public class VolatileQueueableCacheStorage extends DefaultQueueableCacheStorage {
    
    /**
     * how long will a cachekey be allowed to exist
     */
    private static final int DEFAULT_LIFETIME_MS = 30 * 1000;

    private final int lifetimeMillis;
    
    public VolatileQueueableCacheStorage(){
        this(DEFAULT_LIFETIME_MS);
    }
    public VolatileQueueableCacheStorage(int lifetimeMillis){
        this.lifetimeMillis = lifetimeMillis;
    }
    
    private final List<Timer> timers = new ArrayList<Timer>();

    @Override
    protected void putResult(final CacheKey key, final Response response, final String scope) {
        final Timer t = new Timer() {
            @Override
            public void run() {
                try {
                    if (LogConfiguration.loggingIsEnabled()) {
                        Logger.getLogger(VolatileQueueableCacheStorage.class.getName())
                                .finer("removing cache-key " + key + " from scope \"" + scope + "\"");
                    }
                    cache.get(scope).remove(key);
                    timers.remove(this);
                } catch (Exception ex) {
                    Logger.getLogger(VolatileQueueableCacheStorage.class.getName())
                            .severe(ex.getMessage());
                }
            }
        };
        t.schedule(lifetimeMillis);
        timers.add(t);
        
        super.putResult(key, response, scope);
    }
    
    @Override
    public void purge() {
        super.purge();
        if (LogConfiguration.loggingIsEnabled()) {
            Logger.getLogger(DefaultQueueableCacheStorage.class.getName()).finer("remove "
                    + timers.size() + " timers from list.");
        }
        for (Timer t: timers) {
            t.cancel();
        }
        timers.clear();
    }

}
