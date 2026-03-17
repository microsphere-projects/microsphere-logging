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

package io.microsphere.logging.logback;


import io.microsphere.logging.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static io.microsphere.collection.Sets.ofSet;
import static io.microsphere.lang.Prioritized.NORMAL_PRIORITY;
import static io.microsphere.logging.LoggerFactory.getLogger;
import static io.microsphere.logging.LoggingUtils.load;
import static io.microsphere.logging.logback.LogbackLogging.ALL_LEVELS;
import static io.microsphere.logging.logback.LogbackLogging.PRIORITY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.slf4j.Logger.ROOT_LOGGER_NAME;

/**
 * {@link LogbackLogging} Test
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see LogbackLogging
 * @since 1.0.0
 */
class LogbackLoggingTest {

    private static final Logger logger = getLogger(LogbackLoggingTest.class);

    private LogbackLogging logging;

    /**
     * @see ch.qos.logback.classic.Level
     */
    public static final Set<String> LOGBACK_LEVELS = ofSet("OFF", "ERROR", "WARN", "INFO", "DEBUG", "TRACE", "ALL");

    @BeforeEach
    void setUp() {
        this.logging = (LogbackLogging) load();
    }

    @Test
    void testConstants() {
        assertEquals(0, PRIORITY);
        assertEquals(NORMAL_PRIORITY, PRIORITY);
        assertEquals(LOGBACK_LEVELS, ALL_LEVELS);
    }

    @Test
    void testGetRootLoggerName() {
        assertEquals(ROOT_LOGGER_NAME, this.logging.getRootLoggerName());
    }

    @Test
    void testGetLoggerNames() {
        assertFalse(this.logging.getLoggerNames().isEmpty());
    }

    @Test
    void testGetSupportedLoggingLevels() {
        assertEquals(LOGBACK_LEVELS, this.logging.getSupportedLoggingLevels());
    }

    @Test
    void testGetLoggerLevel() {
        assertEquals("TRACE", this.logging.getLoggerLevel(ROOT_LOGGER_NAME));
        assertEquals("TRACE", this.logging.getLoggerLevel("io"));
        assertEquals("TRACE", this.logging.getLoggerLevel("io.microsphere"));
        assertEquals("INFO", this.logging.getLoggerLevel("io.microsphere.logging"));
        assertEquals("INFO", this.logging.getLoggerLevel(logger.getName()));
    }

    @Test
    void testSetLoggerLevel() {
        String loggerName = "io.microsphere.logging.logback.LogbackLogging";
        String level = "DEBUG";
        this.logging.setLoggerLevel(loggerName, level);
        assertEquals(level, this.logging.getLoggerLevel(loggerName));
    }

    @Test
    void testGetParentLoggerName() {
        String loggerName = "io.microsphere.logging.logback.LogbackLogging";
        assertEquals("io.microsphere.logging.logback", this.logging.getParentLoggerName(loggerName));
        assertEquals("io.microsphere.logging", this.logging.getParentLoggerName("io.microsphere.logging.logback"));
        assertEquals("io.microsphere", this.logging.getParentLoggerName("io.microsphere.logging"));
        assertEquals("io", this.logging.getParentLoggerName("io.microsphere"));
        assertEquals(ROOT_LOGGER_NAME, this.logging.getParentLoggerName("io"));
        assertNull(this.logging.getParentLoggerName(ROOT_LOGGER_NAME));
    }

    @Test
    void testGetName() {
        assertEquals("Logback", this.logging.getName());
    }

    @Test
    void testGetPriority() {
        assertEquals(PRIORITY, this.logging.getPriority());
    }
}