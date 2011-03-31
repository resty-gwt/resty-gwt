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

import org.fusesource.restygwt.client.dispatcher.CacheKey;
import org.fusesource.restygwt.client.dispatcher.CacheStorage;

import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;

/**
 * more enhanced cacheinterface, TODO write something
 *
 * @author abalke
 */
public interface QueueableCacheStorage extends CacheStorage {

    public Response getResultOrReturnNull(final CacheKey key);

    public Response getResultOrReturnNull(final CacheKey key, final String scope);

    public boolean hasCallback(final CacheKey k);

    public void addCallback(final CacheKey k, final RequestCallback rc);

    public List<RequestCallback> removeCallbacks(final CacheKey k);

    /**
     * default put method
     */
    public void putResult(final CacheKey key, final Response response);

    /**
     * put by ident/scope. e.g. to invalidate later on by domain class
     *
     * @param key
     * @param scope
     * @param response
     */
    public void putResult(final CacheKey key, final Response response, final String scope);

    /**
     * purge the complete cache
     */
    public void purge();

    /**
     * purge a particular ident, e.g. domain scope
     */
    public void purge(final String scope);
}