/**
 * Copyright (C) 2010 the original author or authors.
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

package org.fusesource.restygwt.client.basic;

import java.util.logging.Logger;

import org.fusesource.restygwt.client.Defaults;
import org.fusesource.restygwt.client.FilterawareRequestCallback;
import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.Resource;
import org.fusesource.restygwt.client.RestServiceProxy;
import org.fusesource.restygwt.client.cache.QueuableRuntimeCacheStorage;
import org.fusesource.restygwt.client.cache.QueueableCacheStorage;
import org.fusesource.restygwt.client.callback.CachingCallbackFilter;
import org.fusesource.restygwt.client.callback.CallbackFactory;
import org.fusesource.restygwt.client.callback.FilterawareRetryingCallback;
import org.fusesource.restygwt.client.callback.ModelChangeCallbackFilter;
import org.fusesource.restygwt.client.dispatcher.CachingRetryingDispatcher;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.logging.client.LogConfiguration;

/**
 * test to check if {@link CachingCallbackFilter} {@link QueueableCacheStorage}
 * and caching stuff in complete works as expected
 *
 * @author <a href="mailto:andi.balke@gmail.com">andi</a>
 */
public class CacheCallbackTestGwt extends GWTTestCase {

    private BlockingTimeoutService service;

    @Override
    public String getModuleName() {
        return "org.fusesource.restygwt.CachingTestGwt";
    }

    /**
     * prove all callbacks are registered and unregistered without using the cache.
     *
     * this is done by just calling the same method multiple times
     */
    public void testNonCachingCallback() {
        service.noncachingCall(0, new MethodCallback<Void>() {
            @Override
            public void onSuccess(Method method, Void response) {
                GWT.log("passing first call");
            }

            @Override
            public void onFailure(Method method, Throwable exception) {
                fail("failure on read: " + exception.getMessage());
            }
        });

        service.noncachingCall(1, new MethodCallback<Void>() {
            @Override
            public void onSuccess(Method method, Void response) {
                GWT.log("passing second call");
            }

            @Override
            public void onFailure(Method method, Throwable exception) {
                fail("failure on read: " + exception.getMessage());
            }
        });

        service.noncachingCall(2, new MethodCallback<Void>() {
            @Override
            public void onSuccess(Method method, Void response) {
                GWT.log("passing third call");
                finishTest();
            }

            @Override
            public void onFailure(Method method, Throwable exception) {
                fail("failure on read: " + exception.getMessage());
            }
        });

        // wait... we are in async testing...
        delayTestFinish(10000);
    }

    /**
     * usually this stuff is all done by gin in a real application. or at least there
     * would be a central place which is not the activity in the end.
     */
    @Override
    public void gwtSetUp() {
        /*
         * configure RESTY to use cache, usually done in gin
         */
        final EventBus eventBus = new SimpleEventBus();
        final QueueableCacheStorage cacheStorage = new QueuableRuntimeCacheStorage();

        Defaults.setDispatcher(
                CachingRetryingDispatcher.singleton(cacheStorage,
                new CallbackFactory() {
                    public FilterawareRequestCallback createCallback(Method method) {
                        final FilterawareRequestCallback retryingCallback = new FilterawareRetryingCallback(
                                method);

                        retryingCallback.addFilter(new CachingCallbackFilter(cacheStorage));
                        retryingCallback.addFilter(new ModelChangeCallbackFilter(eventBus));
                        return retryingCallback;
                    }
                }));


        /*
         *  setup the service, usually done in gin
         */
        Resource resource = new Resource(GWT.getModuleBaseURL());
        service = GWT.create(BlockingTimeoutService.class);
        ((RestServiceProxy) service).setResource(resource);
    }
}