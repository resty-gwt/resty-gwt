package org.fusesource.restygwt.client.dispatcher;

import org.fusesource.restygwt.client.IDispatcher;
import org.fusesource.restygwt.client.IDispatcherFactory;

import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;

public class DispatcherFactoryDefault implements IDispatcherFactory {

    @Override
    public IDispatcher get(RequestBuilder requestBuilder,
            RequestCallback requestCallback) {

        return new DispatcherDefault(requestBuilder, requestCallback);
    }

}
