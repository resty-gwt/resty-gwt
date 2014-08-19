package org.fusesource.restygwt.client;

import com.google.gwt.http.client.URL;

public class FormPostContent {
    private StringBuilder textContent = new StringBuilder();
    private boolean ampersand;

    public void addParameter(String name, String value) {
        if (value == null) {
            return;
        }

        textContent.append((ampersand ? "&" : "") +
                URL.encodeQueryString(name) +
                "=" +
                URL.encodeQueryString(value));
        ampersand = true;
    }

    public void addParameters(String key, Iterable<String> values) {
        if (values == null)
            return;
        key = URL.encodeQueryString(key);
        textContent.append((ampersand ? "&" : ""));
        for (String value : values) {
            if (value == null)
                continue;
            if (ampersand) {
                textContent.append('&');
            } else {
                ampersand = true;
            }
            value = URL.encodeQueryString(value);
            textContent.append(key + "=" + value);
        }
    }

    public String getTextContent() {
        return textContent.toString();
    }
}
