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

package org.fusesource.restygwt.example.client.event;

import com.google.gwt.event.shared.GwtEvent;

/**
 * generic ModelChangeEvent with identifier for the matching domain class. unfortinately
 * its not possible to have different eventclasses for different domain-updates due to
 * the lack of reflection. could do this with a generator class, maybe later..
 *
 * taken from http://stackoverflow.com/questions/2951621/gwt-custom-events/2967359#2967359
 *
 * @author <a href="mailto:andi.balke@gmail.com">andi</<a>
 *
 */
public class ModelChangeEvent extends GwtEvent<ModelChangedEventHandler> {

    public static Type<ModelChangedEventHandler> TYPE = new Type<ModelChangedEventHandler>();

    /**
     * for which domain class things have changed
     */
    private String domainIdentifier;

    public ModelChangeEvent(final String domainIdentifier) {
        this.domainIdentifier = domainIdentifier;
    }

    public String getDomain() {
        return domainIdentifier;
    }

    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<ModelChangedEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(ModelChangedEventHandler handler) {
        handler.onModelChange(this);
    }

    @Override
    public String toString() {
        return "ModelChangeEvent#" + domainIdentifier;
    }
}
