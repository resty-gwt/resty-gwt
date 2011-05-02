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
package org.fusesource.restygwt.client.event;

import java.util.ArrayList;
import java.util.List;

import org.fusesource.restygwt.example.client.event.ModelChangeEvent;
import org.fusesource.restygwt.example.client.event.ModelChangedEventHandler;

public class ModelChangedEventHandlerImpl implements ModelChangedEventHandler {

    /**
     * for testing purposes, we keep all events catched
     */
    private List<ModelChangeEvent> catched = new ArrayList<ModelChangeEvent>();

    @Override
    public void onModelChange(ModelChangeEvent event) {
        catched.add(event);
    }

    /**
     * e.g. in a test, access all events catched
     *
     * @return
     */
    public List<ModelChangeEvent> getAllCatchedEvents() {
        return catched;
    }
}
