package org.fusesource.restygwt.server.complex;

import org.codehaus.jackson.annotate.JsonTypeInfo.Id;
import org.codehaus.jackson.map.jsontype.TypeIdResolver;
import org.codehaus.jackson.map.type.SimpleType;
import org.codehaus.jackson.type.JavaType;
import org.fusesource.restygwt.client.complex.JsonTypeIdResolver.DTO1;
import org.fusesource.restygwt.client.complex.JsonTypeIdResolver.DTO2;

public class DTOTypeResolver implements TypeIdResolver
{
	@Override
	public void init(JavaType baseType)
	{
	}

	@Override
	public String idFromValueAndType(Object value, Class<?> suggestedType)
	{
		return idFromValue(value);
	}

	@Override
	public String idFromValue(Object value)
	{
		if(value instanceof DTO1)
			return "dto1";
		else if (value instanceof DTO2)
			return "dto2";
		else
			throw new IllegalArgumentException("Unknown type: " + value);
	}

	@Override
	public JavaType typeFromId(String id)
	{
		if("dto1".equals(id))
			return SimpleType.construct(DTO1.class);
		else if("dto2".equals(id))
			return SimpleType.construct(DTO2.class);
		else
			throw new IllegalArgumentException("Unknown id: " + id);
	}

	@Override
	public Id getMechanism()
	{
		return Id.NAME;
	}
}
