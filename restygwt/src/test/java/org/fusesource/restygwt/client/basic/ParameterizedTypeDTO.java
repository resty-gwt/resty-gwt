/**
 * Copyright (C) 2009-2011 the original author or authors.
 * See the notice.md file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

public class ParameterizedTypeDTO extends GWTTestCase
{
	public static class DTO<X>
	{
		public int size;
		public X value;
	}

	public static class Thing
	{
		public String name;
	}

	@Path("/api/pdto")
	public static interface DTOService extends RestService
	{
		@GET
		@Produces("application/json")
		@Path("thing")
		void getThing(MethodCallback<DTO<Thing>> callback);

		@GET
		@Produces("application/json")
		@Path("int")
		void getInteger(MethodCallback<DTO<Integer>> callback);
	}

	@Override
	public String getModuleName()
	{
		return "org.fusesource.restygwt.ParameterizedTypeDTO";
	}

	@Test
	public void testThing()
	{
		DTOService service = GWT.create(DTOService.class);
		service.getThing(new MethodCallback<DTO<Thing>>()
		{
			@Override
			public void onFailure(Method method, Throwable exception)
			{
				fail(exception.getMessage());
			}

			@Override
			public void onSuccess(Method method, DTO<Thing> response)
			{
				assertEquals(12, response.size);
				assertEquals("Fred Flintstone", response.value.name);
				finishTest();
			}
		});
		delayTestFinish(10000);
	}

	@Test
	public void testInt()
	{
		DTOService service = GWT.create(DTOService.class);
		service.getInteger(new MethodCallback<DTO<Integer>>()
		{
			@Override
			public void onFailure(Method method, Throwable exception)
			{
				fail(exception.getMessage());
			}

			@Override
			public void onSuccess(Method method, DTO<Integer> response)
			{
				assertEquals(12, response.size);
				assertEquals(Integer.valueOf(123456), response.value);
				finishTest();
			}
		});
		delayTestFinish(10000);
	}
}
