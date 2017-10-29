/**
 * Copyright 2015 the original author or authors.
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

import javax.ws.rs.GET;

import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.Options;
import org.fusesource.restygwt.client.RestService;
import org.fusesource.restygwt.client.dispatcher.DefaultDispatcher;

/**
 *
 * Example of using the @Options annotation without "expect" element.
 *
 * @author <a href="mailto:mail@ralf.sommer.dev@gmail.com">Ralf Sommer</a>
 *
 */
@Options(dispatcher = DefaultDispatcher.class, timeout = 1000 * 30)
public interface ConfiguredWithoutExpectService extends RestService {

    @GET
    @Options(timeout = 1000 * 10)
    public void getExampleDto(MethodCallback<ExampleDto> callback);

}
