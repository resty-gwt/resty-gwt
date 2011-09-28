package org.fusesource.restygwt.server.complex;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.codehaus.jackson.map.type.TypeFactory;
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
			ObjectWriter writer = om.writer().withType(TypeFactory.type(getClass().getMethod("prototype").getGenericReturnType()));
			writer.writeValue(resp.getOutputStream(), Lists.newArrayList(one, two, three));
		}
		catch (Exception e)
		{
			throw new ServletException(e);
		}
	}
}
