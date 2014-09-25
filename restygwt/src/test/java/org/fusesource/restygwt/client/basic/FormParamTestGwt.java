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
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.junit.client.GWTTestCase;
import org.fusesource.restygwt.client.*;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import java.util.*;

/**
 *
 *
 * @author mkristian
 * @author Bogdan Mustiata &lt;bogdan.mustiata@gmail.com&gt;
 *
 */
public class FormParamTestGwt extends GWTTestCase {

    private FormParamTestRestService service;

    @Override
    public String getModuleName() {
        return "org.fusesource.restygwt.EchoTestGwt";
    }

    @Path("/get")
    static interface FormParamTestRestService extends RestService {

        @POST
        void get(@FormParam(value = "id") int id, MethodCallback<Echo> callback);

        @POST
        void get(@FormParam(value = "id") Integer id, MethodCallback<Echo> callback);

        @POST
        void twoParams(@FormParam(value = "id") int id, @FormParam(value = "dto") ExampleDto exampleDto, MethodCallback<Echo> callback);

        @POST
        void listParams(@FormParam(value = "dtoList") List<ExampleDto> exampleDtoList, MethodCallback<Echo> callback);

        @POST
        void arrayParams(@FormParam(value = "dtoArray") ExampleDto[] exampleDtoArray, MethodCallback<Echo> callback);

        @POST
        void enumParam(@FormParam("param") FormParamTestEnum param, MethodCallback<Echo> callback);
    }

    enum FormParamTestEnum {

        VALUE
    }

    class EchoMethodCallback implements MethodCallback<Echo> {
        
        private final String id;

        EchoMethodCallback(String id){
            this.id = id;
        }
        
        @Override
        public void onSuccess(Method method, Echo response) {
            GWT.log("method was called: " + response.params.get("id"));

            assertEquals(response.params.get("id"), id);
            assertEquals(response.params.size(), 1);

            finishTest();

        }

        @Override
        public void onFailure(Method method, Throwable exception) {
            System.out.println("test failed");
            fail();
        }
    }
    
    @Override
    protected void gwtSetUp() throws Exception {
        super.gwtSetUp();        
        service = GWT.create(FormParamTestRestService.class);
        Resource resource = new Resource(GWT.getModuleBaseURL() + "echo");
        ((RestServiceProxy) service).setResource(resource);

        delayTestFinish(10000);
    }

    public void testGetWithInt() {
        service.get(123, new EchoMethodCallback("123"));
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

                finishTest();
            }
        });
    }

    public interface ExampleDtoDecoder extends JsonEncoderDecoder<ExampleDto> {
    }

    public void testPostWithDto() {
        final ExampleDtoDecoder dtoDecoder = GWT.create(ExampleDtoDecoder.class);
        final ExampleDto dto = createDtoObject();

        service.twoParams(3, dto, new MethodCallback<Echo>() {
            @Override
            public void onFailure(Method method, Throwable exception) {
                fail();
            }

            @Override
            public void onSuccess(Method method, Echo response) {
                assertEquals(2, response.params.size());
                assertEquals("3", response.params.get("id"));

                JSONValue jsonDto = JSONParser.parseStrict(response.params.get("dto"));
                assertEquals(dto, dtoDecoder.decode(jsonDto));

                finishTest();
            }
        });
    }

    public void testPostWithDtoList() {
        final ObjectEncoderDecoder objectEncoderDecoder = new ObjectEncoderDecoder();
        final List<ExampleDto> dtoList = Collections.singletonList( createDtoObject() );

        service.listParams(dtoList, new MethodCallback<Echo>() {
            @Override
            public void onFailure(Method method, Throwable exception) {
                fail();
            }

            @Override
            public void onSuccess(Method method, Echo response) {
                assertEquals(1, response.params.size());

                JSONValue jsonDto = JSONParser.parseStrict(response.params.get("dtoList"));
                final Object decoded_object = objectEncoderDecoder.decode(jsonDto);
                if (decoded_object instanceof Collection) {
                    final Collection<String> decoded_list = (Collection<String>) decoded_object;
                    final List decoded_elem_list = new ArrayList();
                    for (final String json_elem : decoded_list) {
                        decoded_elem_list.add(objectEncoderDecoder.decode(json_elem));
                    }
                    assertEquals(createDtoObjectAsList(), decoded_elem_list);
                } else {
                    assertEquals(createDtoObjectAsList(), Arrays.asList(decoded_object));
                }

                finishTest();
            }
        });
    }

    public void testPostWithDtoArray() {
        final ObjectEncoderDecoder objectEncoderDecoder = new ObjectEncoderDecoder();
        final ExampleDto[] dtoList = new ExampleDto[] { createDtoObject() };

        service.arrayParams(dtoList, new MethodCallback<Echo>() {
            @Override
            public void onFailure(Method method, Throwable exception) {
                fail();
            }

            @Override
            public void onSuccess(Method method, Echo response) {
                assertEquals(1, response.params.size());

                JSONValue jsonDto = JSONParser.parseStrict(response.params.get("dtoArray"));
                assertEquals(createDtoObjectAsList(), objectEncoderDecoder.decode(jsonDto));

                finishTest();
            }
        });
    }

    public void testPostWithEnum() {
        service.enumParam(FormParamTestEnum.VALUE, new MethodCallback<Echo>() {
            @Override
            public void onFailure(Method method, Throwable exception) {
                fail();
            }

            @Override
            public void onSuccess(Method method, Echo response) {
                assertEquals(1, response.params.size());
                assertEquals("VALUE", response.params.get("param"));

                finishTest();
            }
        });
    }

    public void testPostWithNullEnum() {
        service.enumParam(null, new MethodCallback<Echo>() {
            @Override
            public void onFailure(Method method, Throwable exception) {
                fail();
            }

            @Override
            public void onSuccess(Method method, Echo response) {
                assertTrue(response.params.isEmpty());

                finishTest();
            }
        });
    }

    private List createDtoObjectAsList() {
        ArrayList result = new ArrayList();

        result.add(
            map("name", "dtoName",
                "complexMap1", map(
                    "1", "one",
                    "2", "two",
                    "3", "three"
                ),
                "complexMap2", null,
                "complexMap3", null,
                "complexMap4", null,
                "complexMap5", null,
                "complexMap7", null,
                "complexMap8", null,
                "complexMap9", null,
                "complexMap10", null,
                "complexMap11", null
            )
        );

        return result;
    }

    public HashMap map(Object... keyValues) {
        HashMap result = new HashMap();

        for (int i = 0; i < keyValues.length; i += 2 ) {
            result.put(keyValues[i], keyValues[i + 1]);
        }

        return result;
    }

    private ExampleDto createDtoObject() {
        final ExampleDto dto = new ExampleDto();
        dto.name = "dtoName";
        dto.complexMap1 = new HashMap<Integer, String>();
        dto.complexMap1.put(1, "one");
        dto.complexMap1.put(2, "two");
        dto.complexMap1.put(3, "three");
        return dto;
    }
}