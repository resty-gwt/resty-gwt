/**
 * 
 */
package org.fusesource.restygwt.examples.client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.fusesource.restygwt.client.JsonEncoderDecoder;
import org.fusesource.restygwt.examples.client.rails.DefaultRailsController;

import com.google.gwt.core.client.GWT;
import com.google.gwt.requestfactory.shared.Receiver;

//TODO should be created via GWT.create(PizzaController.class)
class PizzaControllerImpl extends DefaultRailsController<Pizza, PizzaControllerImpl.PizzaCodec> 
	implements PizzaController {

	static interface PizzaCodec extends JsonEncoderDecoder<Pizza>{}
	
	PizzaControllerImpl() {
		// the basepath is from the @Path of the interface
		super("pizzas", (PizzaControllerImpl.PizzaCodec) GWT.create(PizzaControllerImpl.PizzaCodec.class));
	}

	// GET with Receiver<List> can have offset + limit query paraneters
    public void index(Receiver<List<Pizza>> receiver, int offset, int limit){
    	Map<String,String> query = new HashMap<String,String>();
    	query.put("limit", limit + "");
    	query.put("offset", offset + "");
    	get(path, receiver, query);
    }

}