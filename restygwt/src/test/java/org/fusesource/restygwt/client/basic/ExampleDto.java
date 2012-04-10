/**
 * Copyright (C) 2009-2011 the original author or authors.
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

package org.fusesource.restygwt.client.basic;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;

public class ExampleDto {

	public String name;
	public Map<Integer, String> complexMap1;
	public Map<String, String> complexMap2;
	public Map<Long, String> complexMap3;
	public Map<Boolean, String> complexMap4;
	public Map<Double, String> complexMap5;
	public Map<Float, String> complexMap7;
	public Map<Byte, String> complexMap8;
	public Map<BigDecimal, String> complexMap9;
	public Map<BigInteger, String> complexMap10;
	public Map<Character, String> complexMap11;

	private transient String code;
}
