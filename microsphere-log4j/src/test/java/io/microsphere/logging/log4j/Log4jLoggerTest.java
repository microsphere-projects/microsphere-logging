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

package io.microsphere.logging.log4j;


import org.apache.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.microsphere.util.ArrayUtils.EMPTY_OBJECT_ARRAY;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link Log4j2Logger} Test
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see Log4j2Logger
 * @since 1.0.0
 */
class Log4jLoggerTest {

    private static final String LOGGER_NAME = Log4jLoggerTest.class.getName();

    protected Log4j2Logger logger;

    @BeforeEach
    void setUp() {
        this.logger = new Log4j2Logger(LOGGER_NAME);
    }

    @Test
    void testGetName() {
        assertEquals(LOGGER_NAME, this.logger.getName());
    }

    @Test
    void testIsTraceEnabled() {
        assertFalse(this.logger.isTraceEnabled());
    }

    @Test
    void testTrace() {
        assertDoesNotThrow(() -> this.logger.trace("test"));
        assertDoesNotThrow(() -> this.logger.trace("test", EMPTY_OBJECT_ARRAY));
        assertDoesNotThrow(() -> this.logger.trace("test : {}", "a"));
        assertDoesNotThrow(() -> this.logger.trace("test : {}", "a", new Throwable()));
        assertDoesNotThrow(() -> this.logger.trace("test", new Throwable()));
    }

    @Test
    void testIsDebugEnabled() {
        assertFalse(this.logger.isDebugEnabled());
    }

    @Test
    void testDebug() {
        assertDoesNotThrow(() -> this.logger.debug("test"));
        assertDoesNotThrow(() -> this.logger.debug("test", EMPTY_OBJECT_ARRAY));
        assertDoesNotThrow(() -> this.logger.debug("test : {}", "a"));
        assertDoesNotThrow(() -> this.logger.debug("test : {}", "a", new Throwable()));
        assertDoesNotThrow(() -> this.logger.debug("test", new Throwable()));
    }

    @Test
    void testIsInfoEnabled() {
        assertTrue(this.logger.isInfoEnabled());
    }

    @Test
    void testInfo() {
        assertDoesNotThrow(() -> this.logger.info("test"));
        assertDoesNotThrow(() -> this.logger.info("test", EMPTY_OBJECT_ARRAY));
        assertDoesNotThrow(() -> this.logger.info("test : {}", "a"));
        assertDoesNotThrow(() -> this.logger.info("test : {}", "a", new Throwable()));
        assertDoesNotThrow(() -> this.logger.info("test", new Throwable()));
    }

    @Test
    void testIsWarnEnabled() {
        assertTrue(this.logger.isWarnEnabled());
    }

    @Test
    void testWarn() {
        assertDoesNotThrow(() -> this.logger.warn("test"));
        assertDoesNotThrow(() -> this.logger.warn("test", EMPTY_OBJECT_ARRAY));
        assertDoesNotThrow(() -> this.logger.warn("test : {}", "a"));
        assertDoesNotThrow(() -> this.logger.warn("test : {}", "a", new Throwable()));
        assertDoesNotThrow(() -> this.logger.warn("test", new Throwable()));
    }

    @Test
    void testIsErrorEnabled() {
        assertTrue(this.logger.isErrorEnabled());
    }

    @Test
    void testError() {
        assertDoesNotThrow(() -> this.logger.error("test"));
        assertDoesNotThrow(() -> this.logger.error("test", EMPTY_OBJECT_ARRAY));
        assertDoesNotThrow(() -> this.logger.error("test : {}", "a"));
        assertDoesNotThrow(() -> this.logger.error("test : {}", "a", new Throwable()));
        assertDoesNotThrow(() -> this.logger.error("test", new Throwable()));
    }

    @Test
    void testGetDelegate() {
        Logger logger = this.logger.unwrap(Logger.class);
        assertSame(logger, this.logger.getDelegate());
    }
}