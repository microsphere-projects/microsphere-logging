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

package io.microsphere.logging.jdk;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.microsphere.logging.DefaultLoggingLevelsResolverTest.JAVA_LOGGING_LEVELS;
import static io.microsphere.logging.jdk.StandardLogging.ALL_LEVELS;
import static io.microsphere.logging.jdk.StandardLogging.loggingMXBean;
import static io.microsphere.util.StringUtils.isBlank;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * {@link StandardLogging} Test
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see StandardLogging
 * @since 1.0.0
 */
class StandardLoggingTest {

    private StandardLogging standardLogging;

    @BeforeEach
    void setUp() {
        this.standardLogging = new StandardLogging();
    }

    @Test
    void testConstants() {
        assertEquals(JAVA_LOGGING_LEVELS, ALL_LEVELS);
    }

    @Test
    void testGetLoggerNames() {
        assertEquals(this.standardLogging.getLoggerNames(), loggingMXBean.getLoggerNames());
    }

    @Test
    void testGetSupportedLoggingLevels() {
        assertEquals(JAVA_LOGGING_LEVELS, this.standardLogging.getSupportedLoggingLevels());
    }

    @Test
    void testGetLoggerLevel() {
        List<String> loggerNames = this.standardLogging.getLoggerNames();
        loggerNames.forEach(loggerName -> assertEquals(this.standardLogging.getLoggerLevel(loggerName), loggingMXBean.getLoggerLevel(loggerName)));
    }

    @Test
    void testSetLoggerLevel() {
        List<String> loggerNames = this.standardLogging.getLoggerNames();
        for (String loggerName : loggerNames) {
            String level = this.standardLogging.getLoggerLevel(loggerName);
            if (isBlank(level)) {
                continue;
            }
            for (String newLevel : ALL_LEVELS) {
                this.standardLogging.setLoggerLevel(loggerName, newLevel);
                assertEquals(newLevel, this.standardLogging.getLoggerLevel(loggerName));
            }
            this.standardLogging.setLoggerLevel(loggerName, level);
        }
    }

    @Test
    void testGetParentLoggerName() {
        List<String> loggerNames = this.standardLogging.getLoggerNames();
        for (String loggerName : loggerNames) {
            assertEquals(this.standardLogging.getParentLoggerName(loggerName), loggingMXBean.getParentLoggerName(loggerName));
        }
    }
}