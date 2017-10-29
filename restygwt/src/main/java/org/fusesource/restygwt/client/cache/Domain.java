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

package org.fusesource.restygwt.client.cache;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.fusesource.restygwt.client.Method;

@Documented
@Retention(RUNTIME)
@Target({ TYPE })
public @interface Domain {
    /**
     * When creating the ``RestService`` classes, there will be put some information
     * in {@link Method#addData(String, String)}. To have a centralized place
     * what is the key on that ``put`` (and later ``get``) operation, we have this
     * constant here.
     *
     * Information about the cache-domain is stored in the key::
     */
    public static final String CACHE_DOMAIN_KEY = "cd";

    Class<?>[] value();
}
