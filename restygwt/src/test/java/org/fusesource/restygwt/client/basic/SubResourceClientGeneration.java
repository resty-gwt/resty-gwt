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
