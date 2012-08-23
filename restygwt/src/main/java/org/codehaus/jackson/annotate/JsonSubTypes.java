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

package org.codehaus.jackson.annotate;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used with {@link JsonTypeInfo} to indicate sub types of serializable
 * polymorphic types, and to associate logical names used within JSON content
 * (which is more portable than using physical Java class names).
 * 
 * @since 1.5
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotation
public @interface JsonSubTypes {
    /**
     * Subtypes of the annotated type (annotated class, or property value type
     * associated with the annotated method). These will be checked recursively
     * so that types can be defined by only including direct subtypes.
     */
    public Type[] value();

    /**
     * Definition of a subtype, along with optional name. If name is missing, class
     * of the type will be checked for {@link JsonTypeName} annotation; and if that
     * is also missing or empty, a default
     * name will be constructed by type id mechanism.
     * Default name is usually based on class name.
     */
    public @interface Type {
        /**
         * Class of the subtype
         */
        public Class<?> value();

        /**
         * Logical type name used as the type identifier for the class
         */
        public String name() default "";
    }
}