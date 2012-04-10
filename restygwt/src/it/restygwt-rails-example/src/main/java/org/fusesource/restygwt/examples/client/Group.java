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

package org.fusesource.restygwt.examples.client;

import org.fusesource.restygwt.client.Json;
import org.fusesource.restygwt.client.Json.Style;
 
@Json(style = Style.RAILS)
public class Group {

    public int id;

    public String name;

    @Override
    public boolean equals(Object obj) {
        try {
            Group other = (Group) obj;
            return id == other.id;
        } catch (Throwable e) {
            return false;
        }
    }

}
