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

package io.microsphere.logging.log4j2;


import io.microsphere.logging.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static io.microsphere.collection.Sets.ofSet;
import static io.microsphere.logging.LoggerFactory.getLogger;
import static org.apache.logging.log4j.LogManager.ROOT_LOGGER_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * {@link Log4j2Logging} Test
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see Log4j2Logging
 * @since 1.0.0
 */
class Log4j2LoggingTest {

    private static final Logger logger = getLogger(Log4j2LoggingTest.class);

    private Log4j2Logging logging;

    /**
     * @see org.apache.logging.log4j.Level
     */
    public static final Set<String> LOG4J2_LEVELS = ofSet("OFF", "FATAL", "ERROR", "WARN", "INFO", "DEBUG", "TRACE", "ALL");

    @BeforeEach
    void setUp() {
        this.logging = new Log4j2Logging();
    }

    @Test
    void testGetLoggerNames() {
        assertFalse(this.logging.getLoggerNames().isEmpty());
    }

    @Test
    void testGetSupportedLoggingLevels() {
        assertEquals(LOG4J2_LEVELS, this.logging.getSupportedLoggingLevels());
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
        String loggerName = "io.microsphere.logging.log4j2.Log4j2Logging";
        String level = "DEBUG";
        this.logging.setLoggerLevel(loggerName, level);
        assertEquals(level, this.logging.getLoggerLevel(loggerName));
    }

    @Test
    void testGetParentLoggerName() {
        String loggerName = "io.microsphere.logging.log4j2.Log4j2Logging";
        assertEquals("io.microsphere.logging.log4j2", this.logging.getParentLoggerName(loggerName));
        assertEquals("io.microsphere.logging", this.logging.getParentLoggerName("io.microsphere.logging.log4j2"));
        assertEquals("io.microsphere", this.logging.getParentLoggerName("io.microsphere.logging"));
        assertEquals("io", this.logging.getParentLoggerName("io.microsphere"));
        assertEquals(ROOT_LOGGER_NAME, this.logging.getParentLoggerName("io"));
        assertNull(this.logging.getParentLoggerName(ROOT_LOGGER_NAME));
    }

    @Test
    void testGetName() {
        assertEquals("Log4j2", this.logging.getName());
    }
}