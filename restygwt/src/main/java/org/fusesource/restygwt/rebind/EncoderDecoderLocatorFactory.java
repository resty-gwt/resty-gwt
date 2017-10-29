package org.fusesource.restygwt.rebind;

import com.google.gwt.core.ext.BadPropertyValueException;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.SelectionProperty;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;

public class EncoderDecoderLocatorFactory {


    public static final String USE_GWT_JACKSON_ENCODE_DECODER_PROPERTY_NAME = "restygwt.encodeDecode.useGwtJackson";

    public static EncoderDecoderLocator getEncoderDecoderInstanceLocator(GeneratorContext context, TreeLogger logger)
        throws UnableToCompleteException {

        boolean useGwtJacksonDecoder = false;
        try {
            SelectionProperty prop =
                context.getPropertyOracle().getSelectionProperty(logger, USE_GWT_JACKSON_ENCODE_DECODER_PROPERTY_NAME);
            if (prop != null) {
                String propVal = prop.getCurrentValue();
                if (propVal != null) {
                    useGwtJacksonDecoder = Boolean.parseBoolean(propVal);
                }
            }
        } catch (BadPropertyValueException e) {
        }

        if (useGwtJacksonDecoder) {
            return getGwtJacksonInstance(context, logger);
        }
        return restyGwtInstance(context, logger);
    }

    private static EncoderDecoderLocator restyGwtInstance(GeneratorContext context, TreeLogger logger)
        throws UnableToCompleteException {
        //JsonEncoderDecoderInstance needs to be created every time. Why???????
        return new JsonEncoderDecoderInstanceLocator(context, logger);

        //        if (restyGwtInstanceLocator == null) {
        //            restyGwtInstanceLocator = new JsonEncoderDecoderInstanceLocator(context, logger);
        //        }
        //        return restyGwtInstanceLocator;
    }

    private static EncoderDecoderLocator getGwtJacksonInstance(GeneratorContext context, TreeLogger logger)
        throws UnableToCompleteException {
        return new GwtJacksonEncoderDecoderInstanceLocator(context, logger);

        //        if (gwtJacksonInstanceLocator == null) {
        //            gwtJacksonInstanceLocator = new GwtJacksonEncoderDecoderInstanceLocator(context, logger);
        //        }
        //        return gwtJacksonInstanceLocator;
    }
}
