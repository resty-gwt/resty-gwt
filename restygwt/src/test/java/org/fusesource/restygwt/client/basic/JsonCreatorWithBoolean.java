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

import org.fusesource.restygwt.client.JsonEncoderDecoder;
import org.junit.Test;

import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.junit.client.GWTTestCase;

public class JsonCreatorWithBoolean extends GWTTestCase {

    @Override
    public String getModuleName() {
	return "org.fusesource.restygwt.BasicTestGwt";
    }


    public static class BooleanWithIsGetter {
        
        private boolean m_smallBoolean;
        
        private Boolean m_bigBoolean;
        
        public boolean isSmallBoolean()
        {
            return this.m_smallBoolean;
        }
        
        public void setSmallBoolean( boolean smallBoolean)
        {
            this.m_smallBoolean = smallBoolean;
        }
        
        public Boolean isBigBoolean()
        {
            return this.m_bigBoolean;
        }
        
        public void setBigBoolean( Boolean bigBoolean)
        {
            this.m_bigBoolean = bigBoolean;
        }
        
    }

    static interface BooleanWithIsGetterCodec extends JsonEncoderDecoder<BooleanWithIsGetter> {
    }

    @Test
    public void testBooleanWithIsGetter() {
        BooleanWithIsGetterCodec codec = GWT.create(BooleanWithIsGetterCodec.class);
        BooleanWithIsGetter pojoTrue = new BooleanWithIsGetter();
        pojoTrue.setSmallBoolean( true );
        pojoTrue.setBigBoolean( Boolean.TRUE );
    
        JSONValue jsonTrue = codec.encode( pojoTrue );
        BooleanWithIsGetter roundTrip = codec.decode( jsonTrue );
        assertEquals( roundTrip.isSmallBoolean(), true );
        assertEquals( roundTrip.isBigBoolean(), Boolean.TRUE );
        
        BooleanWithIsGetter pojoFalse = new BooleanWithIsGetter();
        pojoFalse.setSmallBoolean( false );
        pojoFalse.setBigBoolean( Boolean.FALSE );
        JSONValue jsonFalse = codec.encode( pojoFalse );
        roundTrip = codec.decode( jsonFalse );
        assertEquals( roundTrip.isSmallBoolean(), false );
        assertEquals( roundTrip.isBigBoolean(), Boolean.FALSE );
    }
    
    static class BooleanWithHasGetter {
        
        private boolean m_smallBoolean;
        
        private Boolean m_bigBoolean;
        
        public boolean hasSmallBoolean()
        {
            return this.m_smallBoolean;
        }
        
        public void setSmallBoolean( boolean smallBoolean)
        {
            this.m_smallBoolean = smallBoolean;
        }
        
        public Boolean hasBigBoolean()
        {
            return this.m_bigBoolean;
        }
        
        public void setBigBoolean( Boolean bigBoolean)
        {
            this.m_bigBoolean = bigBoolean;
        }
        
    }

    static interface BooleanWithHasGetterCodec extends JsonEncoderDecoder<BooleanWithHasGetter> {
    }

    @Test
    public void testBooleanWithHasGetter() {
        BooleanWithHasGetterCodec codec = GWT.create(BooleanWithHasGetterCodec.class);
        BooleanWithHasGetter pojoTrue = new BooleanWithHasGetter();
        pojoTrue.setSmallBoolean( true );
        pojoTrue.setBigBoolean( Boolean.TRUE );
    
        JSONValue jsonTrue = codec.encode( pojoTrue );
        BooleanWithHasGetter roundTrip = codec.decode( jsonTrue );
        assertEquals( roundTrip.hasSmallBoolean(), true );
        assertEquals( roundTrip.hasBigBoolean(), Boolean.TRUE );
        
        BooleanWithHasGetter pojoFalse = new BooleanWithHasGetter();
        pojoFalse.setSmallBoolean( false );
        pojoFalse.setBigBoolean( Boolean.FALSE );
        JSONValue jsonFalse = codec.encode( pojoFalse );
        roundTrip = codec.decode( jsonFalse );
        assertEquals( roundTrip.hasSmallBoolean(), false );
        assertEquals( roundTrip.hasBigBoolean(), Boolean.FALSE );
    }
}
