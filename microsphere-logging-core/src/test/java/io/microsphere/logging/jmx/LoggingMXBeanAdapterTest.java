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

package io.microsphere.logging.jmx;


import io.microsphere.logging.TestingLogging;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.microsphere.util.StringUtils.isBlank;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link LoggingMXBeanAdapter} Test
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see LoggingMXBeanAdapter
 * @since 1.0.0
 */
class LoggingMXBeanAdapterTest {

    private TestingLogging logging;

    private LoggingMXBeanAdapter loggingMXBeanAdapter;

    @BeforeEach
    void setUp() {
        this.logging = new TestingLogging();
        this.logging.init(
                "io.microsphere.logging", "DEBUG",
                "io.microsphere", "INFO",
                "io", "WARN"
        );
        this.loggingMXBeanAdapter = new LoggingMXBeanAdapter(logging);
    }

    @Test
    void testGetLoggerNames() {
        assertEquals(this.logging.getLoggerNames(), this.loggingMXBeanAdapter.getLoggerNames());
    }

    @Test
    void testGetLoggerLevel() {
        List<String> loggerNames = this.logging.getLoggerNames();
        loggerNames.forEach(loggerName -> {
            assertEquals(this.logging.getLoggerLevel(loggerName), this.loggingMXBeanAdapter.getLoggerLevel(loggerName));
        });
    }

    @Test
    void testSetLoggerLevel() {
        List<String> loggerNames = this.logging.getLoggerNames();
        for (String loggerName : loggerNames) {
            String level = this.logging.getLoggerLevel(loggerName);
            if (isBlank(level)) {
                continue;
            }
            for (String newLevel : this.logging.getSupportedLoggingLevels()) {
                this.loggingMXBeanAdapter.setLoggerLevel(loggerName, newLevel);
                assertEquals(newLevel, this.logging.getLoggerLevel(loggerName));
            }
            this.loggingMXBeanAdapter.setLoggerLevel(loggerName, level);
        }
    }

    @Test
    void testGetParentLoggerName() {
        List<String> loggerNames = this.logging.getLoggerNames();
        loggerNames.forEach(loggerName -> {
            assertEquals(this.logging.getParentLoggerName(loggerName), this.loggingMXBeanAdapter.getParentLoggerName(loggerName));
        });
    }

    @Test
    void testGetDelegate() {
        assertSame(this.logging, this.loggingMXBeanAdapter.getDelegate());
    }

    @Test
    void testHashCode() {
        assertEquals(this.logging.hashCode(), this.loggingMXBeanAdapter.hashCode());
    }

    @Test
    void testEquals() {
        assertTrue(this.loggingMXBeanAdapter.equals(this.logging));
    }

    @Test
    void testToString() {
        assertEquals("LoggingMXBeanAdapter[" + this.logging.toString() + "]", this.loggingMXBeanAdapter.toString());
    }
}