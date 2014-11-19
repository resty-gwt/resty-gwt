package org.fusesource.restygwt.rebind.util;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.google.gwt.core.ext.typeinfo.HasAnnotations;
import com.google.gwt.core.ext.typeinfo.JClassType;

public class AnnotationUtils {

    public static <T extends Annotation> T getAnnotation(HasAnnotations classType, Class<T> annotationType) {
        try {
            T ann = classType.getAnnotation(annotationType);
            if (ann == null) {
                for (Annotation metaAnn : classType.getAnnotations()) {
                    //only return a custom annotation if it contains the JacksonAnnotationsInside annotation
                    if (metaAnn.annotationType().getAnnotation(JacksonAnnotationsInside.class) != null) {
                        ann = metaAnn.annotationType().getAnnotation(annotationType);
                        if (ann != null) {
                            break;
                        }
                    }
                }
            }
            return ann;
        } catch (Exception ex) {
            return null;
        }
    }

    public static <T extends Annotation> T getClassAnnotation(JClassType classType, Class<T> annotationType) {
        T annotation = null;

        if (classType == null) {
            return null;
        } else if (classType.isAnnotationPresent(annotationType)) {
            return classType.getAnnotation(annotationType);
        } else {
            List<JClassType> intefaces = Arrays.asList(classType.getImplementedInterfaces());

            for (JClassType itf: intefaces) {
                annotation = getClassAnnotation(itf, annotationType);
                if (annotation != null) {
                    return annotation;
                }
            }

            return getClassAnnotation(classType.getSuperclass(), annotationType);
        }
    }
}

