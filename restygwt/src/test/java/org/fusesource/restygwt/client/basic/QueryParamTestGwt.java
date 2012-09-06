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

package org.fusesource.restygwt.client.basic;

import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.Resource;
import org.fusesource.restygwt.client.RestService;
import org.fusesource.restygwt.client.RestServiceProxy;

import com.google.gwt.core.client.GWT;
import com.google.gwt.junit.client.GWTTestCase;

/**
 *
 *
 * @author mkristian
 *
 */
public class QueryParamTestGwt extends GWTTestCase {

    private QueryTestRestService service;

    @Override
    public String getModuleName() {
        return "org.fusesource.restygwt.EchoTestGwt";
    }

    @Path("/get")
    static interface QueryTestRestService extends RestService {
        
        void get(@QueryParam(value = "id") int id, MethodCallback<Echo> callback);

        void get(@QueryParam(value = "id") Integer id, MethodCallback<Echo> callback);
    }
    
    class EchoMethodCallback implements MethodCallback<Echo> {
        
        private final String id;

        EchoMethodCallback(String id){
            this.id = id;
        }
        
        @Override
        public void onSuccess(Method method, Echo response) {

            assertEquals(response.params.get("id"), id);
            assertEquals(response.params.size(), 1);

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
        service = GWT.create(QueryTestRestService.class);  
        Resource resource = new Resource(GWT.getModuleBaseURL() + "echo");
        ((RestServiceProxy) service).setResource(resource);
    }

    public void testGetWithInteger() {
    
        service.get(new Integer(2), new EchoMethodCallback("2"));

    }

    public void testGetWithNull() {
    
        service.get(null, new MethodCallback<Echo>(){

            @Override
            public void onFailure(Method method, Throwable exception) {
                
                fail();
                
            }

            @Override
            public void onSuccess(Method method, Echo response) {
                
                assertFalse(response.params.containsKey("id"));
                assertEquals(response.params.size(), 0);
                
            }
        });

    }

    public void testGetWithInt() {
    
        service.get(123, new EchoMethodCallback("123"));

    }

    public void gwtTearDown() {

        // wait... we are in async testing...
        delayTestFinish(10000);
        
    }
}