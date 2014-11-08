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

package org.fusesource.restygwt.rebind;

import java.util.Map;

import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.RestService;

import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;

/**
 * Interface to create Class- and Method-Annotations on RestService Interfaces
 *
 * This allows users of restygwt to give more data to the callback by setting
 * some data on the {@link org.fusesource.restygwt.client.Method} instance.
 *
 * Only Key/Value of String are allowed to use since this logic happens finally
 * on the client.
 *
 * Usecase is to transport those informations, which are only on the interface
 * of a {@link RestService} until we reach the final {@link MethodCallback}. There
 * we can act those informations, e.g. configure caching, send update events, ...
 *
 * @author <a href="mailto:andi.balke@gmail.com">andi</a>
 */
public interface AnnotationResolver {

    /**
     * resolve a class based annotation
     *
     * @param source
     * @return the parameters given to
     * {@link org.fusesource.restygwt.client.Method#addData(String, String)}
     *
     * e.g. returning ``new String[]{"key", "value"}``
     *      will result in ``__method.addData("key", "value")``
     */
    public Map<String, String[]> resolveAnnotation(TreeLogger logger, JClassType source, JMethod method,
            final String restMethod) throws UnableToCompleteException;
}
