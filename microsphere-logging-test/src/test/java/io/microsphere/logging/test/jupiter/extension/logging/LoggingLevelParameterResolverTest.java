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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ParameterContext;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link LoggingLevelParameterResolver} Test
 *
 * @see LoggingLevelParameterResolver
 * @since 1.0.0
 */
class LoggingLevelParameterResolverTest {

    private final LoggingLevelParameterResolver resolver = new LoggingLevelParameterResolver("DEBUG", 2);

    // Helper methods to provide reflective Parameter instances
    @SuppressWarnings("unused")
    static void stringParam(String s) {
    }

    @SuppressWarnings("unused")
    static void intParam(int i) {
    }

    @SuppressWarnings("unused")
    static void doubleParam(double d) {
    }

    @SuppressWarnings("unused")
    static void objectParam(Object o) {
    }

    @Test
    void testSupportsParameterWithStringType() throws Exception {
        Parameter param = getParameter("stringParam", String.class);
        assertTrue(resolver.supportsParameter(parameterContext(param), null));
    }

    @Test
    void testSupportsParameterWithIntType() throws Exception {
        Parameter param = getParameter("intParam", int.class);
        assertTrue(resolver.supportsParameter(parameterContext(param), null));
    }

    @Test
    void testSupportsParameterWithDoubleType() throws Exception {
        Parameter param = getParameter("doubleParam", double.class);
        assertFalse(resolver.supportsParameter(parameterContext(param), null));
    }

    @Test
    void testSupportsParameterWithObjectType() throws Exception {
        Parameter param = getParameter("objectParam", Object.class);
        assertFalse(resolver.supportsParameter(parameterContext(param), null));
    }

    @Test
    void testResolveParameterWithStringType() throws Exception {
        Parameter param = getParameter("stringParam", String.class);
        Object result = resolver.resolveParameter(parameterContext(param), null);
        assertEquals("DEBUG", result);
    }

    @Test
    void testResolveParameterWithIntType() throws Exception {
        Parameter param = getParameter("intParam", int.class);
        Object result = resolver.resolveParameter(parameterContext(param), null);
        assertEquals(2, result);
    }

    private static Parameter getParameter(String methodName, Class<?> paramType) throws NoSuchMethodException {
        return LoggingLevelParameterResolverTest.class
                .getDeclaredMethod(methodName, paramType)
                .getParameters()[0];
    }

    /**
     * Minimal {@link ParameterContext} stub that delegates to a real {@link Parameter}.
     */
    private static ParameterContext parameterContext(Parameter parameter) {
        return new ParameterContext() {

            @Override
            public Parameter getParameter() {
                return parameter;
            }

            @Override
            public int getIndex() {
                return 0;
            }

            @Override
            public Optional<Object> getTarget() {
                return Optional.empty();
            }

            @Override
            public boolean isAnnotated(Class<? extends Annotation> annotationType) {
                return false;
            }

            @Override
            public <A extends Annotation> Optional<A> findAnnotation(Class<A> annotationType) {
                return Optional.empty();
            }

            @Override
            public <A extends Annotation> List<A> findRepeatableAnnotations(Class<A> annotationType) {
                return List.of();
            }
        };
    }
}
