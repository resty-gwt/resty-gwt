package org.fusesource.restygwt.client;

import java.util.HashMap;
import java.util.Map;

/**
 * Allows for setting multiple service roots for cases when several services with different
 * root urls have to be used in one project. The set values can override defaults with the use
 * of the {@link Options#serviceRootKey()} annotation property when annotating the service
 * interface.
 */
public class ServiceRoots {
	private static Map<String, String> serviceRoots = new HashMap<String, String>();
	
	public static void add(String key, String url) {
		if(url != null && !url.endsWith("/")) {
			url += "/";
			serviceRoots.put(key, url);
		}
	}
	
	public static String get(String key) {
		return serviceRoots.get(key);
	}
}