package org.fusesource.restygwt.client.callback;

import org.fusesource.restygwt.client.Method;

import com.google.gwt.http.client.RequestCallback;

public interface CallbackFactory {
    public RequestCallback createCallback(Method method);
}
