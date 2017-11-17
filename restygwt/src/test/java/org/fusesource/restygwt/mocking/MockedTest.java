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

package org.fusesource.restygwt.mocking;

import com.google.gwt.http.client.Request;
import com.google.gwt.junit.GWTMockUtilities;

import junit.framework.TestCase;

import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.basic.ExampleDto;
import org.fusesource.restygwt.client.basic.ExampleService;
import org.junit.Before;

/**
 * Just a real world usability example how services can be mocked in a simple manner.
 * WITHOUT using GwtTestCases that tend to be damn slow.
 *
 * In principle - you just have to implement the interface - right?
 *
 * @author <a href="mailto:mail@raphaelbauer.com">rEyez</a>
 *
 */
public class MockedTest extends TestCase {

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        GWTMockUtilities.disarm();
    }

    public void testMockedDispatcher() {

        /** Mock/Stub mocked example service: */
        ExampleService exampleService = new ExampleService() {
            @Override
            public void getExampleDto(MethodCallback<ExampleDto> callback) {
                ExampleDto exampleDto = new ExampleDto();
                exampleDto.name = "name";
                callback.onSuccess(null, exampleDto);
            }

            @Override
            public Request getExampleDtoCancelable(MethodCallback<ExampleDto> callback) {
                ExampleDto exampleDto = new ExampleDto();
                exampleDto.name = "name";
                callback.onSuccess(null, exampleDto);
                return null;
            }

            @Override
            public void storeDto(ExampleDto exampleDto, MethodCallback<Void> callback) {
                callback.onSuccess(null, null);
            }
        };

        /** test*/
        exampleService.getExampleDto(new MethodCallback<ExampleDto>() {
            @Override
            public void onSuccess(Method method, ExampleDto response) {
                assertEquals(response.name, "name");

            }

            @Override
            public void onFailure(Method method, Throwable exception) {
            }
        });

    }

}
