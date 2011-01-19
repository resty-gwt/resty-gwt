package org.fusesource.restygwt.client.dispatcher;

import java.util.logging.Logger;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;

public abstract class CallbackRetryingAbstract implements RequestCallback {

    /**
     * Used by RetryingCallback
     * default value is 5
     */
    protected static int numberOfRetries = 5;

    protected static Logger logger = Logger.getLogger(CallbackRetrying.class.getName());

    /** time to wait for reconnect upon failure */
    protected int gracePeriod = 1000;

    protected int currentRetryCounter = 0;

    protected final RequestBuilder requestBuilder;
    protected final RequestCallback requestCallback;

    public CallbackRetryingAbstract(
            RequestBuilder requestBuilder,
            RequestCallback requestCallback) {

        this.requestBuilder = requestBuilder;
        this.requestCallback = requestCallback;
    }

    @Override
    public abstract void onResponseReceived(Request request, Response response);


    @Override
    public void onError(Request request, Throwable exception) {
        handleErrorGracefully();
    }

    public void handleErrorGracefully() {

        // error handling...:
        if (currentRetryCounter < numberOfRetries) {
            GWT.log("error handling in progress...");

            currentRetryCounter++;

            Timer t = new Timer() {

                public void run() {

                    try {
                        requestBuilder.send();
                    } catch (RequestException ex){
                        logger.severe(ex.getMessage());
                    }
                }
            };

            t.schedule(gracePeriod);

            gracePeriod = gracePeriod * 2;

        }

        else {
            // Super severe error.
            // reload app or redirect.
            // ===> this breaks the app but that's by intention.
            if (Window.confirm("error")) {

                Window.Location.reload();

            }
        }

    }

}
