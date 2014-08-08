/**
 * Copyright (C) 2009-2011 the original author or authors.
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
package org.fusesource.restygwt.rebind.util;

import java.util.Iterator;

/**
 * An iterator that returns the first time, the first value, and after it always the returns the next value.
 * Useful for scenarios that have an <code>if (first) { first = true; ... }</code> inside a loop approach.
 *
 * @author <a href="mailto:bogdan.mustiata@gmail.com">Bogdan Mustiata</<a>
 * @param <T>
 */
public class OnceFirstIterator<T> implements Iterator<T> {
    private T firstValue;
    private T nextValue;
    private boolean firstReturned;

    public OnceFirstIterator(T firstValue, T nextValue) {
        this.firstValue = firstValue;
        this.nextValue = nextValue;
    }

    @Override
    public boolean hasNext() {
        return true;
    }

    @Override
    public T next() {
        if (firstReturned) {
            return nextValue;
        }
        firstReturned = true;
        return firstValue;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
