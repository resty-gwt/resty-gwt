/**
 * Copyright (C) 2009-2012 the original author or authors.
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

package org.fusesource.restygwt.client.complex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.RestService;

import com.google.gwt.core.client.GWT;
import com.google.gwt.junit.client.GWTTestCase;

public class ObjectEncoderDecoderTestGwt extends GWTTestCase
{
	@Override
	public String getModuleName()
	{
		return "org.fusesource.restygwt.ObjectEncoderDecoder";
	}

	public static interface Properties
	{
		@GET
		@Path("{name}")
		Object getProperty(@PathParam("name") String name);

		@PUT
		@Path("{name}")
		void setProperty(@PathParam("name") String name, Object value);
	}

	@Path("/properties")
	public static interface PropertiesAsync extends RestService
	{
		@GET
		@Path("{name}")
		void getProperty(@PathParam("name") String name, MethodCallback<Object> callback);

		@PUT
		@Path("{name}")
		void setProperty(@PathParam("name") String name, Object value, MethodCallback<Void> callback);
	}

	private void performTest(final String property, final Object expected)
	{
		PropertiesAsync properties = GWT.create(PropertiesAsync.class);
		properties.getProperty(property, new MethodCallback<Object>()
		{
			@Override
			public void onFailure(Method method, Throwable exception)
			{
				fail(exception.getMessage());
			}

			@Override
			public void onSuccess(Method method, Object response)
			{
				assertEquals(expected, response);
				finishTest();
			}
		});
		delayTestFinish(10000);
	}

	public void testNumber()
	{
		performTest("number", 123.0);
	}

	public void testString()
	{
		performTest("string", "Fred Fredstofferson");
	}

	public void testBoolean()
	{
		performTest("boolean", true);
	}

	public void testArray()
	{
		List<Object> list = new ArrayList<Object>();
		list.add(123.0);
		list.add(true);
		list.add("Fred Fredstofferson");
		performTest("array", list);
	}

	public void testMap()
	{
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("blah", 123.0);
		performTest("object", map);
	}
}
