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

import com.google.gwt.core.ext.BadPropertyValueException;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.PropertyOracle;
import com.google.gwt.core.ext.SelectionProperty;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JParameterizedType;
import com.google.gwt.user.rebind.AbstractSourceCreator;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;

import static org.fusesource.restygwt.rebind.util.ClassSourceFileComposerFactoryImportUtil.addFuseSourceStaticImports;

import java.io.PrintWriter;
import java.util.HashSet;

/**
 * provides additional helper methods for generating source..
 *
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 */
public abstract class BaseSourceCreator extends AbstractSourceCreator {

    private static final int MAX_FILE_NAME_LENGTH = 200;
    public static final TreeLogger.Type ERROR = TreeLogger.ERROR;
    public static final TreeLogger.Type WARN = TreeLogger.WARN;
    public static final TreeLogger.Type INFO = TreeLogger.INFO;
    public static final TreeLogger.Type TRACE = TreeLogger.TRACE;
    public static final TreeLogger.Type DEBUG = TreeLogger.DEBUG;
    public static final TreeLogger.Type SPAM = TreeLogger.SPAM;
    public static final TreeLogger.Type ALL = TreeLogger.ALL;

    protected final GeneratorContext context;
    protected final JClassType source;
    protected final String packageName;
    protected final String shortName;
    protected final String name;
    protected SourceWriter sourceWriter;
    private TreeLogger logger;
    private PrintWriter writer;

    private static final ThreadLocal<HashSet<String>> GENERATED_CLASSES = new ThreadLocal<HashSet<String>>();

    public static HashSet<String> getGeneratedClasses() {
        HashSet<String> rc = GENERATED_CLASSES.get();
        if (rc == null) {
            rc = new HashSet<String>();
            GENERATED_CLASSES.set(rc);
        }
        return rc;
    }

    public static void clearGeneratedClasses() {
        GENERATED_CLASSES.set(null);
    }

    public static JClassType find(Class<?> type, TreeLogger logger, GeneratorContext context)
        throws UnableToCompleteException {
        return RestServiceGenerator.find(logger, context, type.getName().replace('$', '.'));
    }

    public BaseSourceCreator(TreeLogger logger, GeneratorContext context, JClassType source, String suffix) {
        this.logger = logger;
        this.context = context;
        this.source = source;
        packageName = getOpenPackageName(source.getPackage().getName());

        if (source instanceof JParameterizedType) {
            JParameterizedType ptype = (JParameterizedType) source;
            StringBuilder builder = new StringBuilder();
            for (JClassType type : ptype.getTypeArgs()) {
                builder.append("__");
                builder.append(parametersName2ClassName(type.getParameterizedQualifiedSourceName()));
            }
            shortName = reduceName(getName(source) + builder.toString() + suffix, suffix);
        } else {
            shortName = reduceName(getName(source) + suffix, suffix);
        }

        name = packageName + "." + shortName;
    }


    //Many filesystems prevent files with names larger than 256 characters.
    //Lets have class name less than 200 to allow new generators safelly to add more sufixes there if needed
    private String reduceName(String newClassName, String suffix) {
        if (newClassName.length() < MAX_FILE_NAME_LENGTH) {
            return newClassName;
        }
        //        String sufx = "_Gen_GwtJackEncDec_";
        //Lets first try to reduce the package name of the parametrized types
        // according to parametersName2ClassName parametrized types
        //Lets find if there are parametrized types

        String noSufix = newClassName.substring(0, newClassName.length() - suffix.length());
        if (newClassName.indexOf("__") > 0) {
            //has generic
            String primaryName = noSufix.substring(0, noSufix.indexOf("__"));
            String genericPart = noSufix.substring(noSufix.indexOf("__") + 2);
            StringBuilder stringBuilder = new StringBuilder();
            String[] eachGeneric = genericPart.split("__");
            for (String genericType : eachGeneric) {
                stringBuilder.append("__");
                stringBuilder.append(reduceType(genericType));
            }
            String finalName = primaryName + stringBuilder.toString() + suffix;
            if (finalName.length() > MAX_FILE_NAME_LENGTH) {
                //File name is still too long wrapping it out aggressively
                String baseName = primaryName + stringBuilder.toString();

                int firstPosition = baseName.indexOf("__");
                int lastPosition = baseName.lastIndexOf("__");
                String middle = baseName.substring(firstPosition, lastPosition);
                finalName =
                    baseName.substring(0, firstPosition) + middle.subSequence(0, 4) + "_" + (middle.length() - 5) +
                        "_" + middle.substring(middle.length() - 9) + baseName.substring(lastPosition) + suffix;
                return finalName;
            }
            return finalName;
        }
        //If there is no generic type lets give an error and force the client to reduce className
        return newClassName;
    }

