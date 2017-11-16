package org.fusesource.restygwt.rebind;

import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JType;

import org.fusesource.restygwt.client.Json.Style;

public interface EncoderDecoderLocator {


    String encodeExpression(JType type, String expression, Style style) throws UnableToCompleteException;

    String decodeExpression(JType type, String expression, Style style) throws UnableToCompleteException;

    //TODO remove this methods
    boolean hasCustomEncoderDecoder(JType type);

    boolean isCollectionType(JClassType clazz);

    JClassType getListType();
}