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
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.junit.client.GWTTestCase;

import java.util.Date;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.fusesource.restygwt.client.Defaults;
import org.fusesource.restygwt.client.DirectRestService;
import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.REST;
import org.fusesource.restygwt.client.Resource;
import org.fusesource.restygwt.client.RestServiceProxy;

/**
 * @author <a href="mailto:bogdan.mustiata@gmail.com">Bogdan Mustiata</a>
 */
public class DirectRestServiceTestGwt extends GWTTestCase {
    @Override
    public String getModuleName() {
        return "org.fusesource.restygwt.DirectRestServiceTestGwt";
    }

    interface InnerDirectRestService extends DirectRestService {
        @GET
        @Path("/something")
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

    public void testDateFormat() {
        delayTestFinish(10000);
        DirectExampleService directExampleService = GWT.create(DirectExampleService.class);

        Resource resource = new Resource(GWT.getModuleBaseURL() + "api");
        ((RestServiceProxy) directExampleService).setResource(resource);

        Defaults.setDateFormat(null);

        final Date date = new Date(1506224400000L);
        REST.withCallback(new MethodCallback<Long>() {
            @Override
            public void onSuccess(Method method, Long response) {
                assertEquals(date.getTime(), response.longValue());
                finishTest();
            }

            @Override
            public void onFailure(Method method, Throwable e) {
                fail(e.getMessage());
            }
        }).call(directExampleService).getDate(date);
    }

    public void testDateFormatIso8601() {
        delayTestFinish(10000);
        DirectExampleService directExampleService = GWT.create(DirectExampleService.class);

        Resource resource = new Resource(GWT.getModuleBaseURL() + "api");
        ((RestServiceProxy) directExampleService).setResource(resource);

        Defaults.setDateFormat(DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.ISO_8601).getPattern());

        final Date date = new Date(1506224400000L);
        REST.withCallback(new MethodCallback<Long>() {
            @Override
            public void onSuccess(Method method, Long response) {
                assertEquals(date.getTime(), response.longValue());
                finishTest();
            }

            @Override
            public void onFailure(Method method, Throwable e) {
                fail(e.getMessage());
            }
        }).call(directExampleService).getDateIso8601(date);
    }

    public void testDateFormatIso8601Null() {
        delayTestFinish(10000);
        DirectExampleService directExampleService = GWT.create(DirectExampleService.class);

        Resource resource = new Resource(GWT.getModuleBaseURL() + "api");
        ((RestServiceProxy) directExampleService).setResource(resource);

        Defaults.setDateFormat(DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.ISO_8601).getPattern());

        REST.withCallback(new MethodCallback<Long>() {
            @Override
            public void onSuccess(Method method, Long response) {
                assertNull(response);
                finishTest();
            }

            @Override
            public void onFailure(Method method, Throwable e) {
                fail(e.getMessage());
            }
        }).call(directExampleService).getDateIso8601(null);
    }

    public void testDateFormatCustomPattern() {
        delayTestFinish(10000);
        DirectExampleService directExampleService = GWT.create(DirectExampleService.class);

        Resource resource = new Resource(GWT.getModuleBaseURL() + "api");
        ((RestServiceProxy) directExampleService).setResource(resource);

        // custom pattern contains double quote and single quote to check JSON escaping
        Defaults.setDateFormat("\"''yyyy-MM-dd'T'HH:mm:ss.SSSZZZ");

        final Date date = new Date(1506224400000L);
        REST.withCallback(new MethodCallback<Long>() {
            @Override
            public void onSuccess(Method method, Long response) {
                assertEquals(date.getTime(), response.longValue());
                finishTest();
            }

            @Override
            public void onFailure(Method method, Throwable e) {
                fail(e.getMessage());
            }
        }).call(directExampleService).getDateCustomPattern(date);
    }

    public void testDateFormatCustomPatternNull() {
        delayTestFinish(10000);
        DirectExampleService directExampleService = GWT.create(DirectExampleService.class);

        Resource resource = new Resource(GWT.getModuleBaseURL() + "api");
        ((RestServiceProxy) directExampleService).setResource(resource);

        // custom pattern contains double quote and single quote to check JSON escaping
        Defaults.setDateFormat("\"''yyyy-MM-dd'T'HH:mm:ss.SSSZZZ");

        REST.withCallback(new MethodCallback<Long>() {
            @Override
            public void onSuccess(Method method, Long response) {
                assertNull(response);
                finishTest();
            }

            @Override
            public void onFailure(Method method, Throwable e) {
                fail(e.getMessage());
            }
        }).call(directExampleService).getDateCustomPattern(null);
    }

    public void testRegexPathParamCall() {
        delayTestFinish(10000);
        DirectExampleService directExampleService = GWT.create(DirectExampleService.class);

        Resource resource = new Resource(GWT.getModuleBaseURL() + "api");
        ((RestServiceProxy) directExampleService).setResource(resource);

        REST.withCallback(new MethodCallback<Integer>() {
            @Override
            public void onSuccess(Method method, Integer response) {
                assertEquals((Integer) 456, response);
                finishTest();
            }

            @Override
            public void onFailure(Method method, Throwable e) {
                fail(e.getMessage());
            }
        }).call(directExampleService).getRegexMultiParams(123, 456);
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