    private String reduceType(String genericType) {
        if (genericType == null || !genericType.contains("_")) {
            return genericType;
        }
        String pack = genericType.substring(0, genericType.lastIndexOf("_"));
        String finalName = genericType.substring(genericType.lastIndexOf("_") + 1);
        int packSize = pack.length();
        if (packSize > 7) {
            pack = pack.subSequence(0, 2) + Integer.toString((packSize - 5)) + pack.substring(packSize - 3);
            return pack + "_" + finalName;
        }
        return genericType;
    }

    public static final String parametersName2ClassName(String parametrizedQualifiedSourceName) {
        return parametrizedQualifiedSourceName.replace('.', '_').replace("<", "__").replace(">", "__");
    }


    protected String getName(JClassType source) {
        if (source.getEnclosingType() != null) {
            return getName(source.getEnclosingType()) + "_" + source.getSimpleSourceName();
        }
        return source.getSimpleSourceName();
    }

    /**
     * Some packages are protected such that any type we generate in that package can't subsequently be loaded
     * because of a {@link SecurityException}, for example <code>java.</code> and <code>javax.</code> packages. 
     * <p>
     * To workaround this issue we add a prefix onto such packages so that the generated code can be loaded
     * later. The prefix added is <code>open.</code>
     *
     * @param name
     * @return
     */
    private String getOpenPackageName(String name) {
        if (name.startsWith("java.") || name.startsWith("javax.")) {
            name = "open." + name;
        }
        return name;
    }

    protected PrintWriter writer() {
        HashSet<String> classes = getGeneratedClasses();
        if (classes.contains(name)) {
            return null;
        }
        classes.add(name);
        PrintWriter writer = context.tryCreate(getLogger(), packageName, shortName);
        if (writer == null) {
            return null;
        }
        return writer;
    }

    public interface Branch<R> {
        R execute() throws UnableToCompleteException;
    }

    protected <R> R branch(String msg, Branch<R> callable) throws UnableToCompleteException {
        return branch(DEBUG, msg, callable);
    }

    protected <R> R branch(TreeLogger.Type level, String msg, Branch<R> callable) throws UnableToCompleteException {
        TreeLogger original = getLogger();
        try {
            logger = getLogger().branch(level, msg);
            return callable.execute();
        } finally {
            logger = original;
        }
    }

    public BaseSourceCreator i(int i) {
        if (i == 1) {
            sourceWriter.indent();
        } else if (i == -1) {
            sourceWriter.outdent();
        } else {
            throw new IllegalArgumentException();
        }
        return this;
    }

    public BaseSourceCreator p(String value) {
        sourceWriter.println(value);

        // System.out.println(value);
        return this;
    }

    protected BaseSourceCreator p() {
        sourceWriter.println();
        return this;
    }

    protected TreeLogger getLogger() {
        return logger;
    }

    static String join(int[] values, String sep) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < values.length; i++) {
            if (i != 0) {
                sb.append(sep);
            }
            sb.append(values[i]);
        }
        return sb.toString();
    }

    static String join(Object[] values, String sep) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < values.length; i++) {
            if (i != 0) {
                sb.append(sep);
            }
            sb.append(values[i]);
        }
        return sb.toString();
    }

    public final String create() throws UnableToCompleteException {
        writer = writer();
        if (writer == null) {
            return name;
        }
        logger = getLogger().branch(TreeLogger.DEBUG, "Generating: " + name);

        ClassSourceFileComposerFactory composerFactory = createComposerFactory();
        addFuseSourceStaticImports(composerFactory);
        sourceWriter = composerFactory.createSourceWriter(context, writer);

        generate();
        sourceWriter.commit(getLogger());
        return name;
    }

    /**
     * Returns the boolean value of the property or the default value.
     */
    protected static boolean getBooleanProperty(TreeLogger logger, PropertyOracle propertyOracle, String propertyName,
                                                boolean defaultValue) {
        try {
            SelectionProperty prop = propertyOracle.getSelectionProperty(logger, propertyName);
            String propVal = prop.getCurrentValue();
            return Boolean.parseBoolean(propVal);
        } catch (BadPropertyValueException e) {
            // return the default value
        }
        return defaultValue;
    }

    protected abstract ClassSourceFileComposerFactory createComposerFactory() throws UnableToCompleteException;

    protected abstract void generate() throws UnableToCompleteException;
}
