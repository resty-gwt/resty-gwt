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

import com.google.gwt.json.client.JSONValue;

/**
 * 
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 */
public interface JsonEncoderDecoder<T> {

    @SuppressWarnings("serial")
    public static class EncodingException extends RuntimeException {
        public EncodingException(String msg) {
            super(msg);
        }
    }

    @SuppressWarnings("serial")
    public static class DecodingException extends RuntimeException {
        public DecodingException(String msg) {
            super(msg);
        }
    }

    public JSONValue encode(T value) throws EncodingException;

    public T decode(JSONValue value) throws DecodingException;

    public T decode(String value) throws DecodingException;
}
