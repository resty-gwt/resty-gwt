/**
 * Copyright (C) 2009-2011 the original author or authors.
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
import com.google.gwt.junit.client.GWTTestCase;
import org.fusesource.restygwt.client.*;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

/**
 * @author <a href="mailto:bogdan.mustiata@gmail.com">Bogdan Mustiata</<a>
 */
public class DirectRestServiceTestGwt extends GWTTestCase {
    @Override
    public String getModuleName() {
        return "org.fusesource.restygwt.DirectRestServiceTestGwt";
    }

    static interface InnerDirectRestService extends DirectRestService {
        @GET @Path("/something")
        void getSomething();
    }

    public void testCallingMethodsDirectlyShouldFail() {
        delayTestFinish(10000);
        DirectExampleService directExampleService = GWT.create(DirectExampleService.class);

        try {
            directExampleService.getExampleDtos("3");
            fail("This code is unreachable, since the proxy can not be called directly.");
        } catch (Exception e) {
            finishTest();
        }
    }

    public void testQueryCall() {
        delayTestFinish(10000);
        DirectExampleService directExampleService = GWT.create(DirectExampleService.class);

        Resource resource = new Resource(GWT.getModuleBaseURL() + "api");
        ((RestServiceProxy) directExampleService).setResource(resource);

        REST.withCallback(new MethodCallback<List<ExampleDto>>() {
            @Override
            public void onSuccess(Method method, List<ExampleDto> response) {
                assertEquals(3, response.size());
                finishTest();
            }

            @Override
            public void onFailure(Method method, Throwable e) {
                fail(e.getMessage());
            }
        }).call(directExampleService).getExampleDtos("3");
    }

    public void testVoidCall() {
        delayTestFinish(10000);
        DirectExampleService directExampleService = GWT.create(DirectExampleService.class);

        Resource resource = new Resource(GWT.getModuleBaseURL() + "api");
        ((RestServiceProxy) directExampleService).setResource(resource);

        REST.withCallback(new MethodCallback<List<ExampleDto>>() {
            @Override
            public void onSuccess(Method method, List<ExampleDto> response) {
                finishTest();
            }

            @Override
            public void onFailure(Method method, Throwable e) {
                fail(e.getMessage());
            }
        }).call(directExampleService).storeDto(new ExampleDto());
    }

    public void testInnerInterface() {
        InnerDirectRestService innerDirectService = GWT.create(InnerDirectRestService.class);
        assertNotNull(innerDirectService);
    }

    public void testPrimitiveDirectRestService() {
        PrimitiveDirectRestService primitiveDirectService = GWT.create(PrimitiveDirectRestService.class);
        assertNotNull(primitiveDirectService);
    }

}
