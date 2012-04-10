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

package org.fusesource.restygwt.server;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mortbay.jetty.HttpStatus;

/**
 * Servlet component of the FlakyConnectionTestGwt.
 * <p>
 * Simulates 2 times a 500 server error. And answers the third time with a 200
 * (ok). => Used to test the retrying async callback.
 * </p>
 *
 * @author <a href="mailto:mail@raphaelbauer.com">rEyez</<a>
 */
public class FlakyConnectionServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static final Logger log = Logger.getLogger(FlakyConnectionServlet.class.getName());

    private int NUMBER_OF_SERVER_FAILURES_TO_SIMULATE = 2;

    private static int currentNumberOfServerFailures = 0;

    private final String DUMMY_RESPONSE = "{\"name\":\"myName\"}";

    @Override
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response) throws IOException {

        log.info("GET: flakyMODE");

        if (currentNumberOfServerFailures < NUMBER_OF_SERVER_FAILURES_TO_SIMULATE) {
            log.info("respond code: " + HttpStatus.ORDINAL_500_Internal_Server_Error + " with purpose");
            ++currentNumberOfServerFailures;
            response.setStatus(HttpStatus.ORDINAL_500_Internal_Server_Error);
        } else {
            log.info("response: " + DUMMY_RESPONSE);
            response.getWriter().print(DUMMY_RESPONSE);
        }
    }
}