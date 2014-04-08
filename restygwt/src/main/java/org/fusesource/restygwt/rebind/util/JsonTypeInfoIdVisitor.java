package org.fusesource.restygwt.rebind.util;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

public abstract class JsonTypeInfoIdVisitor<OUT, EXCEPTION extends Throwable>
{
    public OUT visit(JsonTypeInfo.Id id) throws EXCEPTION {
        switch (id)
        {
            case CLASS:
                return visitClass();
            case CUSTOM:
                return visitCustom();
            case MINIMAL_CLASS:
                return visitMinClass();
            case NAME:
                return visitName();
            case NONE:
                return visitNone();
            default:
                return visitDefault();
        }
    }
    
    public abstract OUT visitClass() throws EXCEPTION;
    public abstract OUT visitCustom() throws EXCEPTION;
    public abstract OUT visitMinClass() throws EXCEPTION;
    public abstract OUT visitName() throws EXCEPTION;
    public abstract OUT visitNone() throws EXCEPTION;
    public abstract OUT visitDefault() throws EXCEPTION;
}
