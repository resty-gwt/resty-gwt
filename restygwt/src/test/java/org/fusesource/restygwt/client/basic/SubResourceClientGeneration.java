package org.fusesource.restygwt.client.basic;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.fusesource.restygwt.client.Resource;
import org.fusesource.restygwt.client.RestService;
import org.fusesource.restygwt.client.RestServiceProxy;
import org.fusesource.restygwt.client.TextCallback;

import com.google.gwt.core.client.GWT;
import com.google.gwt.junit.client.GWTTestCase;

public class SubResourceClientGeneration extends GWTTestCase
{
	public static interface TestInterfaceA extends RestService
	{
		@GET
		@Path("/foo")
		public void getFoo(TextCallback cb);

		@Path("interfaceB/{id}")
		public TestInterfaceB getInterfaceB(@PathParam("id") String nameOfThing);
	}

	public static interface TestInterfaceB extends RestService
	{
		@GET
		@Path("/bar")
		public void getBar(TextCallback cb);
	}

	@Override
	public String getModuleName()
	{
		return "org.fusesource.restygwt.BasicTestGwt";
	}

	public void testSimpleGeneration()
	{
		TestInterfaceA a = GWT.create(TestInterfaceA.class);
		((RestServiceProxy)a).setResource(new Resource("/path/to/interfaceA"));
		TestInterfaceB b = a.getInterfaceB("fred");
		assertEquals("/path/to/interfaceA/interfaceB/fred", ((RestServiceProxy)b).getResource().getPath());
	}
}
