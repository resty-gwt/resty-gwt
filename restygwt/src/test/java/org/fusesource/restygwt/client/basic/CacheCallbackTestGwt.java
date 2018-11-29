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

package org.fusesource.restygwt.client.basic;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.user.client.Timer;

import org.fusesource.restygwt.client.Defaults;
import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.Resource;
import org.fusesource.restygwt.client.RestServiceProxy;
import org.fusesource.restygwt.client.cache.QueueableCacheStorage;
import org.fusesource.restygwt.client.cache.VolatileQueueableCacheStorage;
import org.fusesource.restygwt.client.callback.CachingCallbackFilter;
import org.fusesource.restygwt.client.callback.CallbackFactory;
import org.fusesource.restygwt.client.callback.DefaultFilterawareRequestCallback;
import org.fusesource.restygwt.client.callback.FilterawareRequestCallback;
import org.fusesource.restygwt.client.callback.ModelChangeCallbackFilter;
import org.fusesource.restygwt.client.dispatcher.CachingDispatcherFilter;
import org.fusesource.restygwt.client.dispatcher.DefaultFilterawareDispatcher;
import org.fusesource.restygwt.client.dispatcher.FilterawareDispatcher;

/**
 * test to check if {@link CachingCallbackFilter} {@link QueueableCacheStorage}
 * and caching stuff in complete works as expected
 *
 * @author <a href="mailto:andi.balke@gmail.com">andi</a>
 */
public class CacheCallbackTestGwt extends GWTTestCase {

    private BlockingTimeoutService service;

    private final int TESTCLASS_DELAY_TIMEOUT = 15000;

    @Override
    public String getModuleName() {
        return "org.fusesource.restygwt.CachingTestGwt";
    }

    /**
     * prove all callbacks are registered, called and unregistered without
     * using the cache. in this test all calls will reach the server.
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
        delayTestFinish(TESTCLASS_DELAY_TIMEOUT);
    }

    /**
     * prove all callbacks are registered, performed and unregistered with
     * using the cache.
     *
     * not all calls will reach the server, {@link VolatileQueueableCacheStorage} will
     * need to handle some of the callbacks by its own.
     *
     * this is done by just calling the same method multiple times
     *
     * first the simple case:
     * use the cache when the first method call is back from backend. there wont be
     * any callback queuing yet.
     */
    public void testSequential_NonQueuing_CachingCallback() {
        // backend reaching call
        service.cachingCall(0, new MethodCallback<Void>() {
            @Override
            public void onSuccess(Method method, Void response) {
                GWT.log("passing first non-queuing call");
            }

            @Override
            public void onFailure(Method method, Throwable exception) {
                fail("failure on read: " + exception.getMessage());
            }
        });

        // wait a second for callback to be back for sure
        // usually there should be something like Thread.sleep, but thats not possible here
        new Timer() {
            @Override
            public void run() {
                /*
                 * two calls that are handled directly by the cache
                 * (no backend interaction at all)
                 */
                service.cachingCall(0, new MethodCallback<Void>() {
                    @Override
                    public void onSuccess(Method method, Void response) {
                        GWT.log("passing second non-queuing call");
                    }

                    @Override
                    public void onFailure(Method method, Throwable exception) {
                        fail("failure on read: " + exception.getMessage());
                    }
                });
            }
        }.schedule(1000);

        // this is the third one, started in 3 seconds
        new Timer() {
            @Override
            public void run() {
                service.cachingCall(0, new MethodCallback<Void>() {
                    @Override
                    public void onSuccess(Method method, Void response) {
                        GWT.log("passing third non-queuing call");
                        finishTest();
                    }

                    @Override
                    public void onFailure(Method method, Throwable exception) {
                        fail("failure on read: " + exception.getMessage());
                    }
                });
            }
        }.schedule(3000);

        // wait... we are in async testing...
        delayTestFinish(TESTCLASS_DELAY_TIMEOUT);
    }

    public void testSequential_Queuing_CachingCallback() {
        // backend reaching call
        service.cachingQueuingCall(2, new MethodCallback<Void>() {
            @Override
            public void onSuccess(Method method, Void response) {
                GWT.log("passing first queuing call");
            }

            @Override
            public void onFailure(Method method, Throwable exception) {
                fail("failure on read: " + exception.getMessage());
            }
        });

        /*
         * same call again to get this callback queued
         * and called when the first is back from backend
         */
        service.cachingQueuingCall(2, new MethodCallback<Void>() {
            @Override
            public void onSuccess(Method method, Void response) {
                GWT.log("passing second queuing call");
                finishTest();
            }

            @Override
            public void onFailure(Method method, Throwable exception) {
                fail("failure on read: " + exception.getMessage());
            }
        });

        // wait... we are in async testing...
        delayTestFinish(TESTCLASS_DELAY_TIMEOUT);
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
        final QueueableCacheStorage cacheStorage = new VolatileQueueableCacheStorage();
        FilterawareDispatcher dispatcher = new DefaultFilterawareDispatcher();

        dispatcher.addFilter(new CachingDispatcherFilter(cacheStorage, new CallbackFactory() {
            @Override
            public FilterawareRequestCallback createCallback(Method method) {
                FilterawareRequestCallback retryingCallback = new DefaultFilterawareRequestCallback(method);

                retryingCallback.addFilter(new CachingCallbackFilter(cacheStorage));
                retryingCallback.addFilter(new ModelChangeCallbackFilter(eventBus));
                return retryingCallback;
            }
        }));

        Defaults.setDispatcher(dispatcher);


        /*
         *  setup the service, usually done in gin
         */
        Resource resource = new Resource(GWT.getModuleBaseURL());
        service = GWT.create(BlockingTimeoutService.class);
        ((RestServiceProxy) service).setResource(resource);
    }
}