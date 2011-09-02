/**
 * 
 */
package org.fusesource.restygwt.client.codec;

public class SpriteBasedItemWithWrapper extends LibraryItemWithWrapper {
    public String imageRef;
    public boolean equals(Object other){
        return super.equals(other) && imageRef.equals(((SpriteBasedItemWithWrapper)other).imageRef);
     }
}