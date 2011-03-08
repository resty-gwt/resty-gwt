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

/**
 * more enhanced cacheinterface, TODO write something
 *
 * @author abalke
 */
public interface QueueableCacheStorage extends CacheStorage {
    public boolean hasCallback(CacheKey k);

    public void addCallback(CacheKey k, RequestCallback rc);

    public List<RequestCallback> removeCallbacks(CacheKey k);
}