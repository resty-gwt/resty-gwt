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

package org.fusesource.restygwt.client.basic;

import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.Options;
import org.fusesource.restygwt.client.RestService;
import org.fusesource.restygwt.client.dispatcher.DefaultDispatcher;

import javax.ws.rs.GET;

/**
 *
 * Example of using the @Options annotations.
 *
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 *
 */
@Options(dispatcher = DefaultDispatcher.class, expect = { 200, 201, 204 }, timeout = 1000 * 30)
public interface ConfiguredService extends RestService {

    @GET
    @Options(expect = { 200 }, timeout = 1000 * 10)
    public void getExampleDto(MethodCallback<ExampleDto> callback);

}
