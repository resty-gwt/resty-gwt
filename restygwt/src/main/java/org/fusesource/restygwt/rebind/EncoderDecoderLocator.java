package org.fusesource.restygwt.rebind;

import org.fusesource.restygwt.client.Json.Style;

import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JType;

public interface EncoderDecoderLocator {


    public String encodeExpression(JType type, String expression, Style style) throws UnableToCompleteException;

    public String decodeExpression(JType type, String expression, Style style) throws UnableToCompleteException;

    //TODO remove this methods
    public boolean hasCustomEncoderDecoder(JType type);

    public boolean isCollectionType(JClassType clazz);

    public JClassType getListType();
}