/**
 * Copyright 2015 the original author or authors.
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

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodAccessHelperDispatcher;
import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.REST;
import org.fusesource.restygwt.client.RestServiceProxy;

import com.google.gwt.core.client.GWT;
import com.google.gwt.junit.client.GWTTestCase;

/**
 * @author Ralf Sommer <ralf.sommer.dev@gmail.com>
 */
public class ConfiguredServiceTestGwt extends GWTTestCase {

    @Override
    public String getModuleName() {
        return "org.fusesource.restygwt.BasicTestGwt";
    }

    public void testConfigured_notNull() {
        ConfiguredService service = GWT.create(ConfiguredService.class);
        assertNotNull(service);
    }

    public void testConfiguredWithoutExpect_notNull() {
        ConfiguredWithoutExpectService service = GWT.create(ConfiguredWithoutExpectService.class);
        assertNotNull(service);
    }

    public void testConfiguredWithoutExpectDirect_notNull() {
        ConfiguredWithoutExpectDirectService service = GWT.create(ConfiguredWithoutExpectDirectService.class);
        assertNotNull(service);
    }

    public void testConfiguredDirect_notNullAndRightExpect() {
        ConfiguredDirectService service = GWT.create(ConfiguredDirectService.class);
        assertNotNull(service);

        ((RestServiceProxy) service).setDispatcher(new MethodAccessHelperDispatcher() {
            @Override
            protected void expect(Set<Integer> actualStatuses, boolean anyStatus) {
                assertEquals(false, anyStatus);

                HashSet<Integer> expectedStatuses = new HashSet<Integer>();
                Collections.addAll(expectedStatuses, 200, 204);

                assertEquals(expectedStatuses, actualStatuses);
            }
        });
        REST.withCallback(new MethodCallback<ExampleDto>() {
            @Override
            public void onFailure(Method method, Throwable exception) {
            }

            @Override
            public void onSuccess(Method method, ExampleDto response) {
            }
        }).call(service).getExampleDto();

    }
}
