package org.fusesource.restygwt.rebind;

import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;

public class DirectRestServiceGenerator extends Generator {
    @Override
    public String generate(TreeLogger logger, GeneratorContext context, String source) throws UnableToCompleteException {
        try {
            JClassType restService = find(logger, context, source);

            DirectRestServiceInterfaceClassCreator restyInterfaceCreator =
                    new DirectRestServiceInterfaceClassCreator(logger, context, restService);
            restyInterfaceCreator.create();

            DirectRestServiceClassCreator generator = new DirectRestServiceClassCreator(logger, context, restService);

            return generator.create();
        } finally {
            BaseSourceCreator.clearGeneratedClasses();
        }
    }

    static JClassType find(TreeLogger logger, GeneratorContext context, String type) throws UnableToCompleteException {
        JClassType rc = context.getTypeOracle().findType(type);
        if (rc == null) {
            logger.log(TreeLogger.ERROR, "TypeOracle could not find " + type);
            throw new UnableToCompleteException();
        }
        return rc;
    }
}
