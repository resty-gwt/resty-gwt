package org.fusesource.restygwt.client;

import java.util.List;

import com.google.gwt.json.client.JSONValue;

public abstract class AbstractNestedJsonEncoderDecoder<E, F> extends AbstractJsonEncoderDecoder<E> {

    protected AbstractJsonEncoderDecoder<F> nested;
    
    AbstractNestedJsonEncoderDecoder( AbstractJsonEncoderDecoder<F> encoder ){
        this.nested = encoder;
    }
    
    static public <T> AbstractJsonEncoderDecoder<List<T>> listEncoderDecoder(AbstractJsonEncoderDecoder<T> encoder){
        return new AbstractNestedJsonEncoderDecoder<List<T>, T>( encoder ) {
            
            @Override
            public JSONValue encode(List<T> value)
                    throws EncodingException {
                return toJSON(value, nested);
            }

            @Override
            public List<T> decode(JSONValue value)
                    throws DecodingException {
                return toList(value, nested);
            }
        };
    }        
}