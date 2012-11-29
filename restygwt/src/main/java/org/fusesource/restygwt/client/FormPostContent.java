package org.fusesource.restygwt.client;

import com.google.gwt.http.client.URL;

public class FormPostContent {
    private String textContent = "";
    private boolean ampersand;

    public void addParameter(String name, String value) {
        if (value == null) {
            return;
        }

        textContent += (ampersand ? "&" : "") +
                URL.encodeQueryString(name) +
                "=" +
                URL.encodeQueryString(value);
        ampersand = true;
    }

    public String getTextContent() {
        return textContent;
    }
}
