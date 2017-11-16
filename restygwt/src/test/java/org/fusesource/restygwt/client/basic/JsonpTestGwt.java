/**
 * Copyright (C) 2009-2015 the original author or authors.
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
import com.google.gwt.jsonp.client.JsonpRequest;
import com.google.gwt.junit.DoNotRunWith;
import com.google.gwt.junit.Platform;
import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.user.client.Timer;

import java.util.List;

import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.Resource;
import org.fusesource.restygwt.client.RestServiceProxy;

/**
 *
 * @author <a href="mailto:mail@raphaelbauer.com">rEyez</a>
 * @author Ralf Sommer {@literal <ralf.sommer.dev@gmail.com>}
 *
 */
public class JsonpTestGwt extends GWTTestCase {

    @Override
    public String getModuleName() {
        return "org.fusesource.restygwt.JsonpTestGwt";
    }

    JsonpService service;

    @Override
    protected void gwtSetUp() throws Exception {
        // configure RESTY
        Resource resource = new Resource(GWT.getModuleBaseURL() + "jsonp");

        service = GWT.create(JsonpService.class);
        ((RestServiceProxy) service).setResource(resource);
    }

    public void testDefaultFunction() {
        // wait... we are in async testing...
        delayTestFinish(5000);

        service.someJsonp(new MethodCallback<ExampleDto>() {
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
    }

    /**
     * HtmlUnit execute synchronously. Should be async.
     * <p>
     * Run manually with -Dgwt.args="-prod -runStyle Manual:1" works as expected
     *
     * @see
     * <a href="https://github.com/gwtproject/gwt/blob/138c60c7625a9403f34bd9616cea483fbdbeb2f0/user/test/com/google/gwt/jsonp/client/JsonpRequestTest.java#L191">GWT JsonpRequestTest.java</a>
     */
    @DoNotRunWith(Platform.HtmlUnitBug)
    public void testCancel() {
        delayTestFinish(10000);

        JsonpRequest<ExampleDto> request = service.someCancelableJsonp(new MethodCallback<ExampleDto>() {
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

        // after waiting for 5 seconds assume, that the request has been canceled successfully
        new Timer() {
            @Override
            public void run() {
                finishTest();
            }
        }.schedule(5000);
    }

    public void testNullResult() {
        delayTestFinish(5000);

        service.someOtherJsonp(new MethodCallback<ExampleDto>() {
            @Override
            public void onSuccess(Method method, ExampleDto response) {
                assertNull(response);
                finishTest();
            }

            @Override
            public void onFailure(Method method, Throwable exception) {
                fail();
            }
        });
    }

    public void testListFunction() {
        // wait... we are in async testing...
        delayTestFinish(5000);

        service.someJsonpWithList(new MethodCallback<List<ExampleDto>>() {
            @Override
            public void onSuccess(Method method, List<ExampleDto> response) {
                assertEquals(response.get(0).name, "myName");
                assertEquals(response.get(1).name, "myName2");
                finishTest();
            }

            @Override
            public void onFailure(Method method, Throwable exception) {
                fail();
            }
        });
    }

}