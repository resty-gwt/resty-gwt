/**
 * 
 */
package org.fusesource.restygwt.examples.client.rails;

import java.util.HashMap;
import java.util.List;

import org.fusesource.restygwt.client.JsonEncoderDecoder;

import com.google.gwt.requestfactory.shared.Receiver;

public class DefaultRailsController<T, S extends JsonEncoderDecoder<T>> extends BaseRailsController<T, S>{

    protected DefaultRailsController(String path, S codec){
    	super(path, codec);
    }

    public void index(Receiver<List<T>> receiver){
    	get(path, receiver, new HashMap<String,String>());
    }

    public void show(int id, Receiver<T> receiver){
    	get(path(id), receiver);
    }

    public void create(T value, Receiver<T> receiver){
    	post(path, value, receiver);
    }
    
    public void update(int id, T value, Receiver<T> receiver){
    	put(path(id), value, receiver);
    }

    public void destroy(int id,  Receiver<T> receiver){
    	delete(path(id), receiver);
    }
 
}