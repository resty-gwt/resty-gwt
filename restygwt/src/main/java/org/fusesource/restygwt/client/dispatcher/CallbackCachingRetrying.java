package org.fusesource.restygwt.client.dispatcher;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.Window;

public class CallbackCachingRetrying extends CallbackRetryingAbstract {

    public CallbackCachingRetrying(
            RequestBuilder requestBuilder,
            RequestCallback requestCallback) {
        super(requestBuilder, requestCallback);
    }

    @Override
    public void onResponseReceived(Request request, Response response) {

        if (response.getStatusCode() == Response.SC_UNAUTHORIZED) {
            //FIXME: to be removed...
            Window.Location.replace("login.html");

        } else if (response.getStatusCode() != Response.SC_OK) {

            handleErrorGracefully();

        } else {

            CacheKey cacheKey = new CacheKey(requestBuilder);
            DispatcherCachingRetrying.getCacheStorage().putResult(cacheKey, response);

            requestCallback.onResponseReceived(request, response);
        }
    }




}
