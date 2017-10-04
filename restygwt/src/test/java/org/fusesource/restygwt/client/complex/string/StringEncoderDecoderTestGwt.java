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

import org.fusesource.restygwt.client.FailedResponseException;
import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.TextCallback;
import org.fusesource.restygwt.client.complex.string.service.StringRestService;

import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONException;
import com.google.gwt.junit.client.GWTTestCase;

public class StringEncoderDecoderTestGwt extends GWTTestCase {

    @Override
    public String getModuleName() {
        return "org.fusesource.restygwt.StringEncoderDecoderTestGwt";
    }

    public void testJsonString() {
        StringRestService strings = GWT.create(StringRestService.class);

        delayTestFinish(10000);

        strings.getAsJson(new MethodCallback<String>() {
            @Override
            public void onSuccess(Method method, String response) {
                assertEquals("String as Json", response);
                finishTest();
            }

            @Override
            public void onFailure(Method method, Throwable exception) {
                fail(exception.getMessage());
            }
        });
    }

    /**
     * Test method only success through "onFailure" with restygwt <= 2.0.3 or plain text autodetection set to false (default)
     */
    public void testSendJsonString() {
        StringRestService strings = GWT.create(StringRestService.class);

        delayTestFinish(10000);

        strings.setAsJson("\"Json String?\"", new MethodCallback<Void>() {
            @Override
            public void onSuccess(Method method, Void response) {
                fail();
            }

            @Override
            public void onFailure(Method method, Throwable exception) {
                if (400 == method.getResponse().getStatusCode() && exception instanceof FailedResponseException &&
                        "Wrong Format".equals(((FailedResponseException) exception).getResponse().getText())) {
                    finishTest();
                } else {
                    fail();
                }
            }
        });
    }

    public void testPlainTextStringWithTextCallback() {
        StringRestService strings = GWT.create(StringRestService.class);

        delayTestFinish(10000);

        strings.getAsPlainText(new TextCallback() {
            @Override
            public void onSuccess(Method method, String response) {
                assertEquals("String as plain text", response);
                finishTest();
            }

            @Override
            public void onFailure(Method method, Throwable exception) {
                fail(exception.getMessage());
            }
        });
    }

    /**
     * Test method only success through "onFailure" with restygwt <= 2.0.3 or plain text autodetection set to false (default)
     */
    public void testPlainTextStringWithMethodCallback() {
        StringRestService strings = GWT.create(StringRestService.class);

        delayTestFinish(10000);

        strings.getAsPlainText(new MethodCallback<String>() {
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
        });
    }

    public void testSendPlainTextString() {
        StringRestService strings = GWT.create(StringRestService.class);

        delayTestFinish(10000);

        strings.setAsPlainText("Plain text String?", new MethodCallback<Void>() {
            @Override
            public void onSuccess(Method method, Void response) {
                finishTest();
            }

            @Override
            public void onFailure(Method method, Throwable exception) {
                fail();
            }
        });
    }

}