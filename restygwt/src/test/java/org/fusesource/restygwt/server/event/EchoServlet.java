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

package org.fusesource.restygwt.server.event;

import java.io.IOException;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * servlet to reflect the incoming request
 *
 * @author <a href="mailto:andi.balke@gmail.com">andi</<a>
 */
public class EchoServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger log = Logger.getLogger(EchoServlet.class.getName());
    private final String CONTENT_TYPE = "application/json";

    private final String RESPONSE_CODE_HEADER_NAME = "X-Echo-Code";
    private final String RESPONSE_BODY_HEADER_NAME = "X-Echo-Body";

    @Override
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        if (log.isLoggable(Level.INFO)) {
            log.info("path: " + request.getPathTranslated());
            @SuppressWarnings("unchecked")
            Enumeration<String> headerNames = request.getHeaderNames();
            StringBuilder sb = new StringBuilder();
            sb.append("\n");
            sb.append("URI    : ").append(request.getRequestURI()).append("\n");
            sb.append("Method : ").append(request.getMethod()).append("\n");
            sb.append("Headers:\n");
            sb.append("========\n");
            for (String s = null; headerNames.hasMoreElements(); s = headerNames.nextElement()) {
                if (s != null)
                    sb.append("  ").append(s).append(": ").append(request.getHeader(s)).append("\n");
            }
            sb.append("========\n");

            sb.append("Body   :\n");
            sb.append("========\n");
            String line = null;
            do {
                line = request.getReader().readLine();
                if (null != line)
                    sb.append(line).append("\n");
            } while(null != line);
            sb.append("========\n");
            log.info(sb.toString());
        }
        response.setContentType(CONTENT_TYPE);

        int statusCode = HttpServletResponse.SC_OK;

        if (null != request.getHeader(RESPONSE_CODE_HEADER_NAME)) {
            statusCode = Integer.parseInt(request.getHeader(RESPONSE_CODE_HEADER_NAME));
        }
        response.setStatus(statusCode);

        String out = "";
        if (null != request.getHeader(RESPONSE_BODY_HEADER_NAME)) {
            out = request.getHeader(RESPONSE_BODY_HEADER_NAME);

            response.getWriter().print(out);
        }
        log.info("respond: (" + statusCode + ") `" + out + "´");
    }

    @Override
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        doPost(request, response);
    }

    @Override
    protected void doDelete(HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        doPost(request, response);
    }

    @Override
    protected void doPut(HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        doPost(request, response);
    }
}
