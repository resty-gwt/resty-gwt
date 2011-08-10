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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.fusesource.restygwt.client.Defaults;
import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.cache.CacheKey;
import org.fusesource.restygwt.client.cache.Domain;
import org.fusesource.restygwt.client.cache.ScopableQueueableCacheStorage;
import org.fusesource.restygwt.client.cache.UrlCacheKey;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;

public class CachingCallbackFilter implements CallbackFilter {

    protected ScopableQueueableCacheStorage cache;

    public CachingCallbackFilter(ScopableQueueableCacheStorage cache) {
        this.cache = cache;
    }

    @Override
    public boolean canHandle(final String method, final int code) {

        return method.equals(RequestBuilder.GET.toString());
    }

    /**
     * the real filter method, called independent of the response code
     *
     * TODO method.getResponse() is not equal to response. unfortunately
     */
    @Override
    public RequestCallback filter(final Method method, final Response response,
            RequestCallback callback) {
        final CacheKey ck = new UrlCacheKey(method.builder);
        final List<RequestCallback> removedCallbacks = cache.removeCallbacks(ck);

        if (removedCallbacks != null
                && 1 < removedCallbacks.size()) {
            // remove the first callback from list, as this is called explicitly
            removedCallbacks.remove(0);
            // fetch the builders callback and wrap it with a new one, calling all others too
            final RequestCallback originalCallback = callback;

            callback = new RequestCallback() {
                @Override
                public void onResponseReceived(Request request, Response response) {
                    // call the original callback
                    if (Defaults.canLog()) {
                        Logger.getLogger(CachingCallbackFilter.class.getName())
                                .finer("call original callback for " + ck);
                    }
                    originalCallback.onResponseReceived(request, response);

                    if (Defaults.canLog()) {
                        Logger.getLogger(CachingCallbackFilter.class.getName())
                                .finer("call "+ removedCallbacks.size()
                                        + " more queued callbacks for " + ck);
                    }

                    // and all the others, found in cache
                    for (RequestCallback cb : removedCallbacks) {
                        cb.onResponseReceived(request, response);
                    }
                }

                @Override
                public void onError(Request request, Throwable exception) {
                    if (Defaults.canLog()) {
                        Logger.getLogger(CachingCallbackFilter.class.getName())
                                .severe("cannot call " + (removedCallbacks.size()+1)
                                        + " callbacks for " + ck + " due to error: "
                                        + exception.getMessage());
                    }
                    // call the original callback
                    if (Defaults.canLog()) {
                        Logger.getLogger(CachingCallbackFilter.class.getName())
                                .finer("call original callback for " + ck);
                    }

                    originalCallback.onError(request, exception);

                    if (Defaults.canLog()) {
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
            if (Defaults.canLog()) {
                Logger.getLogger(CachingCallbackFilter.class.getName()).finer("removed one or no " +
                        "callback for cachekey " + ck);
            }
        }

        cache.putResult(ck, response, getCacheDomains(method));
        return callback;
    }

    /**
     * when using the {@link Domain} annotation on services, we are able to group responses
     * of a service to invalitate them later on more fine grained. this method resolves a
     * possible ``domain`` to allow grouping.
     *
     * @return
     */
    private String[] getCacheDomains(final Method method) {
        if (null == method.getData().get(Domain.CACHE_DOMAIN_KEY)) return null;

        final JSONValue jsonValue = JSONParser.parseStrict(method.getData()
                .get(Domain.CACHE_DOMAIN_KEY));
        if (null == jsonValue) return null;

        JSONArray jsonArray = jsonValue.isArray();
        final List<String> dd = new ArrayList<String>();

        if (null != jsonArray) {
            for (int i = 0; i < jsonArray.size(); ++i) {
                dd.add(jsonArray.get(i).isString().stringValue());
            }

            return dd.toArray(new String[dd.size()]);
        }
        return null;
    }
}
