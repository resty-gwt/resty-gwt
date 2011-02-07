/**
 * Copyright (C) 2011 the original author or authors.
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
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, is strictly prohibited.
 */
package org.fusesource.restygwt.client.complex;

import com.google.gwt.core.client.GWT;
import com.google.gwt.junit.client.GWTTestCase;

import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

/**
 * @author Jeff Larsen
 */
public class BigNumberTestGwt extends GWTTestCase {
    public static final BigDecimal bd4 = new BigDecimal("12345.6789");
    public static final BigDecimal bd2 = new BigDecimal("1");
    public static final BigDecimal bd3 = new BigDecimal("1234567890");
    public static final BigDecimal bd1 = new BigDecimal("1234567890000000000000");

    public static final BigInteger bi1 = new BigInteger("1234567890");
    public static final BigInteger bi2 = new BigInteger("1234567890000000000000");
    public static final BigInteger bi3 = new BigInteger("3");

    @Override
    public String getModuleName() {
        return "org.fusesource.restygwt.BigDecimalTestGwt";
    }

    public void testBigDecimal() {
        BigDecimalDto dto = new BigDecimalDto();
        dto.getDecimals().add(bd1);
        dto.getDecimals().add(bd2);
        dto.getDecimals().add(bd3);
        dto.getDecimals().add(bd4);

        BigDecimalDtoAction action = GWT.create(BigDecimalDtoAction.class);
        action.send(dto, new MethodCallback<BigDecimalDto>() {

            @Override
            public void onSuccess(Method method, BigDecimalDto response) {
                List<BigDecimal> decimals = response.getDecimals();
                assertEquals(decimals.get(0), bd4);
                assertEquals(decimals.get(1), bd3);
                assertEquals(decimals.get(2), bd2);
                assertEquals(bd1.toPlainString(), decimals.get(3).toPlainString());
                finishTest();
            }

            @Override
            public void onFailure(Method method, Throwable exception) {
                fail("Servlet call failed");
            }
        });

        delayTestFinish(10000);

    }

    public void testBigInteger() {
        BigIntegerDto dto = new BigIntegerDto();
        dto.getInts().add(bi1);
        dto.getInts().add(bi2);
        dto.getInts().add(bi3);

        BigIntegerDtoAction action = GWT.create(BigIntegerDtoAction.class);
        action.send(dto, new MethodCallback<BigIntegerDto>() {

            @Override
            public void onFailure(Method method, Throwable exception) {
                fail("Servelt call failed");
            }

            @Override
            public void onSuccess(Method method, BigIntegerDto response) {
                List<BigInteger> ints = response.getInts();
                assertEquals(bi3, ints.get(0));
                assertEquals(bi2, ints.get(1));
                assertEquals(bi1, ints.get(2));
                finishTest();
            }

        });
        delayTestFinish(10000);
    }

}
