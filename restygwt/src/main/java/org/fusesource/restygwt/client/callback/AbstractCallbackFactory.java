package org.fusesource.restygwt.client.callback;

import org.fusesource.restygwt.client.FilterawareRequestCallback;
import org.fusesource.restygwt.client.Method;

public abstract class AbstractCallbackFactory implements CallbackFactory {

    public AbstractCallbackFactory() {}

    /**
     * helper method to create the callback with all configurations wanted
     *
     * @param method
     * @return
     */
    public abstract FilterawareRequestCallback createCallback(Method method);
}
