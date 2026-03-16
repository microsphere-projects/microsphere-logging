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
import io.microsphere.logging.LoggerFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.microsphere.util.ServiceLoaderUtils.loadFirstService;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

/**
 * {@link Log4j2LoggerFactory} Test
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see Log4j2LoggerFactory
 * @since 1.0.0
 */
class Log4j2LoggerFactoryTest {

    private Log4j2LoggerFactory factory;

    @BeforeEach
    void setUp() {
        this.factory = (Log4j2LoggerFactory) loadFirstService(LoggerFactory.class);
    }

    @Test
    void testGetDelegateLoggerClassName() {
        assertEquals("org.apache.logging.log4j.Logger", factory.getDelegateLoggerClassName());
    }

    @Test
    void testCreateLogger() {
        Logger logger = factory.createLogger("name");
        assertInstanceOf(Log4j2Logger.class, logger);
    }

    @Test
    void testGetPriority() {
        assertEquals(-5, this.factory.getPriority());
    }
}