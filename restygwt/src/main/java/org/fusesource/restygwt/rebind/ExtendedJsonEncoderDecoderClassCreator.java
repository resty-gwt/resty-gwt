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

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JParameterizedType;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;

import static org.fusesource.restygwt.rebind.util.ClassSourceFileComposerFactoryImportUtil.addFuseSourceStaticImports;

import org.fusesource.restygwt.client.JsonEncoderDecoder;

/**
 *
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 */
public class ExtendedJsonEncoderDecoderClassCreator extends BaseSourceCreator {

    private static final String JSON_ENCODER_DECODER = JsonEncoderDecoder.class.getName();
    private static final String JSON_ENCODER_SUFFIX = "_Generated_ExtendedJsonEncoderDecoder_";

    public ExtendedJsonEncoderDecoderClassCreator(TreeLogger logger, GeneratorContext context, JClassType source) {
        super(logger, context, source, JSON_ENCODER_SUFFIX);
    }

    @Override
    protected ClassSourceFileComposerFactory createComposerFactory() throws UnableToCompleteException {
        ClassSourceFileComposerFactory composerFactory = new ClassSourceFileComposerFactory(packageName, shortName);
        addFuseSourceStaticImports(composerFactory);
        JClassType encodedType = getEncodedType(getLogger(), context, source);
        JsonEncoderDecoderClassCreator generator =
            new JsonEncoderDecoderClassCreator(getLogger(), context, encodedType);
        composerFactory.setSuperclass(generator.create());
        composerFactory.addImplementedInterface(source.getQualifiedSourceName());
        return composerFactory;
    }

    private JClassType getEncodedType(TreeLogger logger, GeneratorContext context, JClassType type)
        throws UnableToCompleteException {
        JClassType intf = type.isInterface();
        if (intf == null) {
            getLogger().log(ERROR, "Expected " + type + " to be an interface.");
            throw new UnableToCompleteException();
        }

        JClassType[] intfs = intf.getImplementedInterfaces();
        for (JClassType t : intfs) {
            getLogger().log(INFO, "checking: " + t.getQualifiedSourceName() + ", type: " + t.getClass());
            if (t.getQualifiedSourceName().equals(JSON_ENCODER_DECODER)) {

                JParameterizedType genericType = t.isParameterized();
                if (genericType == null) {
                    getLogger().log(ERROR,
                        "Expected the " + JSON_ENCODER_DECODER + " declaration to specify a parameterized type.");
                    throw new UnableToCompleteException();
                }
                JClassType[] typeParameters = genericType.getTypeArgs();
                if (typeParameters == null || typeParameters.length != 1) {
                    getLogger().log(ERROR,
                        "Expected the " + JSON_ENCODER_DECODER + " declaration to specify 1 parameterized type.");
                    throw new UnableToCompleteException();
                }
                JClassType jClassType = typeParameters[0];
                return jClassType.isClass() == null ? jClassType.isInterface() : jClassType.isClass();
            }
        }
        getLogger().log(ERROR, "Expected  " + type + " to extend the " + JSON_ENCODER_DECODER + " interface.");
        throw new UnableToCompleteException();
    }

    @Override
    protected void generate() throws UnableToCompleteException {
    }

}