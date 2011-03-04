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

import java.util.Map;

import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.Resource;
import org.fusesource.restygwt.client.RestServiceProxy;
import org.fusesource.restygwt.example.client.event.FooModelChangedEvent;
import org.fusesource.restygwt.example.client.event.FooModelChangedEventHandler;
import org.fusesource.restygwt.example.client.event.FooModelChangedEventHandlerImpl;
import org.fusesource.restygwt.example.client.event.ModelChangeEvent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.junit.client.GWTTestCase;

/**
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
         *  setup the service, usually done in gin
         */
        Resource resource = new Resource(GWT.getModuleBaseURL());
        final ModelChangeAnnotatedService service = GWT.create(ModelChangeAnnotatedService.class);
        ((RestServiceProxy) service).setResource(resource);

        /*
         * setup the eventbus, usually done by gin
         */
        final EventBus eventBus = new SimpleEventBus();
        final FooModelChangedEventHandlerImpl handler = new FooModelChangedEventHandlerImpl();

        eventBus.addHandler(FooModelChangedEvent.TYPE, handler);


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
                assertEquals(null, method.getData().get(ModelChangeEvent.MODEL_CHANGED_DOMAIN_KEY));

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

                        // fetch all data which was put on the method by ModelChangeAnnotationResolver
                        Map<String, String> data = method.getData();

                        /*
                         * as there is the following annotation on the service
                         * @ModelChange(domain="Foo", on={"PUT"})
                         *
                         * this is where a ModelChange event will be published later
                         */
                        assertEquals("Foo", data.get(ModelChangeEvent.MODEL_CHANGED_DOMAIN_KEY));


                        // we dont have an event before
                        assertEquals(0, handler.getAllCatchedEvents().size());

                        /*
                         * fire the event and check it arrived our handler
                         */
                        eventBus.fireEvent(new FooModelChangedEvent());
                        assertEquals(1, handler.getAllCatchedEvents().size());

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