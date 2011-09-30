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
import java.util.Enumeration;
import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

/**
 * This servlet is a horible hack to integrate jersey /w gwt hosted mode junit
 * tests.
 * 
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 */
public class JerseyServlet extends com.sun.jersey.spi.container.servlet.ServletContainer {

    private static final long serialVersionUID = -273961734543645503L;

    private static Properties initParams = new Properties();
    static {
        initParams.put("com.sun.jersey.config.property.packages", "org.fusesource.restygwt.examples.server");
    }

    public void init(final ServletConfig servletConfig) throws ServletException {
        super.init(new ServletConfig() {
            public String getServletName() {
                return servletConfig.getServletName();
            }

            public ServletContext getServletContext() {
                return servletConfig.getServletContext();
            }

            @SuppressWarnings("unchecked")
            public Enumeration getInitParameterNames() {
                return initParams.keys();
            }

            public String getInitParameter(String name) {
                return initParams.getProperty(name);
            }
        });
    }

    @Override
    public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpServletRequestWrapper wrapper = new HttpServletRequestWrapper(request) {
            @Override
            public String getServletPath() {
                if (getPathInfo().startsWith("/org.fusesource.restygwt.examples.JERSEY_JAXB.JUnit/rest")) {
                    return "/org.fusesource.restygwt.examples.JERSEY_JAXB.JUnit/rest";
                }
                return super.getServletPath();
            }
        };
        super.service(wrapper, response);
    }
}
