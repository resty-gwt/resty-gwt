/**
 * Copyright (c) 2011, Ecologic Analytics, LLC
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, is strictly prohibited.
 */
package org.fusesource.restygwt.client.basic;

import com.google.gwt.core.client.GWT;
import com.google.gwt.junit.client.GWTTestCase;

/**
 */
public class ExtendedRestTestGwt extends GWTTestCase {

    @Override
    public String getModuleName() {
        return "org.fusesource.restygwt.BasicTestGwt";
    }

    public void testCreateRestAction() {
        try {
            LoginService action = GWT.create(LoginService.class);
        } catch (Exception e) {
            fail(" generator failed at creating an interface which " +
                    "has an intermediate interface between it and its RestService");
        }
    }

}
