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

package org.fusesource.restygwt.client;

import java.util.Iterator;

/**
 * Iterable wrapper to convert to strings.
 * @author iteratee@google.com (Kyle Butt)
 */
public class StringIterable implements Iterable<String> {

    private final Iterable<?> baseIterable;
    public StringIterable(Iterable<?> baseIterable) {
        this.baseIterable = baseIterable;
    }

    @Override
    public Iterator<String> iterator() {
        final Iterator<?> baseIterator = baseIterable.iterator();
        return new Iterator<String>() {
            @Override
            public boolean hasNext() {
                return baseIterator.hasNext();
            }

            @Override
            public String next() {
                return baseIterator.next().toString();
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
}
