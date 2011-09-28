package org.fusesource.restygwt.rebind;

import java.util.Map;

import org.codehaus.jackson.map.jsontype.TypeIdResolver;

public interface RestyJsonTypeIdResolver {
    public Class<? extends TypeIdResolver> getTypeIdResolverClass();
    public Map<String, Class<?>> getIdClassMap();
}
