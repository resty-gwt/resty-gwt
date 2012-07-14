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

package org.fusesource.restygwt.rebind;

import java.util.HashMap;
import java.util.Map;

import org.fusesource.restygwt.client.cors.CORS;

import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;

/**
 * A {@link AnnotationResolver} that specially applies to the {@link CORS} annotation.
 */
public class CORSAnnotationResolver implements AnnotationResolver {

    /***
     * Looks for {@link CORS} annotations at class- and method level.
     */
    @Override
    public Map<String, String[]> resolveAnnotation(TreeLogger logger, JClassType jClass,
            JMethod method, final String restMethod) throws UnableToCompleteException {
        // get both the method and class annotations
        CORS classAnnotation = jClass.getAnnotation(CORS.class);
        CORS methodAnnotation = method.getAnnotation(CORS.class);
        Map<String, String[]> results = new HashMap<String, String[]>();
        String protocolValue = null;
        String portValue = null;

        // only do something if a CORS annotation is present
        if (methodAnnotation != null || classAnnotation != null) {
            // if there is a class-level annotation ...
            if (classAnnotation != null) {
                // ... assign a domain attribute
                results.put(CORS.DOMAIN, new String[] {classAnnotation.domain()});

                // ... get the protocol and port
                if (!isNullOrEmpty(classAnnotation.protocol())) {
                    protocolValue = classAnnotation.protocol();
                }
                if (!isNullOrEmpty(classAnnotation.port())) {
                    portValue = classAnnotation.port();
                }
            }

            // if there is a method level annotation ...
            if (methodAnnotation != null) {
                // ... override the domain may set at class level
                results.put(CORS.DOMAIN, new String[] {methodAnnotation.domain()});

                // ... get the protocol and port
                if (!isNullOrEmpty(methodAnnotation.protocol())) {
                    protocolValue = methodAnnotation.protocol();
                }
                if (!isNullOrEmpty(methodAnnotation.port())) {
                    portValue = methodAnnotation.port();
                }
            }

            // if no value is set ... assign the default one
            if (protocolValue == null) {
                results.put(CORS.PROTOCOL, new String[] {""});
            } else {
                results.put(CORS.PROTOCOL, new String[] {protocolValue});
            }

            // if no value is set ... assign the default one
            if (portValue == null) {
                results.put(CORS.PORT, new String[] {""});
            } else {
                results.put(CORS.PORT, new String[] {portValue});
            }
        }

        return results;
    }

    /***
     * Checks if the specified string is either null o r has a length of zero.
     */
    protected boolean isNullOrEmpty(String str) {
        return str == null || str.trim().length() == 0;
    }

}
