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

import java.util.LinkedHashMap;
import java.util.Map;

import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.examples.client.MapResult;
import org.fusesource.restygwt.examples.client.MapService;

import com.google.gwt.core.client.GWT;
import com.google.gwt.junit.client.GWTTestCase;

/**
 * 
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 */
public class CxfJaxsonTestGWT extends GWTTestCase {

    public String getModuleName() {
        return "org.fusesource.restygwt.examples.CXF_JAXSON";
    }

    private static final int REQUEST_TIMEOUT = 2000;

    public void testGet() {
        MapService service = GWT.create(MapService.class);
        Map<String, String> map = new LinkedHashMap<String, String>();
        map.put("hello", "world");
        map.put("another", "value");
        service.get(expectResult(new MapResult(map)));
        delayTestFinish(REQUEST_TIMEOUT);
    }

    private <T> MethodCallback<T> expectResult(final T expectedResult) {
        return new MethodCallback<T>() {
            public void onSuccess(Method method, T result) {
                assertEquals(expectedResult, result);
                finishTest();
            }

            public void onFailure(Method method, Throwable exception) {
                fail(exception.getMessage());
            }
        };
    }

}