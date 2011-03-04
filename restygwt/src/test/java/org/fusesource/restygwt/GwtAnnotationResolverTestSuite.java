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

package org.fusesource.restygwt;

import junit.framework.Test;
import junit.framework.TestCase;

import org.fusesource.restygwt.client.event.ModelChangeAnnotationTestGwt;
import org.fusesource.restygwt.rebind.AnnotationResolver;
import org.fusesource.restygwt.rebind.BindingDefaults;
import org.fusesource.restygwt.rebind.ModelChangeAnnotationResolver;

import com.google.gwt.junit.tools.GWTTestSuite;


/**
 * separate testsuite for tests where {@link AnnotationResolver}s are added to restygwt
 *
 * @author <a href="mailto:andi.balke@gmail.com">andi</<a>
 */
public class GwtAnnotationResolverTestSuite extends TestCase {


    /**
     * @return the suite of that module
     */
    public static Test suite() {
        GWTTestSuite suite = new GWTTestSuite("AnnotationResolver GwtTestCases" );

        /*
         * prepare restygwt to recognize our resolvers. this cannot be done in client
         * code as BindingDefaults is not a client class.
         *
         * in a real application this call is allowed to be done only **once**. remember
         * this when placing the call in your app.
         */
        BindingDefaults.addAnnotationResolver(new ModelChangeAnnotationResolver());
        suite.addTestSuite(ModelChangeAnnotationTestGwt.class);

        return suite;
    }
}
