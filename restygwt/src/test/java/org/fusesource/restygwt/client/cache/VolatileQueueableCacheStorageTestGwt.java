/**
 * Copyright (C) 2009-2012 the original author or authors.
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

import com.google.gwt.http.client.Header;
import com.google.gwt.http.client.Response;
import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.user.client.Timer;


public class VolatileQueueableCacheStorageTestGwt extends GWTTestCase {

    static class ResponseMock extends Response {

        @Override
        public String getHeader(String header) {
            return null;
        }

        @Override
        public Header[] getHeaders() {
            return null;
        }

        @Override
        public String getHeadersAsString() {
            return null;
        }

        @Override
        public int getStatusCode() {
            return 0;
        }

        @Override
        public String getStatusText() {
            return null;
        }

        @Override
        public String getText() {
            return null;
        }

    }

    @Override
    public String getModuleName() {
        return "org.fusesource.restygwt.VolatileQueueableCacheStorageTestGwt";
    }

    public void testTimeout() throws Exception {
        final VolatileQueueableCacheStorage storage = new VolatileQueueableCacheStorage(100);
        final CacheKey key = new SimpleCacheKey("first");
        Response resp = new ResponseMock();

        storage.putResult(key, resp);
        // hashCode should be good enough
        assertEquals(resp.hashCode(), storage.getResultOrReturnNull(key).hashCode());
        Timer timer = new Timer() {

            @Override
            public void run() {
                assertNull(storage.getResultOrReturnNull(key));
                finishTest();
            }

        };
        timer.schedule(200);
        delayTestFinish(250);
    }

    public void testPurge() throws Exception {
        final VolatileQueueableCacheStorage storage = new VolatileQueueableCacheStorage();
        final CacheKey key = new SimpleCacheKey("first");
        Response resp = new ResponseMock();

        storage.putResult(key, resp);
        // hashCode should be good enough
        assertEquals(resp.hashCode(), storage.getResultOrReturnNull(key).hashCode());
        storage.purge();
        assertNull(storage.getResultOrReturnNull(key));
    }
}
