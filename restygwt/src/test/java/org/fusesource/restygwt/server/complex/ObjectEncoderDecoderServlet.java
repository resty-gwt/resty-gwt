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

package org.fusesource.restygwt.server.complex;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.ws.rs.Path;

import org.fusesource.restygwt.client.complex.ObjectEncoderDecoderTestGwt;
import org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher;
import org.jboss.resteasy.spi.Registry;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

import com.google.gwt.dev.util.collect.Maps;

public class ObjectEncoderDecoderServlet extends HttpServletDispatcher
{
	private static final long serialVersionUID = 1L;

	private static final Map<String, Object> properties = new HashMap<String, Object>();

	static
	{
		properties.put("number", 123.0);
		properties.put("boolean", true);
		properties.put("string", "Fred Fredstofferson");
		properties.put("array", new Object[]
		{
				123.0, true, "Fred Fredstofferson"
		});
		properties.put("object", Maps.create("blah", 123.0));
	}

	@Path("properties")
	public static class PropertiesImpl implements ObjectEncoderDecoderTestGwt.Properties
	{
		@Override
		public Object getProperty(String name)
		{
			return properties.get(name);
		}

		@Override
		public void setProperty(String name, Object value)
		{
			properties.put(name, value);
		}
	}

	@Override
	public void init(ServletConfig config) throws ServletException
	{
		super.init(config);

		final ResteasyProviderFactory providerFactory = (ResteasyProviderFactory) config.getServletContext().getAttribute(ResteasyProviderFactory.class.getName());
		providerFactory.registerProvider(JsonStringProvider.class);

		final Registry registry = (Registry) config.getServletContext().getAttribute(Registry.class.getName());
		// the prefix must be manually specified because the config's servlet context is does not properly specify it.
		registry.addPerRequestResource(PropertiesImpl.class, "/org.fusesource.restygwt.ObjectEncoderDecoder.JUnit");
	}
}
