package org.fusesource.restygwt.rebind;

import org.fusesource.restygwt.client.ModelChange;

import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.UnableToCompleteException;

/**
 * @author <a href="mailto:andi.balke@gmail.com">andi</<a>
 */
public class ModelChangeAnnotationResolver implements AnnotationResolver {

    public static final String MODEL_CHANGED_DOMAIN_KEY = "_mc";

    @Override
    public String[] resolveAnnotation(TreeLogger logger, JClassType source, JMethod method,
            final String restMethod) throws UnableToCompleteException {
        ModelChange classAnnot = source.getAnnotation(ModelChange.class);

        for (String s : classAnnot.on()) {
            if (s.toUpperCase().equals(restMethod.toUpperCase())) {
                if (classAnnot.domain().equals("")) {
                    logger.log(TreeLogger.ERROR, "found class annotation with empty domain definition in " +
                            source.getName());
                    throw new UnableToCompleteException();
                }
                return new String[]{MODEL_CHANGED_DOMAIN_KEY, classAnnot.domain()};
            }
        }
        return null;
    }
}
