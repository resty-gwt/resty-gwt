package org.fusesource.restygwt;


import com.google.gwt.junit.tools.GWTTestSuite;

import junit.framework.Test;
import junit.framework.TestCase;

import org.fusesource.restygwt.client.basic.CachingTestGwt;
import org.fusesource.restygwt.client.basic.ExtendedRestTestGwt;
import org.fusesource.restygwt.client.basic.FlakyTestGwt;
import org.fusesource.restygwt.client.basic.TimeoutTestGwt;
import org.fusesource.restygwt.client.complex.BigNumberTestGwt;
import org.fusesource.restygwt.client.event.ModelChangeAnnotationTestGwt;
import org.fusesource.restygwt.rebind.BindingDefaults;
import org.fusesource.restygwt.rebind.ModelChangeAnnotationResolver;


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

        suite.addTestSuite(FlakyTestGwt.class);
        suite.addTestSuite(TimeoutTestGwt.class);
        suite.addTestSuite(CachingTestGwt.class);
        suite.addTestSuite(ExtendedRestTestGwt.class);
        suite.addTestSuite(BigNumberTestGwt.class);
        suite.addTestSuite(CachingTestGwt.class);

        return suite;
    }
}
