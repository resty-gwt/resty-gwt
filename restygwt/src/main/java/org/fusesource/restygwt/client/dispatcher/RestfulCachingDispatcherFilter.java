package org.fusesource.restygwt.client.dispatcher;

import org.fusesource.restygwt.client.cache.CacheKey;
import org.fusesource.restygwt.client.cache.QueueableCacheStorage;
import org.fusesource.restygwt.client.cache.UrlCacheKey;
import org.fusesource.restygwt.client.callback.CallbackFactory;

import com.google.gwt.http.client.RequestBuilder;

/**
 * using a different caching 'algorithm' obeying the restful paradigm, i.e.
 * the cache respects the lifecycle of a restful resource:
 * <li>POST /model : will get the user responds into the cache using the location header for the key</li>
 * <li>GET /model/{id} : will use the cached responds from cache if present</li>
 * <li>PUT /model/{id} : will put the responds from the server into the cache. a conflict will delete the cache entry to allow
 * a get to retrieve the up to date date</li>
 * <li>DELETE /model/{id} : will also delete the resource in the cache</li>
 *
 * @author <a href="blog.mkristian.tk">Kristian</a>
 */
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
