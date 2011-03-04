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

package org.fusesource.restygwt.example.client.event;

import com.google.gwt.event.shared.GwtEvent;

/**
 * example event to trigger updates for a domain named "Foo"
 *
 * taken from http://stackoverflow.com/questions/2951621/gwt-custom-events/2967359#2967359
 *
 * @author <a href="mailto:andi.balke@gmail.com">andi</<a>
 *
 */
public class FooModelChangedEvent extends GwtEvent<FooModelChangedEventHandler> {

    public static Type<FooModelChangedEventHandler> TYPE = new Type<FooModelChangedEventHandler>();

    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<FooModelChangedEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(FooModelChangedEventHandler handler) {
        handler.onEvent(this);
    }
}
