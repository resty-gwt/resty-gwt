/**
 * 
 */
package org.fusesource.restygwt.client.codec;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

class CredentialsWithCreator {
    @JsonProperty
    final String password;
    @JsonProperty
    final String email;
    
    int age;
    
    @JsonCreator
    public CredentialsWithCreator(@JsonProperty("email") String email, @JsonProperty("password") String password) {
        this.email = email;
        this.password = password;
    }
    
    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}