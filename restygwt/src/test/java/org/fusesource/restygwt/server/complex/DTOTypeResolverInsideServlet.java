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

package org.fusesource.restygwt.server.complex;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.fusesource.restygwt.client.complex.JsonTypeIdResolver.AbstractCustomDto;
import org.fusesource.restygwt.client.complex.JsonTypeIdResolver.AbstractDTO;
import org.fusesource.restygwt.client.complex.JsonTypeIdResolver.DTOCustom1;
import org.fusesource.restygwt.client.complex.JsonTypeIdResolver.DTOCustom2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gwt.thirdparty.guava.common.collect.Lists;

public class DTOTypeResolverInsideServlet extends HttpServlet {
    private static final long serialVersionUID = 8761900300798640874L;

    /**
     * Fake method to introspect to get generic type
     * @return null
     */
    public List<AbstractDTO> prototype() {
        return null;
    }

    private static class AbstractCustomDtoList extends ArrayList<AbstractCustomDto> {
        public AbstractCustomDtoList(List<AbstractCustomDto> abstractAccountTransactionDTOs) {
            addAll(abstractAccountTransactionDTOs);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        DTOCustom1 one = new DTOCustom1();
        one.name = "Fred Flintstone";
        one.size = 1024;

        DTOCustom2 two = new DTOCustom2();
        two.name = "Barney Rubble";
        two.foo = "schmaltzy";

        DTOCustom2 three = new DTOCustom2();
        three.name = "BamBam Rubble";
        three.foo = "dorky";

        resp.setContentType("application/json");
        ObjectMapper om = new ObjectMapper();
        try {
            AbstractCustomDtoList list = new AbstractCustomDtoList(Lists.newArrayList(one, two, three));
            om.writeValue(resp.getOutputStream(), list);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}
