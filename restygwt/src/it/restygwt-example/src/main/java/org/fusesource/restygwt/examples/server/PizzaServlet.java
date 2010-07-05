/**
 * Copyright (C) 2010, Progress Software Corporation and/or its 
 * subsidiaries or affiliates.  All rights reserved.
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
import java.io.StringWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.map.ObjectMapper;
import org.fusesource.restygwt.client.Resource;
import org.fusesource.restygwt.examples.client.OrderConfirmation;
import org.fusesource.restygwt.examples.client.PizzaOrder;

/**
 * A simple example of how you can use the Jackson object mapper reuse the
 * RestyGWT DTOs to process the RestyGWT service requests.
 * 
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 */
public class PizzaServlet extends HttpServlet {

    private static final long serialVersionUID = -5364009274470240593L;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        System.out.println("Processing Pizza Order...");
        try {

            ObjectMapper mapper = new ObjectMapper();
            PizzaOrder order = mapper.readValue(req.getInputStream(), PizzaOrder.class);

            StringWriter sw = new StringWriter();
            mapper.writeValue(sw, order);
            System.out.println("Request: " + sw.toString());

            OrderConfirmation confirmation = new OrderConfirmation();
            confirmation.order_id = 123123;
            confirmation.order = order;
            confirmation.price = 27.54;
            confirmation.ready_time = System.currentTimeMillis() + 1000 * 60 * 30; // in
                                                                                   // 30
                                                                                   // min.

            sw = new StringWriter();
            mapper.writeValue(sw, confirmation);
            System.out.println("Response: " + sw.toString());

            resp.setContentType(Resource.CONTENT_TYPE_JSON);
            mapper.writeValue(resp.getOutputStream(), confirmation);
            System.out.println("Pizza Order Confirimed: " + confirmation.order_id);

        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            System.out.flush();
            System.err.flush();
        }

    }

}
