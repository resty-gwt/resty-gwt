/**
 * Copyright (C) 2009-2015 the original author or authors.
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

package org.fusesource.restygwt.server.basic;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Simple servlet that answer jsonp test requests.
 *
 * @author Ralf Sommer {@literal <ralf.sommer.dev@gmail.com>}
 *
 */
public class JsonpTestGwtServlet extends HttpServlet {

    private static final long serialVersionUID = 4308269541594795760L;

    private static final String DUMMY_RESPONSE = "{\"name\":\"myName\"}";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/javascript");
        String responseFunction = request.getParameter("callback");
        if (null != responseFunction) {
            if (request.getRequestURI().endsWith("/list")) {
                response.getWriter()
                    .print(responseFunction + "([" + DUMMY_RESPONSE + ",{\"name\":\"myName2\"}" + "]);");
            } else {
                response.getWriter().print(responseFunction + "(" + DUMMY_RESPONSE + ");");
            }
        } else {
            responseFunction = request.getParameter("null");
            if (null != responseFunction) {
                response.getWriter().print(responseFunction + "();");
            }
        }
    }

}