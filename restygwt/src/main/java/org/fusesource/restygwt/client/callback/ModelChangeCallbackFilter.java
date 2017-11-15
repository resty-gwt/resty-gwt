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

package org.fusesource.restygwt.client.callback;

import java.util.logging.Logger;

import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.ModelChange;
import org.fusesource.restygwt.example.client.event.ModelChangeEvent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.logging.client.LogConfiguration;

public class ModelChangeCallbackFilter implements CallbackFilter {

    protected EventBus eventBus;
    private Logger logger;

    public ModelChangeCallbackFilter(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    private Logger getLogger() {
        if (GWT.isClient() && LogConfiguration.loggingIsEnabled() && logger == null) {
            logger = Logger.getLogger(ModelChangeCallbackFilter.class.getName());
        }
        return logger;
    }

    /**
     * the real filter method, called independent of the response code
     *
     * TODO method.getResponse() is not equal to response. unfortunately
     */
    @Override
    public RequestCallback filter(final Method method, final Response response, RequestCallback callback) {
        final int code = response.getStatusCode();

        if (code < Response.SC_MULTIPLE_CHOICES // code < 300
            && code >= Response.SC_OK) { // code >= 200
            String modelChangeIdentifier = method.getData().get(ModelChange.MODEL_CHANGED_DOMAIN_KEY);

            if (modelChangeIdentifier != null) {
                if (getLogger() != null) {
                    getLogger().fine("found modelChangeIdentifier \"" + modelChangeIdentifier + "\" in " + response);
                }
                JSONValue jsonValue = JSONParser.parseStrict(modelChangeIdentifier);
                JSONArray jsonArray = jsonValue.isArray();

                if (jsonArray != null) {
                    for (int i = 0; i < jsonArray.size(); ++i) {
                        ModelChangeEvent e = new ModelChangeEvent(jsonArray.get(i).isString().stringValue());

                        if (getLogger() != null) {
                            getLogger().info("fire event \"" + e + "\" ...");
                        }
                        eventBus.fireEvent(e);
                    }
                } else {
                    if (getLogger() != null) {
                        getLogger().info("found null array for model-change events");
                    }
                }
            }
            return callback;
        }

        if (getLogger() != null) {
            getLogger().fine("no event processing due to invalid response code: " + code);
        }
        return callback;
    }
}
