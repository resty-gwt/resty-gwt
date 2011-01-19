package org.fusesource.restygwt.client.dispatcher;

import org.fusesource.restygwt.client.IDispatcher;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;

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
public class DispatcherCachingRetrying implements IDispatcher {

    private static CacheStorage cacheStorage = new CacheStorage();
    RequestBuilder builder;
    RequestCallback outerRequestCallback;


    public DispatcherCachingRetrying(RequestBuilder requestBuilder, RequestCallback requestCallback) {
        this.builder = requestBuilder;
        this.outerRequestCallback = requestCallback;

    }


    public Request send() throws RequestException {
        // requested key:
        final CacheKey cacheKey = new CacheKey(builder);


        final Response cachedResponse = cacheStorage
                .getResultOrReturnNull(cacheKey);


        if (cachedResponse != null) {

            outerRequestCallback.onResponseReceived(null, cachedResponse);



            return null;

        } else {

            RequestCallback requestCallback = new CallbackCachingRetrying(builder,
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

    public static CacheStorage getCacheStorage() {
        return cacheStorage;
    }

}
