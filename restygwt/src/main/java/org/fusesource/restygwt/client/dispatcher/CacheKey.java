package org.fusesource.restygwt.client.dispatcher;

import com.google.gwt.http.client.RequestBuilder;

public class CacheKey {

    //FIXME:
    private String url;

    //FIXME:
    private String requestData;

    //FIXME:
    private String httpMethod;

    public CacheKey(RequestBuilder requestBuilder) {
        this.url = requestBuilder.getUrl();
        this.requestData = requestBuilder.getRequestData();
        this.httpMethod = requestBuilder.getHTTPMethod();

    }


    /**
     * Needed for saving in HashMap:
     */
    @Override
    public int hashCode() {
        return new String(getEverythingAsConcatenatedString()).hashCode();
    }

    /**
     * Needed for saving in HashMap:
     */
    @Override
    public boolean equals(Object anObject) {

        if (anObject instanceof CacheKey) {


            CacheKey aCacheKey = (CacheKey) anObject;

            if (aCacheKey.getEverythingAsConcatenatedString().equals(
                    getEverythingAsConcatenatedString())) {
                return true;
            }


        }

        return false;

    }


    /**
     * Little helper to get contents...
     *
     * @return
     */
    public String getEverythingAsConcatenatedString() {

        return url + requestData + httpMethod;

    }

}
