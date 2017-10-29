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

package org.fusesource.restygwt.client.complex;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.RestService;
import org.fusesource.restygwt.server.complex.DTOTypeResolver;
import org.fusesource.restygwt.server.complex.InterfaceAndImplementationTypeResolver;
import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.google.gwt.core.client.GWT;
import com.google.gwt.junit.client.GWTTestCase;

public class JsonTypeIdResolver extends GWTTestCase {
    // is this needed?
    @JsonTypeInfo(use = JsonTypeInfo.Id.CUSTOM, include = JsonTypeInfo.As.PROPERTY, property = "@type")
    @com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver(DTOTypeResolver.class)
    public static abstract class AbstractDTO {
        public String name;
    }

    public static class DTO1 extends AbstractDTO {
        public Integer size;
    }

    public static class DTO2 extends AbstractDTO {
        public String foo;
    }

    @Path("api/jsontypeid")
    public static interface ServiceInterface extends RestService {
        @GET
        @Produces("application/json")
        public void getSomeDTOs(MethodCallback<List<AbstractDTO>> callback);
    }

    @AbstractJacksonAnnotationsInside
    public static abstract class AbstractCustomDto {
        public String name;
    }

    public static class DTOCustom1 extends AbstractCustomDto {
        public Integer size;
    }

    public static class DTOCustom2 extends AbstractCustomDto {
        public String foo;
    }

    @Path("api/jsontypeidinside")
    public static interface ServiceInterfaceInside extends RestService {
        @GET
        @Produces("application/json")
        public void getSomeDTOs(MethodCallback<List<AbstractCustomDto>> callback);
    }

    @Override
    public String getModuleName() {
        return "org.fusesource.restygwt.JsonTypeIdResolver";
    }

    @Test
    public void testTypeIdResolver() {
        ServiceInterface service = GWT.create(ServiceInterface.class);
        service.getSomeDTOs(new MethodCallback<List<AbstractDTO>>() {
            @Override
            public void onFailure(Method method, Throwable exception) {
                fail(exception.getMessage());
            }

            @Override
            public void onSuccess(Method method, List<AbstractDTO> response) {
                assertTrue(response.get(0) instanceof DTO1);
                assertEquals("Fred Flintstone", response.get(0).name);

                assertTrue(response.get(1) instanceof DTO2);
                assertEquals("Barney Rubble", response.get(1).name);

                assertTrue(response.get(2) instanceof DTO2);
                assertEquals("BamBam Rubble", response.get(2).name);
                finishTest();
            }
        });
        delayTestFinish(10000);
    }

    @Test
    public void testTypeIdResolverWithJacksonAnnotationInside() {
        ServiceInterfaceInside service = GWT.create(ServiceInterfaceInside.class);
        service.getSomeDTOs(new MethodCallback<List<AbstractCustomDto>>() {
            @Override
            public void onFailure(Method method, Throwable exception) {
                fail(exception.getMessage());
            }

            @Override
            public void onSuccess(Method method, List<AbstractCustomDto> response) {
                assertTrue(response.get(0) instanceof DTOCustom1);
                assertEquals("Fred Flintstone", response.get(0).name);

                assertTrue(response.get(1) instanceof DTOCustom2);
                assertEquals("Barney Rubble", response.get(1).name);

                assertTrue(response.get(2) instanceof DTOCustom2);
                assertEquals("BamBam Rubble", response.get(2).name);
                finishTest();
            }
        });
        delayTestFinish(10000);
    }

    @Path("api/interfaceandimpl")
    public static interface InterfaceAndImplementationService extends RestService {
        @GET
        @Path("interface")
        @Produces("application/json")
        public void getInterface(MethodCallback<List<DTOInterface>> callback);

        @GET
        @Path("implementation")
        @Produces("application/json")
        public void getImplementation(MethodCallback<List<DTOImplementation>> callback);
    }

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
    @com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver(InterfaceAndImplementationTypeResolver.class)
    public interface DTOInterface {
        String getName();

        void setName(String name);
    }

    public static class DTOImplementation implements DTOInterface {
        private String name;

        @Override
        public String getName() {
            return name;
        }

        @Override
        public void setName(String name) {
            this.name = name;
        }
    }

    @Test
    public void testInterface() {
        InterfaceAndImplementationService service = GWT.create(InterfaceAndImplementationService.class);
        service.getInterface(new MethodCallback<List<DTOInterface>>() {
            @Override
            public void onFailure(Method method, Throwable exception) {
                fail(exception.getMessage());
            }

            @Override
            public void onSuccess(Method method, List<DTOInterface> response) {
                assertTrue(response.get(0) instanceof DTOImplementation);
                assertEquals("interface", response.get(0).getName());
                finishTest();
            }
        });
        delayTestFinish(10000);
    }

    @Test
    public void testImplementation() {
        InterfaceAndImplementationService service = GWT.create(InterfaceAndImplementationService.class);
        service.getImplementation(new MethodCallback<List<DTOImplementation>>() {
            @Override
            public void onFailure(Method method, Throwable exception) {
                fail(exception.getMessage());
            }

            @Override
            public void onSuccess(Method method, List<DTOImplementation> response) {
                assertTrue(response.get(0) instanceof DTOImplementation);
                assertEquals("implementation", response.get(0).getName());
                finishTest();
            }
        });
        delayTestFinish(10000);
    }

}
