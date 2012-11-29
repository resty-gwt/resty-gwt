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

package org.fusesource.restygwt.client;

import java.util.Map;

import com.google.gwt.http.client.URL;

/**
 * 
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 */
public class Resource {

    public static final String CONTENT_TYPE_TEXT = "text/plain";
    public static final String CONTENT_TYPE_JSON = "application/json";
    public static final String CONTENT_TYPE_XML = "application/xml";
    public static final String CONTENT_TYPE_RSS = "application/rss+xml";
    public static final String CONTENT_TYPE_ATOM = "application/atom+xml";
    public static final String CONTENT_TYPE_FORM = "application/x-www-form-urlencoded";
    public static final String HEADER_ACCEPT = "Accept";
    public static final String HEADER_CONTENT_TYPE = "Content-Type";

    final String path;
    final String query;
    final Map<String, String> headers;
    
    public Resource(String uri) {
    	this (uri, (Map<String, String>) null);
    }
    
    public Resource(String uri, String query) {
		this(uri, query, null);
	}

    public Resource(final String uri, final Map<String, String> headers) {
        int pos = uri.indexOf('?');
        if (pos >= 0) {
            this.path = uri.substring(0, pos);
            this.query = uri.substring(pos + 1);
        } else {
        	// Strip off trailing "/" so we have a known format to work off of when concatenating paths
            this.path = uri.endsWith("/") ? uri.substring(0, uri.length() - 1) : uri;
            this.query = null;
        }
        this.headers = (headers != null) ? headers : defaultHeaders();
    }

    public Resource(final String uri, final String query, final Map<String, String> headers) {
        // Strip off trailing "/" so we have a known format to work off of when concatenating paths
    	this.path = uri.endsWith("/") ? uri.substring(0, uri.length() - 1) : uri;
        this.query = query;
        this.headers = (headers != null) ? headers : defaultHeaders();
    }    

	public Method head() {
        return new Method(this, "HEAD").headers(headers);
    }

    public Method get() {
        return new Method(this, "GET").headers(headers);
    }

    public Method put() {
        return new Method(this, "PUT").headers(headers);
    }

    public Method post() {
        return new Method(this, "POST").headers(headers);
    }

    public Method delete() {
        return new Method(this, "DELETE").headers(headers);
    }

    public Method options() {
        return new Method(this, "OPTIONS").headers(headers);
    }

    public JsonpMethod jsonp() {
        return new JsonpMethod(this);
    }

    public String getPath() {
        return path;
    }

    public String getQuery() {
        return query;
    }

    public String getUri() {
        if (query != null) {
            return path + "?" + query;
        }
        return path;
    }
    
    public Map<String, String> getHeaders() {
		return headers;
	}

    protected Map<String, String> defaultHeaders() {
        return null;
    }

    // TODO: support fancier resolutions
    public Resource resolve(String path) {

        // it might be an absolute path...
        if (path.startsWith("http:") || path.startsWith("https:") || path.startsWith("file:")) {
            return new Resource(path, this.query, this.headers);
        }

        // strip prefix / if needed...
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        return new Resource(this.path + "/" + path, this.query, this.headers);
    }

    public Resource addQueryParam(String key, String value) {
    	if(value == null) return this;
        key = URL.encodeQueryString(key);
        value = URL.encodeQueryString(value);
        String q = query == null ? "" : query + "&";
        return new Resource(path, q + key + "=" + value, headers);
    }

    public Resource addQueryParams(String key, Iterable<String> values) {
        if(values == null) return this;
        key = URL.encodeQueryString(key);
        StringBuilder q = new StringBuilder(query == null ? "" : query + "&");
        boolean ampersand = false;
        for (String value : values) {
          if(value == null) continue;
          if (ampersand) {
            q.append('&');
          } else {
            ampersand = true;
          }
          value = URL.encodeQueryString(value);
          q.append(key).append("=").append(value);
        }

        return new Resource(path, q.toString(), headers);
    }


}
