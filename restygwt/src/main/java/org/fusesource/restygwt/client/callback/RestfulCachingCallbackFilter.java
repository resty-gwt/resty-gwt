package org.fusesource.restygwt.client.callback;

import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.cache.CacheKey;
import org.fusesource.restygwt.client.cache.QueueableCacheStorage;
import org.fusesource.restygwt.client.cache.UrlCacheKey;

import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.Response;

/**
 * using a different caching 'algorithm'. obeying the restful paradigm the cache
 * respects the lifecycle of a restful resource.
 * <li>POST /model/{id} : will get the user responds into the cache using the location header for the key</li>
 * <li>GET /model/{id} : will use the cached responds from cache if present</li>
 * <li>PUT /model/{id} : will put the responds from the server into the cache. a conflict will delete the cache entry to allow
 * a get to retrieve the up to date date</li>
 * <li>DELETE /model/{id} : will also delete the resource in the cache</li>
 * 
 * @author <a href="blog.mkristian.tk">Kristian</a>
 *
 */
public class RestfulCachingCallbackFilter extends CachingCallbackFilter {

    public RestfulCachingCallbackFilter(QueueableCacheStorage cache) {
        super(cache);
    }
    
    @Override
    protected CacheKey cacheKey(RequestBuilder builder) {
        return new UrlCacheKey(builder);
    }

    @Override
    protected void cacheResult(Method method, Response response) {
        final CacheKey cacheKey;
        if (response.getStatusCode() == Response.SC_CREATED && response.getHeader("Location") != null){
            // TODO very fragile way of getting the URL
            final String uri;
            if(response.getHeader("Location").startsWith("http")){
                uri = response.getHeader("Location");
            }
            else {
                uri = method.builder.getUrl().replaceFirst("/[^/]*$", "") + response.getHeader("Location");
            }
            cacheKey = new UrlCacheKey(uri);
        }
        else {
            cacheKey = cacheKey(method.builder);
        }
            
        if (RequestBuilder.DELETE.toString().equalsIgnoreCase(
               method.builder.getHTTPMethod()) || 
               // in case of a conflict the next GET request needs to
               // go remote !!
               response.getStatusCode() == Response.SC_CONFLICT) {
            cache.remove(cacheKey);
        } else {
            cache.putResult(cacheKey, response);
        }
    }
}
