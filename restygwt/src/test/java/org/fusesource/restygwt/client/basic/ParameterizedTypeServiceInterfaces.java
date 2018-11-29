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

import com.google.gwt.core.client.GWT;
import com.google.gwt.junit.client.GWTTestCase;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.RestService;
import org.fusesource.restygwt.client.complex.JsonTypeIdResolver.DTOImplementation;
import org.fusesource.restygwt.client.complex.JsonTypeIdResolver.DTOInterface;
import org.junit.Test;

public class ParameterizedTypeServiceInterfaces extends GWTTestCase {
    @Override
    public String getModuleName() {
        return "org.fusesource.restygwt.ParameterizedTypeServiceInterface";
    }

    public static class Thing {
        public String name;
        public int shoeSize;
    }

    public interface A<X> extends RestService {
        @GET
        @Produces("application/json")
        void getSomething(MethodCallback<X> callback);
    }

    @Path("/api/ptype/int")
    public interface AOfInt extends A<Integer> {
    }

    @Path("/api/ptype/thing")
    public interface AOfThing extends A<Thing> {
    }

    @Path("/api/ptype")
    public interface SuperOfThing extends RestService {
        @Path("thing")
        A<Thing> getThingInterface();

        @Path("int")
        A<Integer> getIntInterface();
    }

    public interface GenericService<T extends DTOInterface> extends RestService {
        @POST
        @Path("echo")
        void echoName(T dto, MethodCallback<String> callback);
    }

    @Path("/api/concrete")
    public interface ConcreteService extends GenericService<DTOImplementation> {
    }

    @Test
    public void testSimpleType() {
        AOfInt inter = GWT.create(AOfInt.class);
        inter.getSomething(new MethodCallback<Integer>() {
            @Override
            public void onFailure(Method method, Throwable exception) {
                fail(exception.getMessage());
            }

            @Override
            public void onSuccess(Method method, Integer response) {
                assertEquals(Integer.valueOf(123456), response);
                finishTest();
            }
        });
        delayTestFinish(10000);
    }

    @Test
    public void testComplexType() {
        AOfThing inter = GWT.create(AOfThing.class);
        inter.getSomething(new MethodCallback<Thing>() {
            @Override
            public void onFailure(Method method, Throwable exception) {
                fail(exception.getMessage());
            }

            @Override
            public void onSuccess(Method method, Thing value) {
                assertEquals("Fred Flintstone", value.name);
                assertEquals(12, value.shoeSize);
                finishTest();
            }
        });
        delayTestFinish(10000);
    }

    @Test
    public void testSubResource() {
        SuperOfThing inter = GWT.create(SuperOfThing.class);
        inter.getThingInterface().getSomething(new MethodCallback<Thing>() {
            @Override
            public void onFailure(Method method, Throwable exception) {
                fail(exception.getMessage());
            }

            @Override
            public void onSuccess(Method method, Thing value) {
                assertEquals("Fred Flintstone", value.name);
                assertEquals(12, value.shoeSize);
                finishTest();
            }
        });
        delayTestFinish(10000);
    }

    @Test
    public void testSubResource2() {
        SuperOfThing inter = GWT.create(SuperOfThing.class);
        inter.getIntInterface().getSomething(new MethodCallback<Integer>() {
            @Override
            public void onFailure(Method method, Throwable exception) {
                fail(exception.getMessage());
            }

            @Override
            public void onSuccess(Method method, Integer value) {
                assertEquals(Integer.valueOf(123456), value);
                finishTest();
            }
        });
        delayTestFinish(10000);
    }

    @Test
    public void testGenericService() {
        final DTOImplementation dto = new DTOImplementation();
        dto.setName("impl name");
        ConcreteService service = GWT.create(ConcreteService.class);
        service.echoName(dto, new MethodCallback<String>() {
            @Override
            public void onFailure(Method method, Throwable exception) {
                fail(exception.getMessage());
            }

            @Override
            public void onSuccess(Method method, String response) {
                assertEquals(dto.getName(), response);
                finishTest();
            }
        });
        delayTestFinish(10000);
    }

}
