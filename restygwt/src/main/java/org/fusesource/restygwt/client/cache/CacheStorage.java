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

public interface CacheStorage<T> {
    T getResultOrReturnNull(CacheKey key);

    T getResultOrReturnNull(CacheKey key, String scope);

    /**
     * default put method
     */
    void putResult(CacheKey key, T response);

    /**
     * put by ident/scope. e.g. to invalidate later on by domain class
     *
     * @param key
     * @param scope
     * @param response
     */
    void putResult(CacheKey key, T response, String... scope);

    /**
     * default delete method
     */
    void remove(CacheKey key);

    /**
     * delete by ident/scope. e.g. to invalidate later on by domain class
     *
     * @param key
     * @param scopes
     */
    void remove(CacheKey key, String... scopes);

    /**
     * purge the complete cache
     */
    void purge();

    /**
     * purge a particular ident, e.g. domain scope
     */
    void purge(String scope);

}
