package org.fusesource.restygwt.rebind.util;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.google.gwt.core.ext.typeinfo.HasAnnotations;
import com.google.gwt.core.ext.typeinfo.JClassType;

public class AnnotationUtils {

    public static <T extends Annotation> T getAnnotation(HasAnnotations classType, Class<T> annotationType) {
        try {
            T ann = classType.getAnnotation(annotationType);
            if (ann == null) {
                for (Annotation metaAnn : classType.getAnnotations()) {
                    // only return a custom annotation if it contains the JacksonAnnotationsInside annotation
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
        T annotation;

        if (classType == null) {
            return null;
        } else if (getAnnotation(classType, annotationType) != null) {
            return getAnnotation(classType, annotationType);
        } else {
            List<JClassType> intefaces = Arrays.asList(classType.getImplementedInterfaces());

            for (JClassType itf : intefaces) {
                annotation = getClassAnnotation(itf, annotationType);
                if (annotation != null) {
                    return annotation;
                }
            }

            return getClassAnnotation(classType.getSuperclass(), annotationType);
        }
    }

    /**
     * Get all annotations from superclasses and superinterfaces.<br>
     * <br>
     * Works like {@link JClassType#findAnnotationInTypeHierarchy(Class)} but returns all annotations in the type
     * hierarchy.
     *
     * @author Ralf Sommer {@literal <ralf.sommer.dev@gmail.com>}
     *
     * @param classType
     * @return annotations
     */
    public static Annotation[] getAnnotationsInTypeHierarchy(JClassType classType) {
        Set<Annotation> resultAnnotations = new HashSet<Annotation>();

        // Cache to avoid loops
        Set<JClassType> alreadyCheckedTypes = new HashSet<JClassType>();

        // Work queue
        Queue<JClassType> typeQueue = new LinkedList<JClassType>();

        for (JClassType current = classType; current != null; current = typeQueue.poll()) {
            if (!alreadyCheckedTypes.add(current)) {
                continue;
            }

            // Get the annotations only from current, no inherited
            Annotation[] annotations = current.getDeclaredAnnotations();
            Collections.addAll(resultAnnotations, annotations);

            if (current.getSuperclass() != null) {
                // Add the superclass to the queue
                typeQueue.add(current.getSuperclass());
            }

            // Add the Superinterfaces to the queue
            Collections.addAll(typeQueue, current.getImplementedInterfaces());
        }

        return resultAnnotations.toArray(new Annotation[resultAnnotations.size()]);
    }

}
