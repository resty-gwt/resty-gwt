package org.fusesource.restygwt.server.complex;

import java.util.HashMap;
import java.util.Map;

import org.fusesource.restygwt.client.complex.JsonTypeIdResolver.DTO1;
import org.fusesource.restygwt.client.complex.JsonTypeIdResolver.DTO2;
import org.fusesource.restygwt.rebind.JsonTypeResolverClassCreator;

public class DTOTypeResolverGenerator extends JsonTypeResolverClassCreator
{
	@Override
	protected Map<String, Class<?>> getIdClassMap()
	{
		Map<String, Class<?>> map = new HashMap<String, Class<?>>();
		map.put("dto1", DTO1.class);
		map.put("dto2", DTO2.class);
		return map;
	}
}
