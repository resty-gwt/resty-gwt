/**
 * Copyright (C) 2010, Progress Software Corporation and/or its 
 * subsidiaries or affiliates.  All rights reserved.
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

import java.util.LinkedHashMap;
import java.util.Map;

public class MapResult {
    public Map<String, String> result = new LinkedHashMap<String, String>();

    public MapResult() {
    }

    public MapResult(Map<String, String> map) {
        this.result = map;
    }

    @Override
    public int hashCode() {
        return result.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        try {
            MapResult other = (MapResult) obj;
            return result.equals(other.result);
        } catch (Throwable e) {
            return false;
        }
    }

}
