/**
 * 
 */
package org.fusesource.restygwt.examples.client;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.fusesource.restygwt.examples.client.rails.RailsController;

import com.google.gwt.requestfactory.shared.Receiver;

@Path("/pizzas")
interface PizzaController extends RailsController {

	//default @GET
    void index(Receiver<List<Pizza>> receiver);

    @GET
    void index(Receiver<List<Pizza>> receiver, int offset, int limit);

	//default @GET
    void show(int id, Receiver<Pizza> receiver);

	//default @POST
    void create(Pizza p, Receiver<Pizza> receiver);
    
	//default @PUT
    void update(int id, Pizza p, Receiver<Pizza> receiver);

	//default @DELETE
    void destroy(int id, Receiver<Pizza> receiver);
}