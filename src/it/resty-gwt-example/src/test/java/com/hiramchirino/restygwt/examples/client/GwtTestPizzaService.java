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