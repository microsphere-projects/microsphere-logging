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

    private JavaLogging JavaLogging;

    @BeforeEach
    void setUp() {
        this.JavaLogging = (JavaLogging) load();
    }

    @Test
    void testConstants() {
        assertEquals(10, PRIORITY);
        assertEquals(JAVA_LOGGING_LEVELS, ALL_LEVELS);
    }

    @Test
    void testGetLoggerNames() {
        assertEquals(this.JavaLogging.getLoggerNames(), loggingMXBean.getLoggerNames());
    }

    @Test
    void testGetSupportedLoggingLevels() {
        assertEquals(JAVA_LOGGING_LEVELS, this.JavaLogging.getSupportedLoggingLevels());
    }

    @Test
    void testGetLoggerLevel() {
        List<String> loggerNames = this.JavaLogging.getLoggerNames();
        loggerNames.forEach(loggerName -> assertEquals(this.JavaLogging.getLoggerLevel(loggerName), loggingMXBean.getLoggerLevel(loggerName)));
    }

    @Test
    void testSetLoggerLevel() {
        List<String> loggerNames = this.JavaLogging.getLoggerNames();
        for (String loggerName : loggerNames) {
            String level = this.JavaLogging.getLoggerLevel(loggerName);
            if (isBlank(level)) {
                continue;
            }
            for (String newLevel : ALL_LEVELS) {
                this.JavaLogging.setLoggerLevel(loggerName, newLevel);
                assertEquals(newLevel, this.JavaLogging.getLoggerLevel(loggerName));
            }
            this.JavaLogging.setLoggerLevel(loggerName, level);
        }
    }

    @Test
    void testGetParentLoggerName() {
        List<String> loggerNames = this.JavaLogging.getLoggerNames();
        for (String loggerName : loggerNames) {
            assertEquals(this.JavaLogging.getParentLoggerName(loggerName), loggingMXBean.getParentLoggerName(loggerName));
        }
    }

    @Test
    void testGetName() {
        assertEquals("Java Logging", this.JavaLogging.getName());
    }

    @Test
    void testGetPriority() {
        assertEquals(PRIORITY, this.JavaLogging.getPriority());
    }
}