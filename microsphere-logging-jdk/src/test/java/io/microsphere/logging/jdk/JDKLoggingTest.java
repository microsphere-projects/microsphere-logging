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


import io.microsphere.logging.Logging;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static io.microsphere.collection.Sets.ofSet;
import static io.microsphere.logging.jdk.JDKLogging.ALL_LEVELS;
import static io.microsphere.logging.jdk.JDKLogging.PRIORITY;
import static io.microsphere.logging.jdk.JDKLogging.loggingMXBean;
import static io.microsphere.util.ServiceLoaderUtils.loadFirstService;
import static io.microsphere.util.StringUtils.isBlank;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * {@link JDKLogging} Test
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see JDKLogging
 * @since 1.0.0
 */
class JDKLoggingTest {

    /**
     * @see java.util.logging.Level
     */
    public static final Set<String> JAVA_LOGGING_LEVELS = ofSet("OFF", "SEVERE", "WARNING", "INFO", "CONFIG", "FINE", "FINER", "FINEST", "ALL");

    private JDKLogging JDKLogging;

    @BeforeEach
    void setUp() {
        this.JDKLogging = (JDKLogging) loadFirstService(Logging.class);
    }

    @Test
    void testConstants() {
        assertEquals(JAVA_LOGGING_LEVELS, ALL_LEVELS);
    }

    @Test
    void testGetLoggerNames() {
        assertEquals(this.JDKLogging.getLoggerNames(), loggingMXBean.getLoggerNames());
    }

    @Test
    void testGetSupportedLoggingLevels() {
        assertEquals(JAVA_LOGGING_LEVELS, this.JDKLogging.getSupportedLoggingLevels());
    }

    @Test
    void testGetLoggerLevel() {
        List<String> loggerNames = this.JDKLogging.getLoggerNames();
        loggerNames.forEach(loggerName -> assertEquals(this.JDKLogging.getLoggerLevel(loggerName), loggingMXBean.getLoggerLevel(loggerName)));
    }

    @Test
    void testSetLoggerLevel() {
        List<String> loggerNames = this.JDKLogging.getLoggerNames();
        for (String loggerName : loggerNames) {
            String level = this.JDKLogging.getLoggerLevel(loggerName);
            if (isBlank(level)) {
                continue;
            }
            for (String newLevel : ALL_LEVELS) {
                this.JDKLogging.setLoggerLevel(loggerName, newLevel);
                assertEquals(newLevel, this.JDKLogging.getLoggerLevel(loggerName));
            }
            this.JDKLogging.setLoggerLevel(loggerName, level);
        }
    }

    @Test
    void testGetParentLoggerName() {
        List<String> loggerNames = this.JDKLogging.getLoggerNames();
        for (String loggerName : loggerNames) {
            assertEquals(this.JDKLogging.getParentLoggerName(loggerName), loggingMXBean.getParentLoggerName(loggerName));
        }
    }

    @Test
    void testGetName() {
        assertEquals("Java Logging", this.JDKLogging.getName());
    }

    @Test
    void testGetPriority() {
        assertEquals(10, PRIORITY);
        assertEquals(PRIORITY, this.JDKLogging.getPriority());
    }
}