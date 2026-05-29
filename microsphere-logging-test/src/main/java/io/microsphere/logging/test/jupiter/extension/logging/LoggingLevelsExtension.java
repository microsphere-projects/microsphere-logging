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
import java.util.Map;
import java.util.stream.Stream;

import static io.microsphere.collection.ListUtils.newArrayList;
import static io.microsphere.logging.LoggingUtils.loadAll;
import static io.microsphere.reflect.JavaType.from;
import static io.microsphere.util.AnnotationUtils.findMetaAnnotation;
import static io.microsphere.util.AnnotationUtils.getAttributesMap;
import static io.microsphere.util.ArrayUtils.isEmpty;
import static io.microsphere.util.ArrayUtils.length;
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

    /**
     * Creates a new {@link LoggingLevelsExtension} resolving the annotation type from the generic type parameter.
     *
     * <h3>Example Usage</h3>
     * <pre>{@code
     *   // Typically extended by concrete subclasses:
     *   public class LoggingLevelsTestExtension extends LoggingLevelsExtension<LoggingLevelsTest> {}
     * }</pre>
     */
    public LoggingLevelsExtension() {
        this.annotationType = resolveAnnotationType();
    }

    private Class<A> resolveAnnotationType() {
        return from(getClass())
                .as(LoggingLevelsExtension.class)
                .getGenericType(0)
                .toClass();
    }

    /**
     * {@inheritDoc}
     *
     * <h3>Example Usage</h3>
     * <pre>{@code
     *   // Always returns true so this extension supports any class template context
     *   boolean supported = extension.supportsClassTemplate(context);
     * }</pre>
     */
    @Override
    public boolean supportsClassTemplate(ExtensionContext context) {
        return true;
    }

    /**
     * {@inheritDoc}
     *
     * <h3>Example Usage</h3>
     * <pre>{@code
     *   // Always returns true so this extension supports any test template context
     *   boolean supported = extension.supportsTestTemplate(context);
     * }</pre>
     */
    @Override
    public boolean supportsTestTemplate(ExtensionContext context) {
        return true;
    }

    /**
     * {@inheritDoc}
     *
     * <h3>Example Usage</h3>
     * <pre>{@code
     *   // Provides one invocation context per logging level declared in @LoggingLevelsTest
     *   Stream<TestTemplateInvocationContext> contexts =
     *       extension.provideTestTemplateInvocationContexts(extensionContext);
     * }</pre>
     */
    @Override
    public Stream<TestTemplateInvocationContext> provideTestTemplateInvocationContexts(ExtensionContext context) {
        return (Stream) provideInvocationContexts(context, false);
    }

    /**
     * Resolves the {@link LoggingLevelsAttributes} from the annotation on the test class or method.
     *
     * @param context         the current {@link ExtensionContext}
     * @param isClassTemplate {@code true} if resolving from a class template; {@code false} for a test template
     * @return the resolved {@link LoggingLevelsAttributes}
     *
     * <h3>Example Usage</h3>
     * <pre>{@code
     *   LoggingLevelsAttributes attrs = extension.getLoggingLevelsAttributes(context, false);
     *   String[] levels = attrs.levels; // e.g. ["TRACE", "DEBUG", "INFO"]
     * }</pre>
     */
    protected LoggingLevelsAttributes getLoggingLevelsAttributes(ExtensionContext context, boolean isClassTemplate) {
        AnnotatedElement annotatedElement = isClassTemplate ? context.getRequiredTestClass() : context.getRequiredTestMethod();
        A annotation = annotatedElement.getAnnotation(annotationType);
        if (annotation == null) { // Try to find meta-annotation
            annotation = findMetaAnnotation(annotatedElement, annotationType);
        }
        Map<String, Object> annotationAttributes = getAttributesMap(annotation);
        LoggingLevelsAttributes attributes = new LoggingLevelsAttributes();
        attributes.loggingClasses = (Class<?>[]) annotationAttributes.get("loggingClasses");
        attributes.levels = (String[]) annotationAttributes.get("levels");
        return attributes;
    }

    /**
     * {@inheritDoc}
     *
     * <h3>Example Usage</h3>
     * <pre>{@code
     *   // Provides one ClassTemplateInvocationContext per logging level declared in @LoggingLevelsClass
     *   Stream<ClassTemplateInvocationContext> contexts =
     *       extension.provideClassTemplateInvocationContexts(extensionContext);
     * }</pre>
     */
    @Override
    public Stream<? extends ClassTemplateInvocationContext> provideClassTemplateInvocationContexts(ExtensionContext context) {
        return provideInvocationContexts(context, true);
    }

    /**
     * Builds the stream of {@link LoggingLevelTemplateInvocationContext} instances for the given levels.
     *
     * @param context         the current {@link ExtensionContext}
     * @param isClassTemplate {@code true} for class templates; {@code false} for test templates
     * @return a {@link Stream} of invocation contexts, one per logging level
     *
     * <h3>Example Usage</h3>
     * <pre>{@code
     *   Stream<LoggingLevelTemplateInvocationContext> stream =
     *       extension.provideInvocationContexts(context, false);
     * }</pre>
     */
    protected Stream<LoggingLevelTemplateInvocationContext> provideInvocationContexts(ExtensionContext context, boolean isClassTemplate) {
        ClassLoader classLoader = getClassLoader(getClass());
        List<Logging> loggins = loadAll(classLoader);
        LoggingLevelsAttributes attributes = getLoggingLevelsAttributes(context, isClassTemplate);
        String[] levels = attributes.levels;
        String[] loggerNames = getLoggerNames(context, attributes);

        int length = length(levels);

        List<LoggingLevelTemplateInvocationContext> contexts = newArrayList(length);

        for (int i = 0; i < length; i++) {
            String level = levels[i];
            contexts.add(i, new LoggingLevelTemplateInvocationContext(loggins, loggerNames, level, i, isClassTemplate));
        }

        return contexts.stream();
    }

    /**
     * Resolves the logger names from the annotation attributes or falls back to the test class package name.
     *
     * @param context    the current {@link ExtensionContext}
     * @param attributes the resolved {@link LoggingLevelsAttributes}
     * @return an array of logger names
     *
     * <h3>Example Usage</h3>
     * <pre>{@code
     *   String[] loggerNames = extension.getLoggerNames(context, attributes);
     *   // If loggingClasses is empty, returns [testClass.getPackage().getName()]
     * }</pre>
     */
    protected String[] getLoggerNames(ExtensionContext context, LoggingLevelsAttributes attributes) {
        Class<?>[] loggingClasses = attributes.loggingClasses;
        if (isEmpty(loggingClasses)) {
            Class<?> testClass = context.getRequiredTestClass();
            return ofArray(testClass.getPackage().getName());
        }
        return of(loggingClasses).map(Class::getName).toArray(String[]::new);
    }
}
