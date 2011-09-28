package org.fusesource.restygwt.client;

public interface JsonTypeResolver
{
	String getType(Object object);
	
	<T> JsonEncoderDecoder<T> getEncoderDecoder(String type);
}
