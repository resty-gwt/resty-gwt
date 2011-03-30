/**
 * Copyright (C) 2009-2010 the original author or authors.
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

package org.fusesource.restygwt.client.callback;

import java.util.logging.Logger;

import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.example.client.event.ModelChangeEventFactory;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.http.client.Response;
import com.google.gwt.logging.client.LogConfiguration;

public class ModelChangeCallbackFilter implements CallbackFilter {

    protected EventBus eventBus;

    public ModelChangeCallbackFilter(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    /**
     * the real filter method, called independent of the response code
     *
     * TODO method.getResponse() is not equal to response. unfortunately
     */
    @Override
    public void filter(final Method method, final Response response) {
        final int code = response.getStatusCode();

        if (code < Response.SC_MULTIPLE_CHOICES
                && code >= Response.SC_OK) {
            String modelChangeIdentifier = method.getData().get(ModelChangeEventFactory.MODEL_CHANGED_DOMAIN_KEY);

            if (modelChangeIdentifier != null) {
                GWT.log("found modelChangeIdentifier \"" + modelChangeIdentifier + "\" in " + response);
                if (LogConfiguration.loggingIsEnabled()) {
                    Logger.getLogger(ModelChangeCallbackFilter.class.getName()).severe(
                            "found modelChangeIdentifier \"" + modelChangeIdentifier + "\" in " + response);
                }

                GwtEvent e = ModelChangeEventFactory.factory(modelChangeIdentifier);
                if (LogConfiguration.loggingIsEnabled()) {
                    Logger.getLogger(ModelChangeCallbackFilter.class.getName()).severe(
                            "fire event \"" + e + "\" ...");
                }
                System.out.println("fire event \"" + e + "\" ...");
                GWT.log("fire event \"" + e + "\" ...");

                eventBus.fireEvent(e);
            }
            return;
        }

        GWT.log("no event processing due to invalid response code: " + code);
    }
}
