package org.fusesource.restygwt.examples.server;

import java.util.Properties;

import javax.servlet.ServletContext;

import org.jruby.rack.servlet.ServletRackConfig;

public class RestyServletRackConfig extends ServletRackConfig {

    private final Properties properties = new Properties();

    public RestyServletRackConfig(ServletContext context) {
        super(context);
        properties.setProperty("gem.path", "./target/rubygems");
        properties.setProperty("rails.env", "development");
        properties.setProperty("jruby.rack.logging", "stdout");
        properties.setProperty("jruby.rack.layout_class",
                "JRuby::Rack::RailsFilesystemLayout");
    }

    @Override
    public String getProperty(String key) {
        String value;
        if ((value = properties.getProperty(key)) != null) {
            return value;
        }
        return super.getProperty(key);
    }

    @Override
    public String getProperty(String key, String defaultValue) {
        String value;
        if ((value = properties.getProperty(key)) != null) {
            return value;
        }
        return super.getProperty(key, defaultValue);
    }
}