/**
 * 
 */
package org.fusesource.restygwt.client.codec;

import java.util.List;


class LibraryWithArrayWrapper {
    public List<LibraryItemWithArrayWrapper> items;
    public boolean equals(Object other){
        return items.equals(((LibraryWithArrayWrapper)other).items);
     }
}