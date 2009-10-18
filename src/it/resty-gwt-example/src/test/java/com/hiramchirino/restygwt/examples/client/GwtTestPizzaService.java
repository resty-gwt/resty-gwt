/**
 * Copyright (C) 2009  Hiram Chirino
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *    http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hiramchirino.restygwt.examples.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.junit.client.GWTTestCase;
import com.hiramchirino.restygwt.client.Method;
import com.hiramchirino.restygwt.client.MethodCallback;
import com.hiramchirino.restygwt.client.Resource;
import com.hiramchirino.restygwt.client.RestServiceProxy;
import com.hiramchirino.restygwt.examples.client.OrderConfirmation;
import com.hiramchirino.restygwt.examples.client.PizzaOrder;
import com.hiramchirino.restygwt.examples.client.PizzaService;

/**
 * 
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 */
public class GwtTestPizzaService extends GWTTestCase {

    public String getModuleName() {
        return "com.hiramchirino.restygwt.examples.UI";
    }

    public void testSomething() {
        
        // Initialize the pizza service..
        PizzaService service = GWT.create(PizzaService.class);
        Resource resource = new Resource( GWT.getModuleBaseURL() + "pizza-service");
        ((RestServiceProxy)service).setResource(resource);
        
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
                
                assertEquals( 123123, response.order_id);
                assertNotNull( response.order );
                
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