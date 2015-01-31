/**
 * 
 */
package org.fusesource.restygwt.client;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * @author SAMBATH
 *
 */
@Documented
@Retention(RUNTIME)
@Target( { METHOD })
public @interface CUSTOM {

	String value();
	
}
