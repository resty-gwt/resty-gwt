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
 * Static Config class for all things that are relevant
 * for ``ModelChangeEvent``s during runtime.
 *
 * Since all the other things (Annotation parsers and stuff)
 * are located outside the ``client`` package, we need
 * such an additional client-config.
 *
 * @author <a href="mailto:andi.balke@gmail.com">andi</a>
 */
public class ModelChangeEventFactory {

    /**
     * static class, deny instanciation
     */
    private ModelChangeEventFactory() {}

    /**
     * factory method from the annotated domain name to a real event object
     *
     * @param domainName
     * @return
     */
    @SuppressWarnings("rawtypes")
    public static GwtEvent factory(final String domainName) {
        final GwtEvent e = new ModelChangeEvent(domainName);

        return e;
    }
}
