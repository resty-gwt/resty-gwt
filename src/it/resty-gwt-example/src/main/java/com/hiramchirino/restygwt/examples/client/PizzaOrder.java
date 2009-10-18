package com.hiramchirino.restygwt.examples.client;

import java.util.ArrayList;
import java.util.List;



public class PizzaOrder {
    
    public String phone_number;
    
    public boolean delivery;
    public List<String> delivery_address = new ArrayList<String>(4);
    
    public List<Pizza> pizzas = new ArrayList<Pizza>(10);

}
