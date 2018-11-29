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

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;

import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.OverlayCallback;
import org.fusesource.restygwt.client.REST;
import org.fusesource.restygwt.client.Resource;
import org.fusesource.restygwt.client.RestServiceProxy;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 *
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 */
public class UI implements EntryPoint {

    /**
     * This is the entry point method.
     */
    @Override
    public void onModuleLoad() {
        Button button = new Button("Get Greeting");
        button.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                getGreeting();
            }
        });
        RootPanel.get().add(button);
        RootPanel.get().add(new Label("Name:"));
        final TextBox nameInput = new TextBox();
        RootPanel.get().add(nameInput);
        Button customButton = new Button("Get Custom Greeting");
        customButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                getCustomGreeting(nameInput.getValue());
            }
        });
        RootPanel.get().add(customButton);
    }

    private void getGreeting() {
        GreetingService service = GWT.create(GreetingService.class);
        Resource resource = new Resource(GWT.getModuleBaseURL() + "greeting-service");
        ((RestServiceProxy) service).setResource(resource);

        REST.withCallback(new OverlayCallback<Greeting>() {
            @Override
            public void onSuccess(Method method, Greeting greeting) {
                RootPanel.get().add(new Label("server said: " + greeting.getGreeting()));
            }

            @Override
            public void onFailure(Method method, Throwable exception) {
                Window.alert("Error: " + exception);
            }
        }).call(service).getGreeting();
    }

    private void getCustomGreeting(String name) {
        GreetingService service = GWT.create(GreetingService.class);
        Resource resource = new Resource(GWT.getModuleBaseURL() + "greeting-service");
        ((RestServiceProxy) service).setResource(resource);
        NameObject arg = (NameObject) JavaScriptObject.createObject();
        arg.setName(name);

        REST.withCallback(new OverlayCallback<Greeting>() {
            @Override
            public void onSuccess(Method method, Greeting greeting) {
                RootPanel.get().add(new Label("server said: " + greeting.getGreeting()));
            }

            @Override
            public void onFailure(Method method, Throwable exception) {
                Window.alert("Error: " + exception);
            }
        }).call(service).getCustomGreeting(arg);
    }
}
