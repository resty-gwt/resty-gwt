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
package org.fusesource.restygwt.examples.client;

import java.util.ArrayList;
import java.util.List;

import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.examples.client.JSONBindingService;
import org.fusesource.restygwt.examples.client.JSONBindingService.StringMapResponse;
import org.junit.Before;

import com.google.gwt.core.client.GWT;

/**
 * This test verifies that all the http methods 
 * can be accessed via a RestService.
 * 
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 */
public class JSONBindingUITestGWT extends UITestGWT {

	private static final int REQUEST_TIMEOUT = 2000;
    private JSONBindingService service;
	
    protected void gwtSetUp() throws Exception {
        service = GWT.create(JSONBindingService.class);
	}

    public void testGetListOfStrings() {
        
        List<String> expected = new ArrayList<String>();
        expected.add("hello");
        expected.add("world");
        service.getListOfStrings(expectResult(expected));
        delayTestFinish(REQUEST_TIMEOUT);
        
    }

    public void testGetStringMapResponse() {
        StringMapResponse expected = new StringMapResponse();
        expected.data.put("hello", "world");
        service.getStringMapResponse(expectResult(expected));
        delayTestFinish(REQUEST_TIMEOUT);
    }

    private <T> MethodCallback<T> expectResult(final T expectedResult) {
        return new MethodCallback<T>() {
            public void onSuccess(Method method, T result) {
                assertEquals( expectedResult, result);
                finishTest();
            }
            public void onFailure(Method method, Throwable exception) {
                fail(exception.getMessage());
            }
        };
    }
    
}