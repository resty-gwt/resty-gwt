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
 * Marker annotation
* that indicates that the annotated method or field is to be ignored by
* introspection-based
 * serialization and deserialization functionality. That is, it should
 * not be consider a "getter", "setter" or "creator".
 * <p>
 * For example,
 * a "getter" method that would otherwise denote
 * a property (like, say, "getValue" to suggest property "value")
 * to serialize, would be ignored and no such property would
 * be output unless another annotation defines alternative method
 * to use.
 *<p>
 * This annotation works purely on method-by-method (or field-by-field) basis;
 * annotation on one method or field does not imply ignoring other methods
 * or fields.
 * Specifically, marking a "setter" candidate does not change handling
 * of matching "getter" method (or vice versa).
 *<p>
 * Annotation is usually used just a like a marker annotation, that
 * is, without explicitly defining 'value' argument (which defaults
 * to <code>true</code>): but argument can be explicitly defined.
 * This can be done to override an existing JsonIgnore by explictly
 * defining one with 'false' argument.
 *<p>
 * Annotation is similar to {@link javax.xml.bind.annotation.XmlTransient} 
 */
@Target({ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotation
public @interface JsonIgnore
{
    /**
     * Optional argument that defines whether this annotation is active
     * or not. The only use for value 'false' if for overriding purposes
     * (which is not needed often); most likely it is needed for use
     * with "mix-in annotations" (aka "annotation overrides").
     * For most cases, however, default value of "true" is just fine
     * and should be omitted.
     */
    boolean value() default true;
}
