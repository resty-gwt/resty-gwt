package org.fusesource.restygwt.client.basic;

import org.fusesource.restygwt.client.Resource;
import org.fusesource.restygwt.client.RestService;
import org.fusesource.restygwt.client.RestServiceProxy;
import org.junit.Test;

import com.google.gwt.core.client.GWT;
import com.google.gwt.junit.client.GWTTestCase;

public class CastingServiceInterface extends GWTTestCase {

    @Override
    public String getModuleName() {
	return "org.fusesource.restygwt.BasicTestGwt";
    }
    
    public static interface Int1 extends RestService
    {
	public <T extends Int1> T as(Class<T> iface);
    }
    
    public static interface Int2 extends Int1
    {
    }

    @Test
    public void testCastingServiceInterface() {
	Int1 int1 = GWT.create(Int1.class);
	Resource resource = new Resource("http://localhost/foo");
	((RestServiceProxy) int1).setResource(resource);
	
	Int2 int2 = int1.as(Int2.class);
	assertEquals(resource, ((RestServiceProxy)int2).getResource());
    }
}
