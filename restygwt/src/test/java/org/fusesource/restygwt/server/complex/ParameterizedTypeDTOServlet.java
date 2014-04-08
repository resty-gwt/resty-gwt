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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.fusesource.restygwt.client.basic.ParameterizedTypeDTO.DTO;
import org.fusesource.restygwt.client.basic.ParameterizedTypeDTO.Thing;

@SuppressWarnings("serial")
public class ParameterizedTypeDTOServlet
{
	public static abstract class JacksonServlet extends HttpServlet
	{
		@Override
		protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
		{
			Object o = getThing();
			resp.setContentType("application/json");
			new ObjectMapper().writeValue(resp.getOutputStream(), o);
		}

		protected abstract Object getThing();
	}

	public static class IntServlet extends JacksonServlet
	{
		@Override
		protected DTO<Integer> getThing()
		{
			DTO<Integer> dto = new DTO<Integer>();
			dto.size = 12;
			dto.value = 123456;
			return dto;
		}

	}

	public static class ThingServlet extends JacksonServlet
	{
		@Override
		protected DTO<Thing> getThing()
		{
			DTO<Thing> dto = new DTO<Thing>();
			dto.size = 12;
			Thing thing = new Thing();
			thing.name = "Fred Flintstone";
			dto.value = thing;
			return dto;
		}
	}
}
