/**
 * Copyright (C) 2009-2016 the original author or authors.
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

package org.fusesource.restygwt.server.complex;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.ws.rs.Path;

import org.fusesource.restygwt.client.complex.string.service.StringService;
import org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher;
import org.jboss.resteasy.spi.BadRequestException;
import org.jboss.resteasy.spi.Registry;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

/**
 * Servlet to check restygwts autodetection of text for @Produces and Json/text for @Consumes
 */
public class StringEncoderDecoderServlet extends HttpServletDispatcher {

    private static final long serialVersionUID = 1L;

    @Path("/strings")
    public static class StringsImpl implements StringService {
        @Override
        public String getAsJson() {
            return "String as Json";
        }

        @Override
        public String getAsPlainText() {
            return "String as plain text";
        }

        /**
         * Method to test POST with {@code @Consumes(MediaType.APPLICATION_JSON)}.
         *
         * @throws BadRequestException
         *             For restygwt <= 2.0.3 or plain text autodetection set to {@code false} because it sends every String as plain text
         */
        @Override
        public void setAsJson(String text) {
            if (!"\"Json String?\"".equals(text)) {
                throw new BadRequestException("Wrong Format");
            }
        }

        @Override
        public void setAsPlainText(String text) {
            if (!"Plain text String?".equals(text)) {
                throw new BadRequestException("Wrong Format");
            }
        }
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);

        ResteasyProviderFactory providerFactory = servletContainerDispatcher.getDispatcher().getProviderFactory();
        providerFactory.registerProvider(JsonStringProvider.class);

        Registry registry = servletContainerDispatcher.getDispatcher().getRegistry();
        registry.addPerRequestResource(StringsImpl.class, "/org.fusesource.restygwt.StringEncoderDecoderTestGwt.JUnit");
        registry.addPerRequestResource(StringsImpl.class, "/org.fusesource.restygwt.StringEncoderDecoderAutodetectPlainTextTestGwt.JUnit");
    }

}