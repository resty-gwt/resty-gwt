/**
 * Copyright (C) 2010, Progress Software Corporation and/or its 
 * subsidiaries or affiliates.  All rights reserved.
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
package org.fusesource.restygwt.examples.server;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.fusesource.restygwt.examples.client.MapResult;

import com.sun.jersey.spi.resource.Singleton;

@Path("/mapservice")
@Consumes( { MediaType.APPLICATION_JSON })
@Produces( { MediaType.APPLICATION_JSON })
@Singleton
public class MapService {

    @GET
    public MapResult get() {
        Map<String, String> map = new LinkedHashMap<String, String>();
        map.put("hello", "world");
        map.put("another", "value");
        return new MapResult(map);
    }

}