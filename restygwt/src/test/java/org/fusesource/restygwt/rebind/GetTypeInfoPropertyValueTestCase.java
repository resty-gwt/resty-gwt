package org.fusesource.restygwt.rebind;

import junit.framework.TestCase;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import org.junit.Test;

public class GetTypeInfoPropertyValueTestCase extends TestCase
{
    
    @JsonTypeInfo(use=Id.CLASS, include=As.PROPERTY)
    private static class ClassEmpty
    {    
    }
    
    @JsonTypeInfo(use=Id.CLASS, include=As.PROPERTY, property="test")
    private static class ClassNotEmpty
    {    
    }
    
    @JsonTypeInfo(use=Id.NAME, include=As.PROPERTY)
    private static class NameEmpty
    {    
    }

    @JsonTypeInfo(use=Id.NAME, include=As.PROPERTY, property="test2")
    private static class NameNotEmpty
    {    
    }

    @JsonTypeInfo(use=Id.MINIMAL_CLASS, include=As.PROPERTY)
    private static class MinEmpty
    {    
    }
    
    @JsonTypeInfo(use=Id.MINIMAL_CLASS, include=As.PROPERTY, property="test3")
    private static class MinNotEmpty
    {    
    }
    
    @JsonTypeInfo(use=Id.NONE, include=As.PROPERTY)
    private static class NoneEmpty
    {    
    }

    @JsonTypeInfo(use=Id.NONE, include=As.PROPERTY, property="test4")
    private static class NoneNotEmpty
    {    
    }
    
    @JsonTypeInfo(use=Id.CUSTOM, include=As.PROPERTY)
    private static class CustomEmpty
    {    
    }

    @JsonTypeInfo(use=Id.CUSTOM, include=As.PROPERTY, property="test5")
    private static class CustomNotEmpty
    {    
    }
    
    @Test
    public void testClassEmpty() throws Exception{
        check("@class", new ClassEmpty().getClass().getAnnotation(JsonTypeInfo.class));
    }

    @Test
    public void testClassNotEmpty() throws Exception{
        check("test", new ClassNotEmpty().getClass().getAnnotation(JsonTypeInfo.class));
    }

    @Test
    public void testNameEmpty() throws Exception{
        check("@type", new NameEmpty().getClass().getAnnotation(JsonTypeInfo.class));
    }

    @Test
    public void testNameNotEmpty() throws Exception{
        check("test2", new NameNotEmpty().getClass().getAnnotation(JsonTypeInfo.class));
    }

    @Test
    public void testMinEmpty() throws Exception{
        check("@c", new MinEmpty().getClass().getAnnotation(JsonTypeInfo.class));
    }

    @Test
    public void testMinNotEmpty() throws Exception{
        check("test3", new MinNotEmpty().getClass().getAnnotation(JsonTypeInfo.class));
    }
    
    @Test
    public void testNoneEmpty() throws Exception{
        check(null, new NoneEmpty().getClass().getAnnotation(JsonTypeInfo.class));
    }

    @Test
    public void testNoneNotEmpty() throws Exception{
        //should be null ?
        check("test4", new NoneNotEmpty().getClass().getAnnotation(JsonTypeInfo.class));
    }
    
    @Test
    public void testCustomEmpty() throws Exception{
        check(null, new CustomEmpty().getClass().getAnnotation(JsonTypeInfo.class));
    }

    @Test
    public void testCustomNotEmpty() throws Exception{
        //should be null ?
        check("test5", new CustomNotEmpty().getClass().getAnnotation(JsonTypeInfo.class));
    }
    
    @Test
    private void check(final String expected, final JsonTypeInfo annotation)
    {
        final String value = JsonEncoderDecoderClassCreator.getTypeInfoPropertyValue(annotation);
        assertEquals(expected, value);
    }
}
