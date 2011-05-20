/**
 * 
 */
package org.fusesource.restygwt.client.callback;

public class XSSToken {
    private final String headerKey;
    
    public String token;
    
    public XSSToken(){
        this("X-AUTHENTICATION-TOKEN");
    }
    
    public XSSToken(String headerKey){
        this.headerKey = headerKey;
    }
    
    public String getHeaderKey(){
       return this.headerKey;
    }
}