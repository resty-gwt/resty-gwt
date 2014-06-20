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

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;
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
