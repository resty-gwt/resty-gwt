package org.fusesource.restygwt.client.codec;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.RestService;

import com.google.gwt.core.client.GWT;
import com.google.gwt.junit.client.GWTTestCase;

public class InnerClassesEncoderDecoderTestGwt extends GWTTestCase {

    @Override
    public String getModuleName() {
        return "org.fusesource.restygwt.EncoderDecoderTestGwt";
    }
    
    static class Drive {
       
        public static class Settings {
            
            public static class ControlPath {
                public String path;
            }
            
            public Drive.Settings.ControlPath controlPath;
        }
    }
    static class PhysicalLibrary {
       
        public static class Settings {
            
            public static class DriveSerialNumber {
                public int serialNumber;
            }
            
            public PhysicalLibrary.Settings.DriveSerialNumber controlPath;
        }
    }
    
    static interface InnerClassesRestService extends RestService {
        
        @GET
        @Path("/")
        void settings( PhysicalLibrary.Settings settings, MethodCallback<Drive.Settings> callback );
        
    }
    
    public void test(){
        InnerClassesRestService service = GWT.create(InnerClassesRestService.class);
        // just ensure they compile
        assertNotNull( service );
    }
}