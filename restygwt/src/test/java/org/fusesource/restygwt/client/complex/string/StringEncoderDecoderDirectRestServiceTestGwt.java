/**
 * Copyright (C) 2009-2016 the original author or authors.
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

package org.fusesource.restygwt.client.complex.string;

import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONException;
import com.google.gwt.junit.client.GWTTestCase;

import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.REST;
import org.fusesource.restygwt.client.TextCallback;
import org.fusesource.restygwt.client.complex.string.service.StringDirectRestService;

public class StringEncoderDecoderDirectRestServiceTestGwt extends GWTTestCase {

    @Override
    public String getModuleName() {
        return "org.fusesource.restygwt.StringEncoderDecoderTestGwt";
    }

    public void testJsonString() {
        StringDirectRestService service = GWT.create(StringDirectRestService.class);

        delayTestFinish(10000);

        REST.withCallback(new MethodCallback<String>() {
            @Override
            public void onSuccess(Method method, String response) {
                assertEquals("String as Json", response);
                finishTest();
            }

            @Override
            public void onFailure(Method method, Throwable exception) {
                fail(exception.getMessage());
            }
        }).call(service).getAsJson();
    }

    /**
     * Test method only success through "onFailure" with restygwt <= 2.0.3 or plain text autodetection set to false
     * (default)
     */
    public void testSendJsonString() {
        StringDirectRestService service = GWT.create(StringDirectRestService.class);

        delayTestFinish(10000);

        REST.withCallback(new MethodCallback<Void>() {
            @Override
            public void onSuccess(Method method, Void response) {
                fail();
            }

            @Override
            public void onFailure(Method method, Throwable exception) {
                if (400 == method.getResponse().getStatusCode() && "Wrong Format".equals(exception.getMessage())) {
                    finishTest();
                } else {
                    fail();
                }
            }
        }).call(service).setAsJson("\"Json String?\"");
    }

    /**
     * Test method only success through "onFailure" with restygwt <= 2.1.1 or plain text autodetection set to false
     * (default)
     */
    public void testPlainTextStringWithTextCallback() {
        StringDirectRestService service = GWT.create(StringDirectRestService.class);

        delayTestFinish(10000);

        REST.withCallback(new TextCallback() {
            @Override
            public void onSuccess(Method method, String response) {
                fail();
            }

            @Override
            public void onFailure(Method method, Throwable exception) {
                if (exception.getCause() instanceof JSONException) {
                    // Only for backward compatbility test
                    finishTest();
                }

                fail(exception.getMessage());
            }
        }).call(service).getAsPlainText();
    }

    /**
     * Test method only success through "onFailure" with restygwt <= 2.1.1 or plain text autodetection set to false
     * (default)
     */
    public void testPlainTextStringWithMethodCallback() {
        StringDirectRestService service = GWT.create(StringDirectRestService.class);

        delayTestFinish(10000);

        REST.withCallback(new MethodCallback<String>() {
            @Override
            public void onSuccess(Method method, String response) {
                fail();
            }

            @Override
            public void onFailure(Method method, Throwable exception) {
                if (exception.getCause() instanceof JSONException) {
                    // Only for backward compatbility test
                    finishTest();
                }

                fail(exception.getMessage());
            }
        }).call(service).getAsPlainText();
    }

    public void testSendPlainTextString() {
        StringDirectRestService service = GWT.create(StringDirectRestService.class);

        delayTestFinish(10000);

        REST.withCallback(new MethodCallback<Void>() {
            @Override
            public void onSuccess(Method method, Void response) {
                finishTest();
            }

            @Override
            public void onFailure(Method method, Throwable exception) {
                fail();
            }
        }).call(service).setAsPlainText("Plain text String?");
    }

}