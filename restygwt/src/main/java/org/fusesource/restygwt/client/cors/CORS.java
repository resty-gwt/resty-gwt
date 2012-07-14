/**
 * Copyright (C) 2009-2010 the original author or authors.
 * See the notice.md file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.fusesource.restygwt.client.cors;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Signals that another domain should be used for requests.
 *
 * You can apply the annotation at class-level or at method-level.
 * Method-level annotations override class-level (general) settings.
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CORS {

    public static final String PROTOCOL = "cors_protocol";
    public static final String PORT = "cors_port";
    public static final String DOMAIN = "cors_domain";

    /**
     * The port, defaults to an empty string.
     */
    String port() default "";

    /**
     * The protocol, defaults to an empty string.
     */
    String protocol() default "";

    /**
     * The domain you want to access.
     */
    String domain();

}
