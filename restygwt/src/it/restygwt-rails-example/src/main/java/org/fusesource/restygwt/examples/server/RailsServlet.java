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

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

import org.jruby.rack.RackApplicationFactory;
import org.jruby.rack.RackServlet;
import org.jruby.rack.SharedRackApplicationFactory;
import org.jruby.rack.rails.RailsRackApplicationFactory;
import org.jruby.rack.servlet.ServletRackConfig;
import org.jruby.rack.servlet.ServletRackContext;

@SuppressWarnings("serial")
public class RailsServlet extends RackServlet{

    @Override
    public void init(ServletConfig config) {
        if(config.getServletContext().getAttribute(RackApplicationFactory.RACK_CONTEXT) == null){   
            System.out.println("setup rails application via jruby-rack - started . . .");
            ServletContext ctx = config.getServletContext();
            ServletRackConfig rackConfig = new RestyServletRackConfig(ctx);
            final RackApplicationFactory fac = new SharedRackApplicationFactory(new RailsRackApplicationFactory());
            ctx.setAttribute(RackApplicationFactory.FACTORY, fac);
            ServletRackContext rackContext = new ServletRackContext(rackConfig);
            ctx.setAttribute(RackApplicationFactory.RACK_CONTEXT, rackContext);
            try {
                fac.init(rackContext);
                System.out.println("setup rails application via jruby-rack - done . . .");
            } catch (Exception ex) {
                ctx.log("Error: application initialization failed", ex);
            }
        }
        super.init(config);
    }

    
}
