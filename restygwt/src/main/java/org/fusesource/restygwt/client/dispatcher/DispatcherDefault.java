package org.fusesource.restygwt.client.dispatcher;

import org.fusesource.restygwt.client.IDispatcher;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;

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
 * @author <a href="mailto:mail@raphaelbauer.com">rEyez</<a>
 *
 */
public class DispatcherDefault implements IDispatcher {


    RequestBuilder builder;
    RequestCallback outerRequestCallback;

    public DispatcherDefault(RequestBuilder requestBuilder, RequestCallback requestCallback) {
        this.builder = requestBuilder;
        this.outerRequestCallback = requestCallback;

    }

    public Request send() throws RequestException {

            RequestCallback requestCallback = new CallbackDefault(
                    outerRequestCallback);

            builder.setCallback(requestCallback);

            GWT.log("Sending http request: " + builder.getHTTPMethod() + " "
                    + builder.getUrl() + " ,timeout:"
                    + builder.getTimeoutMillis(), null);

            String content = builder.getRequestData();

            if (content != null && content.length() > 0) {
                GWT.log(content, null);
            }

            return builder.send();
        }


}
