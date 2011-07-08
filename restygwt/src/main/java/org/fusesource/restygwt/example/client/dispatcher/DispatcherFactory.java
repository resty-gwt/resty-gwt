package org.fusesource.restygwt.example.client.dispatcher;

import org.fusesource.restygwt.client.cache.DefaultQueueableCacheStorage;
import org.fusesource.restygwt.client.cache.QueueableCacheStorage;
import org.fusesource.restygwt.client.cache.VolatileQueueableCacheStorage;
import org.fusesource.restygwt.client.callback.CachingCallbackFilter;
import org.fusesource.restygwt.client.callback.CallbackFactory;
import org.fusesource.restygwt.client.callback.CallbackFilter;
import org.fusesource.restygwt.client.callback.DefaultCallbackFactory;
import org.fusesource.restygwt.client.callback.RestfulCachingCallbackFilter;
import org.fusesource.restygwt.client.callback.RetryingCallbackFactory;
import org.fusesource.restygwt.client.callback.XSSToken;
import org.fusesource.restygwt.client.callback.XSSTokenCallbackFilter;
import org.fusesource.restygwt.client.dispatcher.CachingDispatcherFilter;
import org.fusesource.restygwt.client.dispatcher.DefaultDispatcherFilter;
import org.fusesource.restygwt.client.dispatcher.DefaultFilterawareDispatcher;
import org.fusesource.restygwt.client.dispatcher.DispatcherFilter;
import org.fusesource.restygwt.client.dispatcher.FilterawareDispatcher;
import org.fusesource.restygwt.client.dispatcher.RestfulCachingDispatcherFilter;
import org.fusesource.restygwt.client.dispatcher.XSSTokenDispatcherFilter;

/**
 * this factory can be used to create a Dispatcher which can be used as dispatcher Option on
 * the RestService interface.<br />
 * <code><pre>
 * public class RestfulRetryingDispatcherSingleton implements Dispatcher{
 *
 *   public static Dispatcher INSTANCE = new DispatcherFactory().restfulCachingDispatcher();
 *   
 *   // do not allow concrete instances of this class
 *   private RestfulRetryingDispatcherSingleton(){
 *     throw new Error("never called");
 *   }
 *
 *   public Request send(Method method, RequestBuilder builder) throws RequestException {
 *     return null;   // dummy
 *   }
 * }
 * </pre></code>
 * 
 * @author kristian
 *
 */
public class DispatcherFactory {

    public FilterawareDispatcher xssProtectionDispatcher(){
        XSSToken xss = new XSSToken();

        DispatcherFilter xssDispatcherFilter = new XSSTokenDispatcherFilter(xss);
        
        CallbackFilter xssCallbackFilter = new XSSTokenCallbackFilter(xss);
        CallbackFactory callbackFactory = new DefaultCallbackFactory(xssCallbackFilter);
        DispatcherFilter defaultDispatcherFilter = new DefaultDispatcherFilter(callbackFactory);
        
        FilterawareDispatcher dispatcher = new DefaultFilterawareDispatcher(xssDispatcherFilter, defaultDispatcherFilter);
        
        return dispatcher;
    }
    
    public FilterawareDispatcher cachingDispatcher(){
        QueueableCacheStorage cache = new DefaultQueueableCacheStorage();
        
        CallbackFilter cachingCallbackFilter = new CachingCallbackFilter(cache);
        CallbackFactory callbackFactory = new DefaultCallbackFactory(cachingCallbackFilter);
        DispatcherFilter cachingDispatcherFilter = new CachingDispatcherFilter(cache,callbackFactory);
        
        FilterawareDispatcher dispatcher = new DefaultFilterawareDispatcher(cachingDispatcherFilter);
        
        return dispatcher;
    }
    
    public FilterawareDispatcher retryingDispatcher(){
        QueueableCacheStorage cache = new VolatileQueueableCacheStorage();
        
        CallbackFilter cachingCallbackFilter = new CachingCallbackFilter(cache);
        CallbackFactory callbackFactory = new RetryingCallbackFactory(cachingCallbackFilter);
        DispatcherFilter cachingDispatcherFilter = new CachingDispatcherFilter(cache, callbackFactory);
        
        FilterawareDispatcher dispatcher = new DefaultFilterawareDispatcher(cachingDispatcherFilter);
        
        return dispatcher;
    }
    
