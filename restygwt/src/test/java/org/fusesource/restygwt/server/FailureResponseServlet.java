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

package org.fusesource.restygwt.server;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mortbay.jetty.HttpStatus;

/**
 * Servlet component of the FailureResponseTestGwt.
 * <p>
 * Simulates a 4xx response. Used to test that the correct failure handling is done in the
 * FilterawareRequestCallback
 * </p>
 *
 * @author <a href="mailto:tim@elbart.com">elbart</<a>
 */
public class FailureResponseServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static final Logger log = Logger
            .getLogger(FailureResponseServlet.class.getName());

    @Override
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response) throws IOException {

        log.info("GET: failure response MODE");

        log.info("response code: " + HttpStatus.ORDINAL_410_Gone
                + " with purpose");
        response.setStatus(HttpStatus.ORDINAL_410_Gone);
    }
}