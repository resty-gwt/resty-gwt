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

package org.fusesource.restygwt.examples.client;

import org.fusesource.restygwt.client.JsonCallback;
import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.Resource;
import org.fusesource.restygwt.client.TextCallback;

import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;

/**
 * This test verifies that all the http methods can be accessed via a
 * RestService.
 * 
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 */
public class JsonpTestGWT extends UITestGWT {

    private static final int REQUEST_TIMEOUT = 2000;
    private Resource resource;

    @Override
    protected void gwtSetUp() throws Exception {
        resource = new Resource(GWT.getModuleBaseURL() + "jsonp-service");
    }

    public void testJsonpMethod() {

        JSONObject expected = new JSONObject();
        expected.put("first", new JSONString("Hiram"));
        expected.put("last", new JSONString("Chirino"));
        expected.put("city", new JSONString("Tampa"));
        resource.jsonp().send(expectJsonIsSetTo(expected));
        // a bit of an oddity.. the jsonp() request is NOT done async.
        // delayTestFinish(REQUEST_TIMEOUT);
    }

    private JsonCallback expectJsonIsSetTo(final JSONObject expected) {
        return new JsonCallback() {
            public void onSuccess(Method method, JSONValue response) {
                System.out.println("Got: " + response.toString());
                assertEquals(expected.toString(), response.toString());
                // finishTest();
            }

            public void onFailure(Method method, Throwable exception) {
                fail(exception.getMessage());
            }
        };
    }

}