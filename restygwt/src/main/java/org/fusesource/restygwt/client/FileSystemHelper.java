package org.fusesource.restygwt.client;

/**
 * 
 * Helps to determine which status codes to expect.
 * 
 * If we are running on file:/// system we have to make sure that we accept ALL
 * status codes.
 * 
 * This can happen if you are developing for Phonegap iOs
 * 
 * @author ra
 * 
 */
public class FileSystemHelper {
	
	public static boolean isRequestGoingToFileSystem(String baseUrl,
													 String requestUrl) {
		
		if (requestUrl.startsWith("file")) {
			return true;
			
		}
		
		if (baseUrl.startsWith("file") && requestUrl.startsWith("/")) {
			return true;
			
		}
		
		if (baseUrl.startsWith("file") && requestUrl.startsWith(".")) {
			return true;
			
		}
		
		return false;
		
	}
	
}
