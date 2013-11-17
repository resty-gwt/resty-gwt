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

import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.Resource;
import org.fusesource.restygwt.client.RestServiceProxy;
import org.junit.Test;

import com.google.gwt.core.client.GWT;
import com.google.gwt.jsonp.client.JsonpRequest;
import com.google.gwt.junit.client.GWTTestCase;

/**
 *
 *
 * @author <a href="mailto:mail@raphaelbauer.com">rEyez</<a>
 *
 */
public class JsonpTestGwt extends GWTTestCase {

    private static final String URI_PATH = "ui/jsonp-service";

    @Override
    public String getModuleName() {
        return "org.fusesource.restygwt.BasicTestGwt";
    }

    public void testDefaultFunction() {

        //configure RESTY
        Resource resource = new Resource(GWT.getModuleBaseURL() + URI_PATH);


        JsonpService service = GWT.create(JsonpService.class);
        ((RestServiceProxy) service).setResource(resource);

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

        // wait... we are in async testing...
        delayTestFinish(10000);

    }
    
    @Test
    public void testCancel() {

        //configure RESTY
        Resource resource = new Resource(GWT.getModuleBaseURL() + URI_PATH);


        JsonpService service = GWT.create(JsonpService.class);
        ((RestServiceProxy) service).setResource(resource);

        JsonpRequest<?> request = service.someCancelableJsonp(new MethodCallback<ExampleDto>() {

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

        // wait... we are in async testing...
        delayTestFinish(10000);
    }

//    public void testListFunction() {
//
//        //configure RESTY
//        Resource resource = new Resource(GWT.getModuleBaseURL() + URI_PATH);
//
//
//        JsonpService service = GWT.create(JsonpService.class);
//        ((RestServiceProxy) service).setResource(resource);
//
//        service.someJsonpWithList(new MethodCallback<List<ExampleDto>>() {
//
//            @Override
//            public void onSuccess(Method method, List<ExampleDto> response) {
//
//                assertEquals(response.get(0).name, "myName");
//                finishTest();
//
//            }
//
//            @Override
//            public void onFailure(Method method, Throwable exception) {
//                fail();
//
//            }
//        });
//
//        // wait... we are in async testing...
//        delayTestFinish(10000);
//
//    }

}