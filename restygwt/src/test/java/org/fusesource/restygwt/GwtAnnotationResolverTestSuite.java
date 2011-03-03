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
