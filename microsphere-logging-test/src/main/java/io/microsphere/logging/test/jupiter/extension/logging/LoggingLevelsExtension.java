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

import io.microsphere.logging.Logging;
import org.junit.jupiter.api.extension.ClassTemplateInvocationContext;
import org.junit.jupiter.api.extension.ClassTemplateInvocationContextProvider;
import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestTemplateInvocationContext;
import org.junit.jupiter.api.extension.TestTemplateInvocationContextProvider;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.List;
import java.util.stream.Stream;

import static io.microsphere.collection.ListUtils.newArrayList;
import static io.microsphere.logging.LoggingUtils.loadAll;
import static io.microsphere.reflect.JavaType.from;
import static io.microsphere.reflect.MethodUtils.invokeMethod;
import static io.microsphere.util.ArrayUtils.isEmpty;
import static io.microsphere.util.ArrayUtils.ofArray;
import static io.microsphere.util.ClassLoaderUtils.getClassLoader;
import static java.util.stream.Stream.of;

/**
 * The {@link Extension} repeats the execution with the specified logging levels.
 *
 * @param <A> the type of annotation
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see LoggingLevelTemplateInvocationContext
 * @see ClassTemplateInvocationContextProvider
 * @see TestTemplateInvocationContextProvider
 * @see TestTemplateInvocationContext
 * @since 1.0.0
 */
public abstract class LoggingLevelsExtension<A extends Annotation> implements ClassTemplateInvocationContextProvider,
        TestTemplateInvocationContextProvider {

    private final Class<A> annotationType;

    public LoggingLevelsExtension() {
        this.annotationType = resolveAnnotationType();
    }

    private Class<A> resolveAnnotationType() {
        return from(getClass())
                .as(LoggingLevelsExtension.class)
                .getGenericType(0)
                .toClass();
    }

    @Override
    public boolean supportsClassTemplate(ExtensionContext context) {
        return true;
    }

    @Override
    public boolean supportsTestTemplate(ExtensionContext context) {
        return true;
    }

    @Override
    public Stream<TestTemplateInvocationContext> provideTestTemplateInvocationContexts(ExtensionContext context) {
        return (Stream) provideInvocationContexts(context, false);
    }

    protected LoggingLevelsAttributes getLoggingLevelsAttributes(ExtensionContext context, boolean isClassTemplate) {
        AnnotatedElement annotatedElement = isClassTemplate ? context.getRequiredTestClass() : context.getRequiredTestMethod();
        A annotation = annotatedElement.getAnnotation(annotationType);
        LoggingLevelsAttributes attributes = new LoggingLevelsAttributes();
        attributes.loggingClasses = invokeMethod(annotation, "loggingClasses");
        attributes.levels = invokeMethod(annotation, "levels");
        return attributes;
    }

    @Override
    public Stream<? extends ClassTemplateInvocationContext> provideClassTemplateInvocationContexts(ExtensionContext context) {
        return provideInvocationContexts(context, true);
    }

    protected Stream<LoggingLevelTemplateInvocationContext> provideInvocationContexts(ExtensionContext context, boolean isClassTemplate) {
        ClassLoader classLoader = getClassLoader(getClass());
        List<Logging> loggins = loadAll(classLoader);
        LoggingLevelsAttributes attributes = getLoggingLevelsAttributes(context, isClassTemplate);
        String[] levels = attributes.levels;
        String[] loggerNames = getLoggerNames(context, attributes);

        int length = levels.length;

        List<LoggingLevelTemplateInvocationContext> contexts = newArrayList(length);

        for (int i = 0; i < length; i++) {
            String level = levels[i];
            contexts.add(i, new LoggingLevelTemplateInvocationContext(loggins, loggerNames, level, i, isClassTemplate));
        }

        return contexts.stream();
    }

    protected String[] getLoggerNames(ExtensionContext context, LoggingLevelsAttributes attributes) {
        Class<?>[] loggingClasses = attributes.loggingClasses;
        if (isEmpty(loggingClasses)) {
            Class<?> testClass = context.getRequiredTestClass();
            return ofArray(testClass.getPackage().getName());
        }
        return of(loggingClasses).map(Class::getName).toArray(String[]::new);
    }
}