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

package org.fusesource.restygwt.client.complex;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.RestService;
import org.fusesource.restygwt.client.TextCallback;

import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONException;
import com.google.gwt.junit.client.GWTTestCase;

public class StringEncoderDecoderTestGwt extends GWTTestCase {

    @Override
    public String getModuleName() {
        return "org.fusesource.restygwt.StringEncoderDecoderTestGwt";
    }

    @Path("/strings")
    public static interface Strings {
        @GET
        @Produces(MediaType.APPLICATION_JSON)
        String getAsJson();

        @GET
        @Produces(MediaType.TEXT_PLAIN)
        String getAsPlainText();

        @POST
        @Consumes(MediaType.APPLICATION_JSON)
        void setAsJson(String text);

        @POST
        @Consumes(MediaType.TEXT_PLAIN)
        void setAsPlainText(String text);
    }

    @Path("/strings")
    public static interface StringsAsync extends RestService {
        @GET
        @Produces(MediaType.APPLICATION_JSON)
        void getAsJson(MethodCallback<String> callback);

        @GET
        @Produces(MediaType.TEXT_PLAIN)
        void getAsPlainText(MethodCallback<String> callback);

        @GET
        @Produces(MediaType.TEXT_PLAIN)
        void getAsPlainText(TextCallback callback);

        @POST
        @Consumes(MediaType.APPLICATION_JSON)
        void setAsJson(String text, MethodCallback<Void> callback);

        @POST
        @Consumes(MediaType.TEXT_PLAIN)
        void setAsPlainText(String text, MethodCallback<Void> callback);
    }

    public void testJsonString() {
        StringsAsync strings = GWT.create(StringsAsync.class);

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
        StringsAsync strings = GWT.create(StringsAsync.class);

        delayTestFinish(10000);

        strings.setAsJson("\"Json String?\"", new MethodCallback<Void>() {
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
        });
    }

    public void testPlainTextStringWithTextCallback() {
        StringsAsync strings = GWT.create(StringsAsync.class);

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
        StringsAsync strings = GWT.create(StringsAsync.class);

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
        StringsAsync strings = GWT.create(StringsAsync.class);

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