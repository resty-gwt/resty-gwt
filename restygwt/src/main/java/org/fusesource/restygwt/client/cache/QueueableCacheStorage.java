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

import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;

import java.util.List;

/**
 * more enhanced cache interface caching Response objects.
 * TODO write something
 *
 * @author abalke
 */
public interface QueueableCacheStorage extends CacheStorage<Response> {

    String RESTY_CACHE_HEADER = "X-Resty-Cache";

    boolean hasCallback(final CacheKey k);

    void addCallback(final CacheKey k, final RequestCallback rc);

    List<RequestCallback> removeCallbacks(final CacheKey k);
}