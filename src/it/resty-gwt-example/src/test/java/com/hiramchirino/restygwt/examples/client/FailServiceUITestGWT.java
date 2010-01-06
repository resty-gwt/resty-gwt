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
import com.hiramchirino.restygwt.client.FailedStatusCodeException;
import com.hiramchirino.restygwt.client.Method;
import com.hiramchirino.restygwt.client.MethodCallback;
import com.hiramchirino.restygwt.client.Resource;
import com.hiramchirino.restygwt.client.RestServiceProxy;

/**
 * This test verifies that all the http methods 
 * can be accessed via a RestService.
 * 
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 */
public class FailServiceUITestGWT extends UITestGWT {

	private MethodService service;

	@Override
	protected void gwtSetUp() throws Exception {
	    Resource resource = new Resource( GWT.getModuleBaseURL() + "test/fail");
        service = GWT.create(MethodService.class);
        ((RestServiceProxy)service).setResource(resource);
	}
	
    public void testGet() {
        service.get(expectFailedStatusCodeException(501));
        delayTestFinish(10000);
    }

	private MethodCallback<String> expectFailedStatusCodeException(final int expectedStatusCode) {
		return new MethodCallback<String>() {
            public void onSuccess(Method method, String response) {
                fail("Expected Failure with Status Code ");
            }
            public void onFailure(Method method, Throwable exception) {
                assertNotNull(method);
                assertNotNull(exception);
                assertTrue(exception instanceof FailedStatusCodeException);
                FailedStatusCodeException fsce = (FailedStatusCodeException) exception;
                assertEquals(expectedStatusCode, fsce.getStatusCode());
            }
        };
	}
    
}