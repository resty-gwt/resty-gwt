package com.hiramchirino.restygwt.examples.server;

import java.io.IOException;
import java.io.StringWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.map.ObjectMapper;

import com.hiramchirino.restygwt.client.Resource;
import com.hiramchirino.restygwt.examples.client.OrderConfirmation;
import com.hiramchirino.restygwt.examples.client.PizzaOrder;

public class PizzaServlet extends HttpServlet {

    private static final long serialVersionUID = -5364009274470240593L;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        
        System.out.println("Processing Pizza Order...");
        try {
            
            ObjectMapper mapper = new ObjectMapper();
            PizzaOrder order = mapper.readValue(req.getInputStream(), PizzaOrder.class);
            
            StringWriter sw = new StringWriter();
            mapper.writeValue(sw, order);
            System.out.println("Request: "+sw.toString());
            
            OrderConfirmation confirmation  = new OrderConfirmation();
            confirmation.order_id = 123123;
            confirmation.order = order;
            confirmation.price = 27.54;
            confirmation.ready_time = System.currentTimeMillis()+ 1000*60*30; // in 30 min.
            
            sw = new StringWriter();
            mapper.writeValue(sw, confirmation);
            System.out.println("Response: "+sw.toString());

            resp.setContentType(Resource.CONTENT_TYPE_JSON);
            mapper.writeValue(resp.getOutputStream(), confirmation);
            System.out.println("Pizza Order Confirimed: "+confirmation.order_id);
            
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            System.out.flush();
            System.err.flush();
        }
        
    }
    
}
