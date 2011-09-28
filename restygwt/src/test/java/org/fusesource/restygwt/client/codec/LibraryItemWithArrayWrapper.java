/**
 * 
 */
package org.fusesource.restygwt.client.codec;

import org.codehaus.jackson.annotate.JsonSubTypes;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.annotate.JsonTypeInfo.As;
import org.codehaus.jackson.annotate.JsonTypeInfo.Id;

@JsonSubTypes({@JsonSubTypes.Type(value = SpriteBasedItemWithArrayWrapper.class)})
@JsonTypeInfo(use = Id.CLASS, include = As.WRAPPER_ARRAY)
abstract public class LibraryItemWithArrayWrapper {
    public String id;
    
    public boolean equals(Object other){
       return getClass().equals(other.getClass()) && id.equals(((LibraryItemWithArrayWrapper)other).id);
    }
}