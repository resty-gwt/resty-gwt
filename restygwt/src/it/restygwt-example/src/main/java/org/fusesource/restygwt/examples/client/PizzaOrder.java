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

package org.fusesource.restygwt.examples.client;

import java.util.ArrayList;
import java.util.List;

import org.fusesource.restygwt.client.*;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.google.gwt.core.client.GWT;

/**
 * 
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 */
public class PizzaOrder {

    public String phone_number;

    public boolean delivery;
    
    @Json(name="delivery-address")
    @JsonProperty("delivery-address")
    public List<String> delivery_address = new ArrayList<String>(4);

    public List<Pizza> pizzas = new ArrayList<Pizza>(10);

    /**
     * Example of how to create an instance of a JsonEncoderDecoder for a data
     * transfer object.
     */
    public interface PizzaOrderJED extends JsonEncoderDecoder<PizzaOrder> {

    }

    @Override
    public String toString() {
        if (GWT.isClient()) {
            // Shows how to access the code generated json encoder/decoder.
            // Only works in client code, won't work on the server side.
            PizzaOrderJED jed = GWT.create(PizzaOrderJED.class);
            return jed.encode(this).toString();
        }
        return super.toString();
    }
}
