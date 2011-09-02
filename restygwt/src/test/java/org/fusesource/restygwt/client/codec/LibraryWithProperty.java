/**
 * 
 */
package org.fusesource.restygwt.client.codec;

import java.util.List;


class LibraryWithProperty {
    public List<LibraryItemWithProperty> items;
    public boolean equals(Object other){
        return items.equals(((LibraryWithProperty)other).items);
     }
}