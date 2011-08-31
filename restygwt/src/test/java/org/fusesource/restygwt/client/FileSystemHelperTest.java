package org.fusesource.restygwt.client;

import junit.framework.TestCase;

import org.fusesource.restygwt.client.FileSystemHelper;

public class FileSystemHelperTest extends TestCase {
	
	public void testFileSystemHelper() {
		
		// Scenarios:
		// 1. Request goes to filesystem, we are running on filesystem
		String mockedBaseUrl = "file:///myLocation";
		String uriRequested = "file:///myJson.json";
		
		assertTrue(FileSystemHelper.isRequestGoingToFileSystem(mockedBaseUrl,uriRequested));
		
		// 2. Request goes to relative path => we are running on filesystem
		mockedBaseUrl = "file:///myLocation";
		uriRequested = "../../myJson.json";
		assertTrue(FileSystemHelper.isRequestGoingToFileSystem(mockedBaseUrl,uriRequested));
		
		
		// 3. Request goes to absolute path => we are running on filesystem
		mockedBaseUrl = "file:///myLocation";
		uriRequested = "/myJson.json";
		assertTrue(FileSystemHelper.isRequestGoingToFileSystem(mockedBaseUrl,uriRequested));
		
		
		// 4. Request goes to absolute path => we are running on filesystem
		mockedBaseUrl = "file:///myLocation";
		uriRequested = "file:///myJson.json";
		assertTrue(FileSystemHelper.isRequestGoingToFileSystem(mockedBaseUrl,uriRequested));
		
		
		
		
		
		// 5. Request goes to absolute path => we are running on http
		mockedBaseUrl = "http://myLocation";
		uriRequested = "/myJson.json";
		assertFalse(FileSystemHelper.isRequestGoingToFileSystem(mockedBaseUrl,uriRequested));
		
		
		// 6. Request goes to relative path => we are running on http
		mockedBaseUrl = "http://myLocation";
		uriRequested = "../../myJson.json";
		assertFalse(FileSystemHelper.isRequestGoingToFileSystem(mockedBaseUrl,uriRequested));
		
		
		// 7. Request goes to relative path => we are running on http
		mockedBaseUrl = "http://myLocation";
		uriRequested = "../../myJson.json";
		assertFalse(FileSystemHelper.isRequestGoingToFileSystem(mockedBaseUrl,uriRequested));
		
		
		// 8. Request goes to absolute http => we are running on http
		mockedBaseUrl = "http://myLocation";
		uriRequested = "http://myJson.json";
		assertFalse(FileSystemHelper.isRequestGoingToFileSystem(mockedBaseUrl,uriRequested));
		
		
	}
	
	
}
