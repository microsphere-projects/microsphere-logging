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
import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestTemplateInvocationContext;

import java.util.ArrayList;
import java.util.List;

/**
 * The facade class of {@link ClassTemplateInvocationContext} and {@link TestTemplateInvocationContext} to set the
 * logging level.
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see ClassTemplateInvocationContext
 * @see TestTemplateInvocationContext
 * @since 1.0.0
 */
class LoggingLevelTemplateInvocationContext implements ClassTemplateInvocationContext, TestTemplateInvocationContext {

    private final List<Logging> loggins;

    private final String[] loggerName;

    private final String level;

    private final int index;

    private final boolean isClassTemplate;

    LoggingLevelTemplateInvocationContext(List<Logging> loggings, String[] loggerName, String level, int index, boolean isClassTemplate) {
        this.loggins = loggings;
        this.loggerName = loggerName;
        this.level = level;
        this.index = index;
        this.isClassTemplate = isClassTemplate;
    }

    @Override
    public String getDisplayName(int invocationIndex) {
        return "[" + this.level + "]";
    }

    @Override
    public List<Extension> getAdditionalExtensions() {
        List<Extension> extensions = new ArrayList<>(this.isClassTemplate ? 2 : 1);
        extensions.add(new LoggingLevelCallback(this.loggins, this.loggerName, this.level));
        if (this.isClassTemplate) {
            extensions.add(new LoggingLevelParameterResolver(this.level, this.index));
        }
        return extensions;
    }

    public void prepareInvocation(ExtensionContext context) {
    }
}
