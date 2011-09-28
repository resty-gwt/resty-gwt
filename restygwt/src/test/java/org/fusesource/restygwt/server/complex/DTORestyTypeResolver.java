package org.fusesource.restygwt.server.complex;

import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.map.jsontype.TypeIdResolver;
import org.fusesource.restygwt.client.complex.JsonTypeIdResolver.DTO1;
import org.fusesource.restygwt.client.complex.JsonTypeIdResolver.DTO2;
import org.fusesource.restygwt.rebind.RestyJsonTypeIdResolver;

public class DTORestyTypeResolver implements RestyJsonTypeIdResolver {
    @Override
    public Map<String, Class<?>> getIdClassMap() {
	Map<String, Class<?>> map = new HashMap<String, Class<?>>();
	map.put("dto1", DTO1.class);
	map.put("dto2", DTO2.class);
	return map;
    }

    @Override
    public Class<? extends TypeIdResolver> getTypeIdResolverClass() {
	return DTOTypeResolver.class;
    }
}
