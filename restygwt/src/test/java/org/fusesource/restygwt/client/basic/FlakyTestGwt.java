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

import org.fusesource.restygwt.client.Defaults;
import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.Resource;
import org.fusesource.restygwt.client.RestServiceProxy;
import org.fusesource.restygwt.client.dispatcher.DispatcherFactoryCachingRetrying;

import com.google.gwt.core.client.GWT;
import com.google.gwt.junit.client.GWTTestCase;

/**
 *
 *
 * @author <a href="mailto:mail@raphaelbauer.com">rEyez</<a>
 *
 */
public class FlakyTestGwt extends GWTTestCase {

    @Override
    public String getModuleName() {
        return "org.fusesource.restygwt.FlakyTestGwt";
    }

    public void testFlakyConnection() {

        Defaults.setDispatcherFactory(new DispatcherFactoryCachingRetrying());

        Resource resource = new Resource(GWT.getModuleBaseURL() + "api/getendpoint");

        //System.out.println("resouce is: " + GWT.getModuleBaseURL() + "api/getendpoint");

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

        delayTestFinish(5000);

    }

}