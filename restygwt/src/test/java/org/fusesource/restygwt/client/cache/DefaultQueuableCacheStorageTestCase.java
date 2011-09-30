/**
 * Copyright (C) 2009-2011 the original author or authors.
 * See the notice.md file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.fusesource.restygwt.client.cache;

import junit.framework.TestCase;

import org.easymock.EasyMock;
import org.fusesource.restygwt.client.cache.DefaultQueueableCacheStorage.ResponseWrapper;

import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.junit.GWTMockUtilities;


public class DefaultQueuableCacheStorageTestCase extends TestCase {

    private DefaultQueueableCacheStorage storage;
    
    protected void setUp() throws Exception{
        super.setUp();
        GWTMockUtilities.disarm();

        this.storage = new DefaultQueueableCacheStorage();
    }
    
    protected void tearDown() {
        GWTMockUtilities.restore();
    }
    
    public void testDefaultScope(){
        CacheKey key = new SimpleCacheKey("first");
        CacheKey secondKey = new SimpleCacheKey("second");
        Response resp = EasyMock.createMock(Response.class);
        EasyMock.replay(resp);
          
        storage.putResult(key, resp);
        storage.putResult(secondKey, resp);
        
        assertNull(storage.getResultOrReturnNull(new SimpleCacheKey("unknown")));
        assertEquals(resp, ((ResponseWrapper)storage.getResultOrReturnNull(key)).response);
        assertEquals(resp, ((ResponseWrapper)storage.getResultOrReturnNull(secondKey)).response);
        
        storage.remove(key);
        assertNull(storage.getResultOrReturnNull(key));
        assertEquals(resp, ((ResponseWrapper)storage.getResultOrReturnNull(secondKey)).response);
        
        // now purge
        storage.purge();
        assertNull(storage.getResultOrReturnNull(key));
        assertNull(storage.getResultOrReturnNull(secondKey));
        
        EasyMock.verify(resp);
    }

    public void testScope(){
        CacheKey key = new SimpleCacheKey("first");
        Response resp = EasyMock.createMock(Response.class);
        EasyMock.replay(resp);
          
        storage.putResult(key, resp);
        
        String scope = "admin";
        CacheKey scopedKey = new SimpleCacheKey("first scoped");
        CacheKey secondScopedKey = new SimpleCacheKey("second scoped");
        Response scopedResp = EasyMock.createMock(Response.class);
        EasyMock.replay(scopedResp);

        storage.putResult(scopedKey, scopedResp, scope);
        storage.putResult(secondScopedKey, scopedResp, scope);
        
        // check the cache content
        assertNull(storage.getResultOrReturnNull(new SimpleCacheKey("unknown")));
        assertNull(storage.getResultOrReturnNull(new SimpleCacheKey("unknown"), scope));
        assertNull(storage.getResultOrReturnNull(key, scope));
        assertEquals(resp, ((ResponseWrapper)storage.getResultOrReturnNull(key)).response);
        assertEquals(scopedResp, ((ResponseWrapper)storage.getResultOrReturnNull(scopedKey, scope)).response);
        assertEquals(scopedResp, ((ResponseWrapper)storage.getResultOrReturnNull(secondScopedKey, scope)).response);

        // wrong key shall leave things as they are
        storage.remove(key, scope);
        assertEquals(resp, ((ResponseWrapper)storage.getResultOrReturnNull(key)).response);
        assertEquals(scopedResp, ((ResponseWrapper)storage.getResultOrReturnNull(scopedKey, scope)).response);

        // remove scoped key and leave unscope cache as it is
        storage.remove(scopedKey, scope);
        assertNull(storage.getResultOrReturnNull(scopedKey, scope));
        assertEquals(scopedResp, ((ResponseWrapper)storage.getResultOrReturnNull(secondScopedKey, scope)).response);
        assertEquals(resp, ((ResponseWrapper)storage.getResultOrReturnNull(key)).response);
        
        // now purge
        storage.purge(scope);
        assertNull(storage.getResultOrReturnNull(scopedKey, scope));
        assertNull(storage.getResultOrReturnNull(secondScopedKey, scope));
        assertEquals(resp, ((ResponseWrapper)storage.getResultOrReturnNull(key)).response);
        
        EasyMock.verify(resp);
        EasyMock.verify(scopedResp);
    }
    
    public void testQueue() {
        CacheKey key = new SimpleCacheKey("first");
        CacheKey secondKey = new SimpleCacheKey("second");
        
        assertFalse(storage.hasCallback(key));
        
        RequestCallback rc1 = EasyMock.createMock(RequestCallback.class);
        RequestCallback rc2 = EasyMock.createMock(RequestCallback.class);
        EasyMock.replay(rc1);
        EasyMock.replay(rc2);
        
        storage.addCallback(key, rc1);
        assertTrue(storage.hasCallback(key));
        assertFalse(storage.hasCallback(secondKey));
        
        storage.addCallback(key, rc2);
        assertTrue(storage.hasCallback(key));
        assertFalse(storage.hasCallback(secondKey));

        assertEquals(2, storage.removeCallbacks(key).size());

        assertFalse(storage.hasCallback(key));
        assertFalse(storage.hasCallback(secondKey));

        EasyMock.verify(rc1);
        EasyMock.verify(rc2);
    }
    
    public void testRestyHeader(){
        CacheKey key = new SimpleCacheKey("first");
        Response resp = EasyMock.createMock(Response.class);
        EasyMock.replay(resp);
        
        storage.putResult(key, resp);
        
        assertNotNull(storage.getResultOrReturnNull(key).getHeader(QueueableCacheStorage.RESTY_CACHE_HEADER));
        
        EasyMock.verify(resp);
    }
}
