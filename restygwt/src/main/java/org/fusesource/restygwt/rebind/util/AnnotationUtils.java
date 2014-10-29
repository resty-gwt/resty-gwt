package org.fusesource.restygwt.rebind.util;

import java.lang.annotation.Annotation;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.google.gwt.core.ext.typeinfo.HasAnnotations;

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
}
