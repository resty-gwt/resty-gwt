/**
 * Copyright (C) 2010, Progress Software Corporation and/or its 
 * subsidiaries or affiliates.  All rights reserved.
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

    private static String serviceRoot = GWT.getModuleBaseURL();
    private static String dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
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

    public static final int getRequestTimeout() {
        return requestTimeout;
    }

    public static final void setRequestTimeout(int requestTimeout) {
        Defaults.requestTimeout = requestTimeout;
    }

}
