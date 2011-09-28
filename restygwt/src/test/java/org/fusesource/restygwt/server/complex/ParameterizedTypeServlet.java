package org.fusesource.restygwt.server.complex;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.map.ObjectMapper;
import org.fusesource.restygwt.client.basic.ParameterizedTypeServiceInterfaces;
import org.fusesource.restygwt.client.basic.ParameterizedTypeServiceInterfaces.Thing;

@SuppressWarnings("serial")
public class ParameterizedTypeServlet
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
		protected Integer getThing()
		{
			return 123456;
		}
		
	}
	
	public static class ThingServlet extends JacksonServlet
	{
		@Override
		protected ParameterizedTypeServiceInterfaces.Thing getThing()
		{
			Thing thing = new Thing();
			thing.name = "Fred Flintstone";
			thing.shoeSize = 12;
			return thing;
		}
	}
}
