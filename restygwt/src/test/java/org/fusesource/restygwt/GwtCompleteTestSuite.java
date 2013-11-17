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

package org.fusesource.restygwt;


import junit.framework.Test;
import junit.framework.TestCase;

import org.fusesource.restygwt.client.basic.CacheCallbackTestGwt;
import org.fusesource.restygwt.client.basic.CachingTestGwt;
import org.fusesource.restygwt.client.basic.DirectRestServiceTestGwt;
import org.fusesource.restygwt.client.basic.FailingTestGwt;
import org.fusesource.restygwt.client.basic.FlakyTestGwt;
import org.fusesource.restygwt.client.basic.FormParamTestGwt;
import org.fusesource.restygwt.client.basic.GenericsTestGwt;
import org.fusesource.restygwt.client.basic.JsonCreatorWithSubtypes;
import org.fusesource.restygwt.client.basic.ParameterizedTypeDTO;
import org.fusesource.restygwt.client.basic.ParameterizedTypeServiceInterfaces;
import org.fusesource.restygwt.client.basic.PathParamTestGwt;
import org.fusesource.restygwt.client.basic.QueryParamTestGwt;
import org.fusesource.restygwt.client.basic.ResourcePassesHeadersTestGwt;
import org.fusesource.restygwt.client.basic.ResourceTestGwt;
import org.fusesource.restygwt.client.basic.SubResourceClientGeneration;
import org.fusesource.restygwt.client.basic.TimeoutTestGwt;
import org.fusesource.restygwt.client.cache.VolatileQueueableCacheStorageTestGwt;
import org.fusesource.restygwt.client.codec.EncoderDecoderTestGwt;
import org.fusesource.restygwt.client.codec.InnerClassesEncoderDecoderTestGwt;
import org.fusesource.restygwt.client.codec.MapInRestServiceEncoderDecoderTestGwt;
import org.fusesource.restygwt.client.complex.JsonTypeIdResolver;

import com.google.gwt.junit.tools.GWTTestSuite;


/**
 *
 * <p>
 * <ul>
 * <li>Add GWTTestCases here</li>
 * <li>See also <a href="http://mojo.codehaus.org/gwt-maven-plugin/user-guide/testing.html">maven docu</a></li>
 * </ul>
 *
 * IMPORTANT: Naming convention
 * <ul>
 * <li>Fast Junit Tests: end with "Test". Correct example: MyTest.java</li>
 * <li>GWT Tests: do NOT end with "Test". Do NOT start with GwtTest. Correct example: DateBeautifierTestGwt.java</li>
 * </ul>
 * </p>
 *
 * @author <a href="mailto:mail@raphaelbauer.com">rEyez</<a>
 */
public class GwtCompleteTestSuite extends TestCase {


    /**
     * @return the suite of that module
     */
    public static Test suite() {
        GWTTestSuite suite = new GWTTestSuite("all GwtTestCases but AnnotationResolver" );

        // keep the cache-callback at the beginning to get it pass
        // TODO why ? and what goes wrong when at located at the end ?
        suite.addTestSuite(CacheCallbackTestGwt.class);
        suite.addTestSuite(InnerClassesEncoderDecoderTestGwt.class);
        suite.addTestSuite(MapInRestServiceEncoderDecoderTestGwt.class);
        suite.addTestSuite(EncoderDecoderTestGwt.class);
        
        suite.addTestSuite(FlakyTestGwt.class);
        suite.addTestSuite(TimeoutTestGwt.class);
        suite.addTestSuite(CachingTestGwt.class);
        suite.addTestSuite(ResourceTestGwt.class);
        suite.addTestSuite(ResourcePassesHeadersTestGwt.class);
        suite.addTestSuite(VolatileQueueableCacheStorageTestGwt.class);       
        suite.addTestSuite(FailingTestGwt.class);
        suite.addTestSuite(GenericsTestGwt.class);
        suite.addTestSuite(ParameterizedTypeDTO.class);
        suite.addTestSuite(ParameterizedTypeServiceInterfaces.class);
        suite.addTestSuite(SubResourceClientGeneration.class);
        suite.addTestSuite(JsonTypeIdResolver.class);
        suite.addTestSuite(JsonCreatorWithSubtypes.class);
        suite.addTestSuite(PathParamTestGwt.class);
        suite.addTestSuite(QueryParamTestGwt.class);
        suite.addTestSuite(FormParamTestGwt.class);
        suite.addTestSuite(DirectRestServiceTestGwt.class);
        
        //TODO not sure what a jsonp servlet looks like, i.e. the one used by the test just
        // produces error on the javascript callback
        //suite.addTestSuite(JsonpTestGwt.class);
        return suite;
    }
}
