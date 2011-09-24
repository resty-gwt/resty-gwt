package org.fusesource.restygwt.client.basic;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.RestService;
import org.junit.Test;

import com.google.gwt.core.client.GWT;
import com.google.gwt.junit.client.GWTTestCase;

public class ParameterizedTypeServiceInterfaces extends GWTTestCase
{
	@Override
	public String getModuleName()
	{
		return "org.fusesource.restygwt.ParameterizedTypeServiceInterface";
	}

	public static class Thing
	{
		public String name;
		public int shoeSize;
	}

	public static interface A<X> extends RestService
	{
		@GET
		@Produces("application/json")
		void getSomething(MethodCallback<X> callback);
	}

	@Path("/api/ptype/int")
	public static interface AOfInt extends A<Integer>
	{
	}

	@Path("/api/ptype/thing")
	public static interface AOfThing extends A<Thing>
	{
	}

	@Path("/api/ptype")
	public static interface SuperOfThing extends RestService
	{
		@Path("thing")
		public A<Thing> getThingInterface();

		@Path("int")
		public A<Integer> getIntInterface();
	}

	@Test
	public void testSimpleType()
	{
		AOfInt inter = GWT.create(AOfInt.class);
		inter.getSomething(new MethodCallback<Integer>()
		{
			@Override
			public void onFailure(Method method, Throwable exception)
			{
				fail(exception.getMessage());
			}

			@Override
			public void onSuccess(Method method, Integer response)
			{
				assertEquals(Integer.valueOf(123456), response);
				finishTest();
			}
		});
		delayTestFinish(10000);
	}

	@Test
	public void testComplexType()
	{
		AOfThing inter = GWT.create(AOfThing.class);
		inter.getSomething(new MethodCallback<Thing>()
		{
			@Override
			public void onFailure(Method method, Throwable exception)
			{
				fail(exception.getMessage());
			}

			@Override
			public void onSuccess(Method method, Thing value)
			{
				assertEquals("Fred Flintstone", value.name);
				assertEquals(12, value.shoeSize);
				finishTest();
			}
		});
		delayTestFinish(10000);
	}

	@Test
	public void testSubResource()
	{
		SuperOfThing inter = GWT.create(SuperOfThing.class);
		inter.getThingInterface().getSomething(new MethodCallback<Thing>()
		{
			@Override
			public void onFailure(Method method, Throwable exception)
			{
				fail(exception.getMessage());
			}

			@Override
			public void onSuccess(Method method, Thing value)
			{
				assertEquals("Fred Flintstone", value.name);
				assertEquals(12, value.shoeSize);
				finishTest();
			}
		});
		delayTestFinish(10000);
	}

	@Test
	public void testSubResource2()
	{
		SuperOfThing inter = GWT.create(SuperOfThing.class);
		inter.getIntInterface().getSomething(new MethodCallback<Integer>()
		{
			@Override
			public void onFailure(Method method, Throwable exception)
			{
				fail(exception.getMessage());
			}

			@Override
			public void onSuccess(Method method, Integer value)
			{
				assertEquals(Integer.valueOf(123456), value);
				finishTest();
			}
		});
		delayTestFinish(10000);
	}
}
