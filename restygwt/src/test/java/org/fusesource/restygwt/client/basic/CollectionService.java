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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.RestService;

public interface CollectionService extends RestService {

    @GET  @Path("/save_names")
    void save(ArrayList<String>names, MethodCallback<Void> callback);

    @GET  @Path("/save_all_names")
    void saveAll(List<String>names, MethodCallback<Void> callback);

    @GET  @Path("/save_some")
    void saveSome(Set<String>names, MethodCallback<Void> callback);

    @GET  @Path("/save_some_names")
    void saveSome(TreeSet<String>names, MethodCallback<Void> callback);

    @GET  @Path("/save_mapping")
    void saveMapping(Map<String,String>namesToLogins, MethodCallback<Void> callback);

    @GET  @Path("/save_some_mapping")
    void saveSomeMapping(HashMap<String,String>namesToLogins, MethodCallback<Void> callback);
}
