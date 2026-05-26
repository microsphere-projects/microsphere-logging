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

package io.microsphere.logging.log4j2.layout;

import io.microsphere.logging.log4j2.appender.InMemoryAppender;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.FileAppender;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.impl.Log4jLogEvent;
import org.apache.logging.log4j.core.layout.ByteBufferDestination;
import org.apache.logging.log4j.message.SimpleMessage;
import org.junit.jupiter.api.Test;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link SmartFileAppenderLayout} Test
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @since 1.0.0
 */
class SmartFileAppenderLayoutTest {

    @Test
    void shouldCoverSelectionAndDelegationPaths() {
        LoggerContext context = new LoggerContext("smart-layout-test");
        Configuration configuration = context.getConfiguration();

        MarkerLayout rootLayout = new MarkerLayout("ROOT");

        FileAppender rootFileAppender = FileAppender.newBuilder()
                .setName("rootFile")
                .setConfiguration(configuration)
                .withFileName("target/root.log")
                .setLayout(rootLayout)
                .build();

        assertNotNull(rootFileAppender, "FileAppender should be created");
        rootFileAppender.start();
        configuration.addAppender(rootFileAppender);

        InMemoryAppender inMemoryAppender = new InMemoryAppender();
        inMemoryAppender.start();

        try {
            LoggerConfig rootConfig = configuration.getRootLogger();
            rootConfig.addAppender(rootFileAppender, Level.INFO, null);

            LoggerConfig childConfig = new LoggerConfig("a.b.c", Level.INFO, true);
            childConfig.addAppender(inMemoryAppender, Level.INFO, null);
            configuration.addLogger("a.b.c", childConfig);

            LoggerConfig orphanConfig = new LoggerConfig("x.y.z", Level.INFO, true);
            configuration.addLogger("x.y.z", orphanConfig);

            context.updateLoggers();

            SmartFileAppenderLayout<Serializable> layout = new SmartFileAppenderLayout<>(context);

            assertArrayEquals(SmartFileAppenderLayout.DEFAULT_LAYOUT.getFooter(), layout.getFooter());
            assertArrayEquals(SmartFileAppenderLayout.DEFAULT_LAYOUT.getHeader(), layout.getHeader());
            assertEquals(SmartFileAppenderLayout.DEFAULT_LAYOUT.getContentType(), layout.getContentType());
            assertEquals(SmartFileAppenderLayout.DEFAULT_LAYOUT.getContentFormat(), layout.getContentFormat());

            LogEvent childEvent = event("a.b.c", "m1");
            assertEquals("m1", String.valueOf(layout.toSerializable(childEvent)));

            TestDestination d1 = new TestDestination();
            layout.encode(childEvent, d1);
            assertTrue(d1.written > 0);

            LogEvent orphanEvent = event("x.y.z", "orphan");
            assertArrayEquals(SmartFileAppenderLayout.DEFAULT_LAYOUT.toByteArray(orphanEvent), layout.toByteArray(orphanEvent));
            assertEquals(SmartFileAppenderLayout.DEFAULT_LAYOUT.toSerializable(orphanEvent), layout.toSerializable(orphanEvent));

            LogEvent unknownEvent = event("unknown.logger", "unknown");
            assertArrayEquals(SmartFileAppenderLayout.DEFAULT_LAYOUT.toByteArray(unknownEvent), layout.toByteArray(unknownEvent));
            assertEquals(SmartFileAppenderLayout.DEFAULT_LAYOUT.toSerializable(unknownEvent), layout.toSerializable(unknownEvent));

            TestDestination d2 = new TestDestination();
            layout.encode(unknownEvent, d2);
            assertTrue(d2.written > 0);
        } finally {
            rootFileAppender.stop();
            inMemoryAppender.stop();
            context.stop();
        }
    }

    @Test
    void shouldPreferRollingFileAppenderOverFileAppender() {
        LoggerContext context = new LoggerContext("smart-layout-rolling-preferred");
        Configuration configuration = context.getConfiguration();

        MarkerLayout fileLayout = new MarkerLayout("FILE");
        MarkerLayout rollingLayout = new MarkerLayout("ROLLING");

        FileAppender fileAppender = FileAppender.newBuilder()
                .setName("fileAppender")
                .setConfiguration(configuration)
                .withFileName("target/prefer-file.log")
                .setLayout(fileLayout)
                .build();
        assertNotNull(fileAppender);
        fileAppender.start();
        configuration.addAppender(fileAppender);

        org.apache.logging.log4j.core.appender.RollingFileAppender rollingAppender =
                org.apache.logging.log4j.core.appender.RollingFileAppender.newBuilder()
                        .setName("rollingAppender")
                        .setConfiguration(configuration)
                        .withFileName("target/prefer-rolling.log")
                        .withFilePattern("target/prefer-rolling-%i.log.gz")
                        .setLayout(rollingLayout)
                        .withPolicy(
                                org.apache.logging.log4j.core.appender.rolling.SizeBasedTriggeringPolicy.createPolicy("10 MB")
                        )
                        .withStrategy(
                                org.apache.logging.log4j.core.appender.rolling.DefaultRolloverStrategy.newBuilder()
                                        .withConfig(configuration)
                                        .build()
                        )
                        .build();

        assertNotNull(rollingAppender, "RollingFileAppender should be created");
        rollingAppender.start();
        configuration.addAppender(rollingAppender);

        try {
            LoggerConfig loggerConfig = new LoggerConfig("p.q.r", Level.INFO, true);
            loggerConfig.addAppender(fileAppender, Level.INFO, null);
            loggerConfig.addAppender(rollingAppender, Level.INFO, null);
            configuration.addLogger("p.q.r", loggerConfig);

            context.updateLoggers();

            SmartFileAppenderLayout<Serializable> layout = new SmartFileAppenderLayout<>(context);

            // After
            LogEvent event = event("p.q.r", "prefer-rolling");
            String actual = String.valueOf(layout.toSerializable(event));
            String fileValue = String.valueOf(fileLayout.toSerializable(event));
            assertNotEquals(fileValue, actual);

            TestDestination destination = new TestDestination();
            layout.encode(event, destination);
            assertTrue(destination.written > 0);
        } finally {
            rollingAppender.stop();
            fileAppender.stop();
            context.stop();
        }
    }

