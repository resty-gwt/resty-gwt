package org.fusesource.restygwt.client;

import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;

/**
 * Can produce new instances of a dispatcher. Needed to make
 * configuration Resty via Defaults class possible.
 *
 * Another way would be to use GIN. But that's no current objective.
 *
 * @author <a href="mailto:mail@raphaelbauer.com">rEyez</<a>
 *
 */
public interface IDispatcherFactory {

    IDispatcher get(RequestBuilder requestBuilder, RequestCallback requestCallback);

}
