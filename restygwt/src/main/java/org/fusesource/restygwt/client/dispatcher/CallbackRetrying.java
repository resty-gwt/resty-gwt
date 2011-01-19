package org.fusesource.restygwt.client.dispatcher;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.Window;

public class CallbackRetrying extends CallbackRetryingAbstract {




    /**
     * Simple security: If a user contacts a service where there is "error
     * forbidden aka 505" The user gets redirected immediately to
     * an URL.
     *
     * Use case: Imagine a GWT client running a social network. The users
     * uses that GWT client in internet cafe. And logs out at the end of his session.
     *
     * Another new user hits the back button. What happens? Old values? No. This
     * automatically redirects to index.html No. This does not replace any
     * other security measures. It's the simplest way to omit stuff.
     *
     */
    private String forceRedirectUrlWhen505 = "index.html";


    public CallbackRetrying(
            RequestBuilder requestBuilder,
            RequestCallback requestCallback) {
        super(requestBuilder, requestCallback);
    }

    @Override
    public void onResponseReceived(Request request, Response response) {

        if (response.getStatusCode() == Response.SC_UNAUTHORIZED) {
            //FIXME: to be removed...
            Window.Location.replace(forceRedirectUrlWhen505);

        } else if (response.getStatusCode() != Response.SC_OK) {

            handleErrorGracefully();

        } else {
            this.requestCallback.onResponseReceived(request, response);
        }
    }

}
