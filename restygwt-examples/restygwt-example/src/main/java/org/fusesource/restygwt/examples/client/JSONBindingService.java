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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.RestService;

/**
 *
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 */
@Path("/test/JSONBinding")
public interface JSONBindingService extends RestService {

    @GET
    @Path("/getListOfStrings")
    void getListOfStrings(MethodCallback<List<String>> callback);

    class StringMapResponse {
        public Map<String, String> data = new HashMap<String, String>();

        @Override
        public int hashCode() {
            return data.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            StringMapResponse other = (StringMapResponse) obj;
            return data.equals(other.data);
        }

    }

    @GET
    @Path("/getStringMapResponse")
    void getStringMapResponse(MethodCallback<StringMapResponse> callback);

}
