package org.fusesource.restygwt.client.basic;

import org.fusesource.restygwt.client.AbstractJsonEncoderDecoder;

import com.google.gwt.json.client.JSONValue;
import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

public class SafeHtmlJsonEncoderDecoderTestGwt extends GWTTestCase {

    @Override
    public String getModuleName() {
        return "org.fusesource.restygwt.SafeHtmlJsonEncoderDecoderTestGwt";
    }

    public void SafeHtmlEncodingTestGwt() {
        String s = "test";
        String tag = "\"";
        SafeHtml safeHtml = new SafeHtmlBuilder().appendEscaped(s).toSafeHtml();
        JSONValue result = AbstractJsonEncoderDecoder.SAFE_HTML.encode(safeHtml);
        assertEquals(tag + s + tag, result.toString());
    }
}
