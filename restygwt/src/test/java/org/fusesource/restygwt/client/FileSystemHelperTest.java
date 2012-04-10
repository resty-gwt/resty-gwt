/**
 * Copyright (C) 2009-2011 the original author or authors.
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
