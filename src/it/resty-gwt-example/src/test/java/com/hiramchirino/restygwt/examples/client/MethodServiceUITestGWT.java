/**
 * Copyright (C) 2009  Hiram Chirino
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *    http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hiramchirino.restygwt.examples.client;

import com.google.gwt.core.client.GWT;
import com.hiramchirino.restygwt.client.Method;
import com.hiramchirino.restygwt.client.MethodCallback;

/**
 * This test verifies that all the http methods 
 * can be accessed via a RestService.
 * 
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 */
public class MethodServiceUITestGWT extends UITestGWT {

	private MethodService service;

	@Override
	protected void gwtSetUp() throws Exception {
        service = GWT.create(MethodService.class);
	}
	
    public void testDelete() {
        service.delete(expectHeaderIsSetTo("echo_method", "delete"));
        delayTestFinish(2000);
    }

    public void testGet() {
        service.get(expectHeaderIsSetTo("echo_method", "get"));
        delayTestFinish(2000);
    }

    public void testHead() {
        service.head(expectHeaderIsSetTo("echo_method", "head"));
        delayTestFinish(2000);
    }

    public void testOptions() {
        service.options(expectHeaderIsSetTo("echo_method", "options"));
        delayTestFinish(2000);
    }

    public void testPost() {
        service.post(expectHeaderIsSetTo("echo_method", "post"));
        delayTestFinish(2000);
    }

    public void testPut() {
        service.put(expectHeaderIsSetTo("echo_method", "put"));
        delayTestFinish(2000);
    }
    
	private MethodCallback<String> expectHeaderIsSetTo(final String name, final String value) {
		return new MethodCallback<String>() {
            public void onSuccess(Method method, String response) {
				assertEquals( value, method.getResponse().getHeader(name));
                finishTest();
            }
            public void onFailure(Method method, Throwable exception) {
                fail(exception.getMessage());
            }
        };
	}
    
}