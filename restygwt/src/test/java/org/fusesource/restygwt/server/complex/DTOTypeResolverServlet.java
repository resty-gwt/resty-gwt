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

package org.fusesource.restygwt.server.complex;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.fusesource.restygwt.client.complex.JsonTypeIdResolver.AbstractDTO;
import org.fusesource.restygwt.client.complex.JsonTypeIdResolver.DTO1;
import org.fusesource.restygwt.client.complex.JsonTypeIdResolver.DTO2;

import com.google.gwt.thirdparty.guava.common.collect.Lists;

public class DTOTypeResolverServlet extends HttpServlet
{
	private static final long serialVersionUID = 8761900300798640874L;

	/**
	 * Fake method to introspect to get generic type
	 * @return null
	 */
	public List<AbstractDTO> prototype()
	{
		return null;
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		DTO1 one = new DTO1();
		one.name = "Fred Flintstone";
		one.size = 1024;

		DTO2 two = new DTO2();
		two.name = "Barney Rubble";
		two.foo = "schmaltzy";

		DTO2 three = new DTO2();
		three.name = "BamBam Rubble";
		three.foo = "dorky";

		resp.setContentType("application/json");
		ObjectMapper om = new ObjectMapper();
		try
		{
			ObjectWriter writer = om.writer().withType(om.constructType(getClass().getMethod("prototype").getGenericReturnType()));
			writer.writeValue(resp.getOutputStream(), Lists.newArrayList(one, two, three));
		}
		catch (Exception e)
		{
			throw new ServletException(e);
		}
	}
}
