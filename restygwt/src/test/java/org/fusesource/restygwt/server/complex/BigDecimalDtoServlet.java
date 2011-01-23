/**
 * Copyright (C) 2011 the original author or authors.
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
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, is strictly prohibited.
 */
package org.fusesource.restygwt.server.complex;

import org.codehaus.jackson.map.ObjectMapper;
import org.fusesource.restygwt.client.complex.BigDecimalDto;

import java.io.IOException;
import java.math.BigDecimal;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Jeff Larsen
 */
public class BigDecimalDtoServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response) throws IOException {

        ObjectMapper mapper = new ObjectMapper();
        BigDecimalDto dto = mapper.readValue(request.getInputStream(), BigDecimalDto.class);

        BigDecimal dto0 = dto.getDecimals().get(0);
        BigDecimal dto1 = dto.getDecimals().get(1);
        BigDecimal dto2 = dto.getDecimals().get(2);
        BigDecimal dto3 = dto.getDecimals().get(3);

        dto = new BigDecimalDto();

        dto.getDecimals().add(dto3);
        dto.getDecimals().add(dto2);
        dto.getDecimals().add(dto1);
        dto.getDecimals().add(dto0);

        mapper.writeValue(response.getWriter(), dto);

    }


}
