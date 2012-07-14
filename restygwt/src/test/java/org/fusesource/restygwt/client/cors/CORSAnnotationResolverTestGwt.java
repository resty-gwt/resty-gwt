/**
 * Copyright (C) 2010 the original author or authors.
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

package org.fusesource.restygwt.client.cors;

import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.Resource;
import org.fusesource.restygwt.client.RestServiceProxy;

import com.google.gwt.core.client.GWT;
import com.google.gwt.junit.client.GWTTestCase;

/**
 * testcase for checking the data attribute of the method object.
 */
public class CORSAnnotationResolverTestGwt extends GWTTestCase {

    @Override
    public String getModuleName() {
        return "org.fusesource.restygwt.Event";
    }

    public void testBasicFunctionality_ClassLevel1() {
        CORSServiceClassLevelOnly service = null;

        try {
            Resource resource = new Resource(GWT.getModuleBaseURL());
            service = GWT.create(CORSServiceClassLevelOnly.class);
            ((RestServiceProxy) service).setResource(resource);
            assertNotNull(service);
        } catch (Exception e) {
            e.printStackTrace();
            fail("failed to generate CORSServiceClassLevelOnly");
        }

        delayTestFinish(1000);
        // the request going nowhere, we merely want any response, so we can check for the data in the method object
        service.getExampleDto(new MethodCallback<Void>() {

            @Override
            public void onSuccess(Method method, Void response) {
                assertEquals("[\"api.host.com\"]", method.getData().get(CORS.DOMAIN));
                assertEquals("[\"https\"]", method.getData().get(CORS.PROTOCOL));
                assertEquals("[\"\"]", method.getData().get(CORS.PORT));
                finishTest();
            }

            @Override
            public void onFailure(Method method, Throwable exception) {
                fail();
            }

        });
    }

    public void testBasicFunctionality_ClassLevel2() {
        CORSServiceClassLevelOnly service = null;

        try {
            Resource resource = new Resource(GWT.getModuleBaseURL());
            service = GWT.create(CORSServiceClassLevelOnly.class);
            ((RestServiceProxy) service).setResource(resource);
            assertNotNull(service);
        } catch (Exception e) {
            e.printStackTrace();
            fail("failed to generate CORSServiceClassLevelOnly");
        }

        delayTestFinish(1000);
        // the request going nowhere, we merely want any response, so we can check for the data in the method object
        service.postExample(new MethodCallback<Void>() {

            @Override
            public void onSuccess(Method method, Void response) {
                assertEquals("[\"api.host.com\"]", method.getData().get(CORS.DOMAIN));
                assertEquals("[\"https\"]", method.getData().get(CORS.PROTOCOL));
                assertEquals("[\"\"]", method.getData().get(CORS.PORT));
                finishTest();
            }

            @Override
            public void onFailure(Method method, Throwable exception) {
                fail();
            }

        });
    }

    public void testBasicFunctionality_MethodLeve1() {
        CORSServiceMethodLevelOnly service = null;

        try {
            Resource resource = new Resource(GWT.getModuleBaseURL());
            service = GWT.create(CORSServiceMethodLevelOnly.class);
            ((RestServiceProxy) service).setResource(resource);
            assertNotNull(service);
        } catch (Exception e) {
            e.printStackTrace();
            fail("failed to generate CORSServiceClassLevelOnly");
        }

        delayTestFinish(1000);
        // the request going nowhere, we merely want any response, so we can check for the data in the method object
        service.getExampleDto(new MethodCallback<Void>() {

            @Override
            public void onSuccess(Method method, Void response) {
                assertEquals("[\"api.host.com\"]", method.getData().get(CORS.DOMAIN));
                assertEquals("[\"https\"]", method.getData().get(CORS.PROTOCOL));
                assertEquals("[\"\"]", method.getData().get(CORS.PORT));
                finishTest();
            }

            @Override
            public void onFailure(Method method, Throwable exception) {
                fail();
            }

        });
    }

    public void testBasicFunctionality_MethodLevel2() {
        CORSServiceMethodLevelOnly service = null;

        try {
            Resource resource = new Resource(GWT.getModuleBaseURL());
            service = GWT.create(CORSServiceMethodLevelOnly.class);
            ((RestServiceProxy) service).setResource(resource);
            assertNotNull(service);
        } catch (Exception e) {
            e.printStackTrace();
            fail("failed to generate CORSServiceClassLevelOnly");
        }

        delayTestFinish(1000);
        // the request going nowhere, we merely want any response, so we can check for the data in the method object
        service.postExample(new MethodCallback<Void>() {

            @Override
            public void onSuccess(Method method, Void response) {
                assertEquals("[\"api2.host.com\"]", method.getData().get(CORS.DOMAIN));
                assertEquals("[\"spdy\"]", method.getData().get(CORS.PROTOCOL));
                assertEquals("[\"\"]", method.getData().get(CORS.PORT));
                finishTest();
            }

            @Override
            public void onFailure(Method method, Throwable exception) {
                fail();
            }

        });
    }

    public void testBasicFunctionality_MixedLevels1() {
        CORSServiceMixedLevels service = null;

        try {
            Resource resource = new Resource(GWT.getModuleBaseURL());
            service = GWT.create(CORSServiceMixedLevels.class);
            ((RestServiceProxy) service).setResource(resource);
            assertNotNull(service);
        } catch (Exception e) {
            e.printStackTrace();
            fail("failed to generate CORSServiceClassLevelOnly");
        }

        delayTestFinish(1000);
        // the request going nowhere, we merely want any response, so we can check for the data in the method object
        service.getExampleDto(new MethodCallback<Void>() {

            @Override
            public void onSuccess(Method method, Void response) {
                assertEquals("[\"api.host.com\"]", method.getData().get(CORS.DOMAIN));
                assertEquals("[\"https\"]", method.getData().get(CORS.PROTOCOL));
                assertEquals("[\"8080\"]", method.getData().get(CORS.PORT));
                finishTest();
            }

            @Override
            public void onFailure(Method method, Throwable exception) {
                fail();
            }

        });
    }

    public void testBasicFunctionality_MixedLevels2() {
        CORSServiceMixedLevels service = null;

        try {
            Resource resource = new Resource(GWT.getModuleBaseURL());
            service = GWT.create(CORSServiceMixedLevels.class);
            ((RestServiceProxy) service).setResource(resource);
            assertNotNull(service);
        } catch (Exception e) {
            e.printStackTrace();
            fail("failed to generate CORSServiceClassLevelOnly");
        }

        delayTestFinish(1000);
        // the request going nowhere, we merely want any response, so we can check for the data in the method object
        service.postExample(new MethodCallback<Void>() {

            @Override
            public void onSuccess(Method method, Void response) {
                assertEquals("[\"api2.host.com\"]", method.getData().get(CORS.DOMAIN));
                assertEquals("[\"https\"]", method.getData().get(CORS.PROTOCOL));
                assertEquals("[\"12345\"]", method.getData().get(CORS.PORT));
                finishTest();
            }

            @Override
            public void onFailure(Method method, Throwable exception) {
                fail();
            }

        });
    }

}
