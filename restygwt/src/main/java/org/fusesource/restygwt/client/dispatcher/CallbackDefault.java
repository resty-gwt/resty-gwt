package org.fusesource.restygwt.client.dispatcher;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;

/**
 * Does nothing in particular. Simply wraps an external RequestCallback.
 * No caching, No retrying, No Batching. No features.
 *
 * @author <a href="mailto:mail@raphaelbauer.com">rEyez</<a>
 *
 */
public class CallbackDefault implements RequestCallback {

    /** The outer RequestCallback */
    private RequestCallback requestCallback;

    public CallbackDefault(final RequestCallback callback) {
        this.requestCallback = callback;
    }

    @Override
    public void onResponseReceived(Request request, Response response) {
        requestCallback.onResponseReceived(request, response);
    }

    @Override
    public void onError(Request request, Throwable exception) {
        requestCallback.onError(request, exception);
    }
}
