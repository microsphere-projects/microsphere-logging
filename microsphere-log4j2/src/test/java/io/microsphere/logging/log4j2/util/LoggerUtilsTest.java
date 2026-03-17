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

package io.microsphere.logging.log4j2.util;


import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Logger;
import org.junit.jupiter.api.Test;

import static io.microsphere.logging.log4j2.util.LoggerUtils.getLevel;
import static io.microsphere.logging.log4j2.util.LoggerUtils.getLevelString;
import static io.microsphere.logging.log4j2.util.LoggerUtils.getLogger;
import static io.microsphere.logging.log4j2.util.LoggerUtils.getLoggerContext;
import static io.microsphere.logging.log4j2.util.LoggerUtils.setLoggerLevel;
import static org.apache.logging.log4j.Level.INFO;
import static org.apache.logging.log4j.Level.TRACE;
import static org.apache.logging.log4j.core.LoggerContext.getContext;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * {@link LoggerUtils} Test
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see LoggerUtils
 * @since 1.0.0
 */
class LoggerUtilsTest {

    @Test
    void testGetLoggerContext() {
        assertSame(getContext(), getLoggerContext());
    }

    @Test
    void testGetLogger() {
        assertNotNull(getLogger("test"));
        assertNotNull(getLogger(LoggerUtilsTest.class));
        assertThrows(NullPointerException.class, () -> getLogger((Class<?>) null));
        assertThrows(NullPointerException.class, () -> getLogger((String) null));
    }

    @Test
    void testGetLevel() {
        assertNull(getLevel((Logger) null));
        assertEquals(INFO, getLevel("io.microsphere.logging.log4j2.util.LoggerUtils"));
        assertEquals(INFO, getLevel("io.microsphere.logging.log4j2.util"));
        assertEquals(INFO, getLevel("io.microsphere.logging.log4j2"));
        assertEquals(INFO, getLevel("io.microsphere.logging"));
        assertEquals(TRACE, getLevel("io.microsphere"));
        assertEquals(TRACE, getLevel("io"));
    }

    @Test
    void testGetLevelString() {
        assertNull(getLevelString((Level) null));
        assertEquals("INFO", getLevelString(INFO));
        assertEquals("TRACE", getLevelString(TRACE));
    }

    @Test
    void testSetLoggerLevel() {
        setLoggerLevel((Logger) null, "DEBUG");
        assertLoggerLevel("io.microsphere.logging.log4j2.util.LoggerUtils", "DEBUG");
    }

    void assertLoggerLevel(String loggerName, String levelString) {
        String oldLevel = getLevelString(loggerName);
        setLoggerLevel(loggerName, levelString);
        assertEquals(levelString, getLevelString(loggerName));
        setLoggerLevel(loggerName, oldLevel);
    }
}