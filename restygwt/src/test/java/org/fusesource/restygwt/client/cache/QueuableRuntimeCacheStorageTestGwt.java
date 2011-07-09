package org.fusesource.restygwt.client.cache;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gwt.http.client.Header;
import com.google.gwt.http.client.Response;
import com.google.gwt.junit.client.GWTTestCase;

/**
 * test caching behaviour. this seems to be necessary in a gwttestcase due to
 * the timer class in {@link QueuableRuntimeCacheStorageTestGwt}
 *
 * @author abalke
 */
public class QueuableRuntimeCacheStorageTestGwt extends GWTTestCase {

    @Override
    public String getModuleName() {
        return "org.fusesource.restygwt.BasicTestGwt";
    }

    /**
     * just check if there is a response if we push it to the cache
     * without a scope
     */
    public void testSimpleUnscroped() {
        QueuableRuntimeCacheStorage_Adv cache = factory();
        Response response = new AutoResponse();
        CacheKey key = new SimpleCacheKey("testing");

        cache.putResult(key, response);
        // when we push a result, there must be a cached entry too
        assertEquals(response, cache.getResultOrReturnNull(key));

        cache.purge();
        // after purge not...
        assertEquals(null, cache.getResultOrReturnNull(key));
    }

    /**
     * just check if there is a response if we push it to the cache
     * with a scope
     */
    public void testSimpleScroped() {
        QueuableRuntimeCacheStorage_Adv cache = factory();
        Response response = new AutoResponse();
        CacheKey key = new SimpleCacheKey("testing");

        cache.putResult(key, response, "foo");
        // when we push a result, there must be a cached entry too
        assertEquals(response, cache.getResultOrReturnNull(key));

        assertEquals(1, cache.getScopeFromKey(key).length);
        assertEquals("foo", cache.getScopeFromKey(key)[0]);

        // purge another scope
        cache.purge("bar");
        // the result must still be there
        assertEquals(response, cache.getResultOrReturnNull(key));

        cache.purge("foo");
        // after purge not...
        assertEquals(null, cache.getResultOrReturnNull(key));
    }

    /**
     * check what happens if we use more than one scope
     */
    public void testDoubleScroped() {
        QueuableRuntimeCacheStorage_Adv cache = factory();
        Response response = new AutoResponse();
        CacheKey key = new SimpleCacheKey("testing");

        cache.putResult(key, response, new String[]{"foo", "fii"});

        // when we push a result, there must be a cached entry too
        assertEquals(response, cache.getResultOrReturnNull(key));

        String[] scopeFromKey = cache.getScopeFromKey(key);
        assertEquals(2, scopeFromKey.length);
        List<String> scopes = Arrays.asList(scopeFromKey);
        assertTrue(scopes.contains("foo"));
        assertTrue(scopes.contains("fii"));

        // purge another scope
        cache.purge("aaa");
        // the result must still be there
        assertEquals(response, cache.getResultOrReturnNull(key));

        cache.purge("foo");
        assertEquals(null, cache.getScopeFromKey(key));

        // after purge not...
        assertEquals(null, cache.getResultOrReturnNull(key));
    }



    /**
     * create a new instance for local usage
     *
     * @return
     */
    private QueuableRuntimeCacheStorage_Adv factory() {
        return new QueuableRuntimeCacheStorage_Adv();
    }

    private class QueuableRuntimeCacheStorage_Adv extends QueuableRuntimeCacheStorage {

        /**
         * only for testing: check the scope of a cachekey
         *
         * @return
         */
        public String[] getScopeFromKey(CacheKey k) {
            if(null == getResultOrReturnNull(k)) {
                return null;
            }

            List<String> ret = new ArrayList<String>();

            for (String scope : scopeRef.keySet()) {
                for (CacheKey c : scopeRef.get(scope)) {
                    if (c == k) {
                        ret.add(scope);
                    }
                }
            }
            return ret.toArray(new String[ret.size()]);
        }
    }

    /**
     * convenience response class to minimize the need for overriding
     * methods.
     *
     * @author abalke
     *
     */
    private class AutoResponse extends Response {

        @Override
        public String getHeader(String header) {
            return null;
        }

        @Override
        public Header[] getHeaders() {
            Header[] h = {
                new Header() {
                    @Override
                    public String getName() {
                      return "Name_Of_The_Header";
                    }

                    @Override
                    public String getValue() {
                      return "SomeValue";
                    }

                    @Override
                    public String toString() {
                      return getName() + " : " + getValue();
                    }
                }
            };

            return h;
        }

        @Override
        public String getHeadersAsString() {
            StringBuilder sb = new StringBuilder();

            for (Header h: getHeaders()) {
                sb.append(h.toString()).append("\n");
            }
            return sb.toString();
        }

        @Override
        public int getStatusCode() {
            return 200;
        }

        @Override
        public String getStatusText() {
            return "OK";
        }

        @Override
        public String getText() {
            return "DummyResponse";
        }
    }
}
