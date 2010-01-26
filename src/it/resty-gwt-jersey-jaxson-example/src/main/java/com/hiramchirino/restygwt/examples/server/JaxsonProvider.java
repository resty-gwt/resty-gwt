package com.hiramchirino.restygwt.examples.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;

import org.codehaus.jackson.map.ObjectMapper;

import com.sun.jersey.core.provider.AbstractMessageReaderWriterProvider;

@Provider
public class JaxsonProvider<T> extends AbstractMessageReaderWriterProvider<T> {
    
    @Produces("application/json")
    @Consumes("application/json")
    public static final class App<T> extends JaxsonProvider<T> {
    }
    
    @Produces("*/*")
    @Consumes("*/*")
    public static final class General<T> extends JaxsonProvider<T> {
        @Override
        protected boolean isSupported(MediaType m) {
            return m.getSubtype().endsWith("+json");
        }
    }
    
    final ObjectMapper mapper = new ObjectMapper();

    protected boolean isSupported(MediaType m) {
        return true;
    }

    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return isSupported(mediaType);
    }

    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return isSupported(mediaType);
    }

    public T readFrom(Class<T> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> headers, InputStream is) throws IOException, WebApplicationException {
        return mapper.readValue(is, type);
    }

    public void writeTo(T value, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> headers, OutputStream os) throws IOException, WebApplicationException {
        mapper.writeValue(os, value);
    }

}
