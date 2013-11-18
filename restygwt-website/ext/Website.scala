/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import org.fusesource.scalate.RenderContext

package

/**
 * <p>
 * </p>
 *
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 */
object Website {

  val project_name= "RestyGWT"
  val project_slogan= "Making Restful Services Accessible to GWT Applications"
  val project_id= "restygwt"
  val project_jira_key= "RESTYGWT"
  val project_issue_url= "http://github.com/chirino/resty-gwt/issues"
  val project_forums_url= "http://groups.google.com/group/restygwt"
  val project_wiki_url= "http://github.com/chirino/resty-gwt/"
  val project_logo= "/images/restygwt-logo.png"
  val project_version= "1.4"
  val project_snapshot_version= "1.5-SNAPSHOT"

  val project_versions = List(
        project_version,
        "1.3",
        "1.2",
        "1.1",
        "1.0")  


  val project_keywords= "rest,gwt,restygwt,java,javascript,json,xml,dto"

  // -------------------------------------------------------------------
  val github_page = "http://github.com/chirino/resty-gwt"
  val git_user_url = "git://github.com/chirino/resty-gwt.git"
  val git_commiter_url = "git@github.com:chirino/resty-gwt.git"
  
  val project_maven_groupId= "org.fusesource.restygwt"
  val project_maven_artifactId= "restygwt"

  val website_base_url= "http://restygwt.fusesource.org"
}
