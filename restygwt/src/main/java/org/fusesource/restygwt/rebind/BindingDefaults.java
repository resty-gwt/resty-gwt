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

package org.fusesource.restygwt.rebind;

import java.util.ArrayList;
import java.util.List;

/**
 * compile-time defaults
 *
 * with this class it will be possible to take part of the generation process of restserviceimpl
 * classes. by default there are no additional resolvers registered, you can add some if wanted.
 *
 * @author abalke
 *
 */
public class BindingDefaults {
    /**
     * additional AnnotationResolvers, added by {@link #addAnnotationResolver(AnnotationResolver)}
     */
    private static final List<AnnotationResolver> annotationResolvers = new ArrayList<AnnotationResolver>();

    /**
     * to avoid returning different Sets of AnnotationResolvers by false
     * usage, the return of the first call to {@link #getAnnotationResolvers()}
     * is cached here.
     *
     * if there will be a call to {@link #addAnnotationResolver(AnnotationResolver)} after
     * that first call, we will throw a runtime exception to avoid nasty bugs.
     */
    private static List<AnnotationResolver> _annotationResolversRequested = null;

    /**
     * access all annotationresolvers that are registered
     *
     * @return a copy of all AnnotationResolvers
     */
    public static List<AnnotationResolver> getAnnotationResolvers() {
        // return a copy
        List<AnnotationResolver> ret = new ArrayList<AnnotationResolver>();

        for (AnnotationResolver a : annotationResolvers) {
            ret.add(a);
        }
        return _annotationResolversRequested = ret;
    }

    /**
     * access all annotationresolvers that are registered
     *
     * @return
     */
    public static void addAnnotationResolver(AnnotationResolver ar) {
        if (_annotationResolversRequested != null) {
            throw new RuntimeException("Sorry, you cannot add more AnnotationResolver instances after the first time " +
                    "`BindingDefaults#getAnnotationResolvers´ has been called. please check your runtime logic.");
        }
        annotationResolvers.add(ar);
    }
}
