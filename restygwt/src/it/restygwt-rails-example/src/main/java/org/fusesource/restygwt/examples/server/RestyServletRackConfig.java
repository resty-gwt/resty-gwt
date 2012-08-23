/**
 * Copyright (C) 2009-2012 the original author or authors.
 * See the notice.md file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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