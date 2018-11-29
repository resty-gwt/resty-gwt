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

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.fusesource.restygwt.client.complex.JsonTypeIdResolver.DTOImplementation;

public class DTOImplementationServlet extends HttpServlet {
    private static final long serialVersionUID = 8761900300798640874L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        DTOImplementation impl = new DTOImplementation();
        impl.setName("implementation");

        resp.setContentType("application/json");
        ObjectMapper om = new ObjectMapper();
        try {
            om.writeValue(resp.getOutputStream(), impl);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}
