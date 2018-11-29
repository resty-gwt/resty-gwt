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
import com.google.gwt.http.client.Request;
import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.user.client.Timer;

import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.Resource;
import org.fusesource.restygwt.client.RestServiceProxy;
import org.junit.Test;

/**
 *
 *
 * @author <a href="mailto:mail@raphaelbauer.com">rEyez</a>
 *
 */
public class BasicTestGwt extends GWTTestCase {

    @Override
    public String getModuleName() {
        return "org.fusesource.restygwt.BasicTestGwt";
    }

    public void testDefaultFunction() {
        //configure RESTY
        Resource resource = new Resource(GWT.getModuleBaseURL() + "api/getendpoint");

        ExampleService service = GWT.create(ExampleService.class);
        ((RestServiceProxy) service).setResource(resource);

        service.getExampleDto(new MethodCallback<ExampleDto>() {

            @Override
            public void onSuccess(Method method, ExampleDto response) {
                assertEquals(response.name, "myName");
                finishTest();
            }

            @Override
            public void onFailure(Method method, Throwable exception) {
                fail();
            }
        });

        // wait... we are in async testing...
        delayTestFinish(10000);
    }

    @Test
    public void testCancelRequest() {
        Resource resource = new Resource(GWT.getModuleBaseURL() + "api/getendpoint");

        ExampleService service = GWT.create(ExampleService.class);
        ((RestServiceProxy) service).setResource(resource);

        Request request = service.getExampleDtoCancelable(new MethodCallback<ExampleDto>() {
            @Override
            public void onSuccess(Method method, ExampleDto response) {
                fail();
            }

            @Override
            public void onFailure(Method method, Throwable exception) {
                fail();
            }
        });

        request.cancel();

        // after waiting for 10 seconds assume, that the request has been canceled successfully
        new Timer() {
            @Override
            public void run() {
                finishTest();
            }
        }.schedule(10000);

        // wait... we are in async testing...
        delayTestFinish(15000);
    }

    @Test
    public void testNullObjectPost() {
        //configure RESTY
        Resource resource = new Resource(GWT.getModuleBaseURL() + "api");

        ExampleService service = GWT.create(ExampleService.class);
        ((RestServiceProxy) service).setResource(resource);

        service.storeDto(null, new MethodCallback<Void>() {
            @Override
            public void onSuccess(Method method, Void response) {
                // Nothing awaited because its a test for send null objects
                finishTest();
            }

            @Override
            public void onFailure(Method method, Throwable exception) {
                fail();
            }
        });

        // wait... we are in async testing...
        delayTestFinish(10000);
    }

}