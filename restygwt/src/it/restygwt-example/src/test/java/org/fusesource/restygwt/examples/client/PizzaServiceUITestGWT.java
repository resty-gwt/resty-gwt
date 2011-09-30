/**
 * Copyright (C) 2009-2011 the original author or authors.
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

import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;

import com.google.gwt.core.client.GWT;

import java.util.List;

/**
 * 
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 */
public class PizzaServiceUITestGWT extends UITestGWT {

        public void testListToppings() {

        // Initialize the pizza service..
        PizzaService service = GWT.create(PizzaService.class);

        service.listToppings(new MethodCallback<List<Topping>>(){
            public void onSuccess(Method method, List<Topping> response) {
                System.out.println(response);
                assertNotNull(response);
                assertEquals(3, response.size());
                finishTest();
            }
            public void onFailure(Method method, Throwable exception) {
                exception.printStackTrace();
                fail(exception.getMessage());
            }

        });
        delayTestFinish(2000);
    }

    public void testOrder() {

        // Initialize the pizza service..
        PizzaService service = GWT.create(PizzaService.class);

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

        pizza = new Pizza();
        pizza.crust = "thin";
        pizza.quantity = 1;
        pizza.size = 16;
        pizza.toppings.add("extra cheese");
        order.pizzas.add(pizza);

        service.order(order, new MethodCallback<OrderConfirmation>() {
            public void onSuccess(Method method, OrderConfirmation response) {
                System.out.println(response);
                assertNotNull(response);

                assertEquals(123123, response.order_id);
                assertNotNull(response.order);

                finishTest();
            }

            public void onFailure(Method method, Throwable exception) {
                exception.printStackTrace();
                fail(exception.getMessage());
            }
        });

        delayTestFinish(2000);
    }

    public void testPing() {

        // Initialize the pizza service..
        PizzaService service = GWT.create(PizzaService.class);

        service.ping(new MethodCallback<java.lang.Void>() {
            public void onSuccess(Method method, java.lang.Void response) {
                System.out.println(response);
                assertNull(response);

                finishTest();
            }

            public void onFailure(Method method, Throwable exception) {
                exception.printStackTrace();
                fail(exception.getMessage());
            }
        });

        delayTestFinish(2000);
    }
}