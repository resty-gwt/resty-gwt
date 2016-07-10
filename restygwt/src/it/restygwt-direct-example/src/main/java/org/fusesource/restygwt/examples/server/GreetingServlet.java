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

package org.fusesource.restygwt.examples.server;

import java.io.IOException;
import java.io.StringWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.fusesource.restygwt.client.Resource;

/**
 * A simple server that serves JSON Data without constructing a Java Object.
 *
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 */
public class GreetingServlet extends HttpServlet {

    private static final long serialVersionUID = -5364009274470240594L;
    private static final String helloWorldJson = "{\"greeting\":\"Hello World\"}";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        System.out.println("Sending Hello World");
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode helloJsonNode = mapper.readTree(helloWorldJson);
            mapper.writeValue(resp.getOutputStream(), helloJsonNode);
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            System.out.flush();
            System.err.flush();
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        System.out.println("Creating custom greeting.");
        try {
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode nameObject = mapper.readValue(req.getInputStream(), ObjectNode.class);
            String name = nameObject.get("name").asText();

            String greeting = "Hello " + name;
            ObjectNode resultObject = new ObjectNode(JsonNodeFactory.instance);
            resultObject.put("greeting", greeting);
            mapper.writeValue(resp.getOutputStream(), resultObject);
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            System.out.flush();
            System.err.flush();
        }
    }
}
