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

import com.google.gwt.core.client.GWT;
import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.safehtml.shared.SafeHtml;

import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.Resource;
import org.fusesource.restygwt.client.RestServiceProxy;

/**
 * tests the behaviour of using the {@link SafeHtml} type within
 * rest DTOs
 *
 * @author <a href="mailto:tim@elbart.com">Tim Eggert</<a>
 */
public class SafeHtmlTestGwt extends GWTTestCase {

    @Override
    public String getModuleName() {
        return "org.fusesource.restygwt.BasicTestGwt";
    }

    public void testSafeHtmlDtoResponse() {

        //configure RESTY
        Resource resource = new Resource(GWT.getModuleBaseURL() + "api/getsafehtmldto");

        SafeHtmlDtoService service = GWT.create(SafeHtmlDtoService.class);
        ((RestServiceProxy) service).setResource(resource);

        service.getSafeHtmlDto(new MethodCallback<SafeHtmlDto>() {

            @Override
            public void onFailure(Method method, Throwable exception) {
                fail();
            }

            @Override
            public void onSuccess(Method method, SafeHtmlDto response) {
                assertTrue(response.getSafeHtml() instanceof SafeHtml);
                assertEquals(response.getSafeHtml().asString(), "&lt;script&gt;alert(123)&lt;/script&gt;");
                assertFalse(response.getUnsafeString().equals(response.getSafeHtml().asString()));

                finishTest();
            }
        });

        // wait... we are in async testing...
        delayTestFinish(10000);
    }
}