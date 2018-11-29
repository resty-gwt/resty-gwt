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

package org.fusesource.restygwt.client.event;

import com.google.gwt.json.client.JSONValue;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.ModelChange;
import org.fusesource.restygwt.client.RestService;
import org.fusesource.restygwt.client.cache.Domain;
import org.fusesource.restygwt.client.event.type.Foo;

/**
 * @author <a href="mailto:andi.balke@gmail.com">Andi</a>
 */
@Domain(Foo.class)
public interface ModelChangeAnnotatedService extends RestService {
    @GET
    @Path("/foo/")
    void getItems(@HeaderParam("X-Echo-Body") String responseBody, MethodCallback<JSONValue> callback);

    @PUT
    @Path("/foo/{fooId}")
    @ModelChange
    void setItem(@HeaderParam("X-Echo-Code") int responseCode, @PathParam("fooId") int fooId,
                 MethodCallback<Void> callback);
}
