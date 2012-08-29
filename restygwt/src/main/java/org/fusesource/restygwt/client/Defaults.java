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


import org.fusesource.restygwt.client.dispatcher.DefaultDispatcher;

import com.google.gwt.core.client.GWT;

/**
 * Provides ability to set the default date format and service root (defaults to
 * GWT.getModuleBaseURL()).
 *
 *
 * @author <a href="http://www.acuedo.com">Dave Finch</a>
 *
 */
public class Defaults {

    public static Dispatcher dispatcher = DefaultDispatcher.INSTANCE;

    private static String serviceRoot = GWT.getModuleBaseURL();
    private static String dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
    private static boolean ignoreJsonNulls = false;
    // patch TNY: timeout ms,
    // if >-1, used in Method class to set timeout
    private static int requestTimeout = -1;


    public static String getServiceRoot() {
        return serviceRoot;
    }

    /**
     * sets the URL prepended to the value of Path annotations.
     *
     * @param serviceRoot
     */
    public static void setServiceRoot(String serviceRoot) {
        // GWT.getModuleBaseURL() is guaranteed to end with a slash, so should any custom service root
        if (!serviceRoot.endsWith("/")) {
            serviceRoot += "/";
        }
        Defaults.serviceRoot = serviceRoot;
    }
    
    public static String getDateFormat() {
        return dateFormat;
    }

    /**
     * Sets the format used when encoding and decoding Dates.
     *
     * @param dateFormat
     */
    public static void setDateFormat(String dateFormat) {
        Defaults.dateFormat = dateFormat;
    }

    /**
     * Indicates whether or not nulls will be ignored during JSON marshalling.
     */
    public static boolean doesIgnoreJsonNulls() {
        return ignoreJsonNulls;
    }

    public static void ignoreJsonNulls() {
        ignoreJsonNulls = true;
    }

    public static void dontIgnoreJsonNulls() {
        ignoreJsonNulls = false;
    }

    public static final int getRequestTimeout() {
        return requestTimeout;
    }

    public static final void setRequestTimeout(int requestTimeout) {
        Defaults.requestTimeout = requestTimeout;
    }

    /**
     * Sets the default dispatcher used by Method instances.
     *
     * @param value
     */
    public static void setDispatcher(Dispatcher value) {
        dispatcher = value;
    }

    /**
     * Returns the default dispatcher.
     *
     * @return
     */
    public static Dispatcher getDispatcher() {
        return dispatcher;
    }
}
