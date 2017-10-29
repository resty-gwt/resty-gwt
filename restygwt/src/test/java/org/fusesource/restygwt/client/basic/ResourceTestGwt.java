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

package org.fusesource.restygwt.client.basic;

import java.util.HashMap;
import java.util.Map;

import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.Resource;

import com.google.gwt.junit.client.GWTTestCase;

/**
 * Tests the construction of {@link Resource} as well as the expected behavior
 * of their functions
 *
 * @author <a href="mailto:maerzbow@gmail.com">Markus Merzinger</a>
 */
public class ResourceTestGwt extends GWTTestCase {

    // Base Uri
    private static final String BU = "http://localhost:8080/service";
    // Expected Query
    private static final String EQ = "a=1&b=2";

    @Override
    public String getModuleName() {
        return "org.fusesource.restygwt.BasicTestGwt";
    }

    // Simple Uri
    public void testUri() {
        Resource r1 = new Resource(BU);

        assertEquals(BU, r1.getPath());
        assertEquals(null, r1.getQuery());
        assertEquals(BU, r1.getUri());

        Resource r2 = r1.addQueryParam("a", "1").addQueryParam("b", "2");

        assertEquals(BU, r2.getPath());
        assertEquals(EQ, r2.getQuery());
        assertEquals(BU + "?" + EQ, r2.getUri());

        assertNull(r2.getHeaders());
    }

    public void testUri_Headers() {
        Map<String, String> headersMap = new HashMap<String, String>();
        headersMap.put("Authentication", "Basic 4711");
        headersMap.put("Accept", "application/json");

        Resource r1 = new Resource(BU, headersMap);

        assertEquals(BU, r1.getPath());
        assertEquals(null, r1.getQuery());
        assertEquals(BU, r1.getUri());

        Resource r2 = r1.addQueryParam("a", "1").addQueryParam("b", "2");

        assertEquals(BU, r2.getPath());
        assertEquals(EQ, r2.getQuery());
        assertEquals(BU + "?" + EQ, r2.getUri());

        assertEquals(2, r2.getHeaders().size());
    }

    public void testUri_Query() {
        Resource r1 = new Resource(BU, "x=y");

        assertEquals(BU, r1.getPath());
        assertEquals("x=y", r1.getQuery());
        assertEquals(BU + "?" + "x=y", r1.getUri());

        Resource r2 = r1.addQueryParam("a", "1").addQueryParam("b", "2");

        assertEquals(BU, r2.getPath());
        assertEquals("x=y" + "&" + EQ, r2.getQuery());
        assertEquals(BU + "?" + "x=y" + "&" + EQ, r2.getUri());

        assertNull(r2.getHeaders());
    }

    public void testUri_Query_Headers() {
        Map<String, String> headersMap = new HashMap<String, String>();
        headersMap.put("Authentication", "Basic 4711");
        headersMap.put("Accept", "application/json");

        Resource r1 = new Resource(BU, "x=y", headersMap);

        assertEquals(BU, r1.getPath());
        assertEquals("x=y", r1.getQuery());
        assertEquals(BU + "?" + "x=y", r1.getUri());

        Resource r2 = r1.addQueryParam("a", "1").addQueryParam("b", "2");

        assertEquals(BU, r2.getPath());
        assertEquals("x=y" + "&" + EQ, r2.getQuery());
        assertEquals(BU + "?" + "x=y" + "&" + EQ, r2.getUri());

        assertEquals(2, r2.getHeaders().size());
    }

    public void testPassOnOfHeaders() {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("X-CustomHeader", "123456789");
        Method method = new Resource("http://localhost", headers).get();
        assertEquals("123456789", method.builder.getHeader("X-CustomHeader"));
    }

