/**
 * Copyright (C) 2009-2013 the original author or authors.
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

package org.fusesource.restygwt.client.basic;

import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

import com.google.gwt.core.client.GWT;
import com.google.gwt.junit.client.GWTTestCase;

import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.Resource;
import org.fusesource.restygwt.client.RestService;
import org.fusesource.restygwt.client.RestServiceProxy;
import org.fusesource.restygwt.rebind.MethodDataParam;

/**
 * @author PhiLhoSoft
 */
public class MethodDataParamTestGwt extends GWTTestCase {
    private static final String QUERY_KEY = "id";
    private static final String DATA_KEY = "callerId";

    private QueryWithParamTestRestService service;

    @Override
    public String getModuleName() {
        return "org.fusesource.restygwt.EchoTestGwt";
    }

    @Path("/get")
    static interface QueryWithParamTestRestService extends RestService {
        void get(
                @QueryParam(QUERY_KEY) int id,
                @MethodDataParam(DATA_KEY) String callerId,
                MethodCallback<Echo> callback);
    }

    class EchoMethodCallback implements MethodCallback<Echo> {
        private final String id;
        private final String callerId;

        EchoMethodCallback(String id, String callerId) {
            this.id = id;
            this.callerId = callerId;
        }

        @Override
        public void onSuccess(Method method, Echo response) {
            assertEquals(response.params.get(QUERY_KEY), id);
            assertEquals(response.params.size(), 1);

            assertEquals(method.getData().get(DATA_KEY), callerId);

            finishTest();
        }

        @Override
        public void onFailure(Method method, Throwable exception) {
            fail();
        }
    }

    @Override
    protected void gwtSetUp() throws Exception {
        super.gwtSetUp();
        service = GWT.create(QueryWithParamTestRestService.class);
        Resource resource = new Resource(GWT.getModuleBaseURL() + "echo");
        ((RestServiceProxy) service).setResource(resource);
    }

    public void testGetWithId() {
        service.get(42, "H2G2", new EchoMethodCallback("42", "H2G2"));
    }

    public void testGetWithNull() {
        service.get(123, null, new EchoMethodCallback("123", null));
    }

    @Override
    public void gwtTearDown() {
        // wait... we are in async testing...
        delayTestFinish(10000);
    }
}
