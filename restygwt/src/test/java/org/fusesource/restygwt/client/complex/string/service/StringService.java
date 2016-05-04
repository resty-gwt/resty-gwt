/**
 * Copyright (C) 2009-2016 the original author or authors.
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

package org.fusesource.restygwt.client.complex.string.service;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/strings")
public interface StringService {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    String getAsJson();

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    String getAsPlainText();

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    void setAsJson(String text);

    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    void setAsPlainText(String text);

}