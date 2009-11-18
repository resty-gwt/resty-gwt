/**
 * Copyright (C) 2009  Hiram Chirino
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *    http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hiramchirino.restygwt.client;

import java.util.Map;

import com.google.gwt.http.client.URL;

/**
 * 
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 */
public class Resource {
	public static final String CONTENT_TYPE_TEXT = "plain/text";
	public static final String CONTENT_TYPE_JSON = "application/json";
	public static final String CONTENT_TYPE_XML = "application/xml";
	public static final String CONTENT_TYPE_RSS = "application/rss+xml";
	public static final String CONTENT_TYPE_ATOM = "application/atom+xml";
	
	public static final String HEADER_ACCEPT = "Accept";
	public static final String HEADER_CONTENT_TYPE = "Content-Type";
		
	String uri;
	String query;
	
	private Map<String, String> headers = defaultHeaders();
	
	public Resource(String uri) {
		int pos = uri.indexOf('?');
		if( pos>=0 ) {
            this.uri = uri.substring(0,pos); 
            this.query = uri.substring(pos+1);
		} else {
	        this.uri = uri;
		}
	}
	
    public Resource(String uri, String query) {
        this.uri = uri; 
        this.query = query;
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

	public String getUri() {
		return uri;
	}
	
    public String getQuery() {
        return query;
    }
	
	protected Map<String, String> defaultHeaders() {
		return null;
	}

	// TODO: support fancier resolutions
    public Resource resolve(String path) {
        return new Resource(uri+path);
    }
    
    public Resource addQueryParam(String key, String value) {
        key = URL.encodeComponent(key);
        value = URL.encodeComponent(value);
        String q = query==null ? "" : query+"&";
        return new Resource(uri, q + key+"="+value);
    }
    
}
