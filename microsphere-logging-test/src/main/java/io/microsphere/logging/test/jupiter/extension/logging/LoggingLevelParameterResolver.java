/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.microsphere.logging.test.jupiter.extension.logging;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

import java.lang.reflect.Parameter;

/**
 * The {@link ParameterResolver} class to resolve the parameter
 * for level(String) or index(int).
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see ParameterResolver
 * @since 1.0.0
 */
class LoggingLevelParameterResolver implements ParameterResolver {

    private final String level;

    private final int index;

    /**
     * Creates a new {@link LoggingLevelParameterResolver} with the given level and index.
     *
     * @param level the logging level string (e.g. "DEBUG", "INFO")
     * @param index the zero-based index of the level in the levels array
     *
     * <h3>Example Usage</h3>
     * <pre>{@code
     *   LoggingLevelParameterResolver resolver = new LoggingLevelParameterResolver("DEBUG", 0);
     * }</pre>
     */
    LoggingLevelParameterResolver(String level, int index) {
        this.level = level;
        this.index = index;
    }

    /**
     * {@inheritDoc}
     *
     * <h3>Example Usage</h3>
     * <pre>{@code
     *   // Returns true if the parameter type is String (for level) or int (for index)
     *   boolean supported = resolver.supportsParameter(parameterContext, extensionContext);
     * }</pre>
     */
    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        Parameter parameter = parameterContext.getParameter();
        Class<?> parameterType = parameter.getType();
        return String.class.equals(parameterType) || int.class.equals(parameterType);
    }

    /**
     * {@inheritDoc}
     *
     * <h3>Example Usage</h3>
     * <pre>{@code
     *   // Returns the level string for String parameters, or the index for int parameters
     *   Object value = resolver.resolveParameter(parameterContext, extensionContext);
     * }</pre>
     */
    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        Parameter parameter = parameterContext.getParameter();
        Class<?> parameterType = parameter.getType();
        if (String.class.equals(parameterType)) {
            return level;
        }
        return index;
    }
}