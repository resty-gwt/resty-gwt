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

import org.fusesource.restygwt.client.Json.Style;

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JType;

/**
 * 
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 */
public class GwtJacksonEncoderDecoderInstanceLocator extends JsonEncoderDecoderInstanceLocator {

    
    public GwtJacksonEncoderDecoderInstanceLocator(GeneratorContext context, TreeLogger logger)
            throws UnableToCompleteException {
    	super(context,logger);
    }



    /* (non-Javadoc)
	 * @see org.fusesource.restygwt.rebind.EncoderDecoderLocator#encodeExpression(com.google.gwt.core.ext.typeinfo.JType, java.lang.String, org.fusesource.restygwt.client.Json.Style)
	 */
    @Override
	public String encodeExpression(JType type, String expression, Style style) throws UnableToCompleteException {
        return encodeDecodeExpression(type, expression,"encode");
    }

    /* (non-Javadoc)
	 * @see org.fusesource.restygwt.rebind.EncoderDecoderLocator#decodeExpression(com.google.gwt.core.ext.typeinfo.JType, java.lang.String, org.fusesource.restygwt.client.Json.Style)
	 */
    @Override
	public String decodeExpression(JType type, String expression, Style style) throws UnableToCompleteException {
        return encodeDecodeExpression(type, expression,"decode");
    }
    
    private String getEncoderDecoder(JType type, TreeLogger logger) throws UnableToCompleteException {
        String rc = builtInEncoderDecoders.get(type);
        if (rc == null) {
            JClassType ct = type.isClass() == null? type.isInterface() : type.isClass();
        	GwtJacksonEncoderDecoderClassCreator generator = new GwtJacksonEncoderDecoderClassCreator(logger, context, ct);
            return generator.create() + ".INSTANCE";
        }
        return rc;
    }

    private String encodeDecodeExpression(JType type, String expression, String encoderMethod)
            throws UnableToCompleteException {

        String encoderDecoder = getEncoderDecoder(type, logger);
        if (encoderDecoder != null) {
            return encoderDecoder + "." + encoderMethod + "(" + expression + ")";
        }
        error("Do not know how to encode/decode " + type);
        return null;
    }

   

}
