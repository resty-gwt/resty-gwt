/**
 * 
 */
package org.fusesource.restygwt.client.callback;

public class XSRFToken {
    private final String headerKey;
    
    public String token;
    
    public XSRFToken(){
        this("X-AUTHENTICATION-TOKEN");
    }
    
    public XSRFToken(String headerKey){
        this.headerKey = headerKey;
    }
    
    public String getHeaderKey(){
       return this.headerKey;
    }
}