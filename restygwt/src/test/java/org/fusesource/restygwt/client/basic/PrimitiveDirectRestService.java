package org.fusesource.restygwt.client.basic;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.fusesource.restygwt.client.DirectRestService;

/**
 * A service containing methods that return all primitive types.
 */
interface PrimitiveDirectRestService extends DirectRestService {
    
	@GET
    @Path("/int")
    int getInt();
    
    @GET
    @Path("/long")
    long getLong();
    
    @GET
    @Path("/float")
    float getFloat();
    
    @GET
    @Path("/double")
    double getDouble();
    
    @GET
    @Path("/short")
    short getShort();
    
    @GET
    @Path("/byte")
    byte getByte();
    
    @GET
    @Path("/char")
    char getChar();
    
    @GET
    @Path("/boolean")
    boolean getBoolean();
    
}
