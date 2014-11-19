/**
 * Copyright (C) 2009-2011 the original author or authors.
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

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.JParameter;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import org.fusesource.restygwt.client.RestService;
import org.fusesource.restygwt.rebind.util.AnnotationCopyUtil;
import org.fusesource.restygwt.rebind.util.OnceFirstIterator;

import java.lang.annotation.Annotation;

import static org.fusesource.restygwt.rebind.DirectRestServiceClassCreator.isVoidMethod;

/**
 * @author <a href="mailto:bogdan.mustiata@gmail.com">Bogdan Mustiata</a>
 */
public class DirectRestServiceInterfaceClassCreator extends DirectRestBaseSourceCreator {
    public static final String DIRECT_REST_SERVICE_SUFFIX = "_DirectRestService";

    public DirectRestServiceInterfaceClassCreator(TreeLogger logger, GeneratorContext context, JClassType source) {
        super(logger, context, source, DIRECT_REST_SERVICE_SUFFIX);
    }

    @Override
    protected ClassSourceFileComposerFactory createComposerFactory() throws UnableToCompleteException {
        return createClassSourceComposerFactory(JavaSourceCategory.INTERFACE,
                getAnnotationsAsStringArray(source.getAnnotations()),
                new String[]{
                        RestService.class.getCanonicalName()
                }
        );
    }

    @Override
    protected void generate() throws UnableToCompleteException {
        for (JMethod method : source.getInheritableMethods()) {
            p(getAnnotationsAsString(method.getAnnotations()));
            p("void " + method.getName() + "(" + getMethodParameters(method) + getMethodCallback(method) + ");");
        }
    }

    private String getMethodParameters(JMethod method) {
        StringBuilder result = new StringBuilder("");

        for (JParameter parameter : method.getParameters()) {
            result.append(getAnnotationsAsString(parameter.getAnnotations()))
                    .append(" ")
                    .append(parameter.getType().getParameterizedQualifiedSourceName())
                    .append(" ")
                    .append(parameter.getName())
                    .append(", ");
        }

        return result.toString();
    }

    private String getMethodCallback(JMethod method) {
        if (isVoidMethod(method)) {
            return "org.fusesource.restygwt.client.MethodCallback<java.lang.Void> callback";
        }
        return "org.fusesource.restygwt.client.MethodCallback<" + method.getReturnType().getParameterizedQualifiedSourceName() + "> callback";
    }

    private String getAnnotationsAsString(Annotation[] annotations) {
        StringBuilder result = new StringBuilder("");
        OnceFirstIterator<String> space = new OnceFirstIterator<String>("", " ");

        for (String annotation : getAnnotationsAsStringArray(annotations)) {
            result.append(space.next()).append(annotation);
        }

        return result.toString();
    }

    private String[] getAnnotationsAsStringArray(Annotation[] annotations) {
        String[] result = new String[annotations.length];

        for (int i = 0; i < annotations.length; i++) {
            Annotation annotation = annotations[i];
            result[i] = AnnotationCopyUtil.getAnnotationAsString(annotation);
        }

        return result;
    }
}
