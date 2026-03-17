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
import java.util.Set;

import static io.microsphere.collection.Sets.ofSet;
import static io.microsphere.logging.LoggingUtils.load;
import static io.microsphere.logging.jdk.JavaLogging.ALL_LEVELS;
import static io.microsphere.logging.jdk.JavaLogging.PRIORITY;
import static io.microsphere.logging.jdk.JavaLogging.ROOT_LOGGER_NAME;
import static io.microsphere.logging.jdk.JavaLogging.loggingMXBean;
import static io.microsphere.util.StringUtils.isBlank;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * {@link JavaLogging} Test
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see JavaLogging
 * @since 1.0.0
 */
class JavaLoggingTest {

    /**
     * @see java.util.logging.Level
     */
    public static final Set<String> JAVA_LOGGING_LEVELS = ofSet("OFF", "SEVERE", "WARNING", "INFO", "CONFIG", "FINE", "FINER", "FINEST", "ALL");

    private JavaLogging logging;

    @BeforeEach
    void setUp() {
        this.logging = (JavaLogging) load();
    }

    @Test
    void testConstants() {
        assertEquals("", ROOT_LOGGER_NAME);
        assertEquals(10, PRIORITY);
        assertEquals(JAVA_LOGGING_LEVELS, ALL_LEVELS);
    }

    @Test
    void testGetRootLoggerName() {
        assertEquals(ROOT_LOGGER_NAME, this.logging.getRootLoggerName());
    }

    @Test
    void testGetLoggerNames() {
        assertEquals(this.logging.getLoggerNames(), loggingMXBean.getLoggerNames());
    }

    @Test
    void testGetSupportedLoggingLevels() {
        assertEquals(JAVA_LOGGING_LEVELS, this.logging.getSupportedLoggingLevels());
    }

    @Test
    void testGetLoggerLevel() {
        List<String> loggerNames = this.logging.getLoggerNames();
        loggerNames.forEach(loggerName -> assertEquals(this.logging.getLoggerLevel(loggerName), loggingMXBean.getLoggerLevel(loggerName)));
    }

    @Test
    void testSetLoggerLevel() {
        List<String> loggerNames = this.logging.getLoggerNames();
        for (String loggerName : loggerNames) {
            String level = this.logging.getLoggerLevel(loggerName);
            if (isBlank(level)) {
                continue;
            }
            for (String newLevel : ALL_LEVELS) {
                this.logging.setLoggerLevel(loggerName, newLevel);
                assertEquals(newLevel, this.logging.getLoggerLevel(loggerName));
            }
            this.logging.setLoggerLevel(loggerName, level);
        }
    }

    @Test
    void testGetParentLoggerName() {
        List<String> loggerNames = this.logging.getLoggerNames();
        for (String loggerName : loggerNames) {
            assertEquals(this.logging.getParentLoggerName(loggerName), loggingMXBean.getParentLoggerName(loggerName));
        }
    }

    @Test
    void testGetName() {
        assertEquals("Java Logging", this.logging.getName());
    }

    @Test
    void testGetPriority() {
        assertEquals(PRIORITY, this.logging.getPriority());
    }
}