package org.fusesource.restygwt.examples.server;

import javax.ws.rs.ext.Provider;
import javax.xml.bind.JAXBContext;

import org.fusesource.restygwt.examples.client.MapResult;

import com.sun.jersey.api.json.JSONConfiguration;
import com.sun.jersey.api.json.JSONJAXBContext;

@Provider
public class JsonJaxbProvider {
    
    private JAXBContext context;
    private Class<?>[] types = { MapResult.class };

    public JsonJaxbProvider() throws Exception {
        JSONConfiguration config = JSONConfiguration.natural().build();
        context = new JSONJAXBContext(config, types);
    } 

    public JAXBContext getContext(Class<?> objectType) {
        for (Class<?> type : types) {
            if (type == objectType) {
                return context;
            }
        }
        return null;
    }

}
