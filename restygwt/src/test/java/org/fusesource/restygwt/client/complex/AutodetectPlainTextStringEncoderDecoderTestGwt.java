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

import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;

import com.google.gwt.core.client.GWT;

public class AutodetectPlainTextStringEncoderDecoderTestGwt extends StringEncoderDecoderTestGwt {

    @Override
    public String getModuleName() {
        return "org.fusesource.restygwt.AutodetectPlainTextStringEncoderDecoderTestGwt";
    }

    /**
     * Changed behaviour to only success through "onSuccess".
     */
    @Override
    public void testPlainTextStringWithMethodCallback() {
        StringsAsync strings = GWT.create(StringsAsync.class);

        delayTestFinish(10000);

        strings.getAsPlainText(new MethodCallback<String>() {
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

}