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

package io.microsphere.logging.test.jupiter;

import io.microsphere.logging.Logging;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.microsphere.logging.LoggingUtils.loadAll;
import static org.junit.Assert.assertNotEquals;

/**
 * {@link LoggingLevelsTest} Test
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see LoggingLevelsTest
 * @since 1.0.0
 */
class LoggingLevelsTestTest {

    @Test
    @LoggingLevelsTest(loggingClasses = {LoggingLevelsTest.class, LoggingLevelsTestTest.class},
            levels = {"WARN", "ERROR"})
    void test() {
        List<Logging> loggins = loadAll();
        for (Logging logging : loggins) {
            String loggerName = LoggingLevelsTest.class.getName();
            assertNotEquals("INFO", logging.getLoggerLevel(loggerName));
        }
    }
}