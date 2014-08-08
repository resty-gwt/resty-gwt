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

package org.fusesource.restygwt.client.dispatcher;

import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.callback.XSRFToken;

import com.google.gwt.http.client.RequestBuilder;

public class XSRFTokenDispatcherFilter implements DispatcherFilter {

    private XSRFToken xsrf;

    public XSRFTokenDispatcherFilter(final XSRFToken xsrf) {
        this.xsrf = xsrf;
    }
    
    @Override
    public boolean filter(final Method method, final RequestBuilder builder) {
        if (this.xsrf.getToken() !=null) {
            method.header(this.xsrf.getHeaderKey(), this.xsrf.getToken());
        }
        return true;// continue filtering
    }
}
