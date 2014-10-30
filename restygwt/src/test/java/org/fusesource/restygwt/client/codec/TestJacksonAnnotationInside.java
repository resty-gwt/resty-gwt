package org.fusesource.restygwt.client.codec;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotationsInside
@JsonTypeInfo(use=Id.CLASS, include=As.PROPERTY)
@JsonSubTypes(value={@Type(SubCredentialsWithProperty.class), @Type(CredentialsWithJacksonAnnotationsInside.class)}) 
public @interface TestJacksonAnnotationInside {

}
