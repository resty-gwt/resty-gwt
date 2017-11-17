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

package org.fusesource.restygwt.client.callback;

import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;

import org.fusesource.restygwt.client.Method;

public interface CallbackFilter {

    /**
     * main filter method for a callbackfilter.
     *
     * pattern is a chain of responsibility. in contrast to dispatcherfilter,
     * each callbackfilter will be called for sure. this comes due to the fact
     * that a dispatcherfilter might want to stop a request (e.g. by caching).
     * whereas a callbackfilter should not stop the processing of other callback-
     * filters, there seems to be no good reason for doing this.
     *
     * @return continue chain or not
     */
    RequestCallback filter(Method method, Response response, RequestCallback callback);

}
