package org.fusesource.restygwt.example.client.dispatcher;

import org.fusesource.restygwt.client.Dispatcher;
import org.fusesource.restygwt.client.cache.VolatileQueueableCacheStorage;
import org.fusesource.restygwt.client.cache.DefaultQueueableCacheStorage;
import org.fusesource.restygwt.client.cache.QueueableCacheStorage;
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
import org.fusesource.restygwt.client.dispatcher.RestfulCachingDispatcherFilter;
import org.fusesource.restygwt.client.dispatcher.XSSTokenDispatcherFilter;

public class DispatcherFactory {

    Dispatcher xssProtectionDispatcher(){
        XSSToken xss = new XSSToken();

        DispatcherFilter xssDispatcherFilter = new XSSTokenDispatcherFilter(xss);
        
        CallbackFilter xssCallbackFilter = new XSSTokenCallbackFilter(xss);
        CallbackFactory callbackFactory = new DefaultCallbackFactory(xssCallbackFilter);
        DispatcherFilter defaultDispatcherFilter = new DefaultDispatcherFilter(callbackFactory);
        
        Dispatcher dispatcher = new DefaultFilterawareDispatcher(xssDispatcherFilter, defaultDispatcherFilter);
        
        return dispatcher;
    }
    
    Dispatcher cachingDispatcher(){
        QueueableCacheStorage cache = new DefaultQueueableCacheStorage();
        
        CallbackFilter cachingCallbackFilter = new CachingCallbackFilter(cache);
        CallbackFactory callbackFactory = new DefaultCallbackFactory(cachingCallbackFilter);
        DispatcherFilter cachingDispatcherFilter = new CachingDispatcherFilter(cache,callbackFactory);
        
        Dispatcher dispatcher = new DefaultFilterawareDispatcher(cachingDispatcherFilter);
        
        return dispatcher;
    }
    
    Dispatcher retryingDispatcher(){
        QueueableCacheStorage cache = new VolatileQueueableCacheStorage();
        
        CallbackFilter cachingCallbackFilter = new CachingCallbackFilter(cache);
        CallbackFactory callbackFactory = new RetryingCallbackFactory(cachingCallbackFilter);
        DispatcherFilter cachingDispatcherFilter = new CachingDispatcherFilter(cache, callbackFactory);
        
        Dispatcher dispatcher = new DefaultFilterawareDispatcher(cachingDispatcherFilter);
        
        return dispatcher;
    }
    
    Dispatcher retryingCachingDispatcher(){
        QueueableCacheStorage cache = new DefaultQueueableCacheStorage();
        
        CallbackFilter cachingCallbackFilter = new CachingCallbackFilter(cache);
        CallbackFactory callbackFactory = new RetryingCallbackFactory(cachingCallbackFilter);
        DispatcherFilter cachingDispatcherFilter = new CachingDispatcherFilter(cache, callbackFactory);
        
        Dispatcher dispatcher = new DefaultFilterawareDispatcher(cachingDispatcherFilter);
        
        return dispatcher;
    }

    Dispatcher retryingCachingXSSProtectionDispatcher(){
        XSSToken xss = new XSSToken();

        DispatcherFilter xssDispatcherFilter = new XSSTokenDispatcherFilter(xss);
        
        CallbackFilter xssCallbackFilter = new XSSTokenCallbackFilter(xss);

        QueueableCacheStorage cache = new DefaultQueueableCacheStorage();
        
        CallbackFilter cachingCallbackFilter = new CachingCallbackFilter(cache);
        CallbackFactory callbackFactory = new RetryingCallbackFactory(xssCallbackFilter, cachingCallbackFilter);
        DispatcherFilter cachingDispatcherFilter = new CachingDispatcherFilter(cache, callbackFactory);
        
        Dispatcher dispatcher = new DefaultFilterawareDispatcher(xssDispatcherFilter, cachingDispatcherFilter);
        
        return dispatcher;
    }
    
    Dispatcher restfulCachingDispatcher(){
        QueueableCacheStorage cache = new DefaultQueueableCacheStorage();
        
        CallbackFilter cachingCallbackFilter = new RestfulCachingCallbackFilter(cache);
        CallbackFactory callbackFactory = new DefaultCallbackFactory(cachingCallbackFilter);
        DispatcherFilter cachingDispatcherFilter = new RestfulCachingDispatcherFilter(cache, callbackFactory);
        
        Dispatcher dispatcher = new DefaultFilterawareDispatcher(cachingDispatcherFilter);
        
        return dispatcher;
    }

    Dispatcher restfulRetryingCachingDispatcher(){
        QueueableCacheStorage cache = new DefaultQueueableCacheStorage();
        
        CallbackFilter cachingCallbackFilter = new RestfulCachingCallbackFilter(cache);
        CallbackFactory callbackFactory = new RetryingCallbackFactory(cachingCallbackFilter);
        DispatcherFilter cachingDispatcherFilter = new RestfulCachingDispatcherFilter(cache, callbackFactory);
        
        Dispatcher dispatcher = new DefaultFilterawareDispatcher(cachingDispatcherFilter);
        
        return dispatcher;
    }
    
    Dispatcher restfulRetryingCachingXSSProtectionDispatcher(){
        XSSToken xss = new XSSToken();

        DispatcherFilter xssDispatcherFilter = new XSSTokenDispatcherFilter(xss);
        
        CallbackFilter xssCallbackFilter = new XSSTokenCallbackFilter(xss);

        QueueableCacheStorage cache = new DefaultQueueableCacheStorage();
        
        CallbackFilter cachingCallbackFilter = new RestfulCachingCallbackFilter(cache);
        CallbackFactory callbackFactory = new RetryingCallbackFactory(xssCallbackFilter, cachingCallbackFilter);
        DispatcherFilter cachingDispatcherFilter = new RestfulCachingDispatcherFilter(cache, callbackFactory);
        
        Dispatcher dispatcher = new DefaultFilterawareDispatcher(xssDispatcherFilter, cachingDispatcherFilter);
        
        return dispatcher;
    }
    
}
