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

package org.fusesource.restygwt.example.client.event;

import java.util.HashMap;
import java.util.Map;

import org.fusesource.restygwt.client.Method;

/**
 * Static Config class for all things that are relevant
 * for ``ModelChangeEvent``s during runtime.
 *
 * Since all the other things (Annotation parsers and stuff)
 * are located outside the ``client`` package, we need
 * such an additional client-config.
 *
 * @author <a href="mailto:andi.balke@gmail.com">andi</<a>
 */
public class ModelChangeEvent {

    /**
     * When creating the ``RestService`` classes, there is put some information
     * in {@link Method#addData(String, String)}. To have a centralized place
     * what is the fieldname, we have this constant here.
     */
    public static final String MODEL_CHANGED_DOMAIN_KEY = "mc";

    public static final Map<String, Class> STRING_TO_EVENT_MAPPING =
            new HashMap<String, Class>();

    static {
        STRING_TO_EVENT_MAPPING.put("Foo", FooModelChangedEvent.class);
    }
}
