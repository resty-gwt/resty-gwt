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

import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;

/**
 *
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 */
public class RestServiceGenerator extends Generator {

    @Override
    public String generate(TreeLogger logger, GeneratorContext context, String source) throws UnableToCompleteException {
        try {
            JClassType restService = find(logger, context, source);
            RestServiceClassCreator generator = new RestServiceClassCreator(logger, context, restService);

            String generated = generator.create();
            return generated;
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
