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

import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.ModelChange;
import org.fusesource.restygwt.example.client.event.ModelChangeEventFactory;

import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;

/**
 * Implementation for an annotationparser which is responsible to put
 * annotation-data from ModelChange annotations to {@link Method} instances.
 *
 * This class transports information about ModelChangeEvents to be triggered,
 * when some servicemethods have been called.
 *
 * @author <a href="mailto:andi.balke@gmail.com">andi</<a>
 */
public class ModelChangeAnnotationResolver implements AnnotationResolver {

    @Override
    public String[] resolveAnnotation(TreeLogger logger, JClassType source, JMethod method,
            final String restMethod) throws UnableToCompleteException {
        ModelChange classAnnot = source.getAnnotation(ModelChange.class);
        ModelChange methodAnnot = method.getAnnotation(ModelChange.class);

        if (methodAnnot != null) {
            if (methodAnnot.domain() == null
                    || methodAnnot.domain().equals("")) {
                logger.log(TreeLogger.ERROR, "found method annotation with empty domain definition in " +
                        source.getName() + " on method " + method.getName());
                throw new UnableToCompleteException();
            }
            // method annotation match
            return new String[]{ModelChangeEventFactory.MODEL_CHANGED_DOMAIN_KEY, methodAnnot.domain()};
        }

        if (classAnnot != null
                && classAnnot.on() != null) {
            for (String s : classAnnot.on()) {
                if (s.toUpperCase().equals(restMethod.toUpperCase())) {
                    if (classAnnot.domain() == null
                            || classAnnot.domain().equals("")) {
                        logger.log(TreeLogger.ERROR, "found class annotation with empty domain definition in " +
                                source.getName());
                        throw new UnableToCompleteException();
                    }
                    // class annotation match for current method
                    return new String[]{ModelChangeEventFactory.MODEL_CHANGED_DOMAIN_KEY, classAnnot.domain()};
                }
            }
        }
        // no match at all
        return null;
    }
}
