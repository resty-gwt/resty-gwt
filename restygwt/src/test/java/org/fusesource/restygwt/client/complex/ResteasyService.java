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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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
    @Nullable
    String getString(@Nullable @QueryParam("string") String string);

    @POST
    @Path("postString")
    @Nullable
    String postString(@Nullable @FormParam("string") String string);

    @POST
    @Path("postBean")
    @Nonnull
    Bean postBean(@Nonnull Bean bean);

    @POST
    @Path("postBeans")
    @Nonnull
    List<Bean> postBeans(@Nonnull List<Bean> beans);

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Path("postBeanAsFormParam")
    @Nonnull
    Bean postBeanAsFormParam(@Nonnull @FormParam("bean") Bean bean);

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Path("postBeansAsFormParams")
    @Nonnull
    List<Bean> postBeansAsFormParams(@Nonnull @FormParam("bean0") Bean bean0, @Nonnull @FormParam("bean1") Bean bean1);

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Path("postBeansAsFormParam")
    @Nonnull
    List<Bean> postBeansAsFormParam(@Nonnull @FormParam("beans") List<Bean> beans);

    @POST
    @Path("postThrowable")
    @Nonnull
    String postThrowable(@Nonnull Throwable throwable);

    @POST
    @Path("postCustomException")
    @Nonnull
    String postCustomException(@Nonnull CustomException customException);

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Path("postCustomExceptionAsFormParam")
    @Nonnull
    String postCustomExceptionAsFormParam(@Nonnull @FormParam("customException") CustomException customException);

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Path("postThrowableAsFormParam")
    @Nonnull
    String postThrowableAsFormParam(@Nonnull @FormParam("throwable") Throwable throwable);

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