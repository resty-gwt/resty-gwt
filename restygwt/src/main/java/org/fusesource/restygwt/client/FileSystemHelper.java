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
