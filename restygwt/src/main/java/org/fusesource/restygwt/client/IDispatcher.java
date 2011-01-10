package org.fusesource.restygwt.client;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestException;

/**
 *
 * Do the dispatch.
 * Check out default implementation DispatcherDefault.
 *
 * @author <a href="mailto:mail@raphaelbauer.com">rEyez</<a>
 *
 */
public interface IDispatcher {

    public Request send() throws RequestException;

}