    @Test
    void shouldFallbackToDefaultWhenParentLoggerIsNull() {
        LoggerContext context = new LoggerContext("smart-layout-null-parent");
        Configuration configuration = context.getConfiguration();

        // Remove all appenders from root logger and add only InMemory
        LoggerConfig rootConfig = configuration.getRootLogger();
        for (String name : new java.util.ArrayList<>(rootConfig.getAppenders().keySet())) {
            rootConfig.removeAppender(name);
        }
        InMemoryAppender inMemoryAppender = new InMemoryAppender();
        inMemoryAppender.start();
        rootConfig.addAppender(inMemoryAppender, Level.ALL, null);

        try {
            context.updateLoggers();

            SmartFileAppenderLayout<Serializable> layout = new SmartFileAppenderLayout<>(context);

            // Root logger has only InMemory appender (removed by NAME), so appendersMap is empty.
            // Root's parent is null, so selectAppender returns null → DEFAULT_LAYOUT is used.
            LogEvent rootEvent = event("", "root-message");
            assertArrayEquals(SmartFileAppenderLayout.DEFAULT_LAYOUT.toByteArray(rootEvent), layout.toByteArray(rootEvent));
            assertEquals(
                    String.valueOf(SmartFileAppenderLayout.DEFAULT_LAYOUT.toSerializable(rootEvent)),
                    String.valueOf(layout.toSerializable(rootEvent))
            );
        } finally {
            inMemoryAppender.stop();
            context.stop();
        }
    }

    @Test
    void shouldFallbackToDefaultWhenAppenderLayoutIsNull() {
        LoggerContext context = new LoggerContext("smart-layout-null-layout");
        Configuration configuration = context.getConfiguration();

        org.apache.logging.log4j.core.Appender nullLayoutAppender = new NullLayoutAppender("nullLayoutAppender");
        nullLayoutAppender.start();
        configuration.addAppender(nullLayoutAppender);

        try {
            LoggerConfig loggerConfig = new LoggerConfig("n.l", Level.INFO, true);
            loggerConfig.addAppender(nullLayoutAppender, Level.INFO, null);
            configuration.addLogger("n.l", loggerConfig);

            context.updateLoggers();

            SmartFileAppenderLayout<Serializable> layout = new SmartFileAppenderLayout<>(context);

            LogEvent event = event("n.l", "msg-null-layout");
            assertArrayEquals(SmartFileAppenderLayout.DEFAULT_LAYOUT.toByteArray(event), layout.toByteArray(event));
            assertEquals(
                    String.valueOf(SmartFileAppenderLayout.DEFAULT_LAYOUT.toSerializable(event)),
                    String.valueOf(layout.toSerializable(event))
            );

            TestDestination destination = new TestDestination();
            layout.encode(event, destination);
            assertTrue(destination.written > 0);
        } finally {
            nullLayoutAppender.stop();
            context.stop();
        }
    }

    private static LogEvent event(String loggerName, String msg) {
        return Log4jLogEvent.newBuilder()
                .setLoggerName(loggerName)
                .setLoggerFqcn(SmartFileAppenderLayoutTest.class.getName())
                .setLevel(Level.INFO)
                .setMessage(new SimpleMessage(msg))
                .build();
    }

    private static final class MarkerLayout implements Layout<Serializable> {
        private final String marker;

        private MarkerLayout(String marker) {
            this.marker = marker;
        }

        @Override
        public byte[] getFooter() {
            return ("F-" + marker).getBytes();
        }

        @Override
        public byte[] getHeader() {
            return ("H-" + marker).getBytes();
        }

        @Override
        public byte[] toByteArray(LogEvent event) {
            return marker.getBytes();
        }

        @Override
        public Serializable toSerializable(LogEvent event) {
            return marker;
        }

        @Override
        public String getContentType() {
            return "text/plain";
        }

        @Override
        public Map<String, String> getContentFormat() {
            return Collections.singletonMap("marker", marker);
        }

        @Override
        public void encode(LogEvent source, ByteBufferDestination destination) {
            byte[] bytes = marker.getBytes();
            destination.writeBytes(bytes, 0, bytes.length);
        }
    }

    private static final class TestDestination implements ByteBufferDestination {
        private ByteBuffer buffer = ByteBuffer.allocate(64);
        private int written;

        @Override
        public ByteBuffer getByteBuffer() {
            return buffer;
        }

        @Override
        public ByteBuffer drain(ByteBuffer buf) {
            written += buf.position();
            buf.clear();
            return buf;
        }

        @Override
        public void writeBytes(ByteBuffer data) {
            written += data.remaining();
            data.position(data.limit());
        }

        @Override
        public void writeBytes(byte[] data, int offset, int length) {
            written += length;
        }
    }

    private static final class NullLayoutAppender extends org.apache.logging.log4j.core.AbstractLifeCycle
            implements org.apache.logging.log4j.core.Appender {

        private final String name;

        private NullLayoutAppender(String name) {
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
            return null;
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
    }
}