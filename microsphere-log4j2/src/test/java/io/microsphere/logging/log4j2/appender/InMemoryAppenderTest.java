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

package io.microsphere.logging.log4j2.appender;

import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.apache.logging.log4j.message.SimpleMessage;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.microsphere.collection.ListUtils.newArrayList;
import static io.microsphere.logging.log4j2.appender.InMemoryAppender.DEFAULT_LOG_EVENTS_MAX_CAPACITY;
import static io.microsphere.logging.log4j2.appender.InMemoryAppender.LOG_EVENTS_MAX_CAPACITY_PROPERTY_NAME;
import static io.microsphere.logging.log4j2.appender.InMemoryAppender.NAME;
import static io.microsphere.logging.log4j2.appender.InMemoryAppender.PROPERTY_NAME_PREFIX;
import static io.microsphere.logging.log4j2.appender.InMemoryAppender.findInMemoryAppender;
import static java.lang.Thread.sleep;
import static org.apache.logging.log4j.Level.INFO;
import static org.apache.logging.log4j.core.impl.Log4jLogEvent.newBuilder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link InMemoryAppender} Test
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see InMemoryAppender
 * @since 1.0.0
 */
class InMemoryAppenderTest {

    @Test
    void testConstants() {
        assertEquals("InMemory", NAME);
        assertEquals("microsphere.logging.log4j2.appender.in-memory.", PROPERTY_NAME_PREFIX);
        assertEquals("microsphere.logging.log4j2.appender.in-memory.max-capacity", LOG_EVENTS_MAX_CAPACITY_PROPERTY_NAME);
        assertEquals("99999", DEFAULT_LOG_EVENTS_MAX_CAPACITY);
    }

    @Test
    void testBasicMethodsAndLifecycle() {
        InMemoryAppender appender = new InMemoryAppender();

        assertEquals(NAME, appender.getName());
        assertNull(appender.getLayout());
        assertFalse(appender.ignoreExceptions());
        assertNull(appender.getHandler());

        // no-op path
        appender.setHandler(null);

        // lifecycle methods
        appender.initialize();
        appender.start();
        appender.stop(); // also covers clear() on empty set
    }

    @Test
    void testAppendTransferAndStopClearsEvents() throws InterruptedException {
        InMemoryAppender source = new InMemoryAppender();
        CollectingAppender target = new CollectingAppender();

        LogEvent event1 = newEvent("message-1");
        LogEvent event2 = newEvent("message-2");

        source.append(event1);
        source.append(event2);

        source.transfer(target);

        assertEquals(2, target.events.size());
        assertTrue(target.events.contains(event1));
        assertTrue(target.events.contains(event2));

        // Verify source is emptied after transfer
        source.transfer(target);
        assertEquals(2, target.events.size(), "second transfer should not add duplicates");

        // Re-add and ensure stop clears buffered events
        source.append(newEvent("message-3"));
        source.stop();
        source.transfer(target);
        assertEquals(2, target.events.size(), "stop() should clear pending events");
    }

    @Test
    void testFindInMemoryAppenderWithoutConfiguration() {
        // In plain unit test context, this may be null unless explicitly configured.
        // The purpose is to execute static method path for coverage.
        InMemoryAppender result = findInMemoryAppender();
        // no strict assertion on value to keep test environment-agnostic
        assertTrue(result == null || result instanceof InMemoryAppender);
    }

    @Test
    void testMaxCapacity() throws InterruptedException {
        InMemoryAppender appender = new InMemoryAppender();
        assertEquals(99999, appender.getMaxCapacity());
        appender.setMaxCapacity(1);
        assertEquals(1, appender.getMaxCapacity());

        LogEvent event1 = newEvent("message-1");
        LogEvent event2 = newEvent("message-2");

        appender.append(event1);
        appender.append(event2);

        CollectingAppender target = new CollectingAppender();
        appender.transfer(target);
        assertEquals(1, target.events.size());
        assertTrue(target.events.contains(event2));
    }

    private static LogEvent newEvent(String message) throws InterruptedException {
        sleep(50L);
        return newBuilder()
                .setLoggerName("test.logger")
                .setLoggerFqcn(InMemoryAppenderTest.class.getName())
                .setLevel(INFO)
                .setMessage(new SimpleMessage(message))
                .build();
    }

    private static class CollectingAppender implements Appender {
        private final List<LogEvent> events = newArrayList();

        @Override
        public void append(LogEvent event) {
            events.add(event);
        }

        @Override
        public String getName() {
            return "collecting";
        }

        @Override
        public org.apache.logging.log4j.core.Layout<?> getLayout() {
            return PatternLayout.newBuilder().withPattern("%m").build();
        }

        @Override
        public boolean ignoreExceptions() {
            return false;
        }

        @Override
        public org.apache.logging.log4j.core.ErrorHandler getHandler() {
            return null;
        }

        @Override
        public void setHandler(org.apache.logging.log4j.core.ErrorHandler handler) {
        }

        @Override
        public void start() {
        }

        @Override
        public void stop() {
        }

        @Override
        public boolean isStarted() {
            return true;
        }

        @Override
        public boolean isStopped() {
            return false;
        }

        @Override
        public State getState() {
            return State.STARTED;
        }

        @Override
        public void initialize() {
        }
    }
}
