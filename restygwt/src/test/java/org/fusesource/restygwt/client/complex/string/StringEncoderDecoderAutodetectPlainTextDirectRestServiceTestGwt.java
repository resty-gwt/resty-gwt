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

import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.REST;
import org.fusesource.restygwt.client.TextCallback;
import org.fusesource.restygwt.client.complex.string.service.StringDirectRestService;

public class StringEncoderDecoderAutodetectPlainTextDirectRestServiceTestGwt
    extends StringEncoderDecoderDirectRestServiceTestGwt {

    @Override
    public String getModuleName() {
        return "org.fusesource.restygwt.StringEncoderDecoderAutodetectPlainTextTestGwt";
    }

    /**
     * Changed behaviour to only success through "onSuccess".
     */
    @Override
    public void testPlainTextStringWithTextCallback() {
        StringDirectRestService service = GWT.create(StringDirectRestService.class);

        delayTestFinish(10000);

        REST.withCallback(new TextCallback() {
            @Override
            public void onSuccess(Method method, String response) {
                assertEquals("String as plain text", response);
                finishTest();
            }

            @Override
            public void onFailure(Method method, Throwable exception) {
                fail(exception.getMessage());
            }
        }).call(service).getAsPlainText();
    }

    /**
     * Changed behaviour to only success through "onSuccess".
     */
    @Override
    public void testPlainTextStringWithMethodCallback() {
        StringDirectRestService service = GWT.create(StringDirectRestService.class);

        delayTestFinish(10000);

        REST.withCallback(new MethodCallback<String>() {
            @Override
            public void onSuccess(Method method, String response) {
                assertEquals("String as plain text", response);
                finishTest();
            }

            @Override
            public void onFailure(Method method, Throwable exception) {
                fail(exception.getMessage());
            }
        }).call(service).getAsPlainText();
    }

    @Override
    public void testSendJsonString() {
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
        }).call(service).setAsJson("\"Json String?\"");
    }

}