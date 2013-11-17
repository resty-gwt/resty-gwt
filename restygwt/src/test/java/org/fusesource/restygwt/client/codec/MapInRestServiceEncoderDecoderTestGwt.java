package org.fusesource.restygwt.client.codec;

import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.RestService;

import com.google.gwt.core.client.GWT;
import com.google.gwt.junit.client.GWTTestCase;

public class MapInRestServiceEncoderDecoderTestGwt extends GWTTestCase {

    @Override
    public String getModuleName() {
        return "org.fusesource.restygwt.EncoderDecoderTestGwt";
    }
    static enum Key { MESSAGE } 
    
    static interface MapWithEnumKeyRestService extends RestService {
        
        @GET
        @Path("/")
        void messages( Map<Key, String> map, MethodCallback<Void> callback );
        
        @GET
        @Path("/api/testMap")
        public void testMap(MethodCallback<Map<Integer, List<String>>> callback);
    }
    
    public void test(){
        MapWithEnumKeyRestService service = GWT.create(MapWithEnumKeyRestService.class);
        // just ensure they compile
        assertNotNull( service );
    }
}