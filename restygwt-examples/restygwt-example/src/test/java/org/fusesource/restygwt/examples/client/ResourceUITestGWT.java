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

import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONValue;

import org.fusesource.restygwt.client.JsonCallback;
import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.Resource;
import org.fusesource.restygwt.examples.client.PizzaOrder.PizzaOrderJED;

/**
 *
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 */
public class ResourceUITestGWT extends UITestGWT {

    @Override
    public String getModuleName() {
        return "org.fusesource.restygwt.examples.UI";
    }

    public void testJsonResource() {

        // Initialize the pizza service..
        Resource resource = new Resource(GWT.getModuleBaseURL() + "pizza-service");

        JSONValue request = createRequestObject();

        resource.post().json(request).send(new JsonCallback() {
            @Override
            public void onSuccess(Method method, JSONValue response) {
                assertNotNull(response);
                System.out.println(response);
                finishTest();
            }

            @Override
            public void onFailure(Method method, Throwable exception) {
                exception.printStackTrace();
                fail(exception.getMessage());
            }
        });

        delayTestFinish(2000);
    }

    private JSONValue createRequestObject() {
        PizzaOrder order = new PizzaOrder();
        order.delivery = true;
        order.delivery_address.add("3434 Pinerun Ave.");
        order.delivery_address.add("Tampa, FL  33734");

        Pizza pizza = new Pizza();
        pizza.crust = "thin";
        pizza.quantity = 1;
        pizza.size = 16;
        pizza.toppings.add("ham");
        pizza.toppings.add("pineapple");
        order.pizzas.add(pizza);

        PizzaOrderJED jed = GWT.create(PizzaOrderJED.class);
        return jed.encode(order);
    }
}