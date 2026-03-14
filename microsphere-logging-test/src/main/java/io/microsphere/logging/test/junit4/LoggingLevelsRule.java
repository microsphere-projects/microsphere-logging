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


import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

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

    protected LoggingLevelsRule(String... levels) {
        this.levels = levels;
    }

    @Override
    public Statement apply(Statement base, Description description) {
        return new LoggingLevelsStatement(base, description, levels);
    }

    public static LoggingLevelsRule levels(String... levels) {
        return new LoggingLevelsRule(levels);
    }
}