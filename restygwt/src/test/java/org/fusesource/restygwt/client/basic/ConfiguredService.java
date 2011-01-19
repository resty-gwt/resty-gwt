package org.fusesource.restygwt.client.basic;

import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.Options;
import org.fusesource.restygwt.client.RestService;
import org.fusesource.restygwt.client.dispatcher.DefaultDispatcher;

import javax.ws.rs.GET;

/**
 *
 * Example of using the @Options annotations.
 *
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 *
 */
@Options(dispatcher = DefaultDispatcher.class, expect = {200,201, 204}, timeout = 1000*30)
public interface ConfiguredService extends RestService {

    @GET
    @Options(expect = {200}, timeout = 1000*10)
    public void getExampleDto(MethodCallback<ExampleDto> callback);


}
