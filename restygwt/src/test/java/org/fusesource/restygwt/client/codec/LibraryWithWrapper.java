/**
 * 
 */
package org.fusesource.restygwt.client.codec;

import java.util.List;


class LibraryWithWrapper {
    public List<LibraryItemWithWrapper> items;
    public boolean equals(Object other){
        return items.equals(((LibraryWithWrapper)other).items);
     }
}