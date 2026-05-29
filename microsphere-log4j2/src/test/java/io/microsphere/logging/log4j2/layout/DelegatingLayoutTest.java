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

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.impl.Log4jLogEvent;
import org.apache.logging.log4j.core.layout.ByteBufferDestination;
import org.apache.logging.log4j.message.SimpleMessage;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DelegatingLayoutTest {

    @Test
    void shouldDelegateAllMethodsWithConstructorDelegate() {
        RecordingLayout delegate = new RecordingLayout();
        DelegatingLayout<String> layout = new DelegatingLayout<>(delegate);
        LogEvent event = newEvent("hello");

        assertArrayEquals(delegate.footer, layout.getFooter());
        assertArrayEquals(delegate.header, layout.getHeader());
        assertArrayEquals(delegate.byteArray, layout.toByteArray(event));
        assertEquals(delegate.serializableValue, layout.toSerializable(event));
        assertEquals(delegate.contentType, layout.getContentType());
        assertEquals(delegate.contentFormat, layout.getContentFormat());

        TestByteBufferDestination destination = new TestByteBufferDestination();
        layout.encode(event, destination);
        assertTrue(delegate.encodeCalled);
        assertNotNull(delegate.lastEncodedEvent);
        assertTrue(destination.writtenBytes > 0);

        assertSame(delegate, layout.getDelegate());
    }

    @Test
    void shouldSupportNoArgConstructorAndSetter() {
        DelegatingLayout<String> layout = new DelegatingLayout<>();
        RecordingLayout delegate = new RecordingLayout();

        layout.setDelegate(delegate);
        assertSame(delegate, layout.getDelegate());

        LogEvent event = newEvent("world");
        assertEquals("SER", layout.toSerializable(event));
    }

    private static LogEvent newEvent(String message) {
        return Log4jLogEvent.newBuilder()
                .setLoggerName("test.logger")
                .setLoggerFqcn(DelegatingLayoutTest.class.getName())
                .setLevel(Level.INFO)
                .setMessage(new SimpleMessage(message))
                .build();
    }

    private static final class RecordingLayout implements Layout<String> {
        private final byte[] footer = new byte[]{1, 2};
        private final byte[] header = new byte[]{3, 4};
        private final byte[] byteArray = new byte[]{5, 6};
        private final String serializableValue = "SER";
        private final String contentType = "text/plain";
        private final Map<String, String> contentFormat = Collections.singletonMap("k", "v");

        private boolean encodeCalled;
        private LogEvent lastEncodedEvent;

        @Override
        public byte[] getFooter() {
            return footer;
        }

        @Override
        public byte[] getHeader() {
            return header;
        }

        @Override
        public byte[] toByteArray(LogEvent event) {
            return byteArray;
        }

        @Override
        public String toSerializable(LogEvent event) {
            return serializableValue;
        }

        @Override
        public String getContentType() {
            return contentType;
        }

        @Override
        public Map<String, String> getContentFormat() {
            return contentFormat;
        }

        @Override
        public void encode(LogEvent source, ByteBufferDestination destination) {
            encodeCalled = true;
            lastEncodedEvent = source;
            destination.writeBytes(new byte[]{9, 9}, 0, 2);
        }
    }

    private static final class TestByteBufferDestination implements ByteBufferDestination {
        private ByteBuffer buffer = ByteBuffer.allocate(32);
        private int writtenBytes;

        @Override
        public ByteBuffer getByteBuffer() {
            return buffer;
        }

        @Override
        public ByteBuffer drain(ByteBuffer buf) {
            writtenBytes += buf.position();
            buf.clear();
            return buf;
        }

        @Override
        public void writeBytes(ByteBuffer data) {
            writtenBytes += data.remaining();
            data.position(data.limit());
        }

        @Override
        public void writeBytes(byte[] data, int offset, int length) {
            writtenBytes += length;
        }
    }
}
