package com.hiramchirino.restygwt.examples.client;

import javax.ws.rs.POST;
import javax.ws.rs.Path;

import com.hiramchirino.restygwt.client.MethodCallback;
import com.hiramchirino.restygwt.client.RestService;

@Path("/pizza-service")
public interface PizzaService extends RestService {
    
    @POST
    public void order(PizzaOrder request, MethodCallback<OrderConfirmation> callback);
    

}
