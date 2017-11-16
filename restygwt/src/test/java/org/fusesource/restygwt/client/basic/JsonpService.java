/**
 * Copyright (C) 2009-2015 the original author or authors.
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

package org.fusesource.restygwt.client.basic;

import java.util.List;

import javax.ws.rs.Path;

import org.fusesource.restygwt.client.JSONP;
import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.RestService;

import com.google.gwt.jsonp.client.JsonpRequest;

/**
 *
 * @author Kristian
 *
 */
public interface JsonpService extends RestService {

    @JSONP
    void someJsonp(MethodCallback<ExampleDto> callback);

    /**
     * Used to make sure the generator handles JsonpRequest result.
     * It can be used to cancel requests.
     */
    @JSONP
    JsonpRequest<ExampleDto> someCancelableJsonp(MethodCallback<ExampleDto> callback);

    @Path("list")
    @JSONP
    void someJsonpWithList(MethodCallback<List<ExampleDto>> callback);

    @JSONP(callbackParam = "null") // param name used by test servlet produce null
    void someOtherJsonp(MethodCallback<ExampleDto> callback);

}