    // Uris with trailing '/'
    public void testUriWithTrailingSlash() {
        Resource r1 = new Resource(BU + "/");

        assertEquals(BU, r1.getPath());
        assertEquals(null, r1.getQuery());
        assertEquals(BU, r1.getUri());

        Resource r2 = r1.addQueryParam("a", "1").addQueryParam("b", "2");

        assertEquals(BU, r2.getPath());
        assertEquals(EQ, r2.getQuery());
        assertEquals(BU + "?" + EQ, r2.getUri());

        assertNull(r2.getHeaders());
    }

    public void testUri_HeadersWithTrailingSlash() {
        Map<String, String> headersMap = new HashMap<String, String>();
        headersMap.put("Authentication", "Basic 4711");
        headersMap.put("Accept", "application/json");

        Resource r1 = new Resource(BU + "/", headersMap);

        assertEquals(BU, r1.getPath());
        assertEquals(null, r1.getQuery());
        assertEquals(BU, r1.getUri());

        Resource r2 = r1.addQueryParam("a", "1").addQueryParam("b", "2");

        assertEquals(BU, r2.getPath());
        assertEquals(EQ, r2.getQuery());
        assertEquals(BU + "?" + EQ, r2.getUri());

        assertEquals(2, r2.getHeaders().size());
    }

    public void testUri_QueryWithTrailingSlash() {
        Resource r1 = new Resource(BU + "/", "x=y");

        assertEquals(BU, r1.getPath());
        assertEquals("x=y", r1.getQuery());
        assertEquals(BU + "?" + "x=y", r1.getUri());

        Resource r2 = r1.addQueryParam("a", "1").addQueryParam("b", "2");

        assertEquals(BU, r2.getPath());
        assertEquals("x=y" + "&" + EQ, r2.getQuery());
        assertEquals(BU + "?" + "x=y" + "&" + EQ, r2.getUri());

        assertNull(r2.getHeaders());
    }

    public void testUri_Query_HeadersWithTrailingSlash() {
        Map<String, String> headersMap = new HashMap<String, String>();
        headersMap.put("Authentication", "Basic 4711");
        headersMap.put("Accept", "application/json");

        Resource r1 = new Resource(BU + "/", "x=y", headersMap);

        assertEquals(BU, r1.getPath());
        assertEquals("x=y", r1.getQuery());
        assertEquals(BU + "?" + "x=y", r1.getUri());

        Resource r2 = r1.addQueryParam("a", "1").addQueryParam("b", "2");

        assertEquals(BU, r2.getPath());
        assertEquals("x=y" + "&" + EQ, r2.getQuery());
        assertEquals(BU + "?" + "x=y" + "&" + EQ, r2.getUri());

        assertEquals(2, r2.getHeaders().size());
    }

    // Uri that includes Query
    public void testUriIncludingQuery() {
        Resource r1 = new Resource(BU + "?" + "x=y");

        assertEquals(BU, r1.getPath());
        assertEquals("x=y", r1.getQuery());
        assertEquals(BU + "?" + "x=y", r1.getUri());

        Resource r2 = r1.addQueryParam("a", "1").addQueryParam("b", "2");

        assertEquals(BU, r2.getPath());
        assertEquals("x=y" + "&" + EQ, r2.getQuery());
        assertEquals(BU + "?" + "x=y" + "&" + EQ, r2.getUri());

        assertNull(r2.getHeaders());
    }

    public void testUriIncludingQuery_Headers() {
        Map<String, String> headersMap = new HashMap<String, String>();
        headersMap.put("Authentication", "Basic 4711");
        headersMap.put("Accept", "application/json");

        Resource r1 = new Resource(BU + "?" + "x=y", headersMap);

        assertEquals(BU, r1.getPath());
        assertEquals("x=y", r1.getQuery());
        assertEquals(BU + "?" + "x=y", r1.getUri());

        Resource r2 = r1.addQueryParam("a", "1").addQueryParam("b", "2");

        assertEquals(BU, r2.getPath());
        assertEquals("x=y" + "&" + EQ, r2.getQuery());
        assertEquals(BU + "?" + "x=y" + "&" + EQ, r2.getUri());

        assertEquals(2, r2.getHeaders().size());
    }

}