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

package org.fusesource.restygwt.client.dispatcher;

import org.fusesource.restygwt.client.Method;

import com.google.gwt.http.client.RequestBuilder;

public interface DispatcherFilter {

    /**
     * main filter method for a dispatcherfilter.
     *
     * pattern is a chain of responsibility. if you want the chain to
     * continue, return ``true``, ``false`` otherwise.
     *
     * if a ``DispatcherFilterDispatcherFilter`` returns ``false`` it is
     * respnsible to trigger the onResponse() method of the callback on
     * its own. otherwise there will never return any response to the origin
     * caller.
     *
     *  this will be necessary in caching case for example. if we have a
     *  caching dispatcherfilter, we will have
     *  - the necessarity to break the chain
     *  - and the necessarity to call the ``onResponse``
     *
     * @return continue chain or not
     */
    public boolean filter(Method method, RequestBuilder builder);

}
