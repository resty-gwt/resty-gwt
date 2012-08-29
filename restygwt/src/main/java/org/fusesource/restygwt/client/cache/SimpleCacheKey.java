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

/**
 * just a string implementation of a cachekey
 *
 * @author abalke
 */
public class SimpleCacheKey implements CacheKey {

    private final String identifier;

    public SimpleCacheKey(final String identifier) {
        this.identifier = identifier;
    }

    @Override
    public int hashCode() {
        return identifier.hashCode();
    }

    @Override
    public boolean equals(Object anObject) {
        if (anObject instanceof CacheKey) {
            CacheKey aCacheKey = (CacheKey) anObject;

            if (aCacheKey.toString().equals(toString())) {
                return true;
            }
        }

        return false;
    }

    @Override
    public String toString() {
        return identifier;
    }
}