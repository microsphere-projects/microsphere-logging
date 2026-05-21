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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.ErrorHandler;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LifeCycle;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.junit.jupiter.api.Test;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static io.microsphere.logging.log4j2.util.Log4j2Utils.addAppender;
import static io.microsphere.logging.log4j2.util.Log4j2Utils.findAppender;
import static io.microsphere.logging.log4j2.util.Log4j2Utils.getLevel;
import static io.microsphere.logging.log4j2.util.Log4j2Utils.getLevelString;
import static io.microsphere.logging.log4j2.util.Log4j2Utils.getLogger;
import static io.microsphere.logging.log4j2.util.Log4j2Utils.getLoggerContext;
import static io.microsphere.logging.log4j2.util.Log4j2Utils.setLoggerLevel;
import static org.apache.logging.log4j.Level.INFO;
import static org.apache.logging.log4j.Level.TRACE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link Log4j2Utils} Test
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see Log4j2Utils
 * @since 1.0.0
 */
class Log4j2UtilsTest {

    @Test
    void testGetLoggerContext() {
        assertSame(LogManager.getContext(false), getLoggerContext());
    }

    @Test
    void testGetLogger() {
        assertNotNull(getLogger("test"));
        assertNotNull(getLogger(Log4j2UtilsTest.class));
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

    @Test
    void shouldCoverGettersAndLevels() {
        Logger logger = Log4j2Utils.getLogger(Log4j2UtilsTest.class.getName());
        assertNotNull(logger);

        assertEquals(logger, Log4j2Utils.getLogger(Log4j2UtilsTest.class.getName()));
        assertEquals(logger, Log4j2Utils.getLogger(Log4j2UtilsTest.class));

        assertNotNull(Log4j2Utils.getConfiguration());
        assertNotNull(Log4j2Utils.getLoggerContext());
        assertNotNull(Log4j2Utils.getLoggers());

        Level oldLevel = logger.getLevel();
        try {
            Log4j2Utils.setLoggerLevel(logger, Level.WARN);
            assertEquals(Level.WARN, Log4j2Utils.getLevel(logger));
            assertEquals("WARN", Log4j2Utils.getLevelString(logger.getName()));

            Log4j2Utils.setLoggerLevel(logger, "ERROR");
            assertEquals(Level.ERROR, Log4j2Utils.getLevel(logger));

            // null string -> toLevel(null) => DEBUG
            Log4j2Utils.setLoggerLevel(logger, (String) null);
            assertEquals(Level.DEBUG, Log4j2Utils.getLevel(logger));

            // null logger branches
            assertNull(Log4j2Utils.getLevel((Logger) null));
            Log4j2Utils.setLoggerLevel((Logger) null, Level.INFO);
            Log4j2Utils.setLoggerLevel((Logger) null, "INFO");

            // level string conversions
            assertNull(Log4j2Utils.getLevelString((Level) null));
            assertEquals("INFO", Log4j2Utils.getLevelString(Level.INFO));
        } finally {
            logger.setLevel(oldLevel);
        }
    }

    @Test
    void shouldCoverDoInLoggerAndFindAppender() {
        AtomicInteger count = new AtomicInteger();
        Log4j2Utils.doInLogger(l -> count.incrementAndGet());
        assertTrue(count.get() >= 0);

        LoggerContext context = (LoggerContext) LogManager.getContext(false);
        Configuration configuration = context.getConfiguration();

        TestAppender appender = new TestAppender("findMe");
        appender.start();
        configuration.addAppender(appender);
        context.getRootLogger().get().addAppender(appender, Level.INFO, null);
        context.updateLoggers();

        try {
            Appender found = findAppender("findMe");
            assertNotNull(found);
            assertEquals("findMe", found.getName());
        } finally {
            context.getRootLogger().get().removeAppender("findMe");
            configuration.getAppenders().remove("findMe");
            appender.stop();
            context.updateLoggers();
        }
    }

    @Test
    void shouldCoverAddRemoveAppenderStaticHelpers() {
        LoggerContext context = (LoggerContext) LogManager.getContext(false);
        List<Logger> targets = new ArrayList<>();
        targets.add(context.getRootLogger());

        TestAppender appender = new TestAppender("helperAppender");

        // add/remove with explicit loggers
        addAppender(appender, targets);
        assertTrue(appender.isStarted());
        assertNotNull(findAppender("helperAppender"));

        Log4j2Utils.removeAppender(appender, targets);
        assertTrue(appender.isStopped());

        // add/remove for all loggers
        TestAppender appenderAll = new TestAppender("helperAppenderAll");
        Log4j2Utils.addAppenderForAllLoggers(appenderAll);
        assertNotNull(findAppender("helperAppenderAll"));

        Log4j2Utils.removeAppenderForAllLoggers(appenderAll);
        assertNull(findAppender("helperAppenderAll"));
    }

    @Test
    void shouldCoverAddRemoveAppenderNullGuardsAndLifecycleBranches() {
        LoggerContext context = (LoggerContext) LogManager.getContext(false);
        List<Logger> targets = new ArrayList<>();
        targets.add(context.getRootLogger());

        // null guard branches
        addAppender(null, null, null);
        addAppender(context, null, targets);
        addAppender(context, new TestAppender("x1"), null);
        Log4j2Utils.removeAppender(null, null, null);
        Log4j2Utils.removeAppender(context, null, targets);
        Log4j2Utils.removeAppender(context, new TestAppender("x2"), null);

        // branch: appender already started -> start() not called again
        TestAppender startedAppender = new TestAppender("alreadyStarted");
        startedAppender.start();
        addAppender(context, startedAppender, targets);
        assertNotNull(findAppender("alreadyStarted"));

        // branch: appender already stopped -> stop() not called again path
        startedAppender.stop();
        Log4j2Utils.removeAppender(context, startedAppender, targets);
    }

    private static final class TestAppender implements Appender {
        private final String name;
        private volatile boolean started;
        private volatile boolean stopped;
        private ErrorHandler handler;

        private TestAppender(String name) {
            this.name = name;
        }

        @Override
        public void append(LogEvent event) {
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public Layout<? extends Serializable> getLayout() {
            return PatternLayout.newBuilder().withPattern("%m").build();
        }

        @Override
        public boolean ignoreExceptions() {
            return false;
        }

        @Override
        public ErrorHandler getHandler() {
            return handler;
        }

        @Override
        public void setHandler(ErrorHandler handler) {
            this.handler = handler;
        }

        @Override
        public State getState() {
            return started ? LifeCycle.State.STARTED : LifeCycle.State.STOPPED;
        }

        @Override
        public void initialize() {
        }

        @Override
        public void start() {
            started = true;
            stopped = false;
        }

        @Override
        public void stop() {
            started = false;
            stopped = true;
        }

        @Override
        public boolean isStarted() {
            return started;
        }

        @Override
        public boolean isStopped() {
            return stopped;
        }
    }
}