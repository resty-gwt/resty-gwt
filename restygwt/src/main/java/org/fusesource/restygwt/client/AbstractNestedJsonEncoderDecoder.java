package org.fusesource.restygwt.client;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.fusesource.restygwt.client.Json.Style;

import com.google.gwt.json.client.JSONValue;

public abstract class AbstractNestedJsonEncoderDecoder<E, F, G> extends AbstractJsonEncoderDecoder<E> {

    protected AbstractJsonEncoderDecoder<F> nested;
    protected AbstractJsonEncoderDecoder<G> second;
    protected Style style;
    
    AbstractNestedJsonEncoderDecoder( AbstractJsonEncoderDecoder<F> encoder ){
        this.nested = encoder;
    }
    
    AbstractNestedJsonEncoderDecoder( AbstractJsonEncoderDecoder<F> encoder, Style style ){
        this.nested = encoder;
        this.style = style;
    }

    AbstractNestedJsonEncoderDecoder( AbstractJsonEncoderDecoder<F> keyEncoder, AbstractJsonEncoderDecoder<G> valueEncoder, Style style ){
        this.nested = keyEncoder;
        this.second = valueEncoder;
        this.style = style;
    }

    static public <T> AbstractJsonEncoderDecoder<T[]> arrayEncoderDecoder(AbstractJsonEncoderDecoder<T> encoder){
        return new AbstractNestedJsonEncoderDecoder<T[], T, Void>( encoder ) {
            
            @Override
            public JSONValue encode(T[] value)
                    throws EncodingException {
                return toJSON(value, nested);
            }

            @SuppressWarnings("unchecked")
            @Override
            public T[] decode(JSONValue value)
                    throws DecodingException {
                return toArray(value, nested, (T[]) new Object[ asArray( value ).size() ]);
            }
        };
    }
    
    static public <T> AbstractJsonEncoderDecoder<List<T>> listEncoderDecoder(AbstractJsonEncoderDecoder<T> encoder){
        return new AbstractNestedJsonEncoderDecoder<List<T>, T, Void>( encoder ) {
            
            @SuppressWarnings("unchecked")
            @Override
            public JSONValue encode(List<T> value)
                    throws EncodingException {
                return toJSON((T[])value.toArray(), nested);
            }

            @Override
            public List<T> decode(JSONValue value)
                    throws DecodingException {
                return toList(value, nested);
            }
        };
    }

    static public <T> AbstractJsonEncoderDecoder<Set<T>> setEncoderDecoder(AbstractJsonEncoderDecoder<T> encoder){
        return new AbstractNestedJsonEncoderDecoder<Set<T>, T, Void>( encoder ) {
            
            @Override
            public JSONValue encode(Set<T> value)
                    throws EncodingException {
                return toJSON(value, nested);
            }

            @Override
            public Set<T> decode(JSONValue value)
                    throws DecodingException {
                return toSet(value, nested);
            }
        };
    }        

    static public <T> AbstractJsonEncoderDecoder<Map<String, T>> mapEncoderDecoder(AbstractJsonEncoderDecoder<T> encoder, Style style){
        return new AbstractNestedJsonEncoderDecoder<Map<String, T>, T, Void>( encoder, style ) {
            
            @Override
            public JSONValue encode(Map<String, T> value)
                    throws EncodingException {
                return toJSON(value, nested, style);
            }

            @Override
            public Map<String, T> decode(JSONValue value)
                    throws DecodingException {
                return toMap(value, nested, style );
            }
        };
    }        

    static public <T, S> AbstractJsonEncoderDecoder<Map<T, S>> mapEncoderDecoder(AbstractJsonEncoderDecoder<T> keyEncoder, 
            AbstractJsonEncoderDecoder<S> valueEncoder, Style style){
        return new AbstractNestedJsonEncoderDecoder<Map<T, S>, T, S>( keyEncoder, valueEncoder, style ) {
            
            @Override
            public JSONValue encode(Map<T, S> value)
                    throws EncodingException {
                return toJSON(value, nested, second, style);
            }

            @Override
            public Map<T, S> decode(JSONValue value)
                    throws DecodingException {
                return toMap(value, nested, second, style );
            }
        };
    }        
}