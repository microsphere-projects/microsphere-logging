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

import java.util.Set;

import static io.microsphere.collection.Sets.ofSet;
import static io.microsphere.logging.DefaultLoggingLevelsResolver.INSTANCE;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * {@link DefaultLoggingLevelsResolver}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see DefaultLoggingLevelsResolver
 * @since 1.0.0
 */
public class DefaultLoggingLevelsResolverTest {

    /**
     * @see java.util.logging.Level
     */
    public static final Set<String> JAVA_LOGGING_LEVELS = ofSet("OFF", "SEVERE", "WARNING", "INFO", "CONFIG", "FINE", "FINER", "FINEST", "ALL");

    /**
     * @see org.slf4j.event.Level
     */
    public static final Set<String> SLF4J_LEVELS = ofSet("ERROR", "WARN", "INFO", "DEBUG", "TRACE");

    /**
     * @see org.apache.log4j.Level
     */
    public static final Set<String> LOG4J_LEVELS = ofSet("OFF", "FATAL", "ERROR", "WARN", "INFO", "DEBUG", "TRACE", "ALL");

    /**
     * @see org.apache.logging.log4j.Level
     */
    public static final Set<String> LOG4J2_LEVELS = ofSet("OFF", "FATAL", "ERROR", "WARN", "INFO", "DEBUG", "TRACE", "ALL");

    /**
     * @see ch.qos.logback.classic.Level
     */
    public static final Set<String> LOGBACK_LEVELS = ofSet("OFF", "ERROR", "WARN", "INFO", "DEBUG", "TRACE", "ALL");

    private DefaultLoggingLevelsResolver resolver;

    @BeforeEach
    void setUp() {
        this.resolver = INSTANCE;
    }

    @Test
    void testResolve() {
        assertEquals(JAVA_LOGGING_LEVELS, this.resolver.resolve(java.util.logging.Level.class));
        assertEquals(SLF4J_LEVELS, this.resolver.resolve(org.slf4j.event.Level.class));
        assertEquals(LOG4J_LEVELS, this.resolver.resolve(org.apache.log4j.Level.class));
        assertEquals(LOG4J2_LEVELS, this.resolver.resolve(org.apache.logging.log4j.Level.class));
        assertEquals(LOGBACK_LEVELS, this.resolver.resolve(ch.qos.logback.classic.Level.class));
    }
}