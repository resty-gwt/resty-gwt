package org.fusesource.restygwt.client.basic;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.RestService;

import com.google.gwt.core.client.GWT;
import com.google.gwt.junit.client.GWTTestCase;

public class GenericsTestGwt extends GWTTestCase
{
	@Override
	public String getModuleName()
	{
		return "org.fusesource.restygwt.BasicTestGwt";
	}

	public static class GenericDTO<T extends Number>
	{
		private String foo;

		public String getFoo()
		{
			return foo;
		}

		public void setFoo(String foo)
		{
			this.foo = foo;
		}
	}

	public static interface GenericAsync<T extends Number> extends RestService
	{
		@GET
		@Path("/foo")
		@Produces("application/json")
		void getThingie(MethodCallback<GenericDTO<T>> callback);
	}

	public void testGenericResource()
	{
		GWT.create(GenericAsync.class);
	}
}
