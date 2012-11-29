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

	@SuppressWarnings("unused")
        private transient String code;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ExampleDto that = (ExampleDto) o;

        if (code != null ? !code.equals(that.code) : that.code != null) return false;
        if (complexMap1 != null ? !complexMap1.equals(that.complexMap1) : that.complexMap1 != null) return false;
        if (complexMap10 != null ? !complexMap10.equals(that.complexMap10) : that.complexMap10 != null) return false;
        if (complexMap11 != null ? !complexMap11.equals(that.complexMap11) : that.complexMap11 != null) return false;
        if (complexMap2 != null ? !complexMap2.equals(that.complexMap2) : that.complexMap2 != null) return false;
        if (complexMap3 != null ? !complexMap3.equals(that.complexMap3) : that.complexMap3 != null) return false;
        if (complexMap4 != null ? !complexMap4.equals(that.complexMap4) : that.complexMap4 != null) return false;
        if (complexMap5 != null ? !complexMap5.equals(that.complexMap5) : that.complexMap5 != null) return false;
        if (complexMap7 != null ? !complexMap7.equals(that.complexMap7) : that.complexMap7 != null) return false;
        if (complexMap8 != null ? !complexMap8.equals(that.complexMap8) : that.complexMap8 != null) return false;
        if (complexMap9 != null ? !complexMap9.equals(that.complexMap9) : that.complexMap9 != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (complexMap1 != null ? complexMap1.hashCode() : 0);
        result = 31 * result + (complexMap2 != null ? complexMap2.hashCode() : 0);
        result = 31 * result + (complexMap3 != null ? complexMap3.hashCode() : 0);
        result = 31 * result + (complexMap4 != null ? complexMap4.hashCode() : 0);
        result = 31 * result + (complexMap5 != null ? complexMap5.hashCode() : 0);
        result = 31 * result + (complexMap7 != null ? complexMap7.hashCode() : 0);
        result = 31 * result + (complexMap8 != null ? complexMap8.hashCode() : 0);
        result = 31 * result + (complexMap9 != null ? complexMap9.hashCode() : 0);
        result = 31 * result + (complexMap10 != null ? complexMap10.hashCode() : 0);
        result = 31 * result + (complexMap11 != null ? complexMap11.hashCode() : 0);
        result = 31 * result + (code != null ? code.hashCode() : 0);
        return result;
    }
}
