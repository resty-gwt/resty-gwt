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

import java.io.PrintWriter;
import java.util.HashSet;

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JParameterizedType;
import com.google.gwt.user.rebind.AbstractSourceCreator;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;

/**
 * provides additional helper methods for generating source..
 *
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 */
public abstract class BaseSourceCreator extends AbstractSourceCreator {

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

    static final private ThreadLocal<HashSet<String>> GENERATED_CLASSES = new ThreadLocal<HashSet<String>>();

    static public HashSet<String> getGeneratedClasses() {
        HashSet<String> rc = GENERATED_CLASSES.get();
        if (rc == null) {
            rc = new HashSet<String>();
            GENERATED_CLASSES.set(rc);
        }
        return rc;
    }

    static public void clearGeneratedClasses() {
        GENERATED_CLASSES.set(null);
    }

    public static JClassType find(Class<?> type, TreeLogger logger, GeneratorContext context) throws UnableToCompleteException {
        return RestServiceGenerator.find(logger, context, type.getName().replace('$', '.'));
    }

    public BaseSourceCreator(final TreeLogger logger, GeneratorContext context, JClassType source, String suffix) {
        this.logger = logger;
        this.context = context;
        this.source = source;
        this.packageName = source.getPackage().getName();

        if(source instanceof JParameterizedType)
        {
    		JParameterizedType ptype = (JParameterizedType)source;
			StringBuilder builder = new StringBuilder();
			for(JClassType type : ptype.getTypeArgs())
			{
				builder.append("__");
				builder.append(type.getParameterizedQualifiedSourceName().replace('.', '_'));
			}
			this.shortName = getName( source ) + builder.toString() + suffix;
        }
        else
        {
        	this.shortName = getName( source ) + suffix;
        }

        this.name = packageName + "." + shortName;
    }

    private String getName( JClassType source ){
        if( source.getEnclosingType() != null ){
            return getName( source.getEnclosingType() ) + "_" + source.getSimpleSourceName();
        }
        else {
            return source.getSimpleSourceName();
        }
    }
    protected PrintWriter writer() throws UnableToCompleteException {
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
            this.sourceWriter.indent();
        } else if (i == -1) {
            this.sourceWriter.outdent();
        } else {
            throw new IllegalArgumentException();
        }
        return this;
    }

    public BaseSourceCreator p(String value) {
        this.sourceWriter.println(value);

        // System.out.println(value);
        return this;
    }

    protected BaseSourceCreator p() {
        this.sourceWriter.println();
        return this;
    }

    protected TreeLogger getLogger()
    {
        return logger;
    }

    static String join(int []values, String sep) {
        StringBuilder sb = new StringBuilder();
        for(int i =0; i < values.length; i++) {
            if( i!=0 ) {
                sb.append(sep);
            }
            sb.append(values[i]);
        }
        return sb.toString();
    }

    static String join(Object []values, String sep) {
        StringBuilder sb = new StringBuilder();
        for(int i =0; i < values.length; i++) {
            if( i!=0 ) {
                sb.append(sep);
            }
            sb.append(values[i]);
        }
        return sb.toString();
    }

    final public String create() throws UnableToCompleteException {
        writer = writer();
        if (writer == null) {
            return name;
        }
        logger = getLogger().branch(TreeLogger.DEBUG, "Generating: " + name);

        ClassSourceFileComposerFactory composerFactory = createComposerFactory();
        sourceWriter = composerFactory.createSourceWriter(context, writer);

        generate();
        sourceWriter.commit(getLogger());
        return name;
    }

    abstract protected ClassSourceFileComposerFactory createComposerFactory() throws UnableToCompleteException;

    abstract protected void generate() throws UnableToCompleteException;
}