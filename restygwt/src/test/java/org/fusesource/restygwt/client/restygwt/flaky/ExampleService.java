package org.fusesource.restygwt.client.restygwt.flaky;

import javax.ws.rs.GET;

import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.RestService;

/**
 *
 * Supersimple example service.
 *
 * Simply to test if connection is retried automatically.
 *
 *
 * @author <a href="mailto:mail@raphaelbauer.com">rEyez</<a>
 *
 */
public interface ExampleService extends RestService {

    @GET
    public void getExampleDto(MethodCallback<ExampleDto> callback);
}
