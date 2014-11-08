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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.ModelChange;
import org.fusesource.restygwt.client.cache.Domain;

import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import static org.fusesource.restygwt.rebind.util.AnnotationUtils.*;

/**
 * Implementation for an annotationparser which is responsible to put
 * annotation-data from ModelChange annotations to {@link Method} instances.
 *
 * This class transports information about ModelChangeEvents to be triggered,
 * when some servicemethods have been called.
 *
 * @author <a href="mailto:andi.balke@gmail.com">andi</a>
 */
public class ModelChangeAnnotationResolver implements AnnotationResolver {

    @Override
    public Map<String, String[]> resolveAnnotation(TreeLogger logger, JClassType source, JMethod method,
            final String restMethod) throws UnableToCompleteException {
        ModelChange classAnnot = getAnnotation(source, ModelChange.class);
        String[] serviceDomains = null;
        ModelChange methodAnnot = getAnnotation(method, ModelChange.class);
        final Map<String, String[]> ret = new java.util.HashMap<String, String[]>();

        if(null != getAnnotation(source, Domain.class)) {
            serviceDomains = getAnnotationsAsStringArray(
            		getAnnotation(source, Domain.class).value());

            // cachedomain annotations are resolved in any case
            logger.log(TreeLogger.TRACE, "found ``Domain`` annotation with " + serviceDomains.length
                    + " domains in " + source.getName());
            ret.put(Domain.CACHE_DOMAIN_KEY, serviceDomains);
        }

        if (methodAnnot != null) {
            String[] domains = null;

            if (methodAnnot.domain() == null
                    || methodAnnot.domain().length == 0) {
                if (serviceDomains == null) {
                    logger.log(TreeLogger.ERROR, "found method annotation with empty domain definition in " +
                            source.getName() + " on method " + method.getName());
                    throw new UnableToCompleteException();
                }
                logger.log(TreeLogger.TRACE, "found ``Domain`` annotation with " + serviceDomains.length
                        + " domains '" + serviceDomains + "' "
                        + source.getName() + " on method " + method.getName());
                domains = serviceDomains;
            } else {
                domains = getAnnotationsAsStringArray(methodAnnot.domain());
                logger.log(TreeLogger.TRACE, "use domain from ModelChange annotation at: "
                        + source.getName() + "#" + method.getName() + ": " + domains);
            }

            // method annotation match
            ret.put(ModelChange.MODEL_CHANGED_DOMAIN_KEY, domains);
            return ret;
        }

        if (classAnnot != null
                && classAnnot.on() != null) {
            for (String s : classAnnot.on()) {
                if (s.toUpperCase().equals(restMethod.toUpperCase())) {
                    String[] domains = null;

                    if (classAnnot.domain() == null
                            || classAnnot.domain().equals("")) {
                        if (serviceDomains == null) {
                            logger.log(TreeLogger.ERROR, "found class annotation with empty domain definition in " +
                                    source.getName());
                            throw new UnableToCompleteException();
                        }
                        domains = serviceDomains;
                    } else {
                        domains = getAnnotationsAsStringArray(classAnnot.domain());
                    }

                    // class annotation match for current method
                    ret.put(ModelChange.MODEL_CHANGED_DOMAIN_KEY, domains);
                    return ret;
                }
            }
        }

        return ret;
    }

    /**
     * convert an array of classes to an array of strings to be usable in js context.
     *
     * @param classes
     * @return
     */
    @SuppressWarnings("rawtypes")
    private String[] getAnnotationsAsStringArray(final Class[] classes) {
        if (null == classes) return null;

        List<String> ret = new ArrayList<String>();

        for(Class c: classes) {
            ret.add(c.getName());
        }

        return ret.toArray(new String[ret.size()]);
    }
}
