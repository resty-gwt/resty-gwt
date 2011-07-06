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

import java.util.List;

import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;

/**
 * more enhanced cacheinterface which is able to queue callbacks to
 * a given cachekey. this means: if there is a pending call to the backend, this
 * is stored in here with a pending callback. when there are more requests coming
 * in from the application until the response is back, we will just queue their
 * callback unless the response *is* back.
 *
 * @author abalke
 */
public interface ScopableQueueableCacheStorage extends ScopableCacheStorage<Response> {

    /**
     * determine if the given cachekey has some (pending) callback
     *
     * @param k
     * @return
     */
    public boolean hasCallback(final CacheKey k);

    /**
     * add a callback to a given cachekey
     *
     * @param k
     * @param rc
     */
    public void addCallback(final CacheKey k, final RequestCallback rc);

    /**
     * we keep all callbacks to a cachekey that need to be performed when a
     * response comes from the server. this method heavily removes them all.
     */
    public List<RequestCallback> removeCallbacks(final CacheKey k);
}