/**
 * 
 */
package org.fusesource.restygwt.examples.client.rails;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.fusesource.restygwt.client.JsonCallback;
import org.fusesource.restygwt.client.JsonEncoderDecoder;
import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.Resource;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.requestfactory.shared.Receiver;
import com.google.gwt.requestfactory.shared.ServerFailure;

public class BaseRailsController<T, S extends JsonEncoderDecoder<T>> implements RailsController{

    protected final String path;
    
    protected final S codec;
    
    BaseRailsController(String path, S codec){
    	this.path = ("/" + path + "/").replace("//", "/");
    	this.codec = codec;
    }

    public String getPath(){
    	return this.path;
    }
    
    public S getCodec(){
    	return this.codec;
    }
    
    protected String path(int id){
    	return this.path + "/" + id;
    }

    protected void get(String path, final Receiver<List<T>> receiver, Map<String, String> query){
    	Resource resource = new Resource(path);
    	for(Map.Entry<String, String> entry: query.entrySet()){
    		resource.addQueryParam(entry.getKey(), entry.getValue());
    	}
    	Method method = resource.get();
		method.send(new JsonCallback() {
			
			@Override
			public void onSuccess(Method method, JSONValue response) {
				JSONArray list = response.isArray();
				List<T> result = new ArrayList<T>(list.size());
				for(int i = 0; i < list.size(); i++){
					result.add(codec.decode(list.get(i)));
				}
				receiver.onSuccess(result);	
			}
			
			@Override
			public void onFailure(Method method, Throwable exception) {
				receiver.onFailure(new ServerFailure(exception.getMessage()));
			}
		});
    }

    protected void get(String path, Receiver<T> receiver){
    	Resource resource = new Resource(path);
    	Method method = resource.get();
    	send(receiver, method);
    }

    protected void post(String path, T value, final Receiver<T> receiver){
    	Resource resource = new Resource(path);
    	Method method = resource.post();
    	send(value, receiver, method);
    }

	private void send(T value, final Receiver<T> receiver, Method method) {
		method = method.json(codec.encode(value));
		send(receiver, method);
	}

	private void send(final Receiver<T> receiver, Method method) {
		method.send(new JsonCallback() {
			
			@Override
			public void onSuccess(Method method, JSONValue response) {
				receiver.onSuccess(codec.decode(response));
			}
			
			@Override
			public void onFailure(Method method, Throwable exception) {
				receiver.onFailure(new ServerFailure(exception.getMessage()));
			}
		});
	}
    
	protected void put(String path, T value, Receiver<T> receiver){
    	Resource resource = new Resource(path);
    	Method method = resource.put();
    	send(value, receiver, method);
    }
    
	protected void delete(String path, Receiver<T> receiver){
    	Resource resource = new Resource(path);
    	Method method = resource.put();
    	send(receiver, method);
    }
}