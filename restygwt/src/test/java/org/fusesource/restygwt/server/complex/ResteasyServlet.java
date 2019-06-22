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

package org.fusesource.restygwt.server.complex;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.ext.ParamConverter;
import javax.ws.rs.ext.ParamConverterProvider;
import javax.ws.rs.ext.Provider;

import org.fusesource.restygwt.client.complex.ResteasyService;
import org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher;
import org.jboss.resteasy.spi.Registry;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

public class ResteasyServlet extends HttpServletDispatcher {

    private static final long serialVersionUID = 1L;

    public static class ResteasyServiceImpl implements ResteasyService {
        @Nullable
        @Override
        public String getString(@Nullable String string) {
            return string;
        }

        @Nullable
        @Override
        public String postString(@Nullable String string) {
            return string;
        }

        @Nullable
        @Override
        public String getStringAsPlainText(@Nullable String string) {
            return string;
        }

        @Nullable
        @Override
        public String postStringAsPlainText(@Nullable String string) {
            return string;
        }

        @Nonnull
        @Override
        public Bean postBean(@Nonnull Bean bean) {
            return bean;
        }

        @Nonnull
        @Override
        public List<Bean> postBeans(@Nonnull List<Bean> beans) {
            return beans;
        }

        @Nonnull
        @Override
        public Bean postBeanAsFormParam(@Nonnull Bean bean) {
            return bean;
        }

        @Nonnull
        @Override
        public List<Bean> postBeansAsFormParams(@Nonnull Bean bean0, @Nonnull Bean bean1) {
            return Arrays.asList(bean0, bean1);
        }

        @Nonnull
        @Override
        public List<Bean> postBeansAsFormParam(@Nonnull List<Bean> beans) {
            return beans;
        }

        @Nonnull
        @Override
        public String postThrowable(@Nonnull Throwable throwable) {
            try {
                return new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(throwable);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }

        @Nonnull
        @Override
        public String postCustomException(@Nonnull CustomException customException) {
            return postThrowable(customException);
        }

        @Nonnull
        @Override
        public String postCustomExceptionAsFormParam(@Nonnull CustomException customException) {
            return postThrowable(customException);
        }

        @Nonnull
        @Override
        public String postThrowableAsFormParam(@Nonnull Throwable throwable) {
            return postThrowable(throwable);
        }
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);

        ResteasyProviderFactory providerFactory = servletContainerDispatcher.getDispatcher().getProviderFactory();
        providerFactory.registerProvider(JsonStringProvider.class);
        providerFactory.registerProvider(JacksonJsonParamConverterProvider.class);

        Registry registry = servletContainerDispatcher.getDispatcher().getRegistry();
        registry.addPerRequestResource(ResteasyServiceImpl.class,
                "/org.fusesource.restygwt.ResteasyGwtJacksonTestGwt.JUnit");
    }

    @Provider
    public static class JacksonJsonParamConverterProvider implements ParamConverterProvider {
        @Override
        public <T> ParamConverter<T> getConverter(final Class<T> rawType, Type genericType, Annotation[] annotations) {
            if (String.class.equals(rawType)) {
                return null;
            }

            final ObjectMapper mapper = new ObjectMapper();
            return new ParamConverter<T>() {
                @Override
                public T fromString(String value) {
                    try {
                        return mapper.reader(rawType).readValue(value);
                    } catch (JsonParseException e) {
                        throw new RuntimeException(String.format("Cannot deserialize \nrawType = %s \nvalue = %s",
                                rawType.getSimpleName(), value), e);
                    } catch (IOException e) {
                        throw new ProcessingException(e);
                    }
                }

                @Override
                public String toString(T value) {
                    try {
                        return mapper.writer().writeValueAsString(value);
                    } catch (JsonProcessingException e) {
                        throw new ProcessingException(e);
                    }
                }
            };
        }
    }
}