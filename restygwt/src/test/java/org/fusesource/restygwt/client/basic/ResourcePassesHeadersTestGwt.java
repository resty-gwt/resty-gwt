/**
 * Copyright (C) 2010 the original author or authors.
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

package org.fusesource.restygwt.client.basic;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.fusesource.restygwt.client.Resource;

import com.google.gwt.core.client.GWT;
import com.google.gwt.junit.client.GWTTestCase;

/**
 * Tests if the headers a {@link Resource} was generated with will be passed to 
 * the (while using immutable behavior) to the newly generated ones 
 * 
 * @author <a href="mailto:maerzbow@gmail.com">Markus Merzinger</<a>
 */
public class ResourcePassesHeadersTestGwt extends GWTTestCase {

	@Override
	public String getModuleName() {
		return "org.fusesource.restygwt.BasicTestGwt";
	}

	public void testAddQueryParam() {
		Map<String, String> headersMap = new HashMap<String, String>();
		headersMap.put("Authentication", "Basic 4711");
		headersMap.put("Accept", "application/json");

		Resource resource = new Resource(GWT.getModuleBaseURL()
				+ "api/getendpoint", headersMap);

		Resource resourceInTest = resource.addQueryParam("p1", "v1");

		assertMapsAreEqual(headersMap, resourceInTest.getHeaders());
	}
	
	public void testAddQueryParams() {
		Map<String, String> headersMap = new HashMap<String, String>();
		headersMap.put("Authentication", "Basic 4711");
		headersMap.put("Accept", "application/json");
		Resource resource = new Resource(GWT.getModuleBaseURL()
				+ "api/getendpoint", headersMap);

		List<String> paramValues = Arrays.asList(new String[] {"v1","v2"});
		Resource resourceInTest = resource.addQueryParams("p1", paramValues);

		assertMapsAreEqual(headersMap, resourceInTest.getHeaders());
	}


	private void assertMapsAreEqual(Map<String, String> map1,
			Map<String, String> map2) {
		assertEquals("Maps must have same size", map1.size(), map2.size());
		
		for (String key : map1.keySet()) {
			String v1 = map1.get(key);
			String v2 = map2.get(key);
			
			assertEquals("Maps must have the same entry for key: " + key, v1, v2);
		}
	}
}