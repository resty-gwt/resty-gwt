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

package org.fusesource.restygwt.client.dispatcher;

import java.util.logging.Logger;

import org.fusesource.restygwt.client.Dispatcher;
import org.fusesource.restygwt.client.Method;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.logging.client.LogConfiguration;

/**
 * Some valuable ideas came from:
 * http://turbomanage.wordpress.com/2010/07/12/caching
 * -batching-dispatcher-for-gwt-dispatch/
 *
 * Thanks David!
 *
 * Especially: - Waiting if a particular request is already on the way
 * (otherwise you end up having many requests on the same source.
 *
 * @author <a href="mailto:mail@raphaelbauer.com">rEyez</a>
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 */
public class DefaultDispatcher implements Dispatcher {

    public static final DefaultDispatcher INSTANCE = new DefaultDispatcher();
    
    @Override
    public Request send(Method method, RequestBuilder builder) throws RequestException {
        if(GWT.isClient() && LogConfiguration.loggingIsEnabled() ){
            Logger logger = Logger.getLogger( DefaultDispatcher.class.getName() );
            logger.fine("Sending http request: " + builder.getHTTPMethod() + " "
                + builder.getUrl() + " ,timeout:"
                + builder.getTimeoutMillis());

            String content = builder.getRequestData();
            if (content != null && content.length() > 0) {
                logger.fine(content);
            }
        }
        return builder.send();
    }


}
