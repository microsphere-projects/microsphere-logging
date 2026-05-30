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

package io.microsphere.logging.test.junit4;

import io.microsphere.annotation.Nullable;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import static io.microsphere.util.ArrayUtils.EMPTY_STRING_ARRAY;

/**
 * The {@link TestRule} to iterate Logging levels
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see LoggingLevelsStatement
 * @see TestRule
 * @since 1.0.0
 */
public class LoggingLevelsRule implements TestRule {

    private String[] levels;

    protected LoggingLevelsRule(@Nullable String... levels) {
        this.levels = levels;
    }

    /**
     * {@inheritDoc}
     *
     * <h3>Example Usage</h3>
     * <pre>{@code
     *   LoggingLevelsRule rule = LoggingLevelsRule.levels("TRACE", "DEBUG", "INFO");
     *   // JUnit 4 applies the rule, iterating through each logging level
     * }</pre>
     */
    @Override
    public Statement apply(Statement base, Description description) {
        return new LoggingLevelsStatement(base, description, levels);
    }

    /**
     * Creates a new {@link LoggingLevelsRule} for the given logging levels.
     *
     * <h3>Example Usage</h3>
     * <pre>{@code
     *   @ClassRule
     *   public static final LoggingLevelsRule loggingLevelsRule = LoggingLevelsRule.levels("TRACE", "DEBUG", "INFO");
     * }</pre>
     * @param levels the logging levels to iterate through (e.g. "TRACE", "DEBUG", "INFO")
     * @return a new {@link LoggingLevelsRule}
     *
     */
    public static LoggingLevelsRule levels(String... levels) {
        return new LoggingLevelsRule(levels);
    }
}
