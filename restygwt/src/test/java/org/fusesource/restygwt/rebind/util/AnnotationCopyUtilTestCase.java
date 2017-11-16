package org.fusesource.restygwt.rebind.util;

import java.lang.reflect.Method;

import javax.ws.rs.Path;

import junit.framework.TestCase;

import org.fusesource.restygwt.client.basic.DirectExampleService;
import org.junit.Test;

public class AnnotationCopyUtilTestCase extends TestCase {
    @Test
    public void testPathWithRegexParam() throws Exception {

        Method method = DirectExampleService.class.getMethod("getRegex", Integer.class);
        Path pathAnnotation = method.getAnnotation(Path.class);

        String result = AnnotationCopyUtil.getAnnotationAsString(pathAnnotation);
        assertEquals("@javax.ws.rs.Path(value = \"/get/{id}\")", result);
    }

    @Test
    public void testPathWithMultiRegexParams() throws Exception {

        Method method = DirectExampleService.class.getMethod("getRegexMultiParams", Integer.class, Integer.class);
        Path pathAnnotation = method.getAnnotation(Path.class);

        String result = AnnotationCopyUtil.getAnnotationAsString(pathAnnotation);
        assertEquals("@javax.ws.rs.Path(value = \"/get/{id}/things/{thing}\")", result);
    }


}
