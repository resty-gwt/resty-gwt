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

import com.google.gwt.http.client.Request;
import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.RestService;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

/**
 * Supersimple example service for testing...
 *
 * @author <a href="mailto:mail@raphaelbauer.com">rEyez</a>
 */
public interface ExampleService extends RestService {
    @GET
    void getExampleDto(MethodCallback<ExampleDto> callback);

    /**
     * Used to make sure the generator handles Request result. 
     * It can be used to cancel requests.
     */
    @GET
    Request getExampleDtoCancelable(MethodCallback<ExampleDto> callback);

    @POST
    @Path("/store")
    void storeDto(ExampleDto exampleDto, MethodCallback<Void> callback);
}
