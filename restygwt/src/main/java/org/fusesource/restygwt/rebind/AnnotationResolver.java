package org.fusesource.restygwt.rebind;

import java.lang.reflect.Method;

import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;

/**
 * Interface to create Class- and Method-Annotations on RestService Interfaces
 *
 * This allows users of restygwt to give more data to the callback by setting
 * some data on the {@link org.fusesource.restygwt.client.Method} instance.
 *
 * Only Key/Value of String are allowed to use since this logic happens finally
 * on the client.
 *
 * **important**
 * remember the fact that this information, attached by some implementors of this
 * interface is visible in the client.
 *
 * @author <a href="mailto:andi.balke@gmail.com">andi</<a>
 */
public interface AnnotationResolver {

    /**
     * resolve a class based annotation
     *
     * @param source
     * @return the parameters given to
     * {@link org.fusesource.restygwt.client.Method#addData(String, String)}
     *
     * e.g. returning ``new String[]{"key", "value"}``
     *      will result in ``__method.addData("key", "value")``
     */
    public String[] resolveAnnotation(TreeLogger logger, JClassType source, JMethod method,
            final String restMethod) throws UnableToCompleteException;
}
