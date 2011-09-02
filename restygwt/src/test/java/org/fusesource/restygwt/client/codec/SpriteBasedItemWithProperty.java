/**
 * 
 */
package org.fusesource.restygwt.client.codec;

public class SpriteBasedItemWithProperty extends LibraryItemWithProperty {
    public String imageRef;
    public boolean equals(Object other){
        return super.equals(other) && imageRef.equals(((SpriteBasedItemWithProperty)other).imageRef);
     }
}