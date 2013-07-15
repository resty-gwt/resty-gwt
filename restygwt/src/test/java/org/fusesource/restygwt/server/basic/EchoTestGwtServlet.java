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

import org.codehaus.jackson.map.ObjectMapper;
import org.fusesource.restygwt.client.basic.Echo;

/**
 *
 * just echos back the request path and the request parameters.
 *
 * @author mkristian
 *
 */
public class EchoTestGwtServlet extends HttpServlet {

    private static final long serialVersionUID = -746275386378921292L;

    @SuppressWarnings("unchecked")
    @Override
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response) throws IOException {

        Echo echo = new Echo();
        echo.path = request.getPathInfo();
 
        echo.params = new HashMap<String, String>();
        for ( Map.Entry<String, String[]> entry:  (Set<Map.Entry<String, String[]>>) request.getParameterMap().entrySet() ){
            echo.params.put(entry.getKey(), entry.getValue()[0]);
        }
        response.setContentType("application/json");
        new ObjectMapper().writeValue(response.getOutputStream(), echo);
    }

}