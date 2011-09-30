/**
 * Copyright (C) 2009-2011 the original author or authors.
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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 */
public class JsonpServlet extends HttpServlet {

    private static final long serialVersionUID = -5364009274470240593L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String function = req.getParameter("callback");
        resp.setContentType("application/javascript");
        String data = "{\"first\":\"Hiram\",\"last\":\"Chirino\",\"city\":\"Tampa\"}";
        String jsonp = function + "(" + data + ")";
        System.out.println("Responding with: " + jsonp);
        resp.getWriter().println(jsonp);
    }

}
