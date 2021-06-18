/**
 * Copyright (C) 2009-2018 the original author or authors.
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

/**a fluent builder to facilitate use with lambdas.  for example:
   <pre>
   MethodCallbackBuilder.withSuccess((m,r) -> ...)
                        .withFailure((m,e) -> ...);
   </pre>
*/
public class MethodCallbackBuilder<T>{
    public static interface Success<T>{
        void onSuccess(Method method, T response);
    }

    public static interface Failure{
        void onFailure(Method method, Throwable exception);
    }

    private final Success<T> _s;

    private static final <T> MethodCallbackBuilder<T> withSuccess(Success<T> s){
        return new MethodCallbackBuilder(s);
    }

    private MethodCallbackBuilder(Success<T> s){
        _s = s;
    }

    public MethodCallback<T> withFailure(final Failure f){
        return new MethodCallback<T>(){
            @Override
            public void onSuccess(Method method, T response){
                _s.onSuccess(method, response);
            }

            @Override
            public void onFailure(Method method, Throwable exception){
                f.onFailure(method, exception);
            }
        };
    }
}
