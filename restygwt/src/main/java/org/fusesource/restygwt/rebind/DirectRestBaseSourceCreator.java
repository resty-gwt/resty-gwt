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
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JGenericType;
import com.google.gwt.core.ext.typeinfo.JTypeParameter;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;

/**
 * @author <a href="mailto:bogdan.mustiata@gmail.com">Bogdan Mustiata</a>
 */
public abstract class DirectRestBaseSourceCreator extends BaseSourceCreator {
    public DirectRestBaseSourceCreator(TreeLogger logger, GeneratorContext context, JClassType source, String suffix) {
        super(logger, context, source, suffix);
    }

    protected ClassSourceFileComposerFactory createClassSourceComposerFactory(JavaSourceCategory createWhat,
                                                                            String [] annotationDeclarations,
                                                                            String [] extendedInterfaces) {
        String genericTypeParameters = createClassDeclarationGenericType();

        ClassSourceFileComposerFactory composerFactory = new ClassSourceFileComposerFactory(
                packageName,
                shortName + genericTypeParameters
        );

        if (createWhat == JavaSourceCategory.INTERFACE) {
            composerFactory.makeInterface();
        }

        if (annotationDeclarations != null) {
            for (String annotationDeclaration : annotationDeclarations) {
                composerFactory.addAnnotationDeclaration(annotationDeclaration);
            }
        }

        if (extendedInterfaces != null) {
            for (String anInterface : extendedInterfaces) {
                composerFactory.addImplementedInterface(anInterface);
            }
        }

        return composerFactory;
    }

    private String createClassDeclarationGenericType() {
        String parameters = "";
        if(source instanceof JGenericType)
        {
            JGenericType genericType = (JGenericType)source;
            StringBuilder builder = new StringBuilder();
            builder.append("<");
            boolean first = true;
            for(JTypeParameter arg : genericType.getTypeParameters())
            {
                if(!first)
                    builder.append(",");
                builder.append(arg.getName());
                builder.append(" extends ");
                builder.append(arg.getFirstBound().getParameterizedQualifiedSourceName());
                first = false;
            }
            builder.append(">");
            parameters = builder.toString();
        }
        return parameters;
    }

}
