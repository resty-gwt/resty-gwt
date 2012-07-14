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

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.RestService;
import org.fusesource.restygwt.client.basic.ExampleDto;
import org.fusesource.restygwt.client.cache.Domain;

/**
 * Supersimple example service for testing...
 */
@Domain({ExampleDto.class})
public interface CORSServiceMethodLevelOnly extends RestService {

    @GET
    @CORS(domain = "api.host.com", protocol = "https")
    @Path("/my/very/unique/path")
    void getExampleDto(MethodCallback<Void> callback);

    @POST
    @CORS(domain = "api2.host.com", protocol = "spdy")
    @Path("/my/very/unique/path")
    // path should be overridden
    // different domains may be useful for Command Query Responsibility Segregation (CQRS)
    void postExample(MethodCallback<Void> callback);

}
