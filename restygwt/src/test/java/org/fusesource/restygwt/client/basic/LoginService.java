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

package org.fusesource.restygwt.client.basic;

import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import org.fusesource.restygwt.client.action.PostAction;
import org.fusesource.restygwt.client.basic.LoginService.LoginContext;
import org.fusesource.restygwt.client.basic.LoginService.LoginResult;

/**
 * @author <a href="mailto:jlarsen@ecologicanalytics.com">Jeff Larsen</a>
 * Jan 20, 2011
 */
@RemoteServiceRelativePath("login")
public interface LoginService extends PostAction<LoginContext, LoginResult> {

    public static class LoginResult {
        private boolean loggedIn;

        public void setLoggedIn(boolean loggedIn) {
            this.loggedIn = loggedIn;
        }

        public boolean getLoggedIn() {
            return loggedIn;
        }

    }

    public static class LoginContext {

        private String contextInfo = "";

        public void setContextInfo(String contextInfo) {
            this.contextInfo = contextInfo;
        }

        public String getContextInfo() {
            return contextInfo;
        }

    }

}
