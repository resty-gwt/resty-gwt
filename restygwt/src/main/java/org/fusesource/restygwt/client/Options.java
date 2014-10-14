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

package org.fusesource.restygwt.client;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Retention(RUNTIME)
@Target( { METHOD, TYPE })
public @interface Options {

    Class<? extends Dispatcher> dispatcher() default Dispatcher.class;

    /**
     * Sets the expected response status code.  If the response status code does not match
     * any of the values specified then the request is considered to have failed.  Defaults to accepting
     * 200,201,204. If set to -1 then any status code is considered a success.
     */
    int[] expect() default {};

    /**
     * Sets the number of milliseconds to wait for a request to complete.  A value of zero disables timeouts.
     *
     * @return
     */
    long timeout() default -1;
    
    /**
     * Sets the key of the service root entry set with the {@link ServiceRoots#add(String, String)} method.
     * If not used the default service root value set with {@link Defaults#setServiceRoot(String)} will be used.
     */
    String serviceRootKey() default "";
}
