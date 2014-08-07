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

import java.util.List;
import java.util.logging.Logger;

import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.cache.CacheKey;
import org.fusesource.restygwt.client.cache.ComplexCacheKey;
import org.fusesource.restygwt.client.cache.Domain;
import org.fusesource.restygwt.client.cache.QueueableCacheStorage;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.logging.client.LogConfiguration;

public class CachingCallbackFilter implements CallbackFilter {

    protected final QueueableCacheStorage cache;

    public CachingCallbackFilter(QueueableCacheStorage cache) {
        this.cache = cache;
    }

    /**
     * the real filter method, called independent of the response code
     *
     * TODO method.getResponse() is not equal to response. unfortunately
     */
    @Override
    public RequestCallback filter(final Method method, final Response response,
            RequestCallback callback) {
        final int code = response.getStatusCode();

        final CacheKey ck = cacheKey(method.builder);
        final List<RequestCallback> removedCallbacks = cache.removeCallbacks(ck);

        if (removedCallbacks != null){
            callback = new RequestCallback() {
                @Override
                public void onResponseReceived(Request request, Response response) {
                    if (GWT.isClient() && LogConfiguration.loggingIsEnabled()) {
                        Logger.getLogger(CachingCallbackFilter.class.getName())
                                .finer("call "+ removedCallbacks.size()
                                        + " more queued callbacks for " + ck);
                    }

                    // call all callbacks found in cache
                    for (RequestCallback cb : removedCallbacks) {
                        cb.onResponseReceived(request, response);
                    }
                }

                @Override
                public void onError(Request request, Throwable exception) {
                    if (LogConfiguration.loggingIsEnabled()) {
                        Logger.getLogger(CachingCallbackFilter.class.getName())
                                .severe("cannot call " + (removedCallbacks.size()+1)
                                        + " callbacks for " + ck + " due to error: "
                                        + exception.getMessage());
                    }

                    if (LogConfiguration.loggingIsEnabled()) {
                        Logger.getLogger(CachingCallbackFilter.class.getName())
                                .finer("call "+ removedCallbacks.size()
                                        + " more queued callbacks for " + ck);
                    }

                    // and all the others, found in cache
                    for (RequestCallback cb : removedCallbacks) {
                        cb.onError(request, exception);
                    }
                }
            };
        } else {
            if (GWT.isClient() && LogConfiguration.loggingIsEnabled()) {
                Logger.getLogger(CachingCallbackFilter.class.getName()).finer("removed one or no " +
                        "callback for cachekey " + ck);
            }
        }

        if (isCachingStatusCode(code)) { 
            cacheResult(method, response);
            return callback;
        }

        if (GWT.isClient() && LogConfiguration.loggingIsEnabled()) {
            Logger.getLogger(CachingCallbackFilter.class.getName())
                    .info("cannot cache due to invalid response code: " + code);
        }
        return callback;
    }

    protected boolean isCachingStatusCode(final int code) {
        return code < Response.SC_MULTIPLE_CHOICES // code < 300
                && code >= Response.SC_OK; // code >= 200
    }

    protected CacheKey cacheKey(final RequestBuilder builder) {
        return new ComplexCacheKey(builder);
    }

    protected void cacheResult(final Method method, final Response response) {
        CacheKey cacheKey = cacheKey(method.builder);
        if (GWT.isClient() && LogConfiguration.loggingIsEnabled()) {
            Logger.getLogger(CachingCallbackFilter.class.getName()).finer("cache to " + cacheKey
                    + ": " + response);
        }
        cache.putResult(cacheKey, response, getCacheDomains(method));
    }

    /**
     * when using the {@link Domain} annotation on services, we are able to group responses
     * of a service to invalidate them later on more fine grained. this method resolves a
     * possible ``domain`` to allow grouping.
     *
     * @return
     */
    protected String[] getCacheDomains(final Method method) {
        if (null == method.getData().get(Domain.CACHE_DOMAIN_KEY)) return null;

        final JSONValue jsonValue = JSONParser.parseStrict(method.getData()
                .get(Domain.CACHE_DOMAIN_KEY));
        if (null == jsonValue) {
            return null;
        }

        final JSONArray jsonArray = jsonValue.isArray();
        if (null != jsonArray) {
            final String[] dd = new String[jsonArray.size()];
            for (int i = 0; i < jsonArray.size(); ++i) {
                dd[i] = jsonArray.get(i).isString().stringValue();
            }

            return dd;
        }
        return null;
    }
}
