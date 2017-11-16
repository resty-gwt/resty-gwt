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


import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.shared.TimeZone;

import org.fusesource.restygwt.client.dispatcher.DefaultDispatcher;

/**
 * Provides ability to set the default date format and service root (defaults to
 * GWT.getModuleBaseURL()).
 *
 *
 * @author <a href="http://www.acuedo.com">Dave Finch</a>
 *
 */
public class Defaults {

    private static Dispatcher dispatcher = DefaultDispatcher.INSTANCE;

    private static String serviceRoot = GWT.getModuleBaseURL();
    private static String dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
    private static boolean dateFormatHasTimeZone = true;
    private static TimeZone timeZone = null;
    private static boolean ignoreJsonNulls = false;
    private static boolean addXHttpMethodOverrideHeader = true;
    // patch TNY: timeout ms,
    // if >-1, used in Method class to set timeout
    private static int requestTimeout = -1;
    private static boolean byteArraysToBase64 = false;

    private static ExceptionMapper exceptionMapper = new ExceptionMapper();

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

    /**
     * Gets the format used when encoding and decoding Dates. Defaults to
     * {@code "yyyy-MM-dd'T'HH:mm:ss.SSSZ"}. If the date format is set to
     * {@code null}, dates will be encoded and decoded as timestamps
     * (the number of milliseconds since the Unix epoch, 1970-01-01). 
     *
     * @return the date format string
     * @see com.google.gwt.i18n.shared.DateTimeFormat DateTimeFormat
     */
    public static String getDateFormat() {
        return dateFormat;
    }

    /**
     * Sets the format used when encoding and decoding Dates. If the date
     * format is set to {@code null}, dates will be encoded and decoded as
     * timestamps (the number of milliseconds since the Unix epoch,
     * 1970-01-01). 
     *
     * @param dateFormat the date format string
     * @see com.google.gwt.i18n.shared.DateTimeFormat DateTimeFormat
     */
    public static void setDateFormat(String dateFormat) {
        Defaults.dateFormat = dateFormat;
        dateFormatHasTimeZone = false;

        if (dateFormat != null) {
            for (int i = 0; i < dateFormat.length(); i++) {
                char ch = dateFormat.charAt(i);

                if (ch == 'Z' || ch == 'z' || ch == 'V' || ch == 'v') {
                    dateFormatHasTimeZone = true;
                    break;
                }
            }
        }
    }

    /* package */
    static boolean dateFormatHasTimeZone() {
        return dateFormatHasTimeZone;
    }

    /**
     * Gets the timezone used when encoding and decoding Dates.
     * <p>
     * The timezone is only taken into consideration if the date format string
     * does not contain a timezone field. If the timezone is set to
     * {@code null}, the browser's default (local) timezone will be used.
     *
     * @return the date format timezone (null for local timezone)
     */
    public static TimeZone getTimeZone() {
        return timeZone;
    }

    /**
     * Gets the timezone used when encoding and decoding Dates.
     * <p>
     * The timezone is only taken into consideration if the date format string
     * does not contain a timezone field. If the timezone is set to null, the
     * browser's default (local) timezone will be used.
     *
     * @param timeZone the new timezone (use null for local timezone)
     */
    public static void setTimeZone(TimeZone timeZone) {
        Defaults.timeZone = timeZone;
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
     * @return the byteArraysToBase64
     */
    public static boolean isByteArraysToBase64() {
        return byteArraysToBase64;
    }

    /**
     * @param byteArraysToBase64 the byteArraysToBase64 to set
     */
    public static void setByteArraysToBase64(boolean byteArraysToBase64) {
        Defaults.byteArraysToBase64 = byteArraysToBase64;
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

    /**
     * Gets the default ExceptionMapper
     * @return
     */
    public static ExceptionMapper getExceptionMapper() {
        return exceptionMapper;
    }

    /**
     * Sets the default ExceptionMapper
     * @param exceptionMapper the new ExceptionMapper to be used by all requests
     */
    public static void setExceptionMapper(ExceptionMapper exceptionMapper) {
        Defaults.exceptionMapper = exceptionMapper;
    }

    /**
     * If true, the 'X-HTTP-Method-Override' header is set on each request.
     * @return
     */
    public static boolean isAddXHttpMethodOverrideHeader() {
        return addXHttpMethodOverrideHeader;
    }

    /**
     * If true, the 'X-HTTP-Method-Override' header is set on each request. Default is true.
     * @param addXHttpMethodOverrideHeader
     */
    public static void setAddXHttpMethodOverrideHeader(boolean addXHttpMethodOverrideHeader) {
        Defaults.addXHttpMethodOverrideHeader = addXHttpMethodOverrideHeader;
    }
}
