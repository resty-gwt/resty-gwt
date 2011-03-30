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

package org.fusesource.restygwt.client.event;

import org.fusesource.restygwt.GwtAnnotationResolverTestSuite;
import org.fusesource.restygwt.client.Defaults;
import org.fusesource.restygwt.client.FilterawareRequestCallback;
import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.Resource;
import org.fusesource.restygwt.client.RestServiceProxy;
import org.fusesource.restygwt.client.cache.QueuableRuntimeCacheStorage;
import org.fusesource.restygwt.client.cache.QueueableCacheStorage;
import org.fusesource.restygwt.client.callback.AbstractCallbackFactory;
import org.fusesource.restygwt.client.callback.CachingCallbackFilter;
import org.fusesource.restygwt.client.callback.FilterawareRetryingCallback;
import org.fusesource.restygwt.client.callback.ModelChangeCallbackFilter;
import org.fusesource.restygwt.client.dispatcher.CachingRetryingDispatcher;
import org.fusesource.restygwt.example.client.event.ModelChangeEvent;
import org.fusesource.restygwt.example.client.event.ModelChangeEventFactory;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.junit.client.GWTTestCase;

/**
 * CANNOT BE RUN FROM ECLIPSE
 *
 * due to missing ``BindingDefaults.addAnnotationResolver(new ModelChangeAnnotationResolver());``
 * in {@link GwtAnnotationResolverTestSuite}
 *
 * @author <a href="mailto:andi.balke@gmail.com">andi</a>
 */
public class ModelChangeAnnotationTestGwt extends GWTTestCase {

    /**
     * fake response for the GET request (service.getItems)
     */
    private static final String responseGetBody = "[{id:1},{id:2},{id:3}]";

    @Override
    public String getModuleName() {
        // load Event.gwt.xml with EchoServlet configured
        return "org.fusesource.restygwt.Event";
    }

    public void testDefaultFunction() {
        /*
         * configure RESTY to use cache, usually done in gin
         */
        final EventBus eventBus = new SimpleEventBus();
        final QueueableCacheStorage cacheStorage = new QueuableRuntimeCacheStorage();

        Defaults.setDispatcher(
                CachingRetryingDispatcher.singleton(cacheStorage,
                new AbstractCallbackFactory() {
                    @Override
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
        final ModelChangeAnnotatedService service = GWT.create(ModelChangeAnnotatedService.class);
        ((RestServiceProxy) service).setResource(resource);

        final ModelChangedEventHandlerImpl handler = new ModelChangedEventHandlerImpl();
        eventBus.addHandler(ModelChangeEvent.TYPE, handler);


        /*
         * first we create a client GET request to prepare all things as it
         * would be when displaying a list of items in the client.
         *
         * we could imageine having some listview with cached response.
         */
        service.getItems(responseGetBody, new MethodCallback<JSONValue>() {
            @Override
            public void onSuccess(Method method, JSONValue response) {
                // cast to array and validate
                JSONArray fooArray = response.isArray();
                assertNotNull(fooArray);

                /*
                 * there is no annotation for a get method on that service,
                 * therefore we wont find any values in the method about it.
                 */
                assertEquals(null, method.getData().get(ModelChangeEventFactory.MODEL_CHANGED_DOMAIN_KEY));

                final int EVENTS_CATCHED_BEFORE_REQUEST = 0;

                assertEquals(EVENTS_CATCHED_BEFORE_REQUEST, handler.getAllCatchedEvents().size());

                /*
                 * now, as we have our list data, we want to modify it to trigger
                 * an update event.
                 *
                 * thus we will initiate a write request next.
                 */
                service.setItem(Response.SC_CREATED, 1, new MethodCallback<Void>() {
                    @Override
                    public void onSuccess(Method method, Void response) {
                        assertEquals(Response.SC_CREATED, method.getResponse().getStatusCode());

                        /*
                         * check the eventhandling itself
                         * ... check it arrived our handler
                         */
                        assertEquals(EVENTS_CATCHED_BEFORE_REQUEST + 1,
                                handler.getAllCatchedEvents().size());

                        finishTest();
                    }

                    @Override
                    public void onFailure(Method method, Throwable exception) {
                        fail("failure on write: " + exception.getMessage());
                    }
                });
            }

            @Override
            public void onFailure(Method method, Throwable exception) {
                fail("failure on read: " + exception.getMessage());
            }
        });

        // wait... we are in async testing...
        delayTestFinish(10000);
    }
}