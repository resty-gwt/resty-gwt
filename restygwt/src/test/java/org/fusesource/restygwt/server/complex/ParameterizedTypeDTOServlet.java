package org.fusesource.restygwt.server.complex;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.map.ObjectMapper;
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
