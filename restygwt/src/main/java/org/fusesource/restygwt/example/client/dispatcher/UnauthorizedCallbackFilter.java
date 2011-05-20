package org.fusesource.restygwt.example.client.dispatcher;

import java.util.logging.Logger;

import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.callback.CallbackFilter;

import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.logging.client.LogConfiguration;
import com.google.gwt.user.client.Window;

public class UnauthorizedCallbackFilter implements CallbackFilter {

    @Override
    public RequestCallback filter(Method method, Response response,
            RequestCallback callback) {     
        if (response.getStatusCode() == Response.SC_UNAUTHORIZED) {
                if (LogConfiguration.loggingIsEnabled()) {
                    Logger.getLogger(CallbackFilter.class.getName()).severe("Unauthorized: "
                            + method.builder.getUrl());
                    Window.Location.assign("login.html" + Window.Location.getQueryString());
                }
            }
        return callback;
    }

}
