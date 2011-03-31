/**
 * Copyright (C) 2009-2010 the original author or authors.
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
package org.fusesource.restygwt.client.dispatcher;

import com.google.gwt.http.client.RequestBuilder;

public class CacheKey {

    //FIXME:
    private String url;

    //FIXME:
    private String requestData;

    //FIXME:
    private String httpMethod;

    public CacheKey(RequestBuilder requestBuilder) {
        this.url = requestBuilder.getUrl();
        this.requestData = requestBuilder.getRequestData();
        this.httpMethod = requestBuilder.getHTTPMethod();

    }

    /**
     * Needed for saving in HashMap:
     */
    @Override
    public int hashCode() {
        return new String(getEverythingAsConcatenatedString()).hashCode();
    }

    /**
     * Needed for saving in HashMap:
     */
    @Override
    public boolean equals(Object anObject) {
        if (anObject instanceof CacheKey) {
            CacheKey aCacheKey = (CacheKey) anObject;
            if (aCacheKey.getEverythingAsConcatenatedString().equals(
                    getEverythingAsConcatenatedString())) {
                return true;
            }
        }

        return false;

    }

    /**
     * Little helper to get contents...
     *
     * @return
     */
    public String getEverythingAsConcatenatedString() {
        return url + requestData + httpMethod;
    }
}