    public FilterawareDispatcher retryingCachingDispatcher(){
        QueueableCacheStorage cache = new DefaultQueueableCacheStorage();
        
        CallbackFilter cachingCallbackFilter = new CachingCallbackFilter(cache);
        CallbackFactory callbackFactory = new RetryingCallbackFactory(cachingCallbackFilter);
        DispatcherFilter cachingDispatcherFilter = new CachingDispatcherFilter(cache, callbackFactory);
        
        FilterawareDispatcher dispatcher = new DefaultFilterawareDispatcher(cachingDispatcherFilter);
        
        return dispatcher;
    }

    public FilterawareDispatcher retryingCachingXSSProtectionDispatcher(){
        XSSToken xss = new XSSToken();

        DispatcherFilter xssDispatcherFilter = new XSSTokenDispatcherFilter(xss);
        
        CallbackFilter xssCallbackFilter = new XSSTokenCallbackFilter(xss);

        QueueableCacheStorage cache = new DefaultQueueableCacheStorage();
        
        CallbackFilter cachingCallbackFilter = new CachingCallbackFilter(cache);
        CallbackFactory callbackFactory = new RetryingCallbackFactory(xssCallbackFilter, cachingCallbackFilter);
        DispatcherFilter cachingDispatcherFilter = new CachingDispatcherFilter(cache, callbackFactory);
        
        FilterawareDispatcher dispatcher = new DefaultFilterawareDispatcher(xssDispatcherFilter, cachingDispatcherFilter);
        
        return dispatcher;
    }
    
    public FilterawareDispatcher restfulCachingDispatcher(){
        QueueableCacheStorage cache = new DefaultQueueableCacheStorage();
        
        CallbackFilter cachingCallbackFilter = new RestfulCachingCallbackFilter(cache);
        CallbackFactory callbackFactory = new DefaultCallbackFactory(cachingCallbackFilter);
        DispatcherFilter cachingDispatcherFilter = new RestfulCachingDispatcherFilter(cache, callbackFactory);
        
        FilterawareDispatcher dispatcher = new DefaultFilterawareDispatcher(cachingDispatcherFilter);
        
        return dispatcher;
    }

    public FilterawareDispatcher restfulRetryingCachingDispatcher(){
        QueueableCacheStorage cache = new DefaultQueueableCacheStorage();
        
        CallbackFilter cachingCallbackFilter = new RestfulCachingCallbackFilter(cache);
        CallbackFactory callbackFactory = new RetryingCallbackFactory(cachingCallbackFilter);
        DispatcherFilter cachingDispatcherFilter = new RestfulCachingDispatcherFilter(cache, callbackFactory);
        
        FilterawareDispatcher dispatcher = new DefaultFilterawareDispatcher(cachingDispatcherFilter);
        
        return dispatcher;
    }
    
    public FilterawareDispatcher restfulRetryingCachingXSSProtectionDispatcher(){
        XSSToken xss = new XSSToken();

        DispatcherFilter xssDispatcherFilter = new XSSTokenDispatcherFilter(xss);
        
        CallbackFilter xssCallbackFilter = new XSSTokenCallbackFilter(xss);

        QueueableCacheStorage cache = new DefaultQueueableCacheStorage();
        
        CallbackFilter cachingCallbackFilter = new RestfulCachingCallbackFilter(cache);
        CallbackFactory callbackFactory = new RetryingCallbackFactory(xssCallbackFilter, cachingCallbackFilter);
        DispatcherFilter cachingDispatcherFilter = new RestfulCachingDispatcherFilter(cache, callbackFactory);
        
        FilterawareDispatcher dispatcher = new DefaultFilterawareDispatcher(xssDispatcherFilter, cachingDispatcherFilter);
        
        return dispatcher;
    }
    
}
