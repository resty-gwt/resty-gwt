/**
 * 
 */
package org.fusesource.restygwt.client.codec;

public class SpriteBasedItemWithArrayWrapper extends LibraryItemWithArrayWrapper {
    public String imageRef;
    public boolean equals(Object other){
        return super.equals(other) && imageRef.equals(((SpriteBasedItemWithArrayWrapper)other).imageRef);
     }
}