package org.fusesource.restygwt.client;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.fusesource.restygwt.client.Json.Style;

import com.google.gwt.json.client.JSONNull;
import com.google.gwt.json.client.JSONValue;

public abstract class AbstractNestedJsonEncoderDecoder<E, F, G> extends AbstractJsonEncoderDecoder<E> {

    protected AbstractJsonEncoderDecoder<F> nested;
    protected AbstractJsonEncoderDecoder<G> second;
    protected Style style;

    AbstractNestedJsonEncoderDecoder(AbstractJsonEncoderDecoder<F> encoder) {
        nested = encoder;
    }

    AbstractNestedJsonEncoderDecoder(AbstractJsonEncoderDecoder<F> encoder, Style style) {
        nested = encoder;
        this.style = style;
    }

    AbstractNestedJsonEncoderDecoder(AbstractJsonEncoderDecoder<F> keyEncoder,
                                     AbstractJsonEncoderDecoder<G> valueEncoder, Style style) {
        nested = keyEncoder;
        second = valueEncoder;
        this.style = style;
    }

    static public <T> AbstractJsonEncoderDecoder<T[]> arrayEncoderDecoder(AbstractJsonEncoderDecoder<T> encoder) {
        return new AbstractNestedJsonEncoderDecoder<T[], T, Void>(encoder) {

            @Override
            public JSONValue encode(T[] value) throws EncodingException {
                return value != null ? toJSON(value, nested) : JSONNull.getInstance();
            }

            @SuppressWarnings("unchecked")
            @Override
            public T[] decode(JSONValue value) throws DecodingException {
                return toArray(value, nested, (T[]) new Object[asArray(value).size()]);
            }
        };
    }

    static public <T> AbstractJsonEncoderDecoder<Collection<T>> collectionEncoderDecoder(
        AbstractJsonEncoderDecoder<T> encoder) {
        return new AbstractNestedJsonEncoderDecoder<Collection<T>, T, Void>(encoder) {

            @SuppressWarnings("unchecked")
            @Override
            public JSONValue encode(Collection<T> value) throws EncodingException {
                return value != null ? toJSON((T[]) value.toArray(), nested) : JSONNull.getInstance();
            }

            @Override
            public Collection<T> decode(JSONValue value) throws DecodingException {
                return toList(value, nested);
            }
        };
    }

    static public <T> AbstractJsonEncoderDecoder<List<T>> listEncoderDecoder(AbstractJsonEncoderDecoder<T> encoder) {
        return new AbstractNestedJsonEncoderDecoder<List<T>, T, Void>(encoder) {

            @SuppressWarnings("unchecked")
            @Override
            public JSONValue encode(List<T> value) throws EncodingException {
                return value != null ? toJSON((T[]) value.toArray(), nested) : JSONNull.getInstance();
            }

            @Override
            public List<T> decode(JSONValue value) throws DecodingException {
                return toList(value, nested);
            }
        };
    }

    static public <T> AbstractJsonEncoderDecoder<Set<T>> setEncoderDecoder(AbstractJsonEncoderDecoder<T> encoder) {
        return new AbstractNestedJsonEncoderDecoder<Set<T>, T, Void>(encoder) {

            @Override
            public JSONValue encode(Set<T> value) throws EncodingException {
                return value != null ? toJSON(value, nested) : JSONNull.getInstance();
            }

            @Override
            public Set<T> decode(JSONValue value) throws DecodingException {
                return toSet(value, nested);
            }
        };
    }

    static public <T> AbstractJsonEncoderDecoder<Map<String, T>> mapEncoderDecoder(
        AbstractJsonEncoderDecoder<T> encoder, Style style) {
        return new AbstractNestedJsonEncoderDecoder<Map<String, T>, T, Void>(encoder, style) {

            @Override
            public JSONValue encode(Map<String, T> value) throws EncodingException {
                return value != null ? toJSON(value, nested, style) : JSONNull.getInstance();
            }

            @Override
            public Map<String, T> decode(JSONValue value) throws DecodingException {
                return toMap(value, nested, style);
            }
        };
    }

    static public <T, S> AbstractJsonEncoderDecoder<Map<T, S>> mapEncoderDecoder(
        AbstractJsonEncoderDecoder<T> keyEncoder, AbstractJsonEncoderDecoder<S> valueEncoder, Style style) {
        return new AbstractNestedJsonEncoderDecoder<Map<T, S>, T, S>(keyEncoder, valueEncoder, style) {

            @Override
            public JSONValue encode(Map<T, S> value) throws EncodingException {
                return value != null ? toJSON(value, nested, second, style) : JSONNull.getInstance();
            }

            @Override
            public Map<T, S> decode(JSONValue value) throws DecodingException {
                return toMap(value, nested, second, style);
            }
        };
    }
}