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
package org.fusesource.restygwt.client.dispatcher;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.Window;

public class CachingRetryingCallback extends AbstractRetryingCallback {

    public CachingRetryingCallback(
            RequestBuilder requestBuilder,
            RequestCallback requestCallback) {
        super(requestBuilder, requestCallback);
    }

    @Override
    public void onResponseReceived(Request request, Response response) {

        if (response.getStatusCode() == Response.SC_UNAUTHORIZED) {
            //FIXME: to be removed...
            Window.Location.replace("login.html");

        } else if (response.getStatusCode() != Response.SC_OK) {

            handleErrorGracefully();

        } else {

            CacheKey cacheKey = new CacheKey(requestBuilder);
            CachingRetryingDispatcher.getCacheStorage().putResult(cacheKey, response);

            requestCallback.onResponseReceived(request, response);
        }
    }


}
