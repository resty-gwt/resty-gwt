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

package org.fusesource.restygwt.client.callback;

import java.util.List;
import java.util.logging.Logger;

import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.cache.QueueableCacheStorage;
import org.fusesource.restygwt.client.dispatcher.CacheKey;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.logging.client.LogConfiguration;

public class CachingCallbackFilter implements CallbackFilter {

    protected QueueableCacheStorage cache;

    public CachingCallbackFilter(QueueableCacheStorage cache) {
        this.cache = cache;
    }

    /**
     * the real filter method, called independent of the response code
     *
     * TODO method.getResponse() is not equal to response. unfortunately
     */
    @Override
    public void filter(final Method method, final Response response) {
        final int code = response.getStatusCode();

        CacheKey ck = new CacheKey(method.builder);
        List<RequestCallback> removedCallbacks = cache.removeCallbacks(ck);

        if (removedCallbacks != null
                && 1 < removedCallbacks.size()) {
            if (LogConfiguration.loggingIsEnabled()) {
                Logger.getLogger(CachingCallbackFilter.class.getName()).severe("Found more than " +
                        "one callback in cachekey, must handle that, but ignore it now. ");
            }
        } else {
            if (LogConfiguration.loggingIsEnabled()) {
                Logger.getLogger(CachingCallbackFilter.class.getName()).fine("removed one or no " +
                        "callback for cachekey " + ck);
            }
        }

        if (code < Response.SC_MULTIPLE_CHOICES
                && code >= Response.SC_OK) {
            if (LogConfiguration.loggingIsEnabled()) {
                Logger.getLogger(CachingCallbackFilter.class.getName()).fine("cache to " + ck
                        + ": " + response);
            }
            cache.putResult(ck, response);
            return;
        }

        GWT.log("cannot cache due to invalid response code: " + code);
    }
}
