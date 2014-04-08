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

package org.fusesource.restygwt.server.basic;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.fusesource.restygwt.client.basic.Echo;

/**
 *
 * just echos back the request path and the request parameters.
 *
 * @author mkristian
 * @author <a href="mailto:bogdan.mustiata@gmail.com">Bogdan Mustiata</<a>
 */
public class EchoTestGwtServlet extends HttpServlet {

    private static final long serialVersionUID = -746275386378921292L;

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response) throws IOException {
        doEchoRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request,
                         HttpServletResponse response) throws IOException {
        doEchoRequest(request, response);
    }

    @SuppressWarnings("unchecked")
    private void doEchoRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Echo echo = new Echo();
        echo.path = request.getPathInfo();

        echo.params = new HashMap<String, String>();
        ObjectMapper mapper = new ObjectMapper();
        for ( Map.Entry<String, String[]> entry:  (Set<Map.Entry<String, String[]>>) request.getParameterMap().entrySet() ){
            if (entry.getValue().length == 1) {
                echo.params.put(entry.getKey(), entry.getValue()[0]);
            } else {
                echo.params.put(entry.getKey(), mapper.writeValueAsString(entry.getValue()));
            }
        }
        response.setContentType("application/json");
        mapper.writeValue(response.getOutputStream(), echo);
    }

}