/**
 * 
 */
package org.fusesource.restygwt.client.codec;

import org.codehaus.jackson.annotate.JsonSubTypes;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.annotate.JsonTypeInfo.As;
import org.codehaus.jackson.annotate.JsonTypeInfo.Id;

@JsonSubTypes({@JsonSubTypes.Type(value = SpriteBasedItemWithWrapper.class)})
@JsonTypeInfo(use = Id.CLASS, include = As.WRAPPER_OBJECT)
abstract public class LibraryItemWithWrapper {
    public String id;
    
    public boolean equals(Object other){
       return getClass().equals(other.getClass()) && id.equals(((LibraryItemWithWrapper)other).id);
    }
}