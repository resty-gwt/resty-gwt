/**
 * Copyright (C) 2009-2011 the original author or authors.
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

package org.fusesource.restygwt.example.client.dispatcher;

import java.util.logging.Logger;

import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.callback.CallbackFilter;

import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.logging.client.LogConfiguration;
import com.google.gwt.user.client.Window;

public class UnauthorizedCallbackFilter implements CallbackFilter {

    @Override
    public RequestCallback filter(Method method, Response response,
            RequestCallback callback) {     
        if (response.getStatusCode() == Response.SC_UNAUTHORIZED) {
                if (LogConfiguration.loggingIsEnabled()) {
                    Logger.getLogger(CallbackFilter.class.getName()).severe("Unauthorized: "
                            + method.builder.getUrl());
                    Window.Location.assign("login.html" + Window.Location.getQueryString());
                }
            }
        return callback;
    }

}
