/**
 * Copyright (C) 2009-2016 the original author or authors.
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

package org.fusesource.restygwt.client.complex;

import java.util.Date;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.fusesource.restygwt.client.DirectRestService;

@Path("api")
public interface ResteasyService extends DirectRestService {

    @GET
    @Path("getString")
    String getString(@QueryParam("string") String string);

    @POST
    @Path("postString")
    String postString(@FormParam("string") String string);

    @POST
    @Path("postBean")
    Bean postBean(Bean bean);

    @POST
    @Path("postBeans")
    List<Bean> postBeans(List<Bean> beans);

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Path("postBeanAsFormParam")
    Bean postBeanAsFormParam(@FormParam("bean") Bean bean);

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Path("postBeansAsFormParams")
    List<Bean> postBeansAsFormParams(@FormParam("bean0") Bean bean0, @FormParam("bean1") Bean bean1);

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Path("postBeansAsFormParam")
    List<Bean> postBeansAsFormParam(@FormParam("beans") List<Bean> beans);

    @POST
    @Path("postThrowable")
    String postThrowable(Throwable throwable);

    @POST
    @Path("postCustomException")
    String postCustomException(CustomException customException);

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Path("postCustomExceptionAsFormParam")
    String postCustomExceptionAsFormParam(@FormParam("customException") CustomException customException);

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Path("postThrowableAsFormParam")
    String postThrowableAsFormParam(@FormParam("throwable") Throwable throwable);

    class Bean {
        public String string;
        public Date date;
        public Long aLong;
        public char aChar;
        public boolean aBoolean;

        public Bean() {
        }

        public Bean(String string, Date date, Long aLong, char aChar, boolean aBoolean) {
            this.string = string;
            this.date = date;
            this.aLong = aLong;
            this.aChar = aChar;
            this.aBoolean = aBoolean;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof Bean)) {
                return false;
            }

            Bean bean = (Bean) o;

            if (aChar != bean.aChar) {
                return false;
            }
            if (aBoolean != bean.aBoolean) {
                return false;
            }
            if (string != null ? !string.equals(bean.string) : bean.string != null) {
                return false;
            }
            if (date != null ? !date.equals(bean.date) : bean.date != null) {
                return false;
            }
            if (aLong != null ? !aLong.equals(bean.aLong) : bean.aLong != null) {
                return false;
            }

            return true;
        }

        @Override
        public int hashCode() {
            int result = string != null ? string.hashCode() : 0;
            result = 31 * result + (date != null ? date.hashCode() : 0);
            result = 31 * result + (aLong != null ? aLong.hashCode() : 0);
            result = 31 * result + (int) aChar;
            result = 31 * result + (aBoolean ? 1 : 0);
            return result;
        }
    }

    class CustomException extends Exception {
        public CustomException() {
        }

        public CustomException(String message) {
            super(message);
        }

        public CustomException(String message, Throwable cause) {
            super(message, cause);
        }

        public CustomException(Throwable cause) {
            super(cause);
        }
    }
}