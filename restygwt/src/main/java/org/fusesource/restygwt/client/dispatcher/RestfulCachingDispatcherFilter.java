package org.fusesource.restygwt.client.dispatcher;

import org.fusesource.restygwt.client.cache.CacheKey;
import org.fusesource.restygwt.client.cache.QueueableCacheStorage;
import org.fusesource.restygwt.client.cache.UrlCacheKey;
import org.fusesource.restygwt.client.callback.CallbackFactory;

import com.google.gwt.http.client.RequestBuilder;

public class RestfulCachingDispatcherFilter extends CachingDispatcherFilter {

    public RestfulCachingDispatcherFilter(QueueableCacheStorage cacheStorage,
            CallbackFactory cf) {
        super(cacheStorage, cf);
    }
    
    protected CacheKey cacheKey(RequestBuilder builder){
        if (RequestBuilder.GET.toString().equalsIgnoreCase(builder.getHTTPMethod())){
            return new UrlCacheKey(builder);
        }
        else {
            return null;
        }
    }
}
