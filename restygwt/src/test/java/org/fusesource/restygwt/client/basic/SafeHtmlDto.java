package org.fusesource.restygwt.client.basic;

import com.google.gwt.safehtml.shared.SafeHtml;

/**
 *
 * @author <a href="mailto:tim@elbart.com">Tim Eggert</a>
 *
 */
public class SafeHtmlDto {

    private SafeHtml safeHtml;
    private String unsafeString;

    public void setSafeHtml(SafeHtml safeHtml) {
        this.safeHtml = safeHtml;
    }

    public SafeHtml getSafeHtml() {
        return safeHtml;
    }

    public void setUnsafeString(String unsafeString) {
        this.unsafeString = unsafeString;
    }

    public String getUnsafeString() {
        return unsafeString;
    }

}
