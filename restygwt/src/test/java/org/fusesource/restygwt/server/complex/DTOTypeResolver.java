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

import org.fusesource.restygwt.client.complex.JsonTypeIdResolver.DTO1;
import org.fusesource.restygwt.client.complex.JsonTypeIdResolver.DTO2;
import org.fusesource.restygwt.client.complex.JsonTypeIdResolver.DTOCustom1;
import org.fusesource.restygwt.client.complex.JsonTypeIdResolver.DTOCustom2;

import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.databind.DatabindContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;
import com.fasterxml.jackson.databind.type.SimpleType;

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
		else if(value instanceof DTOCustom1)
			return "dtocustom1";
		else if (value instanceof DTOCustom2)
			return "dtocustom2";
		else
			throw new IllegalArgumentException("Unknown type: " + value);
	}
	
	@Override
	public String idFromBaseType()
	{
		throw new AssertionError();
	}

	@Override
	public JavaType typeFromId(String id)
	{
		if("dto1".equals(id))
			return SimpleType.construct(DTO1.class);
		else if("dto2".equals(id))
			return SimpleType.construct(DTO2.class);
		else if("dtocustom1".equals(id))
			return SimpleType.construct(DTOCustom1.class);
		else if("dtocustom2".equals(id))
			return SimpleType.construct(DTOCustom2.class);
		else
			throw new IllegalArgumentException("Unknown id: " + id);
	}

	@Override
	public Id getMechanism()
	{
		return Id.NAME;
	}

    @Override
    public JavaType typeFromId(DatabindContext context, String id) {
        return typeFromId(id);
    }
}
