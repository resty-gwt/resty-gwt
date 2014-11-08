package org.fusesource.restygwt.client;

import com.google.gwt.http.client.Response;

/**
 * This class is used by AbstractRequestCallback to map the status code into an exception that is more appropriate for
 * the application. A new ExceptionMapper can be specified through the method
 * {@link org.fusesource.restygwt.client.Defaults#setExceptionMapper(ExceptionMapper)}.
 *
 * This default implementation does exactly what {@link org.fusesource.restygwt.client.AbstractRequestCallback} used
 * to do.
 *
 * See <a href="https://github.com/resty-gwt/resty-gwt/issues/209">Issue 209</a>
 * @author Constantino Cronemberger - ccronembeger@yahoo.com.br
 */
public class ExceptionMapper {

    /**
     * This method is called when Response is null.
     * @return the exception to be passed to the callback
     */
    public Throwable createNoResponseException() {
        return new FailedStatusCodeException("TIMEOUT", 999);
    }

    /**
     * Creates an exception based on the Response.
     * @param method can be used by a subclass to find out the URL used for example
     * @param response the response received
     * @return the exception to be passed to the callback
     */
    public Throwable createFailedStatusException(Method method, Response response) {
        return new FailedStatusCodeException(response.getStatusText(), response.getStatusCode());
    }
}
