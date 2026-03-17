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

package io.microsphere.logging;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.microsphere.collection.Lists.ofList;
import static io.microsphere.lang.Prioritized.MAX_PRIORITY;
import static io.microsphere.logging.DefaultLoggingLevelsResolverTest.JAVA_LOGGING_LEVELS;
import static io.microsphere.logging.LoggingUtils.load;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * {@link Logging} Test
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see Logging
 * @since 1.0.0
 */
class LoggingTest {

    private TestingLogging logging;

    @BeforeEach
    void setUp() {
        this.logging = (TestingLogging) load();
        this.logging.init(
                "com.acme.Test", "INFO",
                "com.acme", "DEBUG",
                "com", "TRACE"
        );
    }

    @Test
    void testGetRootLoggerName() {
        assertEquals("", this.logging.getRootLoggerName());
    }

    @Test
    void testGetLoggerNames() {
        assertEquals(ofList("com.acme.Test", "com.acme", "com"), this.logging.getLoggerNames());
    }

    @Test
    void testGetSupportedLoggingLevels() {
        assertEquals(JAVA_LOGGING_LEVELS, this.logging.getSupportedLoggingLevels());
    }

    @Test
    void testGetLoggerLevel() {
        assertEquals("INFO", this.logging.getLoggerLevel("com.acme.Test"));
        assertEquals("DEBUG", this.logging.getLoggerLevel("com.acme"));
        assertEquals("TRACE", this.logging.getLoggerLevel("com"));
    }

    @Test
    void testSetLoggerLevel() {
        this.logging.setLoggerLevel("com.acme.Test", "WARN");
        assertEquals("WARN", this.logging.getLoggerLevel("com.acme.Test"));
    }

    @Test
    void testGetParentLoggerName() {
        assertEquals("com.acme", this.logging.getParentLoggerName("com.acme.Test"));
        assertEquals("com", this.logging.getParentLoggerName("com.acme"));
        assertEquals("", this.logging.getParentLoggerName("com"));
        assertNull(this.logging.getParentLoggerName(""));
    }

    @Test
    void testGetName() {
        assertEquals("Testing", this.logging.getName());
    }

    @Test
    void testGetPriority() {
        assertEquals(MAX_PRIORITY, this.logging.getPriority());
    }

}