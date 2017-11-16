/**
 * Copyright 2015 the original author or authors.
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

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;

import java.util.Set;

/**
 * Helper class to check the Set of expected statuses
 */
public abstract class MethodAccessHelperDispatcher implements Dispatcher {
    @Override
    public Request send(Method method, RequestBuilder builder) throws RequestException {
        expect(method.expectedStatuses, method.anyStatus);
        return null;
    }

    abstract protected void expect(Set<Integer> expectedStatuses, boolean anyStatus);
}
