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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Properties;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Implements a restful servlet which uses classpath resource files to provide
 * responses and validate expected requests.
 *
 * issues a 500 http response code if the request was not what was expected.
 * Handy for implementing test cases.
 *
 *
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 */
public class TestServlet extends HttpServlet {

    private static final long serialVersionUID = -5364009274470240593L;

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String method = req.getMethod().toLowerCase();
        String path = req.getRequestURI();

        // Strip off the path prefix.. it's different if run in mvn junit vs
        // hosted mode. (not sure why)
        if (path.startsWith("/ui/test/")) {
            path = path.substring("/ui/test/".length());
        } else if (path.startsWith("/org.fusesource.restygwt.examples.UI.JUnit/test/")) {
            path = path.substring("/org.fusesource.restygwt.examples.UI.JUnit/test/".length());
        }

        System.out.println("Servicing request: " + path);

        URL responseHeaders = resource(path + "." + method + ".response-headers");
        URL responseContent = resource(path + "." + method + ".response");

        // Do we know how to service the requested resource?
        if (responseContent == null && responseHeaders == null) {
            resp.sendError(404);
            return;
        }

        // Validate the request headers and content..
        URL requestContent = resource(path + "." + method + ".request");
        if (requestContent != null) {
            ByteArrayOutputStream expected = new ByteArrayOutputStream();
            transfer(requestContent.openStream(), expected);

            ByteArrayOutputStream actual = new ByteArrayOutputStream();
            transfer(req.getInputStream(), actual);

            if (!Arrays.equals(expected.toByteArray(), actual.toByteArray())) {

                System.out.println("expected content does not match request content.");
                System.out.println("<<<ACTUAL\n" + new String(actual.toByteArray()) + "\nACTUAL");
                System.out.println("<<<EXPECTED\n" + new String(actual.toByteArray()) + "\nEXPECTED");

                resp.sendError(500, "expected content does not match request content.");
                return;
            }
        }

        URL requestHeader = resource(path + "." + method + ".request-headers");
        if (requestHeader != null) {
            HashMap<String, String> rc = hashmap(properties(requestHeader));
            for (Entry<String, String> expected : rc.entrySet()) {
                String actual = req.getHeader(expected.getKey());
                if (actual == null || !actual.equals(expected.getValue())) {
                    System.out.println("expected header '" + expected.getKey() + "' not set to: '" + expected.getValue() + "'");
                    resp.sendError(500, "expected header '" + expected.getKey() + "' not set to: '" + expected.getValue() + "'");
                }
            }
        }

        // Send the response header and content
        HashMap<String, String> headers;
        if (responseHeaders != null) {
            headers = hashmap(properties(responseHeaders));
            for (Entry<String, String> entry : headers.entrySet()) {
                if ("Status-Code".equals(entry.getKey())) {
                    resp.setStatus(Integer.parseInt(entry.getValue()));
                } else {
                    resp.setHeader(entry.getKey(), entry.getValue());
                }
            }
        }
        if (responseContent != null) {
            transfer(responseContent.openStream(), resp.getOutputStream());
        }

    }

    private URL resource(String name) {
        return TestServlet.class.getResource(name);
    }

    private HashMap<String, String> hashmap(Properties properties) {
        HashMap<String, String> rc = new HashMap<String, String>();
        for (Entry<Object, Object> entry : properties.entrySet()) {
            rc.put((String) entry.getKey(), (String) entry.getValue());
        }
        return rc;
    }

    private Properties properties(URL source) throws IOException {
        Properties p = new Properties();
        InputStream is = source.openStream();
        try {
            p.load(is);
        } finally {
            is.close();
        }
        return p;
    }

    private void transfer(InputStream is, OutputStream os) throws IOException {
        try {
            try {
                int c = 0;
                while ((c = is.read()) >= 0) {
                    os.write(c);
                }
            } finally {
                is.close();
            }
        } finally {
            os.close();
        }
    }

}
