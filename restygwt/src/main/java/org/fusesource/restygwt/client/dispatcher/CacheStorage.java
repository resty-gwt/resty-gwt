package org.fusesource.restygwt.client.dispatcher;

import java.util.HashMap;

import com.google.gwt.http.client.Response;

public class CacheStorage {

    private static HashMap<CacheKey, Response> cache = new HashMap<CacheKey, Response>();

    // private static HashMap<Integer, ArrayList<AsyncCallback<Integer>>>
    // pendingCallbacks
    // = new HashMap<Integer, ArrayList<AsyncCallback<Integer>>>();

    public static Response getResultOrReturnNull(CacheKey key) {

        Response val = cache.get(key);

        return val;

    }

    public static void putResult(CacheKey key, Response response) {

        cache.put(key, response);

    }

}
