package org.fusesource.restygwt.client.callback;

import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.cache.QueueableCacheStorage;

public class CachingCallbackFactory implements CallbackFactory {

    private final QueueableCacheStorage cacheStorage;

    public CachingCallbackFactory(QueueableCacheStorage cacheStorage) {
        this.cacheStorage = cacheStorage;
    }

    /**
     * helper method to create the callback with all configurations wanted
     *
     * @param method
     * @return
     */
    public FilterawareRequestCallback createCallback(Method method) {
        final FilterawareRequestCallback retryingCallback = new FilterawareRetryingCallback(
                method);

        retryingCallback.addFilter(new CachingCallbackFilter(cacheStorage));
        return retryingCallback;
    }
}
