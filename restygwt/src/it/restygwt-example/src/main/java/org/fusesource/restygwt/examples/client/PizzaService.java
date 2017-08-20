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

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.RestService;

import java.util.List;
import java.util.Map;

/**
 * 
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 */
@Path("/pizza-service")
public interface PizzaService extends RestService {

    @POST
    public void order(PizzaOrder request, MethodCallback<OrderConfirmation> callback);

    @GET
    @Path("/toppings")
    @Produces( MediaType.APPLICATION_JSON )
    public void listToppings(MethodCallback<List<Topping>> callback);

    @OPTIONS
    @Path("/crusts")
    @Produces( MediaType.APPLICATION_JSON )
    public void getCurstPrices(Crust crust, MethodCallback<Map<Integer, Double>> callback);

    @DELETE
    @Path("/ping")
    public void ping(MethodCallback<java.lang.Void> callback);
}
