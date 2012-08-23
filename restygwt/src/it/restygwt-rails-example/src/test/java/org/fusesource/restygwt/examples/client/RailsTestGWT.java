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

import java.util.ArrayList;
import java.util.List;

import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.RestServiceProxy;
import org.fusesource.restygwt.example.client.dispatcher.DispatcherFactory;

import com.google.gwt.core.client.GWT;

/**
 * 
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 */
public class RailsTestGWT extends RailsCreateTestGWT {

    public void testIndex() {
        UsersRestService service = GWT.create(UsersRestService.class);
        ((RestServiceProxy)service).setDispatcher(new DispatcherFactory().xsrfProtectionDispatcher());
        List<User> expected = new ArrayList<User>();
        expected.add(persistentUser);
        service.index(expectResult(expected)); 
        delayTestFinish(REQUEST_TIMEOUT);
    }

    public void testShow() {
        UsersRestService service = GWT.create(UsersRestService.class);
        ((RestServiceProxy)service).setDispatcher(new DispatcherFactory().xsrfProtectionDispatcher());
        service.show(persistentUser.id, expectResult(persistentUser)); 
        delayTestFinish(REQUEST_TIMEOUT);
    }

    public void testUpdate() {
        UsersRestService service = GWT.create(UsersRestService.class);
        ((RestServiceProxy)service).setDispatcher(new DispatcherFactory().xsrfProtectionDispatcher());
        final User user = newUser();
        user.id = persistentUser.id;
        service.update(user, expectResult(persistentUser)); 
        delayTestFinish(REQUEST_TIMEOUT);
    }

    public void testUpdateWithoutXSRF() {
        UsersRestService service = GWT.create(UsersRestService.class);
        final User user = newUser();
        user.id = persistentUser.id;
        service.update(user, new MethodCallback<User>() {

            public void onFailure(Method method, Throwable exception) {
                assertEquals("Not Found", exception.getMessage());
                finishTest();
            }

            public void onSuccess(Method method, User response) {
                fail("expected some failture do to missing xsrf token");
            }
        }); 
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