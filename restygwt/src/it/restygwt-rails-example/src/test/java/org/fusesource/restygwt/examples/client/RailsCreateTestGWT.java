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

import org.fusesource.restygwt.client.Defaults;
import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;

import com.google.gwt.core.client.GWT;
import com.google.gwt.junit.client.GWTTestCase;

/**
 * 
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 */
public class RailsCreateTestGWT extends GWTTestCase {

    public String getModuleName() {
        return "org.fusesource.restygwt.examples.RAILS";
    }

    static final int REQUEST_TIMEOUT = 2000;

    static final User persistentUser = newUser();

    static User newUser(){
        User user = new User();
        user.name = "me and the corner";
        return user;
    }
	
    public void testCreate() {
        UsersRestService service = GWT.create(UsersRestService.class);
        final User user = newUser();
        service.create(user, new MethodCallback<User>() {
            public void onSuccess(Method method, User result) {
                persistentUser.id = result.id;
                persistentUser.name = result.name;
                persistentUser.groups = result.groups;
                assertEquals(user.name, result.name);
                finishTest();
            }

            public void onFailure(Method method, Throwable exception) {
                fail(exception.getMessage());
            }
        });
        delayTestFinish(REQUEST_TIMEOUT);
    }

}