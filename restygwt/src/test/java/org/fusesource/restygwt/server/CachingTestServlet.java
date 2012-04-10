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

/**
 *
 * Super simple servlet that simply does nothing to check if
 * timeout management is okay.
 *
 * @author <a href="mailto:mail@raphaelbauer.com">rEyez</<a>
 *
 */
public class CachingTestServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    /**
     * How many times this servlet was contacted.
     * This is there to test if application test is working.
     */
    public static int contactCounter = 0;

    private static final Logger log = Logger.getLogger(CachingTestServlet.class.getName());

    //5 seconds timeout:
    long TIMEOUT = 5000;

    private final String DUMMY_RESPONSE = "{\"name\":\"myName\"}";

    @Override
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response) throws IOException {

        response.setStatus(200);
        if (request.getPathInfo().equals("/getnumberofcontacts")) {
            log.info("response: \"" + contactCounter + "\"");
            response.getWriter().print(contactCounter);
        } else {
            ++contactCounter;
            log.info("response: \"" + DUMMY_RESPONSE + "\", counter is " + contactCounter);
            response.getWriter().print(DUMMY_RESPONSE);
        }
    }
}