/**
 * Copyright (C) 2009-2010 the original author or authors.
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

package org.fusesource.restygwt.client.callback;

import org.fusesource.restygwt.client.Method;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.Window;

public class RetryingCallback extends FilterawareRetryingCallback {


    /**
     * Simple security: If a user contacts a service where there is "error
     * forbidden aka 505" The user gets redirected immediately to
     * an URL.
     * <p/>
     * Use case: Imagine a GWT client running a social network. The users
     * uses that GWT client in internet cafe. And logs out at the end of his session.
     * <p/>
     * Another new user hits the back button. What happens? Old values? No. This
     * automatically redirects to index.html No. This does not replace any
     * other security measures. It's the simplest way to omit stuff.
     */
    private String forceRedirectUrlWhen505 = "index.html";


    public RetryingCallback(Method method) {
        super(method);
    }

    @Override
    protected void _onResponseReceived(Request request, Response response) {

        if (response.getStatusCode() == Response.SC_UNAUTHORIZED) {
            //FIXME: to be removed...
            Window.Location.replace(forceRedirectUrlWhen505);

        } else if (response.getStatusCode() != Response.SC_OK) {

            handleErrorGracefully();

        } else {
            this.requestCallback.onResponseReceived(request, response);
        }
    }
}